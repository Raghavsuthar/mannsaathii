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
import com.example.data.model.PeriodOfDay
import com.example.data.model.RoutineItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientMyDayScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode
    val isPictureMode = uiState.profile.pictureMode

    val completedCount = uiState.routines.count { it.isCompleted }
    val totalCount = uiState.routines.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back & Header
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
                    modifier = Modifier.testTag("myday_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                // Spoken Summary Button
                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "आज के कुल $totalCount कार्यों में से $completedCount कार्य पूरे हो चुके हैं।"
                            "gu" -> "આજના કુલ $totalCount માંથી $completedCount કાર્યો પૂરા થયા છે."
                            else -> "$completedCount out of $totalCount daily tasks are completed today."
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

        // Title and Progress card
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BentoMyDayBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoMyDayBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🕐 " + LocaleHelper.get("my_day_routine", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoMyDayText,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Completed: $completedCount / $totalCount",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoMyDayText,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                        color = BentoGreenAccent,
                        trackColor = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
            }
        }

        // Routine Items Grouped by Period
        val periods = listOf(
            PeriodOfDay.MORNING to "morning",
            PeriodOfDay.AFTERNOON to "afternoon",
            PeriodOfDay.EVENING to "evening",
            PeriodOfDay.NIGHT to "night"
        )

        periods.forEach { (periodEnum, localeKey) ->
            val periodItems = uiState.routines.filter { it.period.equals(periodEnum.name, ignoreCase = true) }
            if (periodItems.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = periodEnum.iconEmoji, fontSize = 22.sp)
                        Text(
                            text = LocaleHelper.get(localeKey, lang).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoGreenAccent,
                                fontSize = if (isLargeText) 20.sp else 16.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                items(periodItems, key = { it.id }) { routine ->
                    val title = when (lang) {
                        "hi" -> routine.titleHi
                        "gu" -> routine.titleGu
                        else -> routine.titleEn
                    }
                    val voicePrompt = when (lang) {
                        "hi" -> routine.caregiverVoiceTextHi
                        "gu" -> routine.caregiverVoiceTextGu
                        else -> routine.caregiverVoiceTextEn
                    }

                    RoutineCard(
                        item = routine,
                        title = title,
                        voicePrompt = voicePrompt,
                        isLargeText = isLargeText,
                        isPictureMode = isPictureMode,
                        lang = lang,
                        onSpeak = {
                            val textToSpeak = if (voicePrompt.isNotBlank()) voicePrompt else "$title at ${routine.time}"
                            viewModel.speakText(textToSpeak)
                        },
                        onDone = { viewModel.markRoutineDone(routine) },
                        onLater = { viewModel.markRoutineLater(routine) },
                        onHelp = {
                            val helpMsg = when (lang) {
                                "hi" -> "देखभालकर्ता ${uiState.profile.caregiverName} को सहायता के लिए सूचित किया गया है।"
                                "gu" -> "દીકરી ${uiState.profile.caregiverName} ને મદદ માટે જણાવવામાં આવ્યું છે."
                                else -> "Caregiver ${uiState.profile.caregiverName} has been notified."
                            }
                            viewModel.speakText(helpMsg)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineCard(
    item: RoutineItem,
    title: String,
    voicePrompt: String,
    isLargeText: Boolean,
    isPictureMode: Boolean,
    lang: String,
    onSpeak: () -> Unit,
    onDone: () -> Unit,
    onLater: () -> Unit,
    onHelp: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = if (item.isCompleted) BentoMoodBg else Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.5.dp,
                color = if (item.isCompleted) BentoMoodBorder else BentoHeaderBorder,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onSpeak() }
            .testTag("routine_card_${item.id}")
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
                        .size(if (isPictureMode) 60.dp else 48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (item.isCompleted) BentoGreenAccentLight else BentoMyDayBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.isCompleted) "✅" else item.iconEmoji,
                        fontSize = if (isPictureMode) 32.sp else 24.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoGreenAccent
                        )
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (item.isCompleted) BentoMoodText else BentoOnBackground,
                            fontSize = if (isLargeText) 20.sp else 16.sp
                        )
                    )
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

            // Big Action Buttons: DONE, LATER, HELP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCompleted) BentoGreenAccentLight else BentoGreenAccent,
                        contentColor = if (item.isCompleted) BentoGreenAccent else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("btn_done_${item.id}")
                ) {
                    Text(
                        text = if (item.isCompleted) "✓ " + LocaleHelper.get("btn_done", lang) else "✅ " + LocaleHelper.get("btn_done", lang),
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
                        .testTag("btn_later_${item.id}")
                ) {
                    Text(
                        text = "⏰ " + LocaleHelper.get("btn_later", lang),
                        fontWeight = FontWeight.Bold,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 14.sp else 12.sp
                    )
                }

                FilledTonalIconButton(
                    onClick = onHelp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_help_${item.id}")
                ) {
                    Text(text = "❓", fontSize = 18.sp)
                }
            }
        }
    }
}
