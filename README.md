# MediHistory AI

> **"Every medical detail remembered, every doctor better informed."**
>
> *Illustrative concept, not a working medical system. Demo uses fictional patient records only.*

---

## 1. Problem Statement
When children visit different clinics, urgent care centers, and pediatric specialists, parents are left carrying paper prescriptions, crumpled carbon copies, lab PDFs, and discharge summaries. During acute medical visits:
- Critical details are lost or forgotten (e.g. past adverse drug reactions, exact antibiotic dosages, incomplete treatment courses).
- Doctors have only minutes to evaluate a patient and lack a single unified timeline.
- Transcribing records by hand is tedious and error-prone.

**MediHistory AI** bridges this gap: parents upload clinical records or enter history, and AI transforms them into an organized chronological timeline and a doctor-ready clinical summary. Doctors review the timeline on a single screen and can verify every fact against the original source document.

---

## 2. Safety Design (Non-Negotiable Rules)

MediHistory AI adheres strictly to clinical safety principles:

1. **AI Never Diagnoses, Suggests Treatment, or Recommends Doses**:
   - The AI acts purely as an automated factual parser. It extracts only what is explicitly written in the source text.
2. **Tri-State Status Classification**:
   - **Documented (Green)**: Clearly and legibly stated in the source document.
   - **Unclear (Amber)**: Illegible handwriting, ink smudges, torn paper, or ambiguous phrasing. *Unclear items are never presented as confirmed facts.*
   - **Missing (Red)**: Missing required medical parameters (e.g. missing dose/frequency, unrecorded allergy status).
3. **Traceability to Original Source**:
   - Every extracted medicine, lab test, condition, and allergy links back directly to its source document and includes an exact source snippet quote.
4. **Persistent Safety Notice**:
   - Every screen prominently displays: *"Illustrative concept, not a working medical system. Demo uses fictional patient records only."*
5. **Neutral Clinical Wording**:
   - Language is restricted to neutral descriptors: `"documented"`, `"reported"`, `"needs verification"`.
6. **No Speculation / Error Transparency**:
   - If OCR or the AI extraction encounters ambiguous or torn text, it explicitly flags the ambiguity for physician verification instead of guessing.

---

## 3. How AI is Used on Uploaded Documents

### AI Pipeline:
1. **Document Ingestion**:
   - Accepts prescriptions, lab reports, discharge summaries, and clinical notes.
2. **Structured Factual Extraction (Gemini 3.8 Flash)**:
   - Ingests source text with strict system instructions:
     - Prompt rule: *"Extract only what is written. Use null for anything absent. Never infer or assume."*
     - Outputs a strict JSON schema: `document_type`, `document_date`, `facility_or_doctor`, `medicines[]`, `lab_findings[]`, `conditions_mentioned[]`, `allergies_mentioned[]`, `unclear_fields[]`.
3. **Missing-History & Conflict Alert Engine**:
   - **Missing Allergy Alert**: Detects if patient profile allergy status is blank.
   - **Profile vs Document Conflict**: Alerts when a clinical record reports an allergy (e.g. peanut hives) not recorded in the patient profile.
   - **Incomplete Medication**: Alerts when a medication is missing dose or frequency.
   - **Undated Records**: Automatically groups undated events into an explicit `"Date missing"` section.
4. **Doctor Verification Workflow**:
   - Doctors switch to **Doctor Mode** to view the original source side-by-side with extracted facts and can toggle `"Verified by doctor"` with audit timestamps.

---

## 4. Fictional Demo Datasets

The app includes two pre-loaded fictional pediatric patient records:

### Patient 1: Leo Chen (Age 4, O+)
- **Allergies**: Amoxicillin (rash in 2024)
- **Conditions**: Mild reactive airway disease
- **Records**:
  1. *Westside Pediatrics Prescription*: Azithromycin 200mg/5ml, 2.5ml once daily for 5 days.
  2. *Metro Urgent Care (Blurry/Incomplete)*: Smudged carbon copy with unclear cephalosporin dose (`Ceph... 125mg? [ink smudge]`) demonstrating the **Amber: Unclear** status badge and doctor verification requirement.
  3. *Quest Diagnostics Lab Report*: Blood panel showing mild eosinophilia (6.8% High).

### Patient 2: Maya Patel (Age 7, A+)
- **Allergies**: *Unrecorded / Missing* (demonstrates the missing allergy status alert).
- **Conditions**: Recurrent Otitis Media
- **Records**:
  1. *Sunrise Urgent Care*: Reports daycare incident with peanut hives treated with Cetirizine 5mg. Triggers a **High-Priority Conflict Alert** because the allergy is missing in her profile.
  2. *Surgical Hospital Discharge Summary*: Bilateral myringotomy tube placement.
  3. *Regional Immunology Lab*: Peanut IgE antibody panel with *no collection date*, demonstrating automatic grouping into the **"Date missing"** timeline category.

---

## 5. Setup & Running Instructions

### Android Application:
- Built with **Kotlin** and **Jetpack Compose (Material 3)**.
- Local persistence powered by **Room (SQLite)**.
- AI powered by **Gemini 3.8 Flash** via REST API.

### Configuration:
1. Ensure your Gemini API Key is configured in the **Secrets panel in AI Studio** (or in `.env` / `BuildConfig.GEMINI_API_KEY`).
2. Run `compile_applet` to build the APK.
3. The app comes pre-seeded with fictional patient data and includes a **"Reset Demo Data"** button to restore baseline test cases at any time during a demo.
