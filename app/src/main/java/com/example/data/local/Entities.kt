package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val bloodGroup: String,
    val allergies: String,
    val existingConditions: String,
    val emergencyContact: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val title: String,
    val recordType: String, // Prescription, Lab Report, Discharge Summary, Clinical Note
    val recordDate: String?, // YYYY-MM-DD or null if missing
    val facilityOrDoctor: String,
    val rawText: String,
    val filePath: String? = null,
    val isBlurryOrIncomplete: Boolean = false,
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "extracted_items",
    foreignKeys = [
        ForeignKey(
            entity = RecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recordId"), Index("patientId")]
)
data class ExtractedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordId: Long,
    val patientId: Long,
    val category: String, // MEDICINE, LAB_TEST, CONDITION, ALLERGY, VISIT
    val itemName: String,
    val dose: String? = null,
    val frequency: String? = null,
    val duration: String? = null,
    val testResult: String? = null,
    val normalRange: String? = null,
    val status: String, // DOCUMENTED, UNCLEAR, MISSING
    val sourceSnippet: String,
    val verifiedByDoctor: Boolean = false,
    val doctorNotes: String? = null,
    val verifiedAt: Long? = null
)

@Entity(
    tableName = "summaries",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val generatedAt: Long = System.currentTimeMillis(),
    val allergiesSection: String,
    val conditionsSection: String,
    val visitsSection: String,
    val medicinesSection: String,
    val reportsSection: String,
    val unclearOrMissingSection: String,
    val alertsJson: String
)
