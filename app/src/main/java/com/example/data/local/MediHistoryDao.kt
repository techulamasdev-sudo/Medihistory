package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MediHistoryDao {

    // Patients
    @Query("SELECT * FROM patients ORDER BY id ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    fun getPatientById(id: Long): Flow<PatientEntity?>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientByIdDirect(id: Long): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatient(id: Long)

    @Query("DELETE FROM patients")
    suspend fun clearAllPatients()

    // Records
    @Query("SELECT * FROM records WHERE patientId = :patientId ORDER BY uploadedAt DESC")
    fun getRecordsForPatient(patientId: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): RecordEntity?

    @Query("SELECT * FROM records WHERE patientId = :patientId")
    suspend fun getRecordsListDirect(patientId: Long): List<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity): Long

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    // Extracted Items
    @Query("SELECT * FROM extracted_items WHERE patientId = :patientId ORDER BY id ASC")
    fun getExtractedItemsForPatient(patientId: Long): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE recordId = :recordId")
    fun getExtractedItemsForRecord(recordId: Long): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE patientId = :patientId")
    suspend fun getExtractedItemsListDirect(patientId: Long): List<ExtractedItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtractedItem(item: ExtractedItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtractedItems(items: List<ExtractedItemEntity>)

    @Update
    suspend fun updateExtractedItem(item: ExtractedItemEntity)

    @Query("UPDATE extracted_items SET verifiedByDoctor = :verified, doctorNotes = :notes, verifiedAt = :timestamp WHERE id = :itemId")
    suspend fun updateDoctorVerification(itemId: Long, verified: Boolean, notes: String?, timestamp: Long?)

    @Query("DELETE FROM extracted_items WHERE id = :id")
    suspend fun deleteExtractedItem(id: Long)

    @Query("DELETE FROM extracted_items WHERE recordId = :recordId")
    suspend fun deleteExtractedItemsByRecord(recordId: Long)

    // Summaries
    @Query("SELECT * FROM summaries WHERE patientId = :patientId ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestSummaryForPatient(patientId: Long): Flow<SummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long
}
