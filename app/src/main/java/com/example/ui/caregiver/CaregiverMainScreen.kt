package com.example.ui.caregiver

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CaregiverTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.example.util.ExportHelper
import com.example.util.LocaleHelper
import com.example.util.PhotoStorageHelper
import java.text.SimpleDateFormat
import java.util.*

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
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Schedule") },
                    label = { Text("Schedule") },
                    modifier = Modifier.testTag("nav_caregiver_routines")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.MEMORIES,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.MEMORIES) },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = "Memories") },
                    label = { Text("Memories") },
                    modifier = Modifier.testTag("nav_caregiver_memories")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.FAMILY,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.FAMILY) },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Photos") },
                    label = { Text("Photos") },
                    modifier = Modifier.testTag("nav_caregiver_family")
                )
                NavigationBarItem(
                    selected = selectedTab == CaregiverTab.MEDICATIONS,
                    onClick = { viewModel.setCaregiverTab(CaregiverTab.MEDICATIONS) },
                    icon = { Icon(Icons.Default.Medication, contentDescription = "Meds") },
                    label = { Text("Meds") },
                    modifier = Modifier.testTag("nav_caregiver_meds")
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
    val context = LocalContext.current
    val lang = uiState.profile.language
    val completedRoutines = uiState.routines.count { it.isCompleted }
    val totalRoutines = uiState.routines.size
    val routinePct = if (totalRoutines > 0) (completedRoutines * 100) / totalRoutines else 0

    val takenMeds = uiState.medications.count { it.status == MedicationStatus.TAKEN }
    val totalMeds = uiState.medications.size
    val medPct = if (totalMeds > 0) (takenMeds * 100) / totalMeds else 0

    val patientPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val savedPath = PhotoStorageHelper.saveImageFromUri(context, it, "patient")
            if (savedPath != null) {
                viewModel.updateProfile(uiState.profile.copy(photoUri = savedPath))
            }
        }
    }

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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Patient Avatar with Camera Quick Action
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable {
                                    patientPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!uiState.profile.photoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = uiState.profile.photoUri,
                                    contentDescription = uiState.profile.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👵", fontSize = 34.sp)
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Add/Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${uiState.profile.name} (${uiState.profile.preferredName})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "Age: ${uiState.profile.age} • ${uiState.profile.stage}",
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

private fun shiftTimeString(timeStr: String, deltaMinutes: Int): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val date = sdf.parse(timeStr.trim()) ?: return timeStr
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.MINUTE, deltaMinutes)
        }
        sdf.format(cal.time).uppercase(Locale.ENGLISH)
    } catch (e: Exception) {
        timeStr
    }
}

@Composable
fun CaregiverRoutinesTab(uiState: UiState, viewModel: MainViewModel) {
    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var editingRoutine by remember { mutableStateOf<RoutineItem?>(null) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    var titleEn by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }
    var titleGu by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00 AM") }
    var period by remember { mutableStateOf(PeriodOfDay.MORNING) }
    var icon by remember { mutableStateOf("🌅") }
    var voiceReminder by remember { mutableStateOf("") }

    fun openForNew() {
        editingRoutine = null
        titleEn = ""
        titleHi = ""
        titleGu = ""
        time = "08:00 AM"
        period = PeriodOfDay.MORNING
        icon = "🌅"
        voiceReminder = ""
        showAddOrEditDialog = true
    }

    fun openForEdit(item: RoutineItem) {
        editingRoutine = item
        titleEn = item.titleEn
        titleHi = item.titleHi
        titleGu = item.titleGu
        time = item.time
        period = try { PeriodOfDay.valueOf(item.period) } catch (e: Exception) { PeriodOfDay.MORNING }
        icon = item.iconEmoji
        voiceReminder = item.caregiverVoiceTextEn
        showAddOrEditDialog = true
    }

    val completedCount = uiState.routines.count { it.isCompleted }
    val totalCount = uiState.routines.size

    val filteredRoutines = when (selectedFilter) {
        "MORNING" -> uiState.routines.filter { it.period.equals("MORNING", ignoreCase = true) }
        "AFTERNOON" -> uiState.routines.filter { it.period.equals("AFTERNOON", ignoreCase = true) }
        "EVENING" -> uiState.routines.filter { it.period.equals("EVENING", ignoreCase = true) }
        "NIGHT" -> uiState.routines.filter { it.period.equals("NIGHT", ignoreCase = true) }
        else -> uiState.routines
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Schedule Planner Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
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
                                text = "Daily Schedule Planner",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "$completedCount of $totalCount completed today",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Button(
                            onClick = { openForNew() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_add_routine")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Activity")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                uiState.routines.forEach {
                                    if (it.isCompleted) {
                                        viewModel.markRoutineDone(it)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Day", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Daily Checkmarks", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Period Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    "ALL" to "All (${uiState.routines.size})",
                    "MORNING" to "🌅 Morning (${uiState.routines.count { it.period.equals("MORNING", true) }})",
                    "AFTERNOON" to "☀️ Afternoon (${uiState.routines.count { it.period.equals("AFTERNOON", true) }})",
                    "EVENING" to "🌆 Evening (${uiState.routines.count { it.period.equals("EVENING", true) }})",
                    "NIGHT" to "🌙 Night (${uiState.routines.count { it.period.equals("NIGHT", true) }})"
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Routine Items List
        items(filteredRoutines, key = { it.id }) { item ->
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = item.iconEmoji, fontSize = 30.sp)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.time,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.period,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${item.titleEn} / ${item.titleGu}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { viewModel.markRoutineDone(item) }
                        )
                    }

                    if (item.caregiverVoiceTextGu.isNotBlank() || item.caregiverVoiceTextEn.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔊 \"${item.caregiverVoiceTextGu.ifBlank { item.caregiverVoiceTextEn }}\"",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Action Controls: Time Shift, Listen, Edit, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Quick time shifting controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val newTime = shiftTimeString(item.time, -15)
                                    viewModel.saveRoutine(item.copy(time = newTime))
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("-15m", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = {
                                    val newTime = shiftTimeString(item.time, 15)
                                    viewModel.saveRoutine(item.copy(time = newTime))
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("+15m", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val voiceText = item.caregiverVoiceTextGu.ifBlank { item.caregiverVoiceTextEn }
                                    viewModel.speakText(voiceText)
                                }
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { openForEdit(item) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteRoutine(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddOrEditDialog) {
        val quickPresets = listOf("07:00 AM", "08:00 AM", "10:00 AM", "12:30 PM", "02:00 PM", "04:30 PM", "07:00 PM", "09:00 PM")
        val iconOptions = listOf("🌅", "🪥", "🍵", "💊", "🚶", "🍛", "😴", "🛕", "🥣", "🌙", "🧩", "📞")

        AlertDialog(
            onDismissRequest = { showAddOrEditDialog = false },
            title = {
                Text(if (editingRoutine == null) "Set Schedule Activity" else "Edit Schedule Activity")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = {
                            titleEn = it
                            if (voiceReminder.isBlank()) {
                                voiceReminder = "It is time for $it."
                            }
                        },
                        label = { Text("Activity Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = titleGu,
                        onValueChange = { titleGu = it },
                        label = { Text("Title (Gujarati / ગુજરાતી)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Scheduled Time Input & Quick Presets
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Scheduled Time (e.g. 08:00 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Quick Time Presets:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickPresets) { preset ->
                            SuggestionChip(
                                onClick = { time = preset },
                                label = { Text(preset, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    // Period of day selector
                    Text("Period of Day:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PeriodOfDay.values().forEach { p ->
                            FilterChip(
                                selected = period == p,
                                onClick = { period = p },
                                label = { Text(p.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Icon / Emoji selector
                    Text("Select Category Icon:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(iconOptions) { ic ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (icon == ic) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (icon == ic) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { icon = ic }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(ic, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    // Spoken Voice Reminder
                    OutlinedTextField(
                        value = voiceReminder,
                        onValueChange = { voiceReminder = it },
                        label = { Text("Spoken Voice Reminder") },
                        placeholder = { Text("e.g. Ba, it is time for morning walk.") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                if (voiceReminder.isNotBlank()) {
                                    viewModel.speakText(voiceReminder)
                                } else if (titleEn.isNotBlank()) {
                                    viewModel.speakText("It is time for $titleEn.")
                                }
                            }
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Test Voice", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Voice")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleEn.isNotBlank()) {
                            val routineToSave = (editingRoutine ?: RoutineItem(
                                titleEn = titleEn,
                                titleHi = if (titleHi.isBlank()) titleEn else titleHi,
                                titleGu = if (titleGu.isBlank()) titleEn else titleGu,
                                time = time,
                                period = period.name,
                                iconEmoji = icon
                            )).copy(
                                titleEn = titleEn,
                                titleHi = if (titleHi.isBlank()) titleEn else titleHi,
                                titleGu = if (titleGu.isBlank()) titleEn else titleGu,
                                time = time,
                                period = period.name,
                                iconEmoji = icon,
                                caregiverVoiceTextEn = voiceReminder.ifBlank { "It is time for $titleEn." },
                                caregiverVoiceTextGu = if (titleGu.isNotBlank()) "હવે $titleGu કરવાનો સમય થયો છે." else voiceReminder,
                                caregiverVoiceTextHi = if (titleHi.isNotBlank()) "अब $titleHi करने का समय है।" else voiceReminder
                            )
                            viewModel.saveRoutine(routineToSave)
                            showAddOrEditDialog = false
                            titleEn = ""
                            editingRoutine = null
                        }
                    }
                ) { Text("Save Schedule") }
            },
            dismissButton = {
                TextButton(onClick = { showAddOrEditDialog = false }) { Text("Cancel") }
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
    val context = LocalContext.current
    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var editingMember by remember { mutableStateOf<FamilyMember?>(null) }

    var name by remember { mutableStateOf("") }
    var relationshipEn by remember { mutableStateOf("Daughter") }
    var relationshipGu by remember { mutableStateOf("દીકરી") }
    var phone by remember { mutableStateOf("") }
    var introGu by remember { mutableStateOf("") }
    var iconEmoji by remember { mutableStateOf("👩") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    // Dialog photo launcher
    val dialogPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val savedPath = PhotoStorageHelper.saveImageFromUri(context, it, "family")
            if (savedPath != null) {
                photoUri = savedPath
            }
        }
    }

    // Direct card photo picker
    var memberForPhotoUpdate by remember { mutableStateOf<FamilyMember?>(null) }
    val directPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = memberForPhotoUpdate
        if (target != null && uri != null) {
            val savedPath = PhotoStorageHelper.saveImageFromUri(context, uri, "family")
            if (savedPath != null) {
                viewModel.saveFamilyMember(target.copy(photoUri = savedPath))
            }
            memberForPhotoUpdate = null
        }
    }

    fun openForNew() {
        editingMember = null
        name = ""
        relationshipEn = "Daughter"
        relationshipGu = "દીકરી"
        phone = ""
        introGu = ""
        iconEmoji = "👩"
        photoUri = null
        showAddOrEditDialog = true
    }

    fun openForEdit(member: FamilyMember) {
        editingMember = member
        name = member.name
        relationshipEn = member.relationshipEn
        relationshipGu = member.relationshipGu
        phone = member.phone
        introGu = member.introGu
        iconEmoji = member.iconEmoji
        photoUri = member.photoUri
        showAddOrEditDialog = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Family & Loved Ones",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Upload real photos so Ba instantly recognizes faces and hears warm voice introductions.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { openForNew() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_add_family")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Loved One")
                    }
                }
            }
        }

        items(uiState.familyMembers, key = { it.id }) { member ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Photo or Emoji Avatar with Quick Photo Update
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable {
                                    memberForPhotoUpdate = member
                                    directPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!member.photoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = member.photoUri,
                                    contentDescription = member.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = member.iconEmoji, fontSize = 26.sp)
                                    Text("Add Photo", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${member.name} (${member.relationshipGu})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Relationship: ${member.relationshipEn} • Phone: ${member.phone}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                            if (member.introGu.isNotBlank()) {
                                Text(
                                    text = "Voice: \"${member.introGu}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Actions row: Photo, Test Voice, Edit, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                memberForPhotoUpdate = member
                                directPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Add/Change Photo", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (member.photoUri.isNullOrBlank()) "Add Photo" else "Change Photo", style = MaterialTheme.typography.labelSmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val intro = member.introGu.ifBlank { "This is ${member.name}, your ${member.relationshipEn}." }
                                    viewModel.speakText(intro)
                                }
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen Intro", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { openForEdit(member) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteFamilyMember(member) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddOrEditDialog) {
        val emojis = listOf("👩", "👨", "👧", "👦", "👵", "👴", "👶", "🤝")

        AlertDialog(
            onDismissRequest = { showAddOrEditDialog = false },
            title = {
                Text(if (editingMember == null) "Add Family Member" else "Edit Family Member")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Photo Attachment Preview & Button
                    Text("Photo of Loved One:", style = MaterialTheme.typography.labelMedium)
                    if (!photoUri.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Selected Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        dialogPhotoLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Change Photo", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(
                                    onClick = { photoUri = null },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Remove Photo", style = MaterialTheme.typography.labelSmall, color = AlertRed)
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                dialogPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Choose Photo")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose Photo from Gallery")
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name (e.g. Meena)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = relationshipEn,
                        onValueChange = { relationshipEn = it },
                        label = { Text("Relationship (English, e.g. Daughter)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = relationshipGu,
                        onValueChange = { relationshipGu = it },
                        label = { Text("Relationship (Gujarati, e.g. દીકરી)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = introGu,
                        onValueChange = { introGu = it },
                        label = { Text("Voice Intro for Patient") },
                        placeholder = { Text("e.g. આ તમારી દીકરી મીના છે જે અમદાવાદમાં રહે છે.") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Emoji selector
                    Text("Avatar Icon (fallback if no photo):", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(emojis) { em ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (iconEmoji == em) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (iconEmoji == em) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { iconEmoji = em }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(em, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val memberToSave = (editingMember ?: FamilyMember(
                                name = name,
                                relationshipEn = relationshipEn,
                                relationshipHi = relationshipEn,
                                relationshipGu = relationshipGu,
                                phone = phone,
                                introEn = "This is your $relationshipEn $name.",
                                introHi = "यह आपकी $relationshipEn $name हैं।",
                                introGu = introGu.ifBlank { "આ તમારા $relationshipGu $name છે." },
                                iconEmoji = iconEmoji,
                                photoUri = photoUri
                            )).copy(
                                name = name,
                                relationshipEn = relationshipEn,
                                relationshipHi = relationshipEn,
                                relationshipGu = relationshipGu,
                                phone = phone,
                                introGu = introGu.ifBlank { "આ તમારા $relationshipGu $name છે." },
                                iconEmoji = iconEmoji,
                                photoUri = photoUri
                            )
                            viewModel.saveFamilyMember(memberToSave)
                            showAddOrEditDialog = false
                            name = ""
                            editingMember = null
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddOrEditDialog = false }) { Text("Cancel") }
            }
        )
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Voice Guidance (TTS)")
                            Text(
                                text = "Spoken prompts for every card and reminder",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Switch(
                            checked = uiState.profile.voiceAssistanceEnabled,
                            onCheckedChange = { viewModel.setVoiceAssistanceEnabled(it) },
                            modifier = Modifier.testTag("switch_voice_guidance")
                        )
                    }
                }
            }
        }

        // Share Care Summary (offline export for family / doctor visits)
        item {
            val shareContext = LocalContext.current
            Button(
                onClick = {
                    val summary = ExportHelper.buildCareSummary(
                        profile = uiState.profile,
                        medications = uiState.medications,
                        routines = uiState.routines,
                        moods = uiState.moodEntries,
                        appointments = uiState.appointments,
                        notes = uiState.caregiverNotes
                    )
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "MannSaathi Care Summary — ${uiState.profile.name}")
                        putExtra(Intent.EXTRA_TEXT, summary)
                    }
                    shareContext.startActivity(Intent.createChooser(sendIntent, "Share care summary"))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_share_summary")
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Care Summary (Doctor / Family)", fontWeight = FontWeight.Bold)
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
