package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.PatientEntity
import com.example.data.model.AlertSeverity
import com.example.data.model.MedicalAlert
import com.example.ui.UserRole
import com.example.ui.theme.StatusDoctorVerifiedBg
import com.example.ui.theme.StatusDoctorVerifiedBorder
import com.example.ui.theme.StatusDoctorVerifiedText
import com.example.ui.theme.StatusDocumentedBg
import com.example.ui.theme.StatusDocumentedBorder
import com.example.ui.theme.StatusDocumentedText
import com.example.ui.theme.StatusMissingBg
import com.example.ui.theme.StatusMissingBorder
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.StatusUnclearBg
import com.example.ui.theme.StatusUnclearBorder
import com.example.ui.theme.StatusUnclearText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDarkPrimary
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

/**
 * Mandatory Non-Negotiable Safety Notice shown on every screen
 */
@Composable
fun MandatorySafetyBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("safety_banner"),
        color = Color(0xFFFEF3C7), // Amber 100
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical Safety Disclaimer",
                tint = Color(0xFFB45309),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Illustrative concept, not a working medical system. Demo uses fictional patient records only.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF92400E),
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * Role Switcher (Parent / Doctor)
 */
@Composable
fun RoleSwitcherBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("role_switcher_bar"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Parent Option
            RoleOptionButton(
                title = "Parent Mode",
                subtitle = "Upload & Edit",
                icon = Icons.Default.Face,
                isSelected = currentRole == UserRole.PARENT,
                onClick = { onRoleSelected(UserRole.PARENT) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Doctor Option
            RoleOptionButton(
                title = "Doctor Mode",
                subtitle = "Review & Verify",
                icon = Icons.Default.MedicalServices,
                isSelected = currentRole == UserRole.DOCTOR,
                onClick = { onRoleSelected(UserRole.DOCTOR) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoleOptionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) TealPrimary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("role_button_${title.lowercase().replace(" ", "_")}"),
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Status Badge: Documented (Green), Unclear (Amber), Missing (Red)
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val upper = status.uppercase()
    val (bg, textColor, border, label, icon) = when {
        upper.contains("DOC") -> Tuple5(
            StatusDocumentedBg,
            StatusDocumentedText,
            StatusDocumentedBorder,
            "Documented",
            Icons.Default.CheckCircle
        )
        upper.contains("UNCLEAR") -> Tuple5(
            StatusUnclearBg,
            StatusUnclearText,
            StatusUnclearBorder,
            "Unclear",
            Icons.Default.HelpOutline
        )
        else -> Tuple5(
            StatusMissingBg,
            StatusMissingText,
            StatusMissingBorder,
            "Missing",
            Icons.Default.Error
        )
    }

    Surface(
        modifier = modifier.testTag("status_badge_${label.lowercase()}"),
        color = bg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Doctor Verification Badge
 */
@Composable
fun DoctorVerificationBadge(
    isVerified: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    if (isVerified) {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .let { if (onClick != null) it.clickable(onClick = onClick) else it }
                .testTag("badge_verified_doctor"),
            color = StatusDoctorVerifiedBg,
            border = BorderStroke(1.dp, StatusDoctorVerifiedBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified by doctor",
                    tint = StatusDoctorVerifiedText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Verified by doctor",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusDoctorVerifiedText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    } else {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .let { if (onClick != null) it.clickable(onClick = onClick) else it }
                .testTag("badge_unverified"),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Needs verification",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Clinical Alert Card (For allergies conflict, missing items, unreadable records)
 */
@Composable
fun ClinicalAlertCard(
    alert: MedicalAlert,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null
) {
    val (bg, textColor, border, icon) = when (alert.severity) {
        AlertSeverity.HIGH -> Tuple4(
            StatusMissingBg,
            StatusMissingText,
            StatusMissingBorder,
            Icons.Default.Warning
        )
        AlertSeverity.MEDIUM -> Tuple4(
            StatusUnclearBg,
            StatusUnclearText,
            StatusUnclearBorder,
            Icons.Default.HelpOutline
        )
        AlertSeverity.INFO -> Tuple4(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            Color(0xFFBAE6FD),
            Icons.Default.Info
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alert_card_${alert.category.lowercase()}"),
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = alert.title,
                tint = textColor,
                modifier = Modifier
                    .size(22.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Suggested Action: ${alert.suggestedAction}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Extracted Item Card with source snippet and verification controls
 */
@Composable
fun ExtractedItemCard(
    item: ExtractedItemEntity,
    userRole: UserRole,
    documentTitle: String?,
    onToggleVerify: (() -> Unit)?,
    onEditItem: (() -> Unit)?,
    onViewSource: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("extracted_item_${item.id}"),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category + Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TealContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.category.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = TealOnContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = item.status)
                    Spacer(modifier = Modifier.width(6.dp))
                    DoctorVerificationBadge(
                        isVerified = item.verifiedByDoctor,
                        onClick = if (userRole == UserRole.DOCTOR && onToggleVerify != null) onToggleVerify else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Details based on category
            if (!item.dose.isNullOrBlank() || !item.frequency.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    item.dose?.let {
                        Text(
                            text = "Dose: $it",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    item.frequency?.let {
                        Text(
                            text = "Freq: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!item.testResult.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Result: ${item.testResult}" + (item.normalRange?.let { " (Normal: $it)" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Source Snippet Quote
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SOURCE SNIPPET",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        if (!documentTitle.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "· $documentTitle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\"${item.sourceSnippet}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            // Doctor notes if present
            if (item.verifiedByDoctor && !item.doctorNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Doctor Note: ${item.doctorNotes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusDoctorVerifiedText,
                    fontWeight = FontWeight.Medium
                )
            }

            // Action Row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onViewSource != null) {
                    OutlinedButton(
                        onClick = onViewSource,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_view_source_${item.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "View original source",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Source", fontSize = 11.sp)
                    }
                }

                if (userRole == UserRole.PARENT && onEditItem != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onEditItem,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_edit_item_${item.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Correct extracted item",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Correct Item", fontSize = 11.sp)
                    }
                }

                if (userRole == UserRole.DOCTOR && onToggleVerify != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onToggleVerify,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_verify_item_${item.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (item.verifiedByDoctor) StatusDoctorVerifiedBg else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (item.verifiedByDoctor) StatusDoctorVerifiedBorder else MaterialTheme.colorScheme.primary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (item.verifiedByDoctor) Icons.Default.CheckCircle else Icons.Default.Verified,
                            contentDescription = "Verify by doctor",
                            tint = if (item.verifiedByDoctor) StatusDoctorVerifiedText else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.verifiedByDoctor) "Verified ✓" else "Verify Item",
                            color = if (item.verifiedByDoctor) StatusDoctorVerifiedText else MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Helpers for multi-tuples
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
