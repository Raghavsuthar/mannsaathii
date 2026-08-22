package com.example.ui.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CaregiverTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverMainScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val selectedTab = uiState.caregiverTab

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.DASHBOARD,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Overview") },
                    modifier = Modifier.testTag("nav_caregiver_dashboard")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.ROUTINES,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.ROUTINES) },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = "Routines") },
                    label = { Text("Routines") },
                    modifier = Modifier.testTag("nav_caregiver_routines")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.MEDICATIONS,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.MEDICATIONS) },
                    icon = { Icon(Icons.Default.Medication, contentDescription = "Meds") },
                    label = { Text("Meds") },
                    modifier = Modifier.testTag("nav_caregiver_meds")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.VOICE_STUDIO,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.VOICE_STUDIO) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = "Voice") },
                    label = { Text("Voice") },
                    modifier = Modifier.testTag("nav_caregiver_voice")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.NOTES,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.NOTES) },
                    icon = { Icon(Icons.Default.NoteAlt, contentDescription = "Notes") },
                    label = { Text("Notes") },
                    modifier = Modifier.testTag("nav_caregiver_notes")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.SETTINGS,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("nav_caregiver_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                CaregiverTab.DASHBOARD -> CaregiverOverviewTab(uiState, viewModel)
                CaregiverTab.ROUTINES -> CaregiverRoutinesTab(uiState, viewModel)
                CaregiverTab.MEDICATIONS -> CaregiverMedsTab(uiState, viewModel)
                CaregiverTab.APPOINTMENTS -> CaregiverAppointmentsTab(uiState, viewModel)
                CaregiverTab.FAMILY -> CaregiverFamilyTab(uiState, viewModel)
                CaregiverTab.MEMORIES -> CaregiverMemoriesTab(uiState, viewModel)
                CaregiverTab.VOICE_STUDIO -> CaregiverVoiceStudioTab(uiState, viewModel)
                CaregiverTab.NOTES -> CaregiverNotesTab(uiState, viewModel)
                CaregiverTab.SETTINGS -> CaregiverSettingsTab(uiState, viewModel)
            }
        }
    }
}

@Composable
fun CaregiverOverviewTab(uiState: UiState, viewModel: MainViewModel) {
    val lang = uiState.profile.language
    val completedRoutines = uiState.routines.count { it.isCompleted }
    val totalRoutines = uiState.routines.size
    val routinePct = if (totalRoutines > 0) (completedRoutines * 100) / totalRoutines else 0

    val takenMeds = uiState.medications.count { it.status == MedicationStatus.TAKEN }
    val totalMeds = uiState.medications.size
    val medPct = if (totalMeds > 0) (takenMeds * 100) / totalMeds else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Patient Header Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "👵 ${uiState.profile.name} (${uiState.profile.preferredName})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "Age: ${uiState.profile.age} • Stage: ${uiState.profile.stage} • ${uiState.profile.city}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.requestSwitchRole(UserRole.PATIENT) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("To Patient UI")
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$routinePct%",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Routine Done",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$medPct%",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            )
                            Text(
                                text = "Meds Taken",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.moodEntries.lastOrNull()?.moodType?.emoji ?: "😊",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Latest Mood",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    }
                }
            }
        }

        // Quick Management Shortcuts
        item {
            Text(
                text = "Care Management Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.FAMILY) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("👨‍👩‍👧 Family (${uiState.familyMembers.size})")
                }
                OutlinedButton(
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.MEMORIES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("❤️ Memories (${uiState.memories.size})")
                }
                OutlinedButton(
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.APPOINTMENTS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📅 Doctors (${uiState.appointments.size})")
                }
            }
        }

        // Recent Mood Stream
        item {
            Text(
                text = "Patient Emotional Logs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(uiState.moodEntries.take(3), key = { it.id }) { mood ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = mood.moodType.emoji, fontSize = 32.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mood.moodType.labelEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(mood.moodType.colorHex)
                            )
                        )
                        Text(
                            text = "${mood.dateFormatted} • ${mood.timeFormatted} • ${mood.note}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    if (mood.caregiverNotified) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreenContainer
                        ) {
                            Text(
                                text = "Notified",
                                style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverRoutinesTab(uiState: UiState, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var titleEn by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }
    var titleGu by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00 AM") }
    var period by remember { mutableStateOf(PeriodOfDay.MORNING) }
    var icon by remember { mutableStateOf("🌅") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Routine Schedule",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_add_routine")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task")
                }
            }
        }

        items(uiState.routines, key = { it.id }) { item ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = item.iconEmoji, fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.time} • ${item.period}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${item.titleEn} / ${item.titleGu}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { viewModel.markRoutineDone(item) }
                    )
                    IconButton(onClick = { viewModel.deleteRoutine(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Routine Activity") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titleEn, onValueChange = { titleEn = it }, label = { Text("Title (English)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = titleGu, onValueChange = { titleGu = it }, label = { Text("Title (Gujarati)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = titleHi, onValueChange = { titleHi = it }, label = { Text("Title (Hindi)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 09:00 AM)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = icon, onValueChange = { icon = it }, label = { Text("Emoji Icon (e.g. ☕, 🚶)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleEn.isNotBlank()) {
                            val newRoutine = RoutineItem(
                                titleEn = titleEn,
                                titleHi = if (titleHi.isBlank()) titleEn else titleHi,
                                titleGu = if (titleGu.isBlank()) titleEn else titleGu,
                                time = time,
                                period = period.name,
                                iconEmoji = icon,
                                caregiverVoiceTextEn = "It is time for $titleEn.",
                                caregiverVoiceTextGu = "હવે $titleGu કરવાનો સમય થયો છે.",
                                caregiverVoiceTextHi = "अब $titleHi करने का समय है।"
                            )
                            viewModel.saveRoutine(newRoutine)
                            showAddDialog = false
                            titleEn = ""
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CaregiverMedsTab(uiState: UiState, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 Tablet") }
    var time by remember { mutableStateOf("08:30 AM") }
    var instGu by remember { mutableStateOf("પાણી સાથે લો") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Medication Manager",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_add_med")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Med")
                }
            }
        }

        items(uiState.medications, key = { it.id }) { med ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = med.iconEmoji, fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${med.time} • ${med.name} (${med.dosage})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Status: ${med.status.name} • ${med.instructionsGu}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    IconButton(onClick = { viewModel.deleteMedication(med) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Medication") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine Name (e.g. Donepezil 5mg)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage (e.g. 1 Tablet)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 08:30 AM)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = instGu, onValueChange = { instGu = it }, label = { Text("Instructions (Gujarati/Hindi)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newMed = Medication(
                                name = name,
                                dosage = dosage,
                                time = time,
                                instructionsGu = instGu,
                                instructionsHi = instGu,
                                instructionsEn = "Take with water after food",
                                caregiverVoicePromptGu = "બા, આ $name લેવાનો સમય થઈ ગયો છે."
                            )
                            viewModel.saveMedication(newMed)
                            showAddDialog = false
                            name = ""
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CaregiverAppointmentsTab(uiState: UiState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Clinical & Doctor Appointments",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(uiState.appointments, key = { it.id }) { appt ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🩺 ${appt.doctorName}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        IconButton(onClick = { viewModel.deleteAppointment(appt) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                        }
                    }
                    Text(
                        text = "📅 ${appt.date} @ ${appt.time} • ${appt.hospitalClinic}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Notes: ${appt.notes}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
fun CaregiverFamilyTab(uiState: UiState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Registered Family & Loved Ones",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(uiState.familyMembers, key = { it.id }) { member ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = member.iconEmoji, fontSize = 32.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${member.name} (${member.relationshipGu})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Phone: ${member.phone} • Voice Intro: ${member.introGu}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    IconButton(onClick = { viewModel.deleteFamilyMember(member) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverMemoriesTab(uiState: UiState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Cherished Memory Book Items",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(uiState.memories, key = { it.id }) { memory ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${memory.iconEmoji} ${memory.titleGu}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { viewModel.deleteMemory(memory) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                        }
                    }
                    Text(
                        text = "${memory.yearOrEra} • ${memory.location}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = memory.descriptionGu,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
fun CaregiverVoiceStudioTab(uiState: UiState, viewModel: MainViewModel) {
    val lang = uiState.profile.language

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎙️ Caregiver Voice Reminders",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = LocaleHelper.get("voice_studio_desc", lang),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        item {
            Text(
                text = "Test Spoken Prompts in App",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        val testVoices = listOf(
            "બા, સવારની ચા અને નાસ્તાનો સમય થયો છે." to "Morning Tea Prompt",
            "બા, આ બ્લડ પ્રેશરની દવા પાણી સાથે લઈ લો." to "Medication Prompt",
            "બા, આજે સુંદર સાંજ છે, ચાલો તુલસી ક્યારે પાણી પાઈએ." to "Evening Routine Prompt",
            "હું તમારી દીકરી મીના છું, હું તમારી સાથે જ છું." to "Orientation & Reassurance"
        )

        items(testVoices.size) { idx ->
            val (msg, title) = testVoices[idx]
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🗣️", fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "\"$msg\"",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.speakText(msg) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverNotesTab(uiState: UiState, viewModel: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteCategory by remember { mutableStateOf("BEHAVIOR") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Clinical Observations & Notes",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_add_note")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Note")
                }
            }
        }

        items(uiState.caregiverNotes, key = { it.id }) { note ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = note.category,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Recorded on: ${note.dateFormatted}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Clinical Note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, label = { Text("Note Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = noteContent, onValueChange = { noteContent = it }, label = { Text("Content / Observations") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    OutlinedTextField(value = noteCategory, onValueChange = { noteCategory = it }, label = { Text("Category (SLEEP, MOOD, BEHAVIOR)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            viewModel.addCaregiverNote(noteTitle, noteContent, noteCategory)
                            showAddDialog = false
                            noteTitle = ""
                            noteContent = ""
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CaregiverSettingsTab(uiState: UiState, viewModel: MainViewModel) {
    val lang = uiState.profile.language

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Caregiver & App Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Switch to Clinician Mode
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Doctor / Clinician Review Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Open clinical adherence summary for psychiatrist/neurologist",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Button(
                        onClick = { viewModel.requestSwitchRole(UserRole.CLINICIAN) }
                    ) {
                        Text("Open")
                    }
                }
            }
        }

        // Accessibility Toggles
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Display & Accessibility",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Large Text Mode")
                        Switch(
                            checked = uiState.profile.largeTextMode,
                            onCheckedChange = { viewModel.toggleLargeText() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("High Contrast Mode")
                        Switch(
                            checked = uiState.profile.highContrastMode,
                            onCheckedChange = { viewModel.toggleHighContrast() }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Picture / Low Literacy Mode")
                        Switch(
                            checked = uiState.profile.pictureMode,
                            onCheckedChange = { viewModel.togglePictureMode() }
                        )
                    }
                }
            }
        }

        // Reset Demo Data Button
        item {
            Button(
                onClick = { viewModel.resetToDemoData() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_reset_demo")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reload Demo Data (Kamla Ben & Meena)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
