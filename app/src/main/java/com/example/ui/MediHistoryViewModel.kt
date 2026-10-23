package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.MediHistoryDatabase
import com.example.data.local.PatientEntity
import com.example.data.local.RecordEntity
import com.example.data.model.DoctorReadySummary
import com.example.data.model.DoctorSummaryGenerator
import com.example.data.repository.MediHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole {
    PARENT,
    DOCTOR
}

enum class MediScreen(val title: String) {
    DEMO("Demo Showcase"),
    PATIENTS("Patients"),
    RECORDS("Records"),
    TIMELINE("Timeline"),
    SUMMARY("AI Summary")
}

class MediHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MediHistoryRepository

    init {
        val db = MediHistoryDatabase.getDatabase(application)
        repository = MediHistoryRepository(db.mediHistoryDao())
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    private val _currentRole = MutableStateFlow(UserRole.PARENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentScreen = MutableStateFlow(MediScreen.DEMO)
    val currentScreen: StateFlow<MediScreen> = _currentScreen.asStateFlow()

    private val _selectedPatientId = MutableStateFlow<Long>(1L)
    val selectedPatientId: StateFlow<Long> = _selectedPatientId.asStateFlow()

    private val _timelineCategoryFilter = MutableStateFlow("ALL")
    val timelineCategoryFilter: StateFlow<String> = _timelineCategoryFilter.asStateFlow()

    private val _selectedRecordForInspection = MutableStateFlow<RecordEntity?>(null)
    val selectedRecordForInspection: StateFlow<RecordEntity?> = _selectedRecordForInspection.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    // Patients Flow
    val allPatients: StateFlow<List<PatientEntity>> = repository.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Patient Flow
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activePatient: StateFlow<PatientEntity?> = _selectedPatientId.flatMapLatest { id ->
        repository.getPatientById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Records for Active Patient
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val patientRecords: StateFlow<List<RecordEntity>> = _selectedPatientId.flatMapLatest { id ->
        repository.getRecordsForPatient(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Extracted Items for Active Patient
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val patientExtractedItems: StateFlow<List<ExtractedItemEntity>> = _selectedPatientId.flatMapLatest { id ->
        repository.getExtractedItemsForPatient(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Doctor Ready Summary
    val doctorReadySummary: StateFlow<DoctorReadySummary?> = combine(
        activePatient,
        patientRecords,
        patientExtractedItems
    ) { patient, records, items ->
        if (patient != null) {
            DoctorSummaryGenerator.generateSummary(patient, records, items)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setRole(role: UserRole) {
        _currentRole.value = role
        val roleLabel = if (role == UserRole.DOCTOR) "Doctor Mode (Review & Verify)" else "Parent Mode (Upload & Edit)"
        _userMessage.value = "Switched to $roleLabel"
    }

    fun setScreen(screen: MediScreen) {
        _currentScreen.value = screen
    }

    fun selectPatient(patientId: Long) {
        _selectedPatientId.value = patientId
        _selectedRecordForInspection.value = null
    }

    fun setTimelineFilter(filter: String) {
        _timelineCategoryFilter.value = filter
    }

    fun selectRecordForInspection(record: RecordEntity?) {
        _selectedRecordForInspection.value = record
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun toggleDoctorVerification(item: ExtractedItemEntity, notes: String? = null) {
        viewModelScope.launch {
            val newVerified = !item.verifiedByDoctor
            val updatedNotes = if (newVerified) {
                notes ?: "Verified by physician against original document"
            } else {
                null
            }
            repository.updateDoctorVerification(item.id, newVerified, updatedNotes)
            _userMessage.value = if (newVerified) "Item verified by doctor" else "Verification removed"
        }
    }

    fun updateExtractedItem(
        item: ExtractedItemEntity,
        newName: String,
        newDose: String?,
        newFreq: String?,
        newDuration: String?,
        newStatus: String
    ) {
        viewModelScope.launch {
            val updated = item.copy(
                itemName = newName,
                dose = newDose,
                frequency = newFreq,
                duration = newDuration,
                status = newStatus
            )
            repository.updateExtractedItem(updated)
            _userMessage.value = "Item corrected by parent"
        }
    }

    fun deleteExtractedItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteExtractedItem(itemId)
            _userMessage.value = "Item deleted"
        }
    }

    fun savePatient(
        id: Long = 0,
        name: String,
        age: Int,
        bloodGroup: String,
        allergies: String,
        conditions: String,
        emergency: String
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                val newId = repository.insertPatient(
                    PatientEntity(
                        name = name,
                        age = age,
                        bloodGroup = bloodGroup,
                        allergies = allergies,
                        existingConditions = conditions,
                        emergencyContact = emergency
                    )
                )
                _selectedPatientId.value = newId
                _userMessage.value = "Patient profile created: $name"
            } else {
                val existing = repository.getPatientByIdDirect(id)
                if (existing != null) {
                    val updated = existing.copy(
                        name = name,
                        age = age,
                        bloodGroup = bloodGroup,
                        allergies = allergies,
                        existingConditions = conditions,
                        emergencyContact = emergency
                    )
                    repository.updatePatient(updated)
                    _userMessage.value = "Patient profile updated: $name"
                }
            }
        }
    }

    fun uploadRecord(
        patientId: Long,
        title: String,
        type: String,
        date: String?,
        facility: String,
        text: String,
        isBlurry: Boolean
    ) {
        viewModelScope.launch {
            _isExtracting.value = true
            _userMessage.value = "Analyzing document with MediHistory AI..."
            try {
                repository.addRecordAndExtract(
                    patientId = patientId,
                    title = title,
                    type = type,
                    date = date,
                    facilityOrDoctor = facility,
                    rawText = text,
                    isBlurryOrIncomplete = isBlurry
                ) { resultStatus ->
                    _userMessage.value = resultStatus
                }
            } catch (e: Exception) {
                _userMessage.value = "Upload error: ${e.message}"
            } finally {
                _isExtracting.value = false
            }
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
            if (_selectedRecordForInspection.value?.id == recordId) {
                _selectedRecordForInspection.value = null
            }
            _userMessage.value = "Record removed"
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            _selectedPatientId.value = 1L
            _selectedRecordForInspection.value = null
            _userMessage.value = "Demo records reset to default fictional patients"
        }
    }
}
