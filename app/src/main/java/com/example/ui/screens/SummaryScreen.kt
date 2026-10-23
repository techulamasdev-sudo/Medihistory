package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.model.AlertSeverity
import com.example.data.model.DoctorReadySummary
import com.example.ui.UserRole
import com.example.ui.theme.StatusDoctorVerifiedText
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.StatusUnclearText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

@Composable
fun SummaryScreen(
    summary: DoctorReadySummary?,
    userRole: UserRole,
    onToggleVerify: (ExtractedItemEntity, String?) -> Unit,
    onViewRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("summary_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MandatorySafetyBanner()
        }

        if (summary == null) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Loading clinical summary...",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Header Card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = TealContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Doctor-Ready Clinical Summary",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TealOnContainer
                                )
                                Text(
                                    text = "Patient: ${summary.patient.name} (${summary.patient.age}y, ${summary.patient.bloodGroup})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TealOnContainer.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                color = StatusDoctorVerifiedText,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${summary.verificationCount}/${summary.totalItemsCount} Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "SAFETY DECLARATION: AI never diagnoses, suggests treatment, or recommends doses. All facts are organized directly from source records. Unclear items are never shown as confirmed facts.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealOnContainer,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Doctor-Ready Clinical Summary", summary.toFormattedText())
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Doctor summary copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealPrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.testTag("btn_copy_summary")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Summary",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Doctor-Ready Summary", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Missing-History Alerts Box
            if (summary.alerts.isNotEmpty()) {
                item {
                    Text(
                        text = "Missing-History Alerts & Conflicts (${summary.alerts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusMissingText
                    )
                }

                items(summary.alerts) { alert ->
                    ClinicalAlertCard(alert = alert)
                }
            }

            // 1. Documented Allergies Section
            item {
                SummarySectionCard(
                    title = "1. Documented Allergies",
                    icon = Icons.Default.Warning,
                    badgeCount = summary.documentedAllergies.size,
                    emptyMessage = "No confirmed allergy records documented."
                ) {
                    summary.documentedAllergies.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            userRole = userRole,
                            onToggleVerify = { onToggleVerify(item, null) }
                        )
                    }
                }
            }

            // 2. Existing Conditions Section
            item {
                SummarySectionCard(
                    title = "2. Existing Conditions",
                    icon = Icons.Default.LocalHospital,
                    badgeCount = summary.existingConditions.size,
                    emptyMessage = "No clinical conditions documented in submitted records."
                ) {
                    summary.existingConditions.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            userRole = userRole,
                            onToggleVerify = { onToggleVerify(item, null) }
                        )
                    }
                }
            }

            // 3. Previous Visits Section
            item {
                SummarySectionCard(
                    title = "3. Previous Clinical Visits",
                    icon = Icons.Default.Assignment,
                    badgeCount = summary.previousVisits.size,
                    emptyMessage = "No previous visits documented."
                ) {
                    summary.previousVisits.forEach { visit ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = visit.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${visit.facilityOrDoctor} · ${visit.recordType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = visit.recordDate ?: "Date missing",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (visit.recordDate != null) MaterialTheme.colorScheme.primary else StatusMissingText
                                )
                            }
                        }
                    }
                }
            }

            // 4. Reported Medicines Section
            item {
                SummarySectionCard(
                    title = "4. Reported Medicines",
                    icon = Icons.Default.Medication,
                    badgeCount = summary.reportedMedicines.size,
                    emptyMessage = "No medications documented."
                ) {
                    summary.reportedMedicines.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            userRole = userRole,
                            onToggleVerify = { onToggleVerify(item, null) }
                        )
                    }
                }
            }

            // 5. Important Reports & Lab Findings Section
            item {
                SummarySectionCard(
                    title = "5. Important Reports & Lab Findings",
                    icon = Icons.Default.Science,
                    badgeCount = summary.importantReports.size,
                    emptyMessage = "No lab reports on file."
                ) {
                    summary.importantReports.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            userRole = userRole,
                            onToggleVerify = { onToggleVerify(item, null) }
                        )
                    }
                }
            }

            // 6. Missing / Unclear Information Section
            item {
                SummarySectionCard(
                    title = "6. Missing / Unclear Information",
                    icon = Icons.Default.HelpOutline,
                    badgeCount = summary.missingOrUnclearItems.size,
                    emptyMessage = "No unclear items flagged.",
                    isAmber = summary.missingOrUnclearItems.isNotEmpty()
                ) {
                    summary.missingOrUnclearItems.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            userRole = userRole,
                            onToggleVerify = { onToggleVerify(item, null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummarySectionCard(
    title: String,
    icon: ImageVector,
    badgeCount: Int,
    emptyMessage: String,
    isAmber: Boolean = false,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isAmber) Color(0xFFFEF3C7).copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isAmber) StatusUnclearText else TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = if (isAmber) Color(0xFFFCD34D) else TealContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAmber) StatusUnclearText else TealOnContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            if (badgeCount == 0) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            } else {
                content()
            }
        }
    }
}

@Composable
private fun SummaryItemRow(
    item: ExtractedItemEntity,
    userRole: UserRole,
    onToggleVerify: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!item.dose.isNullOrBlank() || !item.frequency.isNullOrBlank()) {
                        Text(
                            text = "Dose: ${item.dose ?: "Unclear"} · Freq: ${item.frequency ?: "Unclear"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!item.testResult.isNullOrBlank()) {
                        Text(
                            text = "Finding: ${item.testResult}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = item.status)
                    Spacer(modifier = Modifier.width(6.dp))
                    DoctorVerificationBadge(
                        isVerified = item.verifiedByDoctor,
                        onClick = if (userRole == UserRole.DOCTOR) onToggleVerify else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"${item.sourceSnippet}\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
