package com.example.ui.caregiver

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.model.Memory
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper
import com.example.util.PhotoStorageHelper
import kotlinx.coroutines.launch

@Composable
fun CaregiverMemoriesTab(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = uiState.profile.language
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showWriteMemoryDialog by remember { mutableStateOf(false) }
    var editingMemory by remember { mutableStateOf<Memory?>(null) }
    var memoryToDelete by remember { mutableStateOf<Memory?>(null) }
    var memoryForPhotoUpdate by remember { mutableStateOf<Memory?>(null) }
    var showPhotoSourceSheet by remember { mutableStateOf(false) }

    // Dialog form fields
    var titleEn by remember { mutableStateOf("") }
    var titleGu by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }
    var yearOrEra by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var descriptionEn by remember { mutableStateOf("") }
    var descriptionGu by remember { mutableStateOf("") }
    var descriptionHi by remember { mutableStateOf("") }
    var promptEn by remember { mutableStateOf("") }
    var promptGu by remember { mutableStateOf("") }
    var promptHi by remember { mutableStateOf("") }
    var iconEmoji by remember { mutableStateOf("🌸") }
    var photoColorHex by remember { mutableStateOf(0xFF9A5B00) }
    var photoUri by remember { mutableStateOf<String?>(null) }

    // Dialog Gallery Picker
    val dialogGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val savedPath = PhotoStorageHelper.saveImageFromUri(context, it, "memory")
            if (savedPath != null) {
                photoUri = savedPath
            }
        }
    }

    // Dialog Camera Picker
    val dialogCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val savedPath = PhotoStorageHelper.saveBitmap(context, it, "memory")
            if (savedPath != null) {
                photoUri = savedPath
            }
        }
    }

    // Direct Card Gallery Picker
    val directGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = memoryForPhotoUpdate
        if (target != null && uri != null) {
            val savedPath = PhotoStorageHelper.saveImageFromUri(context, uri, "memory")
            if (savedPath != null) {
                viewModel.saveMemory(target.copy(photoUri = savedPath))
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Photo attached to ${target.titleEn}")
                }
            }
            memoryForPhotoUpdate = null
        }
    }

    // Direct Card Camera Picker
    val directCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        val target = memoryForPhotoUpdate
        if (target != null && bitmap != null) {
            val savedPath = PhotoStorageHelper.saveBitmap(context, bitmap, "memory")
            if (savedPath != null) {
                viewModel.saveMemory(target.copy(photoUri = savedPath))
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Photo attached to ${target.titleEn}")
                }
            }
            memoryForPhotoUpdate = null
        }
    }

    // Camera needs an explicit runtime grant (TakePicturePreview has none
    // of its own). Queue the intended launch until the grant arrives.
    var pendingCameraLaunch by remember { mutableStateOf<(() -> Unit)?>(null) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pendingCameraLaunch?.invoke()
        pendingCameraLaunch = null
    }
    fun launchWithCameraPermission(action: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            action()
        } else {
            pendingCameraLaunch = action
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun openForNew() {
        editingMemory = null
        titleEn = ""
        titleGu = ""
        titleHi = ""
        yearOrEra = ""
        location = ""
        descriptionEn = ""
        descriptionGu = ""
        descriptionHi = ""
        promptEn = "Do you remember this beautiful day?"
        promptGu = "શું તમને આ સુંદર દિવસ યાદ છે?"
        promptHi = "क्या आपको यह सुंदर दिन याद है?"
        iconEmoji = "🌸"
        photoColorHex = 0xFF9A5B00
        photoUri = null
        showWriteMemoryDialog = true
    }

    fun openForEdit(memory: Memory) {
        editingMemory = memory
        titleEn = memory.titleEn
        titleGu = memory.titleGu
        titleHi = memory.titleHi
        yearOrEra = memory.yearOrEra
        location = memory.location
        descriptionEn = memory.descriptionEn
        descriptionGu = memory.descriptionGu
        descriptionHi = memory.descriptionHi
        promptEn = memory.promptEn
        promptGu = memory.promptGu
        promptHi = memory.promptHi
        iconEmoji = memory.iconEmoji
        photoColorHex = memory.photoColorHex
        photoUri = memory.photoUri
        showWriteMemoryDialog = true
    }

    val fallbackColors = listOf(
        0xFF9A5B00 to "Warm Amber",
        0xFF00695C to "Deep Teal",
        0xFF3F6359 to "Forest Olive",
        0xFF0D47A1 to "Royal Indigo",
        0xFF8E24AA to "Muted Purple",
        0xFFB3261E to "Terracotta",
        0xFF5D4037 to "Warm Walnut"
    )

    val eraSuggestions = listOf(
        "Childhood",
        "Youth & College",
        "Wedding Day",
        "1970s",
        "1980s",
        "1990s",
        "2000s",
        "Family Pilgrimage",
        "Family Gathering",
        "Recent Years"
    )

    val emojis = listOf("🌸", "🛕", "🏡", "🚂", "🏞️", "🎓", "💍", "👶", "🎂", "🌅", "🍵", "🌺")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bento Card with Write Down Memory Button
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "🎞️ Cherished Memories Album",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Curate heartwarming life stories, attach family photos, and record recall prompts for Ba.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            )
                        }

                        Button(
                            onClick = { openForNew() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_write_memory")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Memory")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✍️ " + LocaleHelper.get("write_down_memory", lang),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Empty state if no memories
            if (uiState.memories.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📖", fontSize = 48.sp)
                            Text(
                                text = "No Memories in Album Yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tap 'Write Down Memory' above to add wedding photos, childhood anecdotes, or family trips that comfort Ba.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Memories List
            items(uiState.memories, key = { it.id }) { memory ->
                val cardLangTitle = when (lang) {
                    "hi" -> memory.titleHi.ifBlank { memory.titleEn }
                    "gu" -> memory.titleGu.ifBlank { memory.titleEn }
                    else -> memory.titleEn
                }
                val cardLangStory = when (lang) {
                    "hi" -> memory.descriptionHi.ifBlank { memory.descriptionEn }
                    "gu" -> memory.descriptionGu.ifBlank { memory.descriptionEn }
                    else -> memory.descriptionEn
                }
                val cardLangPrompt = when (lang) {
                    "hi" -> memory.promptHi.ifBlank { memory.promptEn }
                    "gu" -> memory.promptGu.ifBlank { memory.promptEn }
                    else -> memory.promptEn
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Top Media Section: Photo or Fallback Color Banner
                        if (!memory.photoUri.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    .clickable {
                                        memoryForPhotoUpdate = memory
                                        showPhotoSourceSheet = true
                                    }
                            ) {
                                AsyncImage(
                                    model = memory.photoUri,
                                    contentDescription = memory.titleEn,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Location & Era Badge
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "📍 ${memory.location} • 📅 ${memory.yearOrEra}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                // Tap to change hint
                                Surface(
                                    color = Color.Black.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = "Change Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                                        Text("Change", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    .background(Color(memory.photoColorHex))
                                    .clickable {
                                        memoryForPhotoUpdate = memory
                                        showPhotoSourceSheet = true
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(memory.iconEmoji, fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📍 ${memory.location} • 📅 ${memory.yearOrEra}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = "Attach Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                                        Text("+ Add Photo", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }

                        // Content Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${memory.iconEmoji} $cardLangTitle",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                            )
                            if (cardLangTitle != memory.titleEn) {
                                Text(
                                    text = memory.titleEn,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            Text(
                                text = cardLangStory,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                            )

                            if (cardLangPrompt.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("💭", fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = "Gentle Recall Prompt for Ba:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            )
                                            Text(
                                                text = "\"$cardLangPrompt\"",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val testNarration = "$cardLangTitle. $cardLangStory. $cardLangPrompt"
                                        viewModel.speakText(testNarration)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen", style = MaterialTheme.typography.labelMedium)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            memoryForPhotoUpdate = memory
                                            showPhotoSourceSheet = true
                                        }
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Change Photo", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { openForEdit(memory) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Memory", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { memoryToDelete = memory }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = AlertRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Direct Photo Source Picker (Gallery vs Camera)
    if (showPhotoSourceSheet && memoryForPhotoUpdate != null) {
        AlertDialog(
            onDismissRequest = {
                showPhotoSourceSheet = false
                memoryForPhotoUpdate = null
            },
            title = { Text("Attach Photo to Memory") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select source for family memory photo:", style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = {
                            showPhotoSourceSheet = false
                            directGalleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose from Gallery")
                    }
                    OutlinedButton(
                        onClick = {
                            showPhotoSourceSheet = false
                            launchWithCameraPermission { directCameraLauncher.launch(null) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take New Photo")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showPhotoSourceSheet = false
                    memoryForPhotoUpdate = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (memoryToDelete != null) {
        val target = memoryToDelete!!
        AlertDialog(
            onDismissRequest = { memoryToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = AlertRed) },
            title = { Text(LocaleHelper.get("delete_confirm_title", lang)) },
            text = {
                Text(LocaleHelper.get("delete_confirm_desc", lang) + "\n\n\"${target.titleEn}\"")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMemory(target)
                        memoryToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(LocaleHelper.get("memory_deleted", lang))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = Color.White)
                ) {
                    Text(LocaleHelper.get("delete_confirm_btn", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { memoryToDelete = null }) {
                    Text(LocaleHelper.get("cancel", lang))
                }
            }
        )
    }

    // Write Down / Edit Memory Dialog
    if (showWriteMemoryDialog) {
        AlertDialog(
            onDismissRequest = { showWriteMemoryDialog = false },
            title = {
                Text(
                    text = if (editingMemory == null) "✍️ Write Down Cherished Memory" else "✏️ Edit Cherished Memory",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                ) {
                    // Photo Attachment Area
                    item {
                        Text("Attach Photo from Album or Camera:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!photoUri.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                            ) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Memory Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        dialogGalleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change Photo", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(
                                    onClick = { photoUri = null },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = AlertRed)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", style = MaterialTheme.typography.labelSmall, color = AlertRed)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        dialogGalleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gallery", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { launchWithCameraPermission { dialogCameraLauncher.launch(null) } },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Take Photo", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // Fallback Color Picker (when no photo is provided)
                    item {
                        Text("Fallback Color Tone (when no photo is attached):", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(fallbackColors) { (colorHex, _) ->
                                val isSelected = photoColorHex == colorHex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorHex))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .clickable { photoColorHex = colorHex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Nostalgic Emoji Picker
                    item {
                        Text("Select Memory Emoji:", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(emojis) { em ->
                                val isSelected = iconEmoji == em
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
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

                    // Titles
                    item {
                        OutlinedTextField(
                            value = titleEn,
                            onValueChange = { titleEn = it },
                            label = { Text("Memory Title (English) *") },
                            placeholder = { Text("e.g. Meena's Wedding in Ahmedabad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = titleGu,
                            onValueChange = { titleGu = it },
                            label = { Text("યાદનું શીર્ષક (Gujarati)") },
                            placeholder = { Text("દા.ત. મીનાના લગ્નનો આનંદમય દિવસ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = titleHi,
                            onValueChange = { titleHi = it },
                            label = { Text("याद का शीर्षक (Hindi, Optional)") },
                            placeholder = { Text("उदा. मीना की शादी का सुंदर दिन") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Year / Era with quick suggestions
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = yearOrEra,
                                onValueChange = { yearOrEra = it },
                                label = { Text("Year or Life Era (e.g. 1985, Wedding Day)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(eraSuggestions) { era ->
                                    SuggestionChip(
                                        onClick = { yearOrEra = era },
                                        label = { Text(era, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }

                    // Location
                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location (e.g. Somnath Temple, Siddhpur House)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Descriptions / Stories
                    item {
                        OutlinedTextField(
                            value = descriptionEn,
                            onValueChange = { descriptionEn = it },
                            label = { Text("Heartwarming Story (English) *") },
                            placeholder = { Text("Describe the day, what Ba wore, who was there, and the joy felt...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = descriptionGu,
                            onValueChange = { descriptionGu = it },
                            label = { Text("વાર્તા / સ્મૃતિ વર્ણન (Gujarati)") },
                            placeholder = { Text("તે દિવસે શું થયું હતું, બાએ કયા કપડાં પહેર્યા હતા...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = descriptionHi,
                            onValueChange = { descriptionHi = it },
                            label = { Text("कहानी / संस्मरण (Hindi, Optional)") },
                            placeholder = { Text("उस दिन क्या हुआ था, बा ने क्या पहना था...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    // Recall Prompts
                    item {
                        OutlinedTextField(
                            value = promptEn,
                            onValueChange = { promptEn = it },
                            label = { Text("Gentle Recall Prompt for Patient (English)") },
                            placeholder = { Text("e.g. Do you remember the red bandhani saree you wore?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = promptGu,
                            onValueChange = { promptGu = it },
                            label = { Text("યાદ કરવાનો સંકેત (Gujarati)") },
                            placeholder = { Text("દા.ત. શું તમને યાદ છે તમે પહેરેલી લાલ બાંધણી સાડી?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Narration Preview Button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    val testVoice = listOf(titleEn, descriptionEn, promptEn)
                                        .filter { it.isNotBlank() }
                                        .joinToString(". ")
                                    if (testVoice.isNotBlank()) {
                                        viewModel.speakText(testVoice)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Test Narration Voice", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Preview Narration Voice")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleEn.isNotBlank() || titleGu.isNotBlank()) {
                            val finalTitleEn = titleEn.ifBlank { titleGu }
                            val finalTitleGu = titleGu.ifBlank { titleEn }
                            val finalTitleHi = titleHi.ifBlank { finalTitleEn }

                            val finalDescEn = descriptionEn.ifBlank { "A joyful family memory shared with loved ones." }
                            val finalDescGu = descriptionGu.ifBlank { finalDescEn }
                            val finalDescHi = descriptionHi.ifBlank { finalDescEn }

                            val finalPromptEn = promptEn.ifBlank { "Do you remember this beautiful day?" }
                            val finalPromptGu = promptGu.ifBlank { finalPromptEn }
                            val finalPromptHi = promptHi.ifBlank { finalPromptEn }

                            val memoryToSave = (editingMemory ?: Memory(
                                titleEn = finalTitleEn,
                                titleHi = finalTitleHi,
                                titleGu = finalTitleGu,
                                yearOrEra = yearOrEra.ifBlank { "Past Years" },
                                location = location.ifBlank { "Home" },
                                descriptionEn = finalDescEn,
                                descriptionHi = finalDescHi,
                                descriptionGu = finalDescGu,
                                promptEn = finalPromptEn,
                                promptHi = finalPromptHi,
                                promptGu = finalPromptGu,
                                iconEmoji = iconEmoji,
                                photoColorHex = photoColorHex,
                                photoUri = photoUri
                            )).copy(
                                titleEn = finalTitleEn,
                                titleHi = finalTitleHi,
                                titleGu = finalTitleGu,
                                yearOrEra = yearOrEra.ifBlank { "Past Years" },
                                location = location.ifBlank { "Home" },
                                descriptionEn = finalDescEn,
                                descriptionHi = finalDescHi,
                                descriptionGu = finalDescGu,
                                promptEn = finalPromptEn,
                                promptHi = finalPromptHi,
                                promptGu = finalPromptGu,
                                iconEmoji = iconEmoji,
                                photoColorHex = photoColorHex,
                                photoUri = photoUri
                            )
                            viewModel.saveMemory(memoryToSave)
                            showWriteMemoryDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(LocaleHelper.get("memory_saved", lang))
                            }
                            editingMemory = null
                        }
                    }
                ) {
                    Text("Save Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showWriteMemoryDialog = false
                    editingMemory = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
