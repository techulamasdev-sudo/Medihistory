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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.PatientEntity
import com.example.data.local.RecordEntity
import com.example.ui.UserRole
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.StatusUnclearBg
import com.example.ui.theme.StatusUnclearText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

@Composable
fun TimelineScreen(
    patient: PatientEntity?,
    records: List<RecordEntity>,
    extractedItems: List<ExtractedItemEntity>,
    userRole: UserRole,
    categoryFilter: String,
    onCategoryFilterChange: (String) -> Unit,
    onSelectRecordForInspection: (RecordEntity) -> Unit,
    onToggleVerify: (ExtractedItemEntity, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map items to their records
    val recordMap = records.associateBy { it.id }

    // Filter items by category
    val filteredItems = when (categoryFilter) {
        "MEDICINE" -> extractedItems.filter { it.category == "MEDICINE" }
        "LAB_TEST" -> extractedItems.filter { it.category == "LAB_TEST" }
        "CONDITION" -> extractedItems.filter { it.category == "CONDITION" }
        "ALLERGY" -> extractedItems.filter { it.category == "ALLERGY" }
        else -> extractedItems
    }

    // Partition into dated and undated
    val datedItems = filteredItems.filter { item ->
        val record = recordMap[item.recordId]
        record?.recordDate != null && record.recordDate.isNotBlank()
    }.sortedByDescending { item ->
        recordMap[item.recordId]?.recordDate ?: ""
    }

    val undatedItems = filteredItems.filter { item ->
        val record = recordMap[item.recordId]
        record?.recordDate.isNullOrBlank()
    }

    // Group dated items by date
    val groupedByDate = datedItems.groupBy { item ->
        recordMap[item.recordId]?.recordDate ?: "Date missing"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("timeline_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MandatorySafetyBanner()
        }

        // Header
        item {
            Column {
                Text(
                    text = "Chronological Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Medical events for ${patient?.name ?: "Patient"} sorted by clinical encounter date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Categories",
                    "MEDICINE" to "Medicines",
                    "LAB_TEST" to "Labs & Panels",
                    "CONDITION" to "Conditions",
                    "ALLERGY" to "Allergies"
                )
                items(filters) { (key, label) ->
                    val isSelected = categoryFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChange(key) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Prominent "Date missing" group if any undated items exist
        if (undatedItems.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = StatusUnclearBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Date missing",
                                tint = StatusUnclearText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Date Missing Group (${undatedItems.size} items)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusUnclearText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SAFETY RULE: Undated events are never guessed or placed randomly. They are grouped here until clinic verification.",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusUnclearText.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        undatedItems.forEach { item ->
                            val record = recordMap[item.recordId]
                            ExtractedItemCard(
                                item = item,
                                userRole = userRole,
                                documentTitle = record?.title ?: "Undated Specimen",
                                onToggleVerify = { onToggleVerify(item, null) },
                                onEditItem = null,
                                onViewSource = { record?.let { onSelectRecordForInspection(it) } },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Dated Timeline Groups
        if (groupedByDate.isEmpty() && undatedItems.isEmpty()) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No clinical events matching filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            groupedByDate.forEach { (dateStr, itemsForDate) ->
                item {
                    Column {
                        // Date Node Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = dateStr,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val facility = recordMap[itemsForDate.firstOrNull()?.recordId]?.facilityOrDoctor
                            if (facility != null) {
                                Text(
                                    text = "· $facility",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Items for this date
                        itemsForDate.forEach { item ->
                            val record = recordMap[item.recordId]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Timeline connecting vertical track
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(80.dp)
                                            .background(TealPrimary.copy(alpha = 0.3f))
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                ExtractedItemCard(
                                    item = item,
                                    userRole = userRole,
                                    documentTitle = record?.title,
                                    onToggleVerify = { onToggleVerify(item, null) },
                                    onEditItem = null,
                                    onViewSource = { record?.let { onSelectRecordForInspection(it) } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
