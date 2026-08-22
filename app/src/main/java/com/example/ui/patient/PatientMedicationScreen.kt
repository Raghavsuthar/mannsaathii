package com.example.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientMedicationScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode
    val isPictureMode = uiState.profile.pictureMode

    val pendingCount = uiState.medications.count { it.status == MedicationStatus.PENDING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button & Spoken Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.setPatientScreen(PatientScreen.HOME) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoHeaderBg,
                        contentColor = BentoOnBackground
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                    modifier = Modifier.testTag("med_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "आज की दवाइयों में से $pendingCount दवाइयां बाकी हैं।"
                            "gu" -> "આજની દવાઓમાંથી $pendingCount દવાઓ લેવાની બાકી છે."
                            else -> "You have $pendingCount pending medications today."
                        }
                        viewModel.speakText(speech)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, BentoHeaderBorder, RoundedCornerShape(16.dp))
                ) {
                    Text(text = "🔊", fontSize = 24.sp)
                }
            }
        }

        // Title Header
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BentoMedicineBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoMedicineBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💊 " + LocaleHelper.get("medicines", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoMedicineText,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = if (pendingCount > 0) "$pendingCount ${LocaleHelper.get("status_pending", lang)}" else "✨ All scheduled medicines taken!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoMedicineText.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Medication Cards
        items(uiState.medications, key = { it.id }) { med ->
            val instructions = when (lang) {
                "hi" -> med.instructionsHi
                "gu" -> med.instructionsGu
                else -> med.instructionsEn
            }
            val voicePrompt = when (lang) {
                "hi" -> med.caregiverVoicePromptHi
                "gu" -> med.caregiverVoicePromptGu
                else -> med.caregiverVoicePromptEn
            }

            MedicationCard(
                med = med,
                instructions = instructions,
                voicePrompt = voicePrompt,
                isLargeText = isLargeText,
                isPictureMode = isPictureMode,
                lang = lang,
                onSpeak = {
                    val spoken = if (voicePrompt.isNotBlank()) voicePrompt else "${med.name}, ${med.dosage} at ${med.time}. $instructions"
                    viewModel.speakText(spoken)
                },
                onTaken = { viewModel.markMedicationTaken(med) },
                onLater = { viewModel.markMedicationLater(med) },
                onHelp = {
                    val helpMsg = when (lang) {
                        "hi" -> "दवाई की सहायता के लिए देखभालकर्ता ${uiState.profile.caregiverName} को सूचित कर दिया गया है।"
                        "gu" -> "દવાની મદદ માટે દીકરી ${uiState.profile.caregiverName} ને જણાવવામાં આવ્યું છે."
                        else -> "Caregiver ${uiState.profile.caregiverName} notified for medication assistance."
                    }
                    viewModel.speakText(helpMsg)
                }
            )
        }

        // Medical Non-Prescribing Disclaimer Footnote
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BentoHeaderBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoHeaderBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = BentoGreenAccent
                    )
                    Text(
                        text = LocaleHelper.get("med_disclaimer", lang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoOnBackground.copy(alpha = 0.8f),
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationCard(
    med: Medication,
    instructions: String,
    voicePrompt: String,
    isLargeText: Boolean,
    isPictureMode: Boolean,
    lang: String,
    onSpeak: () -> Unit,
    onTaken: () -> Unit,
    onLater: () -> Unit,
    onHelp: () -> Unit
) {
    val isTaken = med.status == MedicationStatus.TAKEN
    val isDelayed = med.status == MedicationStatus.DELAYED

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = if (isTaken) BentoMoodBg else if (isDelayed) BentoMemoriesBg else Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.5.dp,
                color = if (isTaken) BentoMoodBorder else if (isDelayed) BentoMemoriesBorder else BentoMedicineBorder,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onSpeak() }
            .testTag("med_card_${med.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isPictureMode) 64.dp else 52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isTaken) BentoGreenAccentLight else BentoMedicineBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isTaken) "✅" else med.iconEmoji,
                        fontSize = if (isPictureMode) 34.sp else 26.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⏰ ${med.time}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = if (isTaken) BentoMoodText else BentoMedicineText
                            )
                        )
                        if (isTaken && med.takenTime != null) {
                            Text(
                                text = "(${LocaleHelper.get("status_taken", lang)} @ ${med.takenTime})",
                                style = MaterialTheme.typography.labelSmall.copy(color = BentoMoodText, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Text(
                        text = "${med.name} • ${med.dosage}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground,
                            fontSize = if (isLargeText) 20.sp else 16.sp
                        )
                    )

                    if (instructions.isNotBlank() && !isPictureMode) {
                        Text(
                            text = instructions,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoOnBackground.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    if (voicePrompt.isNotBlank() && !isPictureMode) {
                        Text(
                            text = "🗣️ \"$voicePrompt\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoGreenAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BentoHeaderBorder, RoundedCornerShape(12.dp))
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }
            }

            // Big Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTaken,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTaken) BentoGreenAccentLight else BentoGreenAccent,
                        contentColor = if (isTaken) BentoGreenAccent else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("btn_take_${med.id}")
                ) {
                    Text(
                        text = if (isTaken) "✓ " + LocaleHelper.get("status_taken", lang) else "✅ " + LocaleHelper.get("mark_taken", lang),
                        fontWeight = FontWeight.Black,
                        fontSize = if (isLargeText) 16.sp else 14.sp
                    )
                }

                OutlinedButton(
                    onClick = onLater,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BentoHeaderBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_later_med_${med.id}")
                ) {
                    Text(
                        text = "⏰ " + LocaleHelper.get("mark_later", lang),
                        fontWeight = FontWeight.Bold,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 14.sp else 12.sp
                    )
                }

                FilledTonalIconButton(
                    onClick = onHelp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(text = "❓", fontSize = 18.sp)
                }
            }
        }
    }
}
