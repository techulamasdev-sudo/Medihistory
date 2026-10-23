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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PatientEntity
import com.example.ui.MediScreen
import com.example.ui.UserRole
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.StatusDoctorVerifiedText
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.StatusUnclearText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

@Composable
fun DemoScreen(
    patients: List<PatientEntity>,
    selectedPatientId: Long,
    currentRole: UserRole,
    onSelectPatient: (Long) -> Unit,
    onNavigate: (MediScreen) -> Unit,
    onResetDemoData: () -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("demo_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = TealContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = "MediHistory AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MediHistory AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TealOnContainer
                            )
                            Text(
                                text = "Every medical detail remembered, every doctor better informed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TealOnContainer.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Parents upload a child's prescriptions, lab reports and discharge summaries. AI organizes them into a chronological timeline and doctor-ready summary for single-screen review.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TealOnContainer.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Mandatory Safety Notice
        item {
            MandatorySafetyBanner()
        }

        // Demo Patient Selection
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Fictional Patient Demo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = onResetDemoData,
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_reset_demo"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Demo",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Demo", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    patients.forEach { patient ->
                        val isSelected = patient.id == selectedPatientId
                        val isLeo = patient.id == 1L

                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectPatient(patient.id) }
                                .testTag("demo_patient_card_${patient.id}"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = patient.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${patient.age}y · ${patient.bloodGroup}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isLeo) "Test Case: Blurry Rx & Amoxicillin" else "Test Case: Missing Allergy & Conflict",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isLeo) StatusUnclearText else StatusMissingText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isLeo) "Shows unclear smudged dose & hematology" else "Shows urgent peanut hives alert & undated lab",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4 Interactive Showcase Cards
        item {
            Text(
                text = "Key Safety & Extraction Scenarios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            DemoFeatureCard(
                title = "1. Blurry / Incomplete Record Detection",
                badgeText = "Amber: Unclear",
                badgeColor = StatusUnclearText,
                icon = Icons.Default.HelpOutline,
                description = "Leo's urgent care note has an illegible ink smudge ('Ceph... 125mg?'). AI marks status as 'Unclear'—never presenting assumptions as confirmed facts.",
                buttonText = "Inspect Record",
                onClick = {
                    onSelectPatient(1L)
                    onNavigate(MediScreen.RECORDS)
                }
            )
        }

        item {
            DemoFeatureCard(
                title = "2. Profile vs Document Conflict Alert",
                badgeText = "Red: Alert",
                badgeColor = StatusMissingText,
                icon = Icons.Default.Warning,
                description = "Maya's profile has no allergies recorded, but an urgent care record reports peanut hives. MediHistory triggers a high-priority conflict alert.",
                buttonText = "View Summary Alerts",
                onClick = {
                    onSelectPatient(2L)
                    onNavigate(MediScreen.SUMMARY)
                }
            )
        }

        item {
            DemoFeatureCard(
                title = "3. Undated Event Handling",
                badgeText = "Date Missing",
                badgeColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.DateRange,
                description = "Maya's allergy IgE lab report omitted the collection date. Rather than guessing, the timeline organizes it into an explicit 'Date missing' group.",
                buttonText = "View Timeline",
                onClick = {
                    onSelectPatient(2L)
                    onNavigate(MediScreen.TIMELINE)
                }
            )
        }

        item {
            DemoFeatureCard(
                title = "4. Doctor Verification & Split View",
                badgeText = "Doctor Mode",
                badgeColor = StatusDoctorVerifiedText,
                icon = Icons.Default.Verified,
                description = "Doctors view the original document text side-by-side with extracted items and can sign off with 'Verified by doctor' timestamps.",
                buttonText = "Switch to Doctor Mode",
                onClick = {
                    onSwitchRole(UserRole.DOCTOR)
                    onNavigate(MediScreen.RECORDS)
                }
            )
        }

        // Quick Navigation Grid
        item {
            Text(
                text = "Explore App Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickNavButton(
                    label = "Patients",
                    icon = Icons.Default.Person,
                    onClick = { onNavigate(MediScreen.PATIENTS) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    label = "Records",
                    icon = Icons.Default.Description,
                    onClick = { onNavigate(MediScreen.RECORDS) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    label = "Timeline",
                    icon = Icons.Default.DateRange,
                    onClick = { onNavigate(MediScreen.TIMELINE) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    label = "AI Summary",
                    icon = Icons.Default.Assignment,
                    onClick = { onNavigate(MediScreen.SUMMARY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DemoFeatureCard(
    title: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Text(buttonText, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Navigate",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickNavButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp)
            )
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
