package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PinDialog(
    uiState: UiState,
    viewModel: MainViewModel
) {
    if (!uiState.showPinDialog) return

    var pinText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val lang = uiState.profile.language

    AlertDialog(
        onDismissRequest = { viewModel.dismissPinDialog() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = LocaleHelper.get("enter_pin", lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Caregiver & Clinician settings are secured to prevent accidental changes. (Demo PIN: 1234)",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = pinText,
                    onValueChange = {
                        if (it.length <= 4) {
                            pinText = it
                            showError = false
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    placeholder = { Text("••••") },
                    isError = showError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pin_input_field")
                )

                if (showError) {
                    Text(
                        text = LocaleHelper.get("pin_incorrect", lang),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = viewModel.verifyPinAndUnlock(pinText)
                    if (!success) {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("pin_confirm_button")
            ) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.dismissPinDialog() },
                modifier = Modifier.testTag("pin_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LanguageDialog(
    uiState: UiState,
    viewModel: MainViewModel
) {
    if (!uiState.showLanguageDialog) return

    val lang = uiState.profile.language

    AlertDialog(
        onDismissRequest = { viewModel.showLanguageSelector(false) },
        title = {
            Text(
                text = LocaleHelper.get("language_select", lang),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppLanguage.values().forEach { appLang ->
                    val isSelected = appLang.code == lang
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setLanguage(appLang.code) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = appLang.flag, fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = appLang.nativeName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = appLang.displayName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.showLanguageSelector(false) }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun MedicalDisclaimerDialog(
    uiState: UiState,
    viewModel: MainViewModel
) {
    if (!uiState.showDisclaimerDialog) return

    val lang = uiState.profile.language
    val title = LocaleHelper.get("medical_disclaimer_title", lang)
    val body = LocaleHelper.get("medical_disclaimer", lang)

    AlertDialog(
        onDismissRequest = { viewModel.showDisclaimer(false) },
        icon = {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )
                HorizontalDivider()
                Text(
                    text = "Developer & Clinical Purpose: Created in India to support elderly patients living with Mild Cognitive Impairment, Alzheimer's, or Vascular Dementia through voice, visual routines, orientation cues, and caregiver connection.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.showDisclaimer(false) },
                modifier = Modifier.testTag("disclaimer_accept_button")
            ) {
                Text("Understood")
            }
        }
    )
}

@Composable
fun SafeAiAssistantDialog(
    uiState: UiState,
    viewModel: MainViewModel
) {
    if (!uiState.showAiAssistant) return

    var queryText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    val lang = uiState.profile.language

    val sampleQuestions = when (lang) {
        "hi" -> listOf(
            "आज क्या वार और तारीख है?",
            "मेरी अगली दवाई कब है?",
            "मेरी बेटी कौन है?",
            "आज मेरा डॉक्टर का अपॉइंटमेंट कब है?",
            "मेरा अगला कार्य क्या है?"
        )
        "gu" -> listOf(
            "આજે કયો વાર અને તારીખ છે?",
            "મારી દવા ક્યારે છે?",
            "મારી દીકરી કોણ છે?",
            "ડૉક્ટર પાસે ક્યારે જવાનું છે?",
            "મારું હવે પછીનું કામ શું છે?"
        )
        else -> listOf(
            "What day and date is today?",
            "When is my next medicine?",
            "Who is my daughter?",
            "When is my doctor appointment?",
            "What is my next routine task?"
        )
    }

    Dialog(onDismissRequest = { viewModel.showAiAssistant(false) }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎤", fontSize = 24.sp)
                        Column {
                            Text(
                                text = LocaleHelper.get("talk_ai", lang),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Safe Memory & Routine Assistant",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.showAiAssistant(false) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Spoken answer card
                if (answerText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MannSaathi:",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                IconButton(
                                    onClick = { viewModel.speakText(answerText) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak Answer",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = answerText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }

                // Quick Question Buttons (elderly-friendly)
                Text(
                    text = "Tap a question to ask aloud:",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sampleQuestions.forEach { q ->
                        OutlinedButton(
                            onClick = {
                                queryText = q
                                val ans = viewModel.askSafeOrientationAssistant(q)
                                answerText = ans
                                viewModel.speakText(ans)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "💬 $q",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Safe guidance footnote
                Text(
                    text = "🔒 Safety Rule: MannSaathi only provides orientation from registered records and never diagnoses or advises medication changes.",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

/**
 * First-run family setup. Pre-filled with demo identity; the caregiver
 * replaces it with real names/phone/language. Completing exits demo mode
 * (persisted); skipping keeps exploring on sample data.
 */
@Composable
fun FamilySetupDialog(
    uiState: UiState,
    viewModel: MainViewModel
) {
    var name by remember { mutableStateOf(uiState.profile.name) }
    var preferredName by remember { mutableStateOf(uiState.profile.preferredName) }
    var caregiverName by remember { mutableStateOf(uiState.profile.caregiverName) }
    var caregiverPhone by remember { mutableStateOf(uiState.profile.caregiverPhone) }
    var language by remember { mutableStateOf(uiState.profile.language) }

    Dialog(onDismissRequest = { viewModel.dismissSetupDialog() }) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("family_setup_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mannsaathi_logo),
                        contentDescription = "MannSaathi",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                    Text(
                        text = "Welcome to MannSaathi",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = "Add your family's real details, or skip to keep exploring the demo.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Elder's full name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = preferredName,
                    onValueChange = { preferredName = it },
                    label = { Text("Loving nickname (e.g. Ba)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = caregiverName,
                    onValueChange = { caregiverName = it },
                    label = { Text("Caregiver name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = caregiverPhone,
                    onValueChange = { caregiverPhone = it },
                    label = { Text("Caregiver phone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "App language",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppLanguage.values().forEach { option ->
                        FilterChip(
                            selected = language == option.code,
                            onClick = { language = option.code },
                            label = { Text(option.nativeName) },
                            modifier = Modifier.testTag("setup_lang_${option.code}")
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.completeSetup(name, preferredName, caregiverName, caregiverPhone, language)
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("setup_complete_button")
                ) {
                    Text("Save & Start", fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = { viewModel.dismissSetupDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_skip_button")
                ) {
                    Text("Skip — explore demo")
                }
            }
        }
    }
}
