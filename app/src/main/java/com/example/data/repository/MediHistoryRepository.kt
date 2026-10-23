package com.example.data.repository

import com.example.data.local.ExtractedItemEntity
import com.example.data.local.MediHistoryDao
import com.example.data.local.PatientEntity
import com.example.data.local.RecordEntity
import com.example.data.local.SummaryEntity
import com.example.data.model.DoctorReadySummary
import com.example.data.model.DoctorSummaryGenerator
import com.example.data.remote.GeminiMedicalExtractor
import kotlinx.coroutines.flow.Flow

class MediHistoryRepository(private val dao: MediHistoryDao) {

    fun getAllPatients(): Flow<List<PatientEntity>> = dao.getAllPatients()

    fun getPatientById(id: Long): Flow<PatientEntity?> = dao.getPatientById(id)

    suspend fun getPatientByIdDirect(id: Long): PatientEntity? = dao.getPatientByIdDirect(id)

    suspend fun insertPatient(patient: PatientEntity): Long = dao.insertPatient(patient)

    suspend fun updatePatient(patient: PatientEntity) = dao.updatePatient(patient)

    suspend fun deletePatient(id: Long) = dao.deletePatient(id)

    fun getRecordsForPatient(patientId: Long): Flow<List<RecordEntity>> =
        dao.getRecordsForPatient(patientId)

    suspend fun getRecordById(id: Long): RecordEntity? = dao.getRecordById(id)

    fun getExtractedItemsForPatient(patientId: Long): Flow<List<ExtractedItemEntity>> =
        dao.getExtractedItemsForPatient(patientId)

    fun getExtractedItemsForRecord(recordId: Long): Flow<List<ExtractedItemEntity>> =
        dao.getExtractedItemsForRecord(recordId)

    suspend fun updateDoctorVerification(
        itemId: Long,
        verified: Boolean,
        notes: String?
    ) {
        val timestamp = if (verified) System.currentTimeMillis() else null
        dao.updateDoctorVerification(itemId, verified, notes, timestamp)
    }

    suspend fun updateExtractedItem(item: ExtractedItemEntity) {
        dao.updateExtractedItem(item)
    }

    suspend fun deleteExtractedItem(itemId: Long) {
        dao.deleteExtractedItem(itemId)
    }

    suspend fun deleteRecord(recordId: Long) {
        dao.deleteRecord(recordId)
    }

    suspend fun addRecordAndExtract(
        patientId: Long,
        title: String,
        type: String,
        date: String?,
        facilityOrDoctor: String,
        rawText: String,
        isBlurryOrIncomplete: Boolean = false,
        onExtractionResult: (String) -> Unit = {}
    ): Long {
        val record = RecordEntity(
            patientId = patientId,
            title = title,
            recordType = type,
            recordDate = date?.takeIf { it.isNotBlank() },
            facilityOrDoctor = facilityOrDoctor,
            rawText = rawText,
            isBlurryOrIncomplete = isBlurryOrIncomplete
        )
        val recordId = dao.insertRecord(record)

        // Run AI Extraction
        val extractionResult = GeminiMedicalExtractor.extractMedicalData(rawText, title)
        extractionResult.onSuccess { data ->
            onExtractionResult("Extracted with ${data.extractionSource}")
            val itemsToInsert = mutableListOf<ExtractedItemEntity>()

            // Allergies
            data.allergies.forEach { a ->
                itemsToInsert.add(
                    ExtractedItemEntity(
                        recordId = recordId,
                        patientId = patientId,
                        category = "ALLERGY",
                        itemName = if (a.reaction != null) "${a.allergen} (${a.reaction})" else a.allergen,
                        status = a.status,
                        sourceSnippet = a.sourceSnippet,
                        verifiedByDoctor = false
                    )
                )
            }

            // Conditions
            data.conditions.forEach { c ->
                itemsToInsert.add(
                    ExtractedItemEntity(
                        recordId = recordId,
                        patientId = patientId,
                        category = "CONDITION",
                        itemName = c.conditionName,
                        status = c.status,
                        sourceSnippet = c.sourceSnippet,
                        verifiedByDoctor = false
                    )
                )
            }

            // Medicines
            data.medicines.forEach { m ->
                itemsToInsert.add(
                    ExtractedItemEntity(
                        recordId = recordId,
                        patientId = patientId,
                        category = "MEDICINE",
                        itemName = m.name,
                        dose = m.dose,
                        frequency = m.frequency,
                        duration = m.duration,
                        status = m.status,
                        sourceSnippet = m.sourceSnippet,
                        verifiedByDoctor = false
                    )
                )
            }

            // Lab Findings
            data.labFindings.forEach { l ->
                itemsToInsert.add(
                    ExtractedItemEntity(
                        recordId = recordId,
                        patientId = patientId,
                        category = "LAB_TEST",
                        itemName = l.testName,
                        testResult = l.result,
                        normalRange = l.normalRange,
                        status = l.status,
                        sourceSnippet = l.sourceSnippet,
                        verifiedByDoctor = false
                    )
                )
            }

            if (itemsToInsert.isNotEmpty()) {
                dao.insertExtractedItems(itemsToInsert)
            }
        }.onFailure { err ->
            onExtractionResult("AI Extraction error: ${err.message}. Never invent content.")
        }

        return recordId
    }

    suspend fun generateDoctorSummary(patientId: Long): DoctorReadySummary? {
        val patient = dao.getPatientByIdDirect(patientId) ?: return null
        val records = dao.getRecordsListDirect(patientId)
        val extractedItems = dao.getExtractedItemsListDirect(patientId)
        return DoctorSummaryGenerator.generateSummary(patient, records, extractedItems)
    }

    suspend fun resetDemoData() {
        com.example.data.local.DemoDataProvider.resetToDefault(dao)
    }

    suspend fun seedIfEmpty() {
        com.example.data.local.DemoDataProvider.seedDatabaseIfEmpty(dao)
    }
}
