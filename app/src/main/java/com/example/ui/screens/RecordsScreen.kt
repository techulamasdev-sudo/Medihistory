package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.PatientEntity
import com.example.data.local.RecordEntity
import com.example.ui.UserRole
import com.example.ui.theme.StatusDoctorVerifiedText
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.StatusUnclearBg
import com.example.ui.theme.StatusUnclearText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    patient: PatientEntity?,
    records: List<RecordEntity>,
    extractedItems: List<ExtractedItemEntity>,
    userRole: UserRole,
    selectedRecord: RecordEntity?,
    isExtracting: Boolean,
    onSelectRecord: (RecordEntity?) -> Unit,
    onToggleVerify: (ExtractedItemEntity, String?) -> Unit,
    onUpdateExtractedItem: (item: ExtractedItemEntity, name: String, dose: String?, freq: String?, dur: String?, status: String) -> Unit,
    onUploadRecord: (patientId: Long, title: String, type: String, date: String?, facility: String, text: String, isBlurry: Boolean) -> Unit,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ExtractedItemEntity?>(null) }

    // If a record is selected, show the split / side-by-side inspection view
    if (selectedRecord != null) {
        RecordSideBySideViewer(
            record = selectedRecord,
            patient = patient,
            extractedItems = extractedItems.filter { it.recordId == selectedRecord.id },
            userRole = userRole,
            onClose = { onSelectRecord(null) },
            onToggleVerify = onToggleVerify,
            onEditItem = { itemToEdit = it },
            onDeleteRecord = {
                onDeleteRecord(selectedRecord.id)
                onSelectRecord(null)
            }
        )
    } else {
        Scaffold(
            modifier = modifier.testTag("records_screen"),
            floatingActionButton = {
                if (userRole == UserRole.PARENT && patient != null) {
                    FloatingActionButton(
                        onClick = { showUploadDialog = true },
                        containerColor = TealPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_upload_record")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Record")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Record", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    MandatorySafetyBanner()
                }

                // Active Patient Banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = TealContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Records for ${patient?.name ?: "Patient"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TealOnContainer
                                )
                                Text(
                                    text = "${records.size} documents uploaded · ${extractedItems.size} extracted clinical items",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TealOnContainer.copy(alpha = 0.8f)
                                )
                            }
                            if (userRole == UserRole.DOCTOR) {
                                Surface(
                                    color = StatusDoctorVerifiedText,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Doctor Audit View",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isExtracting) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "AI extracting structured clinical facts... (Safety rules enforced)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                if (records.isEmpty()) {
                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "No records",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No records on file",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Use the upload button below to add prescriptions or clinical notes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(records) { record ->
                        val recordItems = extractedItems.filter { it.recordId == record.id }
                        val unclearCount = recordItems.count { it.status == "UNCLEAR" }
                        val verifiedCount = recordItems.count { it.verifiedByDoctor }

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectRecord(record) }
                                .testTag("record_card_${record.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.elevatedCardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = TealContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = record.recordType,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TealOnContainer,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = record.recordDate ?: "Date missing",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (record.recordDate != null) MaterialTheme.colorScheme.onSurfaceVariant else StatusMissingText,
                                                fontWeight = if (record.recordDate != null) FontWeight.Normal else FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = record.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = record.facilityOrDoctor,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (record.isBlurryOrIncomplete) {
                                        Surface(
                                            color = StatusUnclearBg,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Blurry / Incomplete",
                                                    tint = StatusUnclearText,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Incomplete/Blurry",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = StatusUnclearText,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Badges Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${recordItems.size} Extracted Items",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 11.sp
                                            )
                                        }

                                        if (unclearCount > 0) {
                                            Surface(
                                                color = StatusUnclearBg,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "$unclearCount Unclear [Alert]",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = StatusUnclearText,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        if (verifiedCount > 0) {
                                            Surface(
                                                color = Color(0xFFEEF2FF),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "$verifiedCount Verified",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = StatusDoctorVerifiedText,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onSelectRecord(record) },
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "Inspect",
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (userRole == UserRole.DOCTOR) "Doctor Audit" else "View Details", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Item Dialog (Parent Review & Correction)
    if (itemToEdit != null) {
        val item = itemToEdit!!
        var itemName by remember { mutableStateOf(item.itemName) }
        var dose by remember { mutableStateOf(item.dose ?: "") }
        var freq by remember { mutableStateOf(item.frequency ?: "") }
        var dur by remember { mutableStateOf(item.duration ?: "") }
        var status by remember { mutableStateOf(item.status) }

        AlertDialog(
            onDismissRequest = { itemToEdit = null },
            title = { Text("Parent Review: Correct Extracted Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Original snippet: \"${item.sourceSnippet}\"",
                        style = MaterialTheme.typography.labelSmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dose,
                        onValueChange = { dose = it },
                        label = { Text("Dose") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = freq,
                        onValueChange = { freq = it },
                        label = { Text("Frequency") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dur,
                        onValueChange = { dur = it },
                        label = { Text("Duration") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Status:", style = MaterialTheme.typography.labelMedium)
                        Button(
                            onClick = { status = "DOCUMENTED" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status == "DOCUMENTED") TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (status == "DOCUMENTED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Documented", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { status = "UNCLEAR" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status == "UNCLEAR") StatusUnclearText else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (status == "UNCLEAR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Unclear", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateExtractedItem(
                            item,
                            itemName.trim(),
                            dose.trim().takeIf { it.isNotBlank() },
                            freq.trim().takeIf { it.isNotBlank() },
                            dur.trim().takeIf { it.isNotBlank() },
                            status
                        )
                        itemToEdit = null
                    }
                ) {
                    Text("Save Correction")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Upload New Record Dialog
    if (showUploadDialog && patient != null) {
        var title by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("Prescription") }
        var date by remember { mutableStateOf("2024-11-20") }
        var isDateMissing by remember { mutableStateOf(false) }
        var facility by remember { mutableStateOf("Children's Health Clinic") }
        var rawText by remember { mutableStateOf("") }
        var isBlurry by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Upload", tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Medical Record & AI Parse")
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "SAFETY: AI extracts only documented facts. AI never diagnoses, suggests treatment, or recommends doses.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Quick Presets
                    item {
                        Text("Quick Demo Presets:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    title = "Follow-up Clinic Rx"
                                    type = "Prescription"
                                    date = "2024-11-22"
                                    facility = "Northwest Pediatric Clinic"
                                    rawText = "NORTHWEST PEDIATRIC CLINIC\nPatient: ${patient.name}\nDx: Viral Pharyngitis\nRx: Ibuprofen pediatric oral suspension 100 mg / 5 mL\nDose: 7.5 mL orally every 6-8 hours as needed for pain/fever\nDuration: 3 days. Do not exceed 4 doses daily."
                                    isBlurry = false
                                },
                                modifier = Modifier.height(30.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Ibuprofen Rx", fontSize = 10.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    title = "Emergency Visit Slip [Blurry]"
                                    type = "Clinical Note"
                                    date = "2024-11-25"
                                    facility = "Downtown Emergency Care"
                                    rawText = "EMERGENCY CLINIC SLIP [SMUDGED]\nPt: ${patient.name}\nChief Complaint: Mild cough\nRx: Amox... [smudged tear] 250mg?\nDose: [ink blot] mL twice daily for ? days."
                                    isBlurry = true
                                },
                                modifier = Modifier.height(30.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Smudged Note", fontSize = 10.sp)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Record Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_record_title")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = facility,
                            onValueChange = { facility = it },
                            label = { Text("Facility / Doctor Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isDateMissing,
                                onCheckedChange = { isDateMissing = it }
                            )
                            Text("Date missing / omitted in source", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (!isDateMissing) {
                        item {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("Date (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isBlurry,
                                onCheckedChange = { isBlurry = it }
                            )
                            Text("Mark as blurry / smudged record", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = rawText,
                            onValueChange = { rawText = it },
                            label = { Text("Document Text / OCR Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("input_record_text"),
                            placeholder = { Text("Paste document text, prescription label, or lab report...") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && rawText.isNotBlank()) {
                            onUploadRecord(
                                patient.id,
                                title.trim(),
                                type,
                                if (isDateMissing) null else date.trim(),
                                facility.trim(),
                                rawText.trim(),
                                isBlurry
                            )
                            showUploadDialog = false
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_upload")
                ) {
                    Text("Analyze & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Side-by-Side / Split View for Doctor review against original source
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordSideBySideViewer(
    record: RecordEntity,
    patient: PatientEntity?,
    extractedItems: List<ExtractedItemEntity>,
    userRole: UserRole,
    onClose: () -> Unit,
    onToggleVerify: (ExtractedItemEntity, String?) -> Unit,
    onEditItem: (ExtractedItemEntity) -> Unit,
    onDeleteRecord: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("record_side_by_side_viewer")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = record.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${record.recordType} · ${record.facilityOrDoctor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (userRole == UserRole.PARENT) {
                        IconButton(onClick = onDeleteRecord) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete record", tint = StatusMissingText)
                        }
                    }
                }
            }
        }

        MandatorySafetyBanner(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))

        // Secondary Tab Row: [Original Document] | [Extracted Facts & Verification]
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Original Document (Source)", fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Extracted Facts (${extractedItems.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 0) {
                // Original Document View (Simulated Medical Stationery / Paper Record)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (record.isBlurryOrIncomplete) {
                        item {
                            Surface(
                                color = StatusUnclearBg,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFCD34D))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Smudge warning",
                                        tint = StatusUnclearText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AUDIT NOTE: Source contains ink smudges, torn carbon copy, or ambiguous handwriting. Extracted items marked 'Unclear' require doctor verification.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusUnclearText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = Color(0xFFFFFDF8) // Warm clinic paper
                            ),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                // Clinical Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = record.facilityOrDoctor.uppercase(),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "CLINICAL SOURCE RECORD · OFFICIAL DOCUMENT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = record.recordType.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF334155),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFCBD5E1))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Patient: ${patient?.name ?: "Unknown"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Date: ${record.recordDate ?: "[OMITTED / MISSING]"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (record.recordDate != null) Color(0xFF475569) else StatusMissingText,
                                        fontWeight = if (record.recordDate != null) FontWeight.Normal else FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Raw Document Text in Monospace / Clinical Print
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Text(
                                        text = record.rawText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(14.dp),
                                        lineHeight = 20.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Archived Timestamp: ${record.uploadedAt}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "ORIGINAL SOURCE VERIFIED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Switch to Extracted Items & Verification →")
                        }
                    }
                }
            } else {
                // Extracted Facts & Doctor Verification List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Structured Items (${extractedItems.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (userRole == UserRole.DOCTOR) {
                                Text(
                                    text = "Tap 'Verify' to sign off item",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusDoctorVerifiedText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (extractedItems.isEmpty()) {
                        item {
                            Text(
                                text = "No structured clinical facts extracted for this document.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(extractedItems) { item ->
                            ExtractedItemCard(
                                item = item,
                                userRole = userRole,
                                documentTitle = record.title,
                                onToggleVerify = { onToggleVerify(item, null) },
                                onEditItem = { onEditItem(item) },
                                onViewSource = { selectedTab = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}
