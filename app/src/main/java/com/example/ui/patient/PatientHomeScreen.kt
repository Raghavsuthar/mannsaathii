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
    // Clocks render from timeDisplay12h in the patient's own language,
    // never the device default.
    val timeDisplay12h = remember(lang) { SimpleDateFormat("hh:mm a", LocaleHelper.localeFor(lang)).format(now) }
    val dayFormatted = remember { SimpleDateFormat("EEEE, dd MMM", Locale.ENGLISH).format(now) }
    val dayRegional = remember(lang) { SimpleDateFormat("EEEE", LocaleHelper.localeFor(lang)).format(now) }
    val dateRegional = remember(lang) { SimpleDateFormat("dd MMMM yyyy", LocaleHelper.localeFor(lang)).format(now) }

    val nextMed = uiState.medications.firstOrNull { it.status == MedicationStatus.PENDING }
    val nextRoutine = uiState.routines.firstOrNull { !it.isCompleted }

    val calendar = remember { Calendar.getInstance() }
    val hourOfDay = remember { calendar.get(Calendar.HOUR_OF_DAY) }
    val greetingEn = remember(hourOfDay) {
        when (hourOfDay) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
    val greetingRegional = remember(lang, hourOfDay) {
        when (lang) {
            "hi" -> when (hourOfDay) {
                in 5..11 -> "शुभ प्रभात"
                in 12..16 -> "शुभ दोपहर"
                in 17..21 -> "शुभ संध्या"
                else -> "शुभ रात्रि"
            }
            "gu" -> when (hourOfDay) {
                in 5..11 -> "શુભ સવાર"
                in 12..16 -> "શુભ બપોર"
                in 17..21 -> "શુભ સાંજ"
                else -> "શુભ રાત્રિ"
            }
            else -> greetingEn
        }
    }
    val greetingWord = remember(lang) {
        when (lang) {
            "hi" -> "नमस्ते,"
            "gu" -> "નમસ્તે,"
            else -> "Namaste,"
        }
    }
    val nextUpLabel = remember(lang) {
        when (lang) {
            "hi" -> "आगे • NEXT UP"
            "gu" -> "હવે પછી • NEXT UP"
            else -> "NEXT UP • હવે પછી"
        }
    }
    val nextTimeChip = nextMed?.time ?: nextRoutine?.time ?: "--:--"

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
        // 1. Bento Header Section — Stitch p1 orientation banner
        item(span = { GridItemSpan(2) }) {
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = BentoHeaderBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoHeaderBorder, RoundedCornerShape(36.dp))
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
                        // Left: Date pill + multilingual greeting
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.85f),
                                shadowElevation = 1.dp,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BentoGreenAccent)
                                    )
                                    Text(
                                        text = dayFormatted.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = BentoGreenAccent,
                                            letterSpacing = 1.2.sp,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = greetingWord,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoGreenAccent,
                                    fontSize = if (isLargeText) 30.sp else 26.sp,
                                    lineHeight = if (isLargeText) 34.sp else 30.sp
                                )
                            )
                            Text(
                                text = uiState.profile.name,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoOnBackground,
                                    lineHeight = if (isLargeText) 34.sp else 30.sp,
                                    fontSize = if (isLargeText) 28.sp else 24.sp
                                )
                            )
                            Text(
                                text = "$greetingRegional • $greetingEn",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoOnBackground.copy(alpha = 0.65f),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Right: circular speaker + clock + city
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 3.dp,
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
                                    Text(text = "🔊", fontSize = 24.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = timeDisplay12h,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = BentoOnBackground,
                                        fontSize = 20.sp
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(text = "📍", fontSize = 12.sp)
                                    Text(
                                        text = uiState.profile.city,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BentoOnBackground.copy(alpha = 0.65f),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Bottom pill: Next-up medication cue with time chip
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(BentoGreenAccentLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = nextActionIcon, fontSize = 20.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = nextUpLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = BentoGreenAccent,
                                            letterSpacing = 1.sp,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = nextActionText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BentoOnBackground,
                                            fontSize = if (isLargeText) 17.sp else 15.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = BentoGreenAccentLight,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = nextTimeChip,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = BentoGreenAccent,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
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

        // 3. Giant Emergency HELP Footer Card — Stitch p1 SOS bar
        item(span = { GridItemSpan(2) }) {
            // Outer glow pulse for SOS badge
            val sosGlow by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 0.55f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "SosGlow"
            )
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = BentoHelpRed,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .border(
                        width = 3.dp,
                        color = BentoHelpRedBorder,
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
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🚨", fontSize = 26.sp)
                            Text(
                                text = "HELP",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 3.sp,
                                    fontSize = if (isLargeText) 32.sp else 28.sp
                                )
                            )
                        }
                        Text(
                            text = "મદદ / तुरंत सहायता",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 14.sp
                            )
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = sosGlow))
                        )
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🆘", fontSize = 30.sp)
                        }
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
    // Gentle bounce for the pending-dose badge (Stitch p1 red dot)
    val badgePulse = rememberInfiniteTransition(label = "BadgePulse")
    val badgeScale by badgePulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BadgeScale"
    )
    Surface(
        shape = RoundedCornerShape(36.dp),
        color = backgroundColor,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 148.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(36.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Decorative top-right halo, as in Stitch bento tiles
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 28.dp, y = (-28).dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.30f))
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .scale(badgeScale)
                        .clip(CircleShape)
                        .background(BentoHelpRed)
                        .defaultMinSize(minWidth = 28.dp, minHeight = 28.dp)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = if (isPictureMode) 52.sp else 44.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        fontSize = if (isLargeText) 18.sp else 17.sp,
                        letterSpacing = 0.8.sp
                    )
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
