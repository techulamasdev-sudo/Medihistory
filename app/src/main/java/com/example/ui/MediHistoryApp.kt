package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DemoScreen
import com.example.ui.screens.PatientsScreen
import com.example.ui.screens.RecordsScreen
import com.example.ui.screens.RoleSwitcherBar
import com.example.ui.screens.SummaryScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealOnContainer
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediHistoryApp(
    viewModel: MediHistoryViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedPatientId by viewModel.selectedPatientId.collectAsState()
    val allPatients by viewModel.allPatients.collectAsState()
    val activePatient by viewModel.activePatient.collectAsState()
    val patientRecords by viewModel.patientRecords.collectAsState()
    val patientExtractedItems by viewModel.patientExtractedItems.collectAsState()
    val doctorReadySummary by viewModel.doctorReadySummary.collectAsState()
    val timelineFilter by viewModel.timelineCategoryFilter.collectAsState()
    val selectedRecordForInspection by viewModel.selectedRecordForInspection.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isExtracting by viewModel.isExtracting.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Handle back button for secondary inspection or return to Demo screen
    BackHandler(enabled = selectedRecordForInspection != null || currentScreen != MediScreen.DEMO) {
        if (selectedRecordForInspection != null) {
            viewModel.selectRecordForInspection(null)
        } else if (currentScreen != MediScreen.DEMO) {
            viewModel.setScreen(MediScreen.DEMO)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .testTag("navigation_drawer")
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TealContainer)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = "MediHistory AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MediHistory AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TealOnContainer
                            )
                            Text(
                                text = "Clinical Memory Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = TealOnContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Every medical detail remembered, every doctor better informed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TealOnContainer.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient Quick Selector in Sidebar
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "ACTIVE PATIENT PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    allPatients.forEach { p ->
                        val isSelected = p.id == selectedPatientId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    viewModel.selectPatient(p.id)
                                    scope.launch { drawerState.close() }
                                },
                            color = if (isSelected) TealPrimary.copy(alpha = 0.12f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${p.name} (${p.age}y)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TealPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Items
                val navItems = listOf(
                    NavigationItem(MediScreen.DEMO, "Demo Showcase", Icons.Default.SmartToy),
                    NavigationItem(MediScreen.PATIENTS, "Patients", Icons.Default.People),
                    NavigationItem(MediScreen.RECORDS, "Records", Icons.Default.Description),
                    NavigationItem(MediScreen.TIMELINE, "Timeline", Icons.Default.DateRange),
                    NavigationItem(MediScreen.SUMMARY, "AI Summary", Icons.Default.Assignment)
                )

                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = {
                            viewModel.setScreen(item.screen)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("nav_item_${item.label.lowercase().replace(" ", "_")}"),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Drawer Footer Safety Notice
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SAFETY RULES:\n• AI never diagnoses or recommends doses\n• Unclear items marked needs verification\n• All items link to original document",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.resetDemoData()
                                scope.launch { drawerState.close() }
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Demo Data", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentScreen.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                activePatient?.let { p ->
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = TealContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TealOnContainer,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("btn_open_menu")
                            ) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Role Switcher Bar (Parent Mode / Doctor Mode)
                    RoleSwitcherBar(
                        currentRole = currentRole,
                        onRoleSelected = { viewModel.setRole(it) }
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        NavigationItem(MediScreen.DEMO, "Demo", Icons.Default.SmartToy),
                        NavigationItem(MediScreen.PATIENTS, "Patients", Icons.Default.People),
                        NavigationItem(MediScreen.RECORDS, "Records", Icons.Default.Description),
                        NavigationItem(MediScreen.TIMELINE, "Timeline", Icons.Default.DateRange),
                        NavigationItem(MediScreen.SUMMARY, "Summary", Icons.Default.Assignment)
                    )

                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setScreen(item.screen) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                selectedTextColor = TealPrimary,
                                indicatorColor = TealContainer
                            )
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        MediScreen.DEMO -> DemoScreen(
                            patients = allPatients,
                            selectedPatientId = selectedPatientId,
                            currentRole = currentRole,
                            onSelectPatient = { viewModel.selectPatient(it) },
                            onNavigate = { viewModel.setScreen(it) },
                            onResetDemoData = { viewModel.resetDemoData() },
                            onSwitchRole = { viewModel.setRole(it) }
                        )

                        MediScreen.PATIENTS -> PatientsScreen(
                            patients = allPatients,
                            selectedPatientId = selectedPatientId,
                            userRole = currentRole,
                            onSelectPatient = { viewModel.selectPatient(it) },
                            onSavePatient = { id, name, age, blood, allergies, conditions, emergency ->
                                viewModel.savePatient(id, name, age, blood, allergies, conditions, emergency)
                            }
                        )

                        MediScreen.RECORDS -> RecordsScreen(
                            patient = activePatient,
                            records = patientRecords,
                            extractedItems = patientExtractedItems,
                            userRole = currentRole,
                            selectedRecord = selectedRecordForInspection,
                            isExtracting = isExtracting,
                            onSelectRecord = { viewModel.selectRecordForInspection(it) },
                            onToggleVerify = { item, notes -> viewModel.toggleDoctorVerification(item, notes) },
                            onUpdateExtractedItem = { item, name, dose, freq, dur, status ->
                                viewModel.updateExtractedItem(item, name, dose, freq, dur, status)
                            },
                            onUploadRecord = { patientId, title, type, date, facility, text, isBlurry ->
                                viewModel.uploadRecord(patientId, title, type, date, facility, text, isBlurry)
                            },
                            onDeleteRecord = { viewModel.deleteRecord(it) }
                        )

                        MediScreen.TIMELINE -> TimelineScreen(
                            patient = activePatient,
                            records = patientRecords,
                            extractedItems = patientExtractedItems,
                            userRole = currentRole,
                            categoryFilter = timelineFilter,
                            onCategoryFilterChange = { viewModel.setTimelineFilter(it) },
                            onSelectRecordForInspection = { record ->
                                viewModel.selectRecordForInspection(record)
                                viewModel.setScreen(MediScreen.RECORDS)
                            },
                            onToggleVerify = { item, notes -> viewModel.toggleDoctorVerification(item, notes) }
                        )

                        MediScreen.SUMMARY -> SummaryScreen(
                            summary = doctorReadySummary,
                            userRole = currentRole,
                            onToggleVerify = { item, notes -> viewModel.toggleDoctorVerification(item, notes) },
                            onViewRecord = { recordId ->
                                val rec = patientRecords.find { it.id == recordId }
                                if (rec != null) {
                                    viewModel.selectRecordForInspection(rec)
                                    viewModel.setScreen(MediScreen.RECORDS)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private data class NavigationItem(
    val screen: MediScreen,
    val label: String,
    val icon: ImageVector
)
