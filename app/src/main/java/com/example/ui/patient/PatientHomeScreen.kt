package com.example.ui.patient

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
fun PatientHomeScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isPictureMode = uiState.profile.pictureMode
    val isLargeText = uiState.profile.largeTextMode

    val now = remember { Date() }
    val timeFormatted = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(now) }
    val timeDisplay12h = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now) }
    val dayFormatted = remember { SimpleDateFormat("EEEE, dd MMM", Locale.ENGLISH).format(now) }
    val dayRegional = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(now) }
    val dateRegional = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(now) }

    // Next upcoming action
    val nextMed = uiState.medications.firstOrNull { it.status == MedicationStatus.PENDING }
    val nextRoutine = uiState.routines.firstOrNull { !it.isCompleted }

    val nextActionText = when {
        nextMed != null -> "${nextMed.name} at ${nextMed.time}"
        nextRoutine != null -> {
            val title = when (lang) {
                "hi" -> nextRoutine.titleHi
                "gu" -> nextRoutine.titleGu
                else -> nextRoutine.titleEn
            }
            "$title at ${nextRoutine.time}"
        }
        else -> LocaleHelper.get("all_done_great", lang)
    }

    val nextActionIcon = when {
        nextMed != null -> "💊"
        nextRoutine != null -> nextRoutine.iconEmoji
        else -> "✨"
    }

    // Pulse animation for the emergency help badge
    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Bento Header Section
        item(span = { GridItemSpan(2) }) {
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = BentoHeaderBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoHeaderBorder, RoundedCornerShape(36.dp))
                    .clickable {
                        val spokenOrientation = when (lang) {
                            "hi" -> "नमस्ते ${uiState.profile.preferredName}! आज $dayRegional, $dateRegional है। समय $timeDisplay12h है। आप ${uiState.profile.city} में हैं।"
                            "gu" -> "નમસ્તે ${uiState.profile.preferredName}! આજે $dayRegional, $dateRegional છે. સમય $timeDisplay12h છે. તમે ${uiState.profile.city}માં છો."
                            else -> "Hello ${uiState.profile.preferredName}! Today is $dayFormatted. The time is $timeDisplay12h in ${uiState.profile.city}."
                        }
                        viewModel.speakText(spokenOrientation)
                    }
                    .testTag("orientation_banner")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Date Tag & Greeting
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dayFormatted.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoGreenAccent,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (lang) {
                                    "hi" -> "नमस्ते,\n${uiState.profile.name}"
                                    "gu" -> "નમસ્તે,\n${uiState.profile.name}"
                                    else -> "Namaste,\n${uiState.profile.name}"
                                },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoOnBackground,
                                    lineHeight = if (isLargeText) 34.sp else 30.sp,
                                    fontSize = if (isLargeText) 28.sp else 24.sp
                                )
                            )
                        }

                        // Right: Speaker & Clock
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clickable {
                                        val spokenOrientation = when (lang) {
                                            "hi" -> "आज $dayRegional है, तारीख $dateRegional है। समय $timeDisplay12h है।"
                                            "gu" -> "આજે $dayRegional છે, તારીખ $dateRegional છે. સમય $timeDisplay12h છે."
                                            else -> "Today is $dayFormatted. Time is $timeDisplay12h."
                                        }
                                        viewModel.speakText(spokenOrientation)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🔊", fontSize = 26.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = timeFormatted,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = BentoOnBackground
                                    )
                                )
                                Text(
                                    text = uiState.profile.city,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BentoOnBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Next Up Glass Card
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.9f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BentoGreenAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = nextActionIcon, fontSize = 22.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LocaleHelper.get("next_event", lang).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = BentoGreenAccent,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = nextActionText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoOnBackground,
                                        fontSize = if (isLargeText) 18.sp else 15.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Bento Grid Items (Deep Rounded Corners with 3D bottom borders)

        // Card 1: PEOPLE
        item {
            BentoGridCard(
                title = "PEOPLE",
                subtitle = "પરિવાર / लोग",
                iconEmoji = "👨‍👩‍👧",
                backgroundColor = BentoPeopleBg,
                borderColor = BentoPeopleBorder,
                textColor = BentoPeopleText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_people",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "आपके प्यारे परिवार के सदस्य।"
                        "gu" -> "તમારા વહાલા પરિવારના સભ્યો."
                        else -> "People who love you."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.PEOPLE)
                }
            )
        }

        // Card 2: MY DAY
        item {
            BentoGridCard(
                title = "MY DAY",
                subtitle = "દિનચર્યા / दिन",
                iconEmoji = "🕐",
                backgroundColor = BentoMyDayBg,
                borderColor = BentoMyDayBorder,
                textColor = BentoMyDayText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_my_day",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "आपकी आज की दिनचर्या।"
                        "gu" -> "તમારો આજનો દિવસ અને કામો."
                        else -> "Your daily routine."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.MY_DAY)
                }
            )
        }

        // Card 3: MEMORIES
        item {
            BentoGridCard(
                title = "MEMORIES",
                subtitle = "યાદો / यादें",
                iconEmoji = "🎞️",
                backgroundColor = BentoMemoriesBg,
                borderColor = BentoMemoriesBorder,
                textColor = BentoMemoriesText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_memories",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "आपकी सुंदर और प्यारी यादें।"
                        "gu" -> "તમારી સુંદર અને મીઠી યાદો."
                        else -> "Your cherished memories."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.MEMORIES)
                }
            )
        }

        // Card 4: PLAY
        item {
            BentoGridCard(
                title = "PLAY",
                subtitle = "રમત / खेल",
                iconEmoji = "🧩",
                backgroundColor = BentoPlayBg,
                borderColor = BentoPlayBorder,
                textColor = BentoPlayText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_games",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "मजेदार और हल्की दिमागी गतिविधियां।"
                        "gu" -> "આનંદદાયક અને સરળ રમતો."
                        else -> "Gentle mind games."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.PLAY)
                }
            )
        }

        // Card 5: MEDICINE
        item {
            BentoGridCard(
                title = "MEDICINE",
                subtitle = "દવાઓ / दवाई",
                iconEmoji = "💊",
                backgroundColor = BentoMedicineBg,
                borderColor = BentoMedicineBorder,
                textColor = BentoMedicineText,
                badgeCount = uiState.medications.count { it.status == MedicationStatus.PENDING },
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_medicines",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "दवाई का समय और जानकारी।"
                        "gu" -> "દવાનો સમય અને માહિતી."
                        else -> "Medication reminders."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.MEDICINE)
                }
            )
        }

        // Card 6: MOOD
        item {
            BentoGridCard(
                title = "MOOD",
                subtitle = "મન / मन",
                iconEmoji = "😊",
                backgroundColor = BentoMoodBg,
                borderColor = BentoMoodBorder,
                textColor = BentoMoodText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_mood",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "आप अभी कैसा महसूस कर रहे हैं?"
                        "gu" -> "તમારું મન અત્યારે કેવું છે?"
                        else -> "How are you feeling right now?"
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.MOOD)
                }
            )
        }

        // Card 7: TODAY
        item {
            BentoGridCard(
                title = "TODAY",
                subtitle = "આજ / आज",
                iconEmoji = "☀️",
                backgroundColor = BentoTodayBg,
                borderColor = BentoTodayBorder,
                textColor = BentoTodayText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_today",
                onClick = {
                    val prompt = when (lang) {
                        "hi" -> "आज का दिन और समय की जानकारी।"
                        "gu" -> "આજનો દિવસ અને સમયની માહિતી."
                        else -> "Today orientation."
                    }
                    viewModel.speakText(prompt)
                    viewModel.setPatientScreen(PatientScreen.TODAY)
                }
            )
        }

        // Card 8: ASK SAATHI (Voice AI)
        item {
            BentoGridCard(
                title = "ASK SAATHI",
                subtitle = "વાત કરો / बोलें",
                iconEmoji = "🎤",
                backgroundColor = BentoAiBg,
                borderColor = BentoAiBorder,
                textColor = BentoAiText,
                isPictureMode = isPictureMode,
                isLargeText = isLargeText,
                testTag = "btn_ai_talk",
                onClick = {
                    viewModel.showAiAssistant(true)
                }
            )
        }

        // 3. Giant Emergency HELP Footer Card
        item(span = { GridItemSpan(2) }) {
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = BentoHelpRed,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .border(
                        width = 4.dp,
                        color = BentoHelpRedDark,
                        shape = RoundedCornerShape(36.dp)
                    )
                    .clickable {
                        val prompt = when (lang) {
                            "hi" -> "तुरंत मदद या परिवार को फोन करने के लिए संपर्क।"
                            "gu" -> "તરત મદદ મેળવવા કે દીકરીને ફોન કરવા માટે."
                            else -> "Help and emergency calling."
                        }
                        viewModel.speakText(prompt)
                        viewModel.setPatientScreen(PatientScreen.HELP)
                    }
                    .testTag("btn_help")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "HELP",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 3.sp,
                                fontSize = if (isLargeText) 32.sp else 28.sp
                            )
                        )
                        Text(
                            text = "મદદ / तुरंत सहायता",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🆘", fontSize = 30.sp)
                    }
                }
            }
        }

        // 4. Bento Navigation Strip & Disclaimer
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Navigation Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home (Active)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(BentoGreenAccent)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "HOME",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoGreenAccent
                            )
                        )
                    }

                    // Caregiver
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.requestSwitchRole(com.example.data.model.UserRole.CAREGIVER) }
                    ) {
                        Text(text = "👤", fontSize = 18.sp)
                        Text(
                            text = "CAREGIVER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBackground.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Language
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.showLanguageSelector(true) }
                    ) {
                        Text(text = "🌐", fontSize = 18.sp)
                        Text(
                            text = lang.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // Disclaimer line
                Text(
                    text = "MannSaathi is a daily cognitive support companion. It does not provide medical diagnosis, treatment, or clinical prescriptions.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BentoOnBackground.copy(alpha = 0.4f),
                        lineHeight = 14.sp,
                        fontSize = 10.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
fun BentoGridCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    badgeCount: Int = 0,
    isPictureMode: Boolean,
    isLargeText: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(36.dp),
        color = backgroundColor,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (isPictureMode) 145.dp else 135.dp)
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(36.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (badgeCount > 0) {
                Badge(
                    containerColor = BentoHelpRed,
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(text = badgeCount.toString(), fontWeight = FontWeight.Bold)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = if (isPictureMode) 52.sp else 42.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        fontSize = if (isLargeText) 18.sp else 16.sp,
                        letterSpacing = 0.5.sp
                    )
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.65f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
