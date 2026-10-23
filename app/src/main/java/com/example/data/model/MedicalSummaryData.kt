package com.example.data.model

import com.example.data.local.ExtractedItemEntity
import com.example.data.local.PatientEntity
import com.example.data.local.RecordEntity

enum class AlertSeverity {
    HIGH,   // Red
    MEDIUM, // Amber
    INFO    // Blue
}

data class MedicalAlert(
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val category: String,
    val affectedRecordId: Long? = null,
    val suggestedAction: String
)

data class DoctorReadySummary(
    val patient: PatientEntity,
    val generatedDate: String,
    val documentedAllergies: List<ExtractedItemEntity>,
    val existingConditions: List<ExtractedItemEntity>,
    val previousVisits: List<RecordEntity>,
    val reportedMedicines: List<ExtractedItemEntity>,
    val importantReports: List<ExtractedItemEntity>,
    val missingOrUnclearItems: List<ExtractedItemEntity>,
    val alerts: List<MedicalAlert>,
    val verificationCount: Int,
    val totalItemsCount: Int
) {
    fun toFormattedText(): String {
        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("      MEDI-HISTORY AI CLINICAL SUMMARY   \n")
        sb.append("========================================\n")
        sb.append("PATIENT: ${patient.name} | AGE: ${patient.age}y | BLOOD: ${patient.bloodGroup}\n")
        sb.append("EMERGENCY CONTACT: ${patient.emergencyContact}\n")
        sb.append("PROFILE ALLERGIES: ${if (patient.allergies.isNotBlank()) patient.allergies else "NOT RECORDED [ALERT]"}\n")
        sb.append("PROFILE CONDITIONS: ${if (patient.existingConditions.isNotBlank()) patient.existingConditions else "None documented"}\n")
        sb.append("GENERATED: $generatedDate\n")
        sb.append("SAFETY NOTICE: Illustrative concept, not a working medical system. AI never diagnoses, suggests treatment, or recommends doses.\n")
        sb.append("VERIFICATION STATUS: $verificationCount of $totalItemsCount items verified by physician.\n\n")

        if (alerts.isNotEmpty()) {
            sb.append("--- MISSING-HISTORY ALERTS & CONFLICTS ---\n")
            alerts.forEach { alert ->
                sb.append("[${alert.severity.name}] ${alert.title}: ${alert.description}\n")
                sb.append("  Action: ${alert.suggestedAction}\n")
            }
            sb.append("\n")
        }

        sb.append("1. DOCUMENTED ALLERGIES:\n")
        if (documentedAllergies.isEmpty()) {
            sb.append("  No confirmed allergy records documented.\n")
        } else {
            documentedAllergies.forEach { a ->
                val verifiedBadge = if (a.verifiedByDoctor) "[VERIFIED BY DOCTOR]" else "[NEEDS VERIFICATION]"
                sb.append("  • ${a.itemName} - Status: ${a.status} $verifiedBadge\n")
                sb.append("    Source: \"${a.sourceSnippet}\"\n")
            }
        }
        sb.append("\n")

        sb.append("2. EXISTING CONDITIONS:\n")
        if (existingConditions.isEmpty()) {
            sb.append("  No clinical conditions documented in submitted records.\n")
        } else {
            existingConditions.forEach { c ->
                val verifiedBadge = if (c.verifiedByDoctor) "[VERIFIED BY DOCTOR]" else "[NEEDS VERIFICATION]"
                sb.append("  • ${c.itemName} - Status: ${c.status} $verifiedBadge\n")
                sb.append("    Source: \"${c.sourceSnippet}\"\n")
            }
        }
        sb.append("\n")

        sb.append("3. PREVIOUS VISITS & CLINICAL ENCOUNTERS:\n")
        if (previousVisits.isEmpty()) {
            sb.append("  No previous visits documented.\n")
        } else {
            previousVisits.forEach { v ->
                val dateStr = v.recordDate ?: "[Date Missing]"
                sb.append("  • $dateStr: ${v.facilityOrDoctor} (${v.recordType})\n")
            }
        }
        sb.append("\n")

        sb.append("4. REPORTED MEDICINES:\n")
        if (reportedMedicines.isEmpty()) {
            sb.append("  No medications documented.\n")
        } else {
            reportedMedicines.forEach { m ->
                val verifiedBadge = if (m.verifiedByDoctor) "[VERIFIED BY DOCTOR]" else "[NEEDS VERIFICATION]"
                val doseStr = m.dose ?: "Dose Unclear"
                val freqStr = m.frequency ?: "Freq Unclear"
                val durStr = m.duration ?: "Duration Unclear"
                sb.append("  • ${m.itemName} | Dose: $doseStr | Freq: $freqStr | Dur: $durStr [${m.status}] $verifiedBadge\n")
                sb.append("    Source: \"${m.sourceSnippet}\"\n")
            }
        }
        sb.append("\n")

        sb.append("5. IMPORTANT LAB REPORTS & DIAGNOSTICS:\n")
        if (importantReports.isEmpty()) {
            sb.append("  No lab reports on file.\n")
        } else {
            importantReports.forEach { l ->
                val verifiedBadge = if (l.verifiedByDoctor) "[VERIFIED BY DOCTOR]" else "[NEEDS VERIFICATION]"
                val rangeStr = l.normalRange?.let { " (Ref: $it)" } ?: ""
                sb.append("  • ${l.itemName}: ${l.testResult}$rangeStr [${l.status}] $verifiedBadge\n")
                sb.append("    Source: \"${l.sourceSnippet}\"\n")
            }
        }
        sb.append("\n")

        sb.append("6. MISSING / UNCLEAR INFORMATION (NEEDS VERIFICATION):\n")
        if (missingOrUnclearItems.isEmpty() && alerts.none { it.severity == AlertSeverity.HIGH }) {
            sb.append("  No unclear items flagged.\n")
        } else {
            missingOrUnclearItems.forEach { u ->
                sb.append("  [!] UNCLEAR ITEM: ${u.itemName} (${u.category})\n")
                sb.append("      Snippet: \"${u.sourceSnippet}\"\n")
            }
            alerts.forEach { alert ->
                sb.append("  [!] ALERT: ${alert.title} - ${alert.description}\n")
            }
        }
        sb.append("========================================\n")
        return sb.toString()
    }
}

object DoctorSummaryGenerator {

    fun generateSummary(
        patient: PatientEntity,
        records: List<RecordEntity>,
        extractedItems: List<ExtractedItemEntity>
    ): DoctorReadySummary {
        val alerts = mutableListOf<MedicalAlert>()

        // 1. Alert: Allergy status not recorded in profile
        if (patient.allergies.isBlank()) {
            alerts.add(
                MedicalAlert(
                    title = "Allergy Status Not Recorded in Profile",
                    description = "Patient profile has no documented allergies on file. Pediatric patients require explicit allergy notation.",
                    severity = AlertSeverity.HIGH,
                    category = "MISSING_ALLERGY",
                    suggestedAction = "Parent or provider should confirm allergy status and update profile."
                )
            )
        }

        // 2. Alert: Conflict between profile and documents
        val documentedAllergies = extractedItems.filter { it.category == "ALLERGY" }
        for (item in documentedAllergies) {
            val allergenKeyword = item.itemName.split(" ", "(", ",").firstOrNull()?.trim() ?: ""
            if (allergenKeyword.isNotBlank()) {
                val foundInProfile = patient.allergies.contains(allergenKeyword, ignoreCase = true)
                if (!foundInProfile) {
                    val matchingRecord = records.find { it.id == item.recordId }
                    alerts.add(
                        MedicalAlert(
                            title = "Profile vs Record Conflict: $allergenKeyword",
                            description = "Record '${matchingRecord?.title ?: "Clinical Note"}' reports allergic reaction to '$allergenKeyword', but this is missing in patient profile.",
                            severity = AlertSeverity.HIGH,
                            category = "ALLERGY_CONFLICT",
                            affectedRecordId = item.recordId,
                            suggestedAction = "Verify with guardian whether patient has confirmed $allergenKeyword allergy and update patient profile."
                        )
                    )
                }
            }
        }

        // 3. Alert: Medicine without dose or frequency / unclear
        val medicines = extractedItems.filter { it.category == "MEDICINE" }
        for (med in medicines) {
            if (med.status == "UNCLEAR" || med.dose.isNullOrBlank() || med.frequency.isNullOrBlank()) {
                val matchingRecord = records.find { it.id == med.recordId }
                alerts.add(
                    MedicalAlert(
                        title = "Incomplete Medication Entry: ${med.itemName}",
                        description = "Medication lacks clear dose or frequency in '${matchingRecord?.title ?: "record"}'. Unclear items are never shown as confirmed facts.",
                        severity = AlertSeverity.MEDIUM,
                        category = "INCOMPLETE_MED",
                        affectedRecordId = med.recordId,
                        suggestedAction = "Physician must inspect original prescription to verify exact dose and frequency."
                    )
                )
            }
        }

        // 4. Alert: Record without a date
        for (record in records) {
            if (record.recordDate.isNullOrBlank()) {
                alerts.add(
                    MedicalAlert(
                        title = "Record Date Missing: ${record.title}",
                        description = "Document has no legible collection or encounter date. Placed in 'Date missing' timeline group.",
                        severity = AlertSeverity.MEDIUM,
                        category = "MISSING_DATE",
                        affectedRecordId = record.id,
                        suggestedAction = "Contact facility '${record.facilityOrDoctor}' to verify encounter date."
                    )
                )
            }
        }

        // 5. Alert: Illegible / blurry text
        for (record in records) {
            if (record.isBlurryOrIncomplete) {
                alerts.add(
                    MedicalAlert(
                        title = "Illegible / Incomplete Record: ${record.title}",
                        description = "Contains ink smudges, torn carbon copy, or ambiguous handwriting.",
                        severity = AlertSeverity.MEDIUM,
                        category = "ILLEGIBLE_TEXT",
                        affectedRecordId = record.id,
                        suggestedAction = "Doctor should review original image view side by side with extracted text."
                    )
                )
            }
        }

        val conditions = extractedItems.filter { it.category == "CONDITION" }
        val labReports = extractedItems.filter { it.category == "LAB_TEST" }
        val unclearItems = extractedItems.filter { it.status == "UNCLEAR" }
        val verifiedCount = extractedItems.count { it.verifiedByDoctor }

        return DoctorReadySummary(
            patient = patient,
            generatedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
            documentedAllergies = documentedAllergies,
            existingConditions = conditions,
            previousVisits = records,
            reportedMedicines = medicines,
            importantReports = labReports,
            missingOrUnclearItems = unclearItems,
            alerts = alerts,
            verificationCount = verifiedCount,
            totalItemsCount = extractedItems.size
        )
    }
}
