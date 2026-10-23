package com.example.data.local

object DemoDataProvider {

    val demoPatient1 = PatientEntity(
        id = 1,
        name = "Leo Chen",
        age = 4,
        bloodGroup = "O+",
        allergies = "Amoxicillin (rash in 2024)",
        existingConditions = "Mild reactive airway disease",
        emergencyContact = "Sarah Chen (Mother) · (555) 234-5678"
    )

    val demoPatient2 = PatientEntity(
        id = 2,
        name = "Maya Patel",
        age = 7,
        bloodGroup = "A+",
        allergies = "", // INTENTIONALLY MISSING to demonstrate Missing-History Alert
        existingConditions = "Recurrent Otitis Media",
        emergencyContact = "Dev Patel (Father) · (555) 876-4321"
    )

    // Records for Leo Chen
    val leoRecord1 = RecordEntity(
        id = 1,
        patientId = 1,
        title = "Westside Pediatrics Prescription",
        recordType = "Prescription",
        recordDate = "2024-10-12",
        facilityOrDoctor = "Dr. Angela Brooks, MD · Westside Pediatrics",
        rawText = """
            WESTSIDE PEDIATRICS CLINICAL RX
            Patient: Leo Chen (DOB: 2020-04-12) | Age: 4y | Wt: 16.5 kg
            Date: 2024-10-12 | Provider: Dr. Angela Brooks, MD
            Diagnosis: Acute Bronchitis
            ALLERGY ALERT: Known mild rash reaction to Amoxicillin. Avoid penicillin derivatives.
            Rx: Azithromycin oral suspension (200 mg / 5 mL)
            Dispense: 15 mL bottle
            Sig: Take 2.5 mL (100 mg) orally once daily for 5 days.
            Instruction: Give with or after food. Call clinic if barking cough or wheezing worsens.
        """.trimIndent(),
        isBlurryOrIncomplete = false
    )

    val leoRecord2 = RecordEntity(
        id = 2,
        patientId = 1,
        title = "Metro Urgent Care Walk-In Note",
        recordType = "Prescription",
        recordDate = "2024-11-04",
        facilityOrDoctor = "Metro 24/7 Urgent Care",
        rawText = """
            METRO URGENT CARE - WALK-IN SLIP [CARBON COPY - SMUDGED INK]
            Pt: Leo Chen | Date: 2024-11-04
            C/O: Right ear discomfort & low fever x 2d. Tympanic membrane erythema.
            Rx: Ceph... [ink smudge/tear] 125mg? suspension.
            Dose: [illegible ink smudge] mL
            Frequency: [illegible mark] for ? days.
            Doctor note: Re-evaluate with primary pediatrician in 48-72h if no resolution.
        """.trimIndent(),
        isBlurryOrIncomplete = true // Blurry / Incomplete demo record
    )

    val leoRecord3 = RecordEntity(
        id = 3,
        patientId = 1,
        title = "Pediatric Blood & Allergy Panel",
        recordType = "Lab Report",
        recordDate = "2024-11-15",
        facilityOrDoctor = "Quest Diagnostics Laboratory",
        rawText = """
            QUEST DIAGNOSTICS - HEMATOLOGY REPORT
            Patient: Leo Chen | Accession: QD-882914 | Date: 2024-11-15
            Ordering Physician: Dr. Angela Brooks
            Test: Complete Blood Count with Differential
            - White Blood Cell (WBC): 7.2 K/uL [Reference: 5.0 - 12.0 K/uL] - Normal
            - Hemoglobin: 12.8 g/dL [Reference: 11.5 - 14.5 g/dL] - Normal
            - Platelet Count: 285 K/uL [Reference: 150 - 450 K/uL] - Normal
            - Eosinophils %: 6.8% HIGH [Reference: 1.0 - 4.0%] - Mild eosinophilia noted
            Note: Mild eosinophilia reported; correlate clinically with reactive airway / atopy.
        """.trimIndent(),
        isBlurryOrIncomplete = false
    )

    // Extracted Items for Leo Chen
    val leoItems = listOf(
        // From Record 1
        ExtractedItemEntity(
            id = 1,
            recordId = 1,
            patientId = 1,
            category = "ALLERGY",
            itemName = "Amoxicillin (rash)",
            status = "DOCUMENTED",
            sourceSnippet = "ALLERGY ALERT: Known mild rash reaction to Amoxicillin. Avoid penicillin derivatives.",
            verifiedByDoctor = true,
            doctorNotes = "Confirmed maternal report of mild rash in early 2024.",
            verifiedAt = 1728800000000L
        ),
        ExtractedItemEntity(
            id = 2,
            recordId = 1,
            patientId = 1,
            category = "CONDITION",
            itemName = "Acute Bronchitis",
            status = "DOCUMENTED",
            sourceSnippet = "Diagnosis: Acute Bronchitis",
            verifiedByDoctor = true,
            doctorNotes = "Treated in clinic.",
            verifiedAt = 1728800000000L
        ),
        ExtractedItemEntity(
            id = 3,
            recordId = 1,
            patientId = 1,
            category = "MEDICINE",
            itemName = "Azithromycin oral suspension",
            dose = "2.5 mL (100 mg / 5 mL concentration)",
            frequency = "Once daily",
            duration = "5 days",
            status = "DOCUMENTED",
            sourceSnippet = "Sig: Take 2.5 mL (100 mg) orally once daily for 5 days.",
            verifiedByDoctor = true,
            doctorNotes = "Completed course without side effects.",
            verifiedAt = 1728800000000L
        ),

        // From Record 2 (Blurry/Incomplete!)
        ExtractedItemEntity(
            id = 4,
            recordId = 2,
            patientId = 1,
            category = "CONDITION",
            itemName = "Tympanic membrane erythema (Ear redness)",
            status = "DOCUMENTED",
            sourceSnippet = "C/O: Right ear discomfort & low fever x 2d. Tympanic membrane erythema.",
            verifiedByDoctor = false
        ),
        ExtractedItemEntity(
            id = 5,
            recordId = 2,
            patientId = 1,
            category = "MEDICINE",
            itemName = "Cephalexin [Unclear exact drug & strength]",
            dose = "125mg? [ink smudge/tear]",
            frequency = "[Illegible mark]",
            duration = "? days",
            status = "UNCLEAR", // Amber Unclear badge
            sourceSnippet = "Rx: Ceph... [ink smudge/tear] 125mg? suspension. Dose: [illegible ink smudge] mL Frequency: [illegible mark]",
            verifiedByDoctor = false,
            doctorNotes = null
        ),

        // From Record 3
        ExtractedItemEntity(
            id = 6,
            recordId = 3,
            patientId = 1,
            category = "LAB_TEST",
            itemName = "Complete Blood Count (WBC & Hemoglobin)",
            testResult = "WBC 7.2 K/uL, Hgb 12.8 g/dL",
            normalRange = "5.0-12.0 K/uL, 11.5-14.5 g/dL",
            status = "DOCUMENTED",
            sourceSnippet = "White Blood Cell (WBC): 7.2 K/uL [Normal], Hemoglobin: 12.8 g/dL [Normal]",
            verifiedByDoctor = true,
            doctorNotes = "Hematology parameters within healthy pediatric limits.",
            verifiedAt = 1731660000000L
        ),
        ExtractedItemEntity(
            id = 7,
            recordId = 3,
            patientId = 1,
            category = "LAB_TEST",
            itemName = "Eosinophils % (Differential)",
            testResult = "6.8% HIGH",
            normalRange = "1.0 - 4.0%",
            status = "DOCUMENTED",
            sourceSnippet = "Eosinophils %: 6.8% HIGH [Reference: 1.0 - 4.0%] - Mild eosinophilia noted",
            verifiedByDoctor = false
        )
    )

    // Records for Maya Patel
    val mayaRecord1 = RecordEntity(
        id = 4,
        patientId = 2,
        title = "Sunshine Daycare Incident & Urgent Care",
        recordType = "Clinical Note",
        recordDate = "2024-09-18",
        facilityOrDoctor = "Sunrise Pediatric Urgent Care",
        rawText = """
            SUNRISE URGENT CARE - CLINICAL REPORT
            Patient: Maya Patel (Age: 7) | Date: 2024-09-18
            Chief Complaint: Allergic reaction at daycare snack time.
            History of Present Illness: Child reportedly touched food containing peanut butter.
            Within 15 minutes developed erythema, urticaria (hives) on both forearms and perioral pruritus.
            No stridor, no wheezing, respiratory effort uncompromised.
            Intervention: Administered Cetirizine 5 mg liquid orally at clinic. Hives resolved over 45 min.
            DISCHARGE PLAN: Strict peanut avoidance at daycare and home. Prescription for emergency EpiPen Jr 0.15 mg provided. Urgent allergist consult scheduled.
        """.trimIndent(),
        isBlurryOrIncomplete = false
    )

    val mayaRecord2 = RecordEntity(
        id = 5,
        patientId = 2,
        title = "Bilateral Myringotomy Discharge Summary",
        recordType = "Discharge Summary",
        recordDate = "2024-05-10",
        facilityOrDoctor = "Valley Children's Surgical Center · Dr. Kenneth Vance",
        rawText = """
            VALLEY CHILDREN'S SURGICAL HOSPITAL - DISCHARGE SUMMARY
            Patient: Maya Patel | DOB: 2017-06-22 | Date of Surgery: 2024-05-10
            Attending Surgeon: Dr. Kenneth Vance, MD (Otolaryngology)
            Preoperative Diagnosis: Chronic bilateral serous otitis media with conductive hearing decrease.
            Procedure: Bilateral myringotomy with fluoroplastic tympanostomy grommet insertion.
            Course: Uneventful. Patient recovered well in PACU.
            Post-Op Rx: Ciprofloxacin 0.3% + Dexamethasone 0.1% Otic suspension.
            Sig: 3 drops in each ear twice daily for 3 days post-surgery.
            Precautions: Use ear plugs during bathing and swimming. Follow-up audiogram in 6 weeks.
        """.trimIndent(),
        isBlurryOrIncomplete = false
    )

    val mayaRecord3 = RecordEntity(
        id = 6,
        patientId = 2,
        title = "Serum Allergy IgE Screening Panel",
        recordType = "Lab Report",
        recordDate = null, // INTENTIONALLY UNDATED to demonstrate "Date missing" group
        facilityOrDoctor = "Regional Immunology Reference Lab",
        rawText = """
            REGIONAL IMMUNOLOGY REFERENCE LAB - SPECIFIC IGE PROFILE
            Patient: Maya Patel | Specimen ID: #IMM-449102
            Collection Date: [OMITTED ON CLINIC REQUISITION SLIP - DATE MISSING]
            Findings:
            - Total Serum IgE: 142 kU/L [Reference: < 90 kU/L] - Elevated
            - Peanut (f13) specific IgE: 3.4 kUA/L [Class 3 - Positive moderate/high antibody level]
            - Ara h 2 (Peanut major allergen): Positive 2.8 kUA/L
            - Egg White (f1): < 0.10 kUA/L [Negative]
            - Cow's Milk (f2): < 0.10 kUA/L [Negative]
            Clinical Interpretation: Positive specific IgE consistent with peanut sensitization. Correlate with clinical history.
        """.trimIndent(),
        isBlurryOrIncomplete = false
    )

    // Extracted Items for Maya Patel
    val mayaItems = listOf(
        // From Record 1
        ExtractedItemEntity(
            id = 8,
            recordId = 4,
            patientId = 2,
            category = "ALLERGY",
            itemName = "Peanut (urticaria / hives & pruritus)",
            status = "DOCUMENTED",
            sourceSnippet = "Child reportedly touched food containing peanut butter. Within 15 minutes developed erythema, urticaria (hives) on both forearms",
            verifiedByDoctor = false
        ),
        ExtractedItemEntity(
            id = 9,
            recordId = 4,
            patientId = 2,
            category = "MEDICINE",
            itemName = "Cetirizine liquid",
            dose = "5 mg",
            frequency = "Single clinic dose",
            duration = "Immediate",
            status = "DOCUMENTED",
            sourceSnippet = "Administered Cetirizine 5 mg liquid orally at clinic. Hives resolved over 45 min.",
            verifiedByDoctor = true,
            doctorNotes = "Appropriate urgent intervention.",
            verifiedAt = 1726700000000L
        ),
        ExtractedItemEntity(
            id = 10,
            recordId = 4,
            patientId = 2,
            category = "MEDICINE",
            itemName = "EpiPen Jr (Epinephrine auto-injector)",
            dose = "0.15 mg",
            frequency = "As needed for anaphylaxis",
            duration = "Prescribed for emergency supply",
            status = "DOCUMENTED",
            sourceSnippet = "Prescription for emergency EpiPen Jr 0.15 mg provided.",
            verifiedByDoctor = false
        ),

        // From Record 2
        ExtractedItemEntity(
            id = 11,
            recordId = 5,
            patientId = 2,
            category = "CONDITION",
            itemName = "Chronic bilateral serous otitis media",
            status = "DOCUMENTED",
            sourceSnippet = "Preoperative Diagnosis: Chronic bilateral serous otitis media with conductive hearing decrease.",
            verifiedByDoctor = true,
            doctorNotes = "Surgically treated with bilateral tympanostomy tubes.",
            verifiedAt = 1715400000000L
        ),
        ExtractedItemEntity(
            id = 12,
            recordId = 5,
            patientId = 2,
            category = "MEDICINE",
            itemName = "Ciprofloxacin 0.3% + Dexamethasone 0.1% Otic suspension",
            dose = "3 drops each ear",
            frequency = "Twice daily",
            duration = "3 days",
            status = "DOCUMENTED",
            sourceSnippet = "Sig: 3 drops in each ear twice daily for 3 days post-surgery.",
            verifiedByDoctor = true,
            doctorNotes = "Post-tympanostomy drop regimen.",
            verifiedAt = 1715400000000L
        ),

        // From Record 3 (Undated!)
        ExtractedItemEntity(
            id = 13,
            recordId = 6,
            patientId = 2,
            category = "LAB_TEST",
            itemName = "Total Serum IgE",
            testResult = "142 kU/L [Elevated]",
            normalRange = "< 90 kU/L",
            status = "DOCUMENTED",
            sourceSnippet = "Total Serum IgE: 142 kU/L [Reference: < 90 kU/L] - Elevated",
            verifiedByDoctor = false
        ),
        ExtractedItemEntity(
            id = 14,
            recordId = 6,
            patientId = 2,
            category = "LAB_TEST",
            itemName = "Peanut Specific IgE (Ara h 2)",
            testResult = "3.4 kUA/L [Class 3 Positive]",
            normalRange = "< 0.35 kUA/L",
            status = "DOCUMENTED",
            sourceSnippet = "Peanut (f13) specific IgE: 3.4 kUA/L [Class 3 - Positive moderate/high antibody level] Ara h 2: Positive 2.8 kUA/L",
            verifiedByDoctor = false
        )
    )

    suspend fun seedDatabaseIfEmpty(dao: MediHistoryDao) {
        val existing = dao.getAllPatients()
        // If empty, insert demo data
        val patientList = dao.getRecordsListDirect(1)
        if (patientList.isEmpty()) {
            dao.insertPatient(demoPatient1)
            dao.insertPatient(demoPatient2)

            dao.insertRecord(leoRecord1)
            dao.insertRecord(leoRecord2)
            dao.insertRecord(leoRecord3)

            dao.insertExtractedItems(leoItems)

            dao.insertRecord(mayaRecord1)
            dao.insertRecord(mayaRecord2)
            dao.insertRecord(mayaRecord3)

            dao.insertExtractedItems(mayaItems)
        }
    }

    suspend fun resetToDefault(dao: MediHistoryDao) {
        dao.clearAllPatients()
        dao.insertPatient(demoPatient1)
        dao.insertPatient(demoPatient2)

        dao.insertRecord(leoRecord1)
        dao.insertRecord(leoRecord2)
        dao.insertRecord(leoRecord3)

        dao.insertExtractedItems(leoItems)

        dao.insertRecord(mayaRecord1)
        dao.insertRecord(mayaRecord2)
        dao.insertRecord(mayaRecord3)

        dao.insertExtractedItems(mayaItems)
    }
}
