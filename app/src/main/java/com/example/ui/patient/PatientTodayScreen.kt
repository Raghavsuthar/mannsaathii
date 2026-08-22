package com.example.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.MedicationStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientTodayScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode

    val now = remember { Date() }
    val timeFormatted = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now) }
    val dayFormatted = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(now) }
    val dateFormatted = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(now) }

    val nextMed = uiState.medications.firstOrNull { it.status == MedicationStatus.PENDING }
    val nextRoutine = uiState.routines.firstOrNull { !it.isCompleted }

    val nextEventTitle = when {
        nextMed != null -> "💊 ${nextMed.name} (${nextMed.time})"
        nextRoutine != null -> {
            val title = when (lang) {
                "hi" -> nextRoutine.titleHi
                "gu" -> nextRoutine.titleGu
                else -> nextRoutine.titleEn
            }
            "${nextRoutine.iconEmoji} $title (${nextRoutine.time})"
        }
        else -> LocaleHelper.get("all_done_great", lang)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back & Title Bar
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
                    modifier = Modifier.testTag("today_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val fullSpeech = when (lang) {
                            "hi" -> "आज $dayFormatted है, तारीख $dateFormatted है। समय $timeFormatted है। आप ${uiState.profile.city} में हैं। आप ${uiState.profile.name} हैं। आपकी देखभालकर्ता ${uiState.profile.caregiverName} हैं।"
                            "gu" -> "આજે $dayFormatted છે, તારીખ $dateFormatted છે. સમય $timeFormatted છે. તમે ${uiState.profile.city}માં છો. તમારું નામ ${uiState.profile.name} છે. તમારી સંભાળ રાખનાર ${uiState.profile.caregiverName} છે."
                            else -> "Today is $dayFormatted, $dateFormatted. Time is $timeFormatted. You are in ${uiState.profile.city}. You are ${uiState.profile.name}. Your caregiver is ${uiState.profile.caregiverName}."
                        }
                        viewModel.speakText(fullSpeech)
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

        item {
            Text(
                text = "☀️ " + LocaleHelper.get("orientation_title", lang),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = BentoOnBackground,
                    fontSize = if (isLargeText) 28.sp else 24.sp
                )
            )
            Text(
                text = "👉 " + LocaleHelper.get("tap_to_hear", lang),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoGreenAccent,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // 1. DAY & TIME Bento Card
        item {
            OrientationBentoCard(
                icon = "⏰",
                label = LocaleHelper.get("time", lang),
                value = timeFormatted,
                subValue = dayFormatted,
                backgroundColor = BentoTodayBg,
                borderColor = BentoTodayBorder,
                textColor = BentoTodayText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "अभी का समय $timeFormatted है और आज $dayFormatted है।"
                        "gu" -> "અત્યારે સમય $timeFormatted છે અને આજે $dayFormatted છે."
                        else -> "The current time is $timeFormatted and today is $dayFormatted."
                    }
                    viewModel.speakText(speech)
                }
            )
        }

        // 2. DATE Bento Card
        item {
            OrientationBentoCard(
                icon = "📅",
                label = LocaleHelper.get("date", lang),
                value = dateFormatted,
                subValue = "Year 2026",
                backgroundColor = BentoMemoriesBg,
                borderColor = BentoMemoriesBorder,
                textColor = BentoMemoriesText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "आज की तारीख $dateFormatted है।"
                        "gu" -> "આજની તારીખ $dateFormatted છે."
                        else -> "Today's date is $dateFormatted."
                    }
                    viewModel.speakText(speech)
                }
            )
        }

        // 3. PLACE Bento Card
        item {
            OrientationBentoCard(
                icon = "📍",
                label = LocaleHelper.get("location", lang),
                value = uiState.profile.city,
                subValue = "At home, surrounded by love",
                backgroundColor = BentoMyDayBg,
                borderColor = BentoMyDayBorder,
                textColor = BentoMyDayText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "आप अपने घर ${uiState.profile.city} में सुरक्षित हैं।"
                        "gu" -> "તમે તમારા ઘરમાં ${uiState.profile.city}માં સુરક્ષિત છો."
                        else -> "You are safely at home in ${uiState.profile.city}."
                    }
                    viewModel.speakText(speech)
                }
            )
        }

        // 4. YOU ARE (Patient Identity Card)
        item {
            OrientationBentoCard(
                icon = "👵",
                label = LocaleHelper.get("you_are", lang),
                value = "${uiState.profile.name} (${uiState.profile.preferredName})",
                subValue = "Age: ${uiState.profile.age} Years",
                backgroundColor = BentoPeopleBg,
                borderColor = BentoPeopleBorder,
                textColor = BentoPeopleText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "आप ${uiState.profile.name} हैं। सब आपको प्यार से ${uiState.profile.preferredName} कहते हैं।"
                        "gu" -> "તમે ${uiState.profile.name} છો. બધા તમને વહાલથી ${uiState.profile.preferredName} કહે છે."
                        else -> "You are ${uiState.profile.name}, lovingly called ${uiState.profile.preferredName}."
                    }
                    viewModel.speakText(speech)
                }
            )
        }

        // 5. CAREGIVER Card
        item {
            OrientationBentoCard(
                icon = "👩",
                label = LocaleHelper.get("caregiver_is", lang),
                value = uiState.profile.caregiverName,
                subValue = "${uiState.profile.caregiverRelationship} • ${uiState.profile.caregiverPhone}",
                backgroundColor = BentoAiBg,
                borderColor = BentoAiBorder,
                textColor = BentoAiText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "आपकी मुख्य देखभालकर्ता आपकी ${uiState.profile.caregiverRelationship} ${uiState.profile.caregiverName} हैं।"
                        "gu" -> "તમારી સંભાળ રાખનાર તમારી ${uiState.profile.caregiverRelationship} ${uiState.profile.caregiverName} છે."
                        else -> "Your caregiver is your ${uiState.profile.caregiverRelationship}, ${uiState.profile.caregiverName}."
                    }
                    viewModel.speakText(speech)
                }
            )
        }

        // 6. NEXT IMPORTANT EVENT Card
        item {
            OrientationBentoCard(
                icon = "✨",
                label = LocaleHelper.get("next_event", lang),
                value = nextEventTitle,
                subValue = "Next scheduled item for today",
                backgroundColor = BentoMoodBg,
                borderColor = BentoMoodBorder,
                textColor = BentoMoodText,
                onClick = {
                    val speech = when (lang) {
                        "hi" -> "आपका अगला कार्य $nextEventTitle है।"
                        "gu" -> "તમારું હવે પછીનું કાર્ય $nextEventTitle છે."
                        else -> "Your next scheduled task is $nextEventTitle."
                    }
                    viewModel.speakText(speech)
                }
            )
        }
    }
}

@Composable
fun OrientationBentoCard(
    icon: String,
    label: String,
    value: String,
    subValue: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = textColor.copy(alpha = 0.75f),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                )
                if (subValue.isNotBlank()) {
                    Text(
                        text = subValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textColor.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Speak",
                tint = textColor.copy(alpha = 0.7f)
            )
        }
    }
}
