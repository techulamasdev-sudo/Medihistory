package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PatientEntity
import com.example.ui.UserRole
import com.example.ui.theme.StatusMissingText
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary

@Composable
fun PatientsScreen(
    patients: List<PatientEntity>,
    selectedPatientId: Long,
    userRole: UserRole,
    onSelectPatient: (Long) -> Unit,
    onSavePatient: (id: Long, name: String, age: Int, blood: String, allergies: String, conditions: String, emergency: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPatient by remember { mutableStateOf<PatientEntity?>(null) }

    Scaffold(
        modifier = modifier.testTag("patients_screen"),
        floatingActionButton = {
            if (userRole == UserRole.PARENT) {
                FloatingActionButton(
                    onClick = {
                        editingPatient = null
                        showDialog = true
                    },
                    containerColor = TealPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_patient")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Patient")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MandatorySafetyBanner()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Patient Profiles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (userRole == UserRole.PARENT) "Select or edit child medical profile" else "Doctor read-only clinical profile view",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(patients) { patient ->
                val isSelected = patient.id == selectedPatientId
                val hasMissingAllergy = patient.allergies.isBlank()

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectPatient(patient.id) }
                        .testTag("patient_profile_card_${patient.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isSelected) TealContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(if (isSelected) 3.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = patient.name,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = patient.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Age: ${patient.age} years · Blood Group: ${patient.bloodGroup}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isSelected) {
                                Surface(
                                    color = TealPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Allergies Section
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (hasMissingAllergy) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (hasMissingAllergy) Color(0xFFFCA5A5) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (hasMissingAllergy) Icons.Default.Warning else Icons.Default.LocalHospital,
                                    contentDescription = "Allergy status",
                                    tint = if (hasMissingAllergy) StatusMissingText else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "DOCUMENTED ALLERGIES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasMissingAllergy) StatusMissingText else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                    Text(
                                        text = if (hasMissingAllergy) "NOT RECORDED [Alert: Missing Status]" else patient.allergies,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (hasMissingAllergy) StatusMissingText else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (hasMissingAllergy) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Conditions
                        if (patient.existingConditions.isNotBlank()) {
                            Text(
                                text = "Existing Conditions: ${patient.existingConditions}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Emergency Contact
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Emergency Contact",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Emergency: ${patient.emergencyContact}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Parent edit action button
                        if (userRole == UserRole.PARENT) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        editingPatient = patient
                                        showDialog = true
                                    },
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("btn_edit_patient_${patient.id}"),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit profile",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (hasMissingAllergy) "Fix Missing Allergy" else "Edit Profile",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Patient Dialog
    if (showDialog) {
        var name by remember { mutableStateOf(editingPatient?.name ?: "") }
        var ageText by remember { mutableStateOf(editingPatient?.age?.toString() ?: "5") }
        var bloodGroup by remember { mutableStateOf(editingPatient?.bloodGroup ?: "O+") }
        var allergies by remember { mutableStateOf(editingPatient?.allergies ?: "") }
        var conditions by remember { mutableStateOf(editingPatient?.existingConditions ?: "") }
        var emergency by remember { mutableStateOf(editingPatient?.emergencyContact ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (editingPatient == null) "New Patient Profile" else "Edit Patient Profile")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_patient_name")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("Age (years)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_patient_age")
                        )
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            label = { Text("Blood Group") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_patient_blood")
                        )
                    }
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergies (leave blank to test alert)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_patient_allergies"),
                        supportingText = { Text("e.g. 'Amoxicillin (rash)', 'Peanut'") }
                    )
                    OutlinedTextField(
                        value = conditions,
                        onValueChange = { conditions = it },
                        label = { Text("Existing Conditions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_patient_conditions")
                    )
                    OutlinedTextField(
                        value = emergency,
                        onValueChange = { emergency = it },
                        label = { Text("Emergency Contact") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_patient_emergency")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val age = ageText.toIntOrNull() ?: 5
                        onSavePatient(
                            editingPatient?.id ?: 0L,
                            name.ifBlank { "New Patient" },
                            age,
                            bloodGroup.ifBlank { "O+" },
                            allergies.trim(),
                            conditions.trim(),
                            emergency.trim()
                        )
                        showDialog = false
                    },
                    modifier = Modifier.testTag("btn_save_patient_confirm")
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
