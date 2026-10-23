package com.example

import com.example.data.local.DemoDataProvider
import com.example.data.model.AlertSeverity
import com.example.data.model.DoctorSummaryGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLeoChenSummaryAndIncompleteMedicationAlert() {
        val summary = DoctorSummaryGenerator.generateSummary(
            patient = DemoDataProvider.demoPatient1,
            records = listOf(DemoDataProvider.leoRecord1, DemoDataProvider.leoRecord2, DemoDataProvider.leoRecord3),
            extractedItems = DemoDataProvider.leoItems
        )

        assertNotNull(summary)
        assertEquals("Leo Chen", summary.patient.name)
        // Verify 6 sections have data
        assertTrue(summary.documentedAllergies.isNotEmpty())
        assertTrue(summary.existingConditions.isNotEmpty())
        assertTrue(summary.reportedMedicines.isNotEmpty())
        assertTrue(summary.importantReports.isNotEmpty())

        // Incomplete/unclear medicine from smudged record must be flagged
        val incompleteAlert = summary.alerts.find { it.category == "INCOMPLETE_MED" }
        assertNotNull("Smudged medicine should trigger INCOMPLETE_MED alert", incompleteAlert)

        // Unclear item must be in missingOrUnclearItems list
        assertTrue(summary.missingOrUnclearItems.any { it.status == "UNCLEAR" })
    }

    @Test
    fun testMayaPatelMissingAllergyAndConflictAlert() {
        val summary = DoctorSummaryGenerator.generateSummary(
            patient = DemoDataProvider.demoPatient2,
            records = listOf(DemoDataProvider.mayaRecord1, DemoDataProvider.mayaRecord2, DemoDataProvider.mayaRecord3),
            extractedItems = DemoDataProvider.mayaItems
        )

        assertNotNull(summary)
        assertEquals("Maya Patel", summary.patient.name)

        // Alert 1: Profile has blank allergy status
        val missingAllergyAlert = summary.alerts.find { it.category == "MISSING_ALLERGY" }
        assertNotNull("Blank profile allergy should trigger MISSING_ALLERGY alert", missingAllergyAlert)
        assertEquals(AlertSeverity.HIGH, missingAllergyAlert?.severity)

        // Alert 2: Daycare record mentions Peanut allergy which conflicts with profile
        val conflictAlert = summary.alerts.find { it.category == "ALLERGY_CONFLICT" }
        assertNotNull("Peanut record should trigger ALLERGY_CONFLICT alert", conflictAlert)
        assertEquals(AlertSeverity.HIGH, conflictAlert?.severity)

        // Alert 3: Lab report is undated
        val dateMissingAlert = summary.alerts.find { it.category == "MISSING_DATE" }
        assertNotNull("Undated lab report should trigger MISSING_DATE alert", dateMissingAlert)
    }
}
