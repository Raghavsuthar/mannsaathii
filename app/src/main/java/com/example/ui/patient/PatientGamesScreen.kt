package com.example.ui.patient

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.patient.games.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientGamesScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode

    var selectedActivityId by rememberSaveable { mutableStateOf<Int?>(null) }
    var completedCount by rememberSaveable { mutableIntStateOf(0) }
    val favoriteIds = remember { mutableStateListOf<Int>() }

    val activities = remember(uiState.familyMembers, uiState.memories, lang) {
        CognitiveActivityProvider.getActivities(uiState)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation & Stats Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (selectedActivityId != null) {
                            selectedActivityId = null
                        } else {
                            viewModel.setPatientScreen(PatientScreen.HOME)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoHeaderBg,
                        contentColor = BentoOnBackground
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BentoHeaderBorder),
                    modifier = Modifier.testTag("games_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoGreenAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedActivityId != null) {
                            LocaleHelper.get("all_activities", lang)
                        } else {
                            "Home"
                        },
                        fontWeight = FontWeight.Black,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 18.sp else 15.sp
                    )
                }

                // Completed Counter Badge
                if (completedCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BentoMoodBg,
                        border = BorderStroke(1.dp, BentoMoodBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⭐", fontSize = 16.sp)
                            Text(
                                text = "$completedCount ${LocaleHelper.get("activities_completed_today", lang)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoMoodText
                                )
                            )
                        }
                    }
                }

                // Voice Overview Speaker
                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "यहाँ मनोरंजक और हल्की गतिविधियां हैं। कोई अंक या परीक्षा नहीं है। आनंद लें।"
                            "gu" -> "અહીં મનોરંજક અને હળવી રમતો છે. કોઈ સ્કોર કે પરીક્ષા નથી. મજા માણો."
                            else -> "Gentle mind activities. No scores, no pressure, pure joy."
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

        if (selectedActivityId == null) {
            // Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = BentoPlayBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(3.dp, BentoPlayBorder, RoundedCornerShape(28.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🧩 " + LocaleHelper.get("games_title", lang),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoPlayText,
                                fontSize = if (isLargeText) 26.sp else 22.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = LocaleHelper.get("games_subtitle", lang),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoPlayText.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLargeText) 16.sp else 14.sp
                            )
                        )
                    }
                }
            }

            // Spotlight / Recommended Activity Card
            val recommendedActivity = activities.firstOrNull { it.id == 1 } ?: activities.first()
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = BentoMemoriesBg,
                    border = BorderStroke(2.dp, BentoMemoriesBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedActivityId = recommendedActivity.id
                            viewModel.speakText(recommendedActivity.getQuestion(lang))
                        }
                        .testTag("recommended_activity_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White)
                                .border(1.5.dp, BentoMemoriesBorder, RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🌟 " + LocaleHelper.get("recommended_today", lang).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoMemoriesText,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = LocaleHelper.get(recommendedActivity.titleKey, lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoOnBackground,
                                    fontSize = if (isLargeText) 20.sp else 17.sp
                                )
                            )
                        }

                        Button(
                            onClick = {
                                selectedActivityId = recommendedActivity.id
                                viewModel.speakText(recommendedActivity.getQuestion(lang))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoGreenAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = LocaleHelper.get("tap_to_start", lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // All 10 Activities List
            items(activities, key = { it.id }) { activity ->
                val gameTitle = LocaleHelper.get(activity.titleKey, lang)
                val isFavorite = favoriteIds.contains(activity.id)

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BentoPlayBorder, RoundedCornerShape(24.dp))
                        .clickable {
                            selectedActivityId = activity.id
                            viewModel.speakText(activity.getQuestion(lang))
                        }
                        .testTag("game_item_${activity.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Leading Activity Icon Box
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(BentoPlayBg)
                                .border(1.dp, BentoPlayBorder, RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = activity.iconEmoji, fontSize = 28.sp)
                        }

                        // Title & Type
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVITY ${activity.id}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoPlayText,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = gameTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoOnBackground,
                                    fontSize = if (isLargeText) 20.sp else 16.sp
                                )
                            )
                        }

                        // Favorite Heart Button
                        IconButton(
                            onClick = {
                                if (isFavorite) {
                                    favoriteIds.remove(activity.id)
                                } else {
                                    favoriteIds.add(activity.id)
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFE91E63) else BentoPlayText.copy(alpha = 0.5f)
                            )
                        }

                        // Play Arrow
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoGreenAccentLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start",
                                tint = BentoGreenAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Active Cognitive Activity View
            val currentActivity = activities.firstOrNull { it.id == selectedActivityId }
            if (currentActivity != null) {
                item {
                    ActiveCognitiveActivityViewer(
                        activity = currentActivity,
                        lang = lang,
                        isLargeText = isLargeText,
                        onActivityCompleted = {
                            completedCount++
                        },
                        onNextActivity = {
                            val nextId = if (currentActivity.id >= activities.size) 1 else currentActivity.id + 1
                            selectedActivityId = nextId
                            val nextAct = activities.firstOrNull { it.id == nextId }
                            if (nextAct != null) {
                                viewModel.speakText(nextAct.getQuestion(lang))
                            }
                        },
                        onBackToDirectory = {
                            selectedActivityId = null
                        },
                        onSpeak = { text -> viewModel.speakText(text) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveCognitiveActivityViewer(
    activity: CognitiveActivity,
    lang: String,
    isLargeText: Boolean,
    onActivityCompleted: () -> Unit,
    onNextActivity: () -> Unit,
    onBackToDirectory: () -> Unit,
    onSpeak: (String) -> Unit
) {
    // Shuffled options per activity so option 0 is NEVER guaranteed to be the right answer!
    val shuffledOptions = remember(activity.id) { activity.options.shuffled() }

    var selectedOptionId by remember(activity.id) { mutableStateOf<String?>(null) }
    var feedbackMessage by remember(activity.id) { mutableStateOf<String?>(null) }
    var isCelebration by remember(activity.id) { mutableStateOf(false) }

    // Special state for 3-item memory recall stage
    var hasRevealedMemory by remember(activity.id) {
        mutableStateOf(activity.type != ActivityType.MEMORY_RECALL_3_ITEMS)
    }

    val questionText = activity.getQuestion(lang)

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.5.dp, BentoPlayBorder, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Info Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoPlayBg,
                    border = BorderStroke(1.dp, BentoPlayBorder)
                ) {
                    Text(
                        text = "ACTIVITY ${activity.id} • ${LocaleHelper.get(activity.titleKey, lang)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = BentoPlayText,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = { onSpeak(questionText) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoPlayBg)
                        .border(1.dp, BentoPlayBorder, RoundedCornerShape(14.dp))
                ) {
                    Text(text = "🔊", fontSize = 20.sp)
                }
            }

            // Question Prompt
            Text(
                text = questionText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (isLargeText) 24.sp else 20.sp,
                    color = BentoOnBackground,
                    lineHeight = if (isLargeText) 32.sp else 28.sp
                )
            )

            // Stage 1 for 3-Item Recall: Observation Tray
            if (activity.type == ActivityType.MEMORY_RECALL_3_ITEMS && !hasRevealedMemory) {
                MemoryObservationTray(
                    items = activity.observationItems,
                    lang = lang,
                    isLargeText = isLargeText,
                    onReady = {
                        hasRevealedMemory = true
                        val prompt = LocaleHelper.get("what_did_you_see", lang)
                        onSpeak(prompt)
                    }
                )
            } else {
                // Visual Hero Focus Frame (if applicable)
                if (activity.targetHeroEmoji != null) {
                    HeroTargetFrame(
                        activity = activity,
                        lang = lang,
                        isLargeText = isLargeText
                    )
                }

                // Options List (Shuffled & Large Interactive Surfaces)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    shuffledOptions.forEachIndexed { index, option ->
                        val isChosen = selectedOptionId == option.id
                        val isCorrectOption = option.isCorrect || activity.isReminiscence

                        ActivityOptionCard(
                            option = option,
                            index = index,
                            lang = lang,
                            isLargeText = isLargeText,
                            isSelected = isChosen,
                            isAnswered = selectedOptionId != null,
                            onClick = {
                                selectedOptionId = option.id

                                if (activity.isReminiscence) {
                                    val affirmation = when (lang) {
                                        "hi" -> "बहुत ही सुंदर और शांतिदायक पसंद! 🕊️"
                                        "gu" -> "ખૂબ જ સુંદર અને શાંતિપૂર્ણ પસંદગી! 🕊️"
                                        else -> "A wonderfully peaceful choice! 🕊️"
                                    }
                                    feedbackMessage = affirmation
                                    isCelebration = true
                                    onSpeak(affirmation)
                                    onActivityCompleted()
                                } else if (option.isCorrect) {
                                    val successMsg = when (lang) {
                                        "hi" -> "बहुत सुंदर! आपने बिल्कुल सही पहचाना! 🌸"
                                        "gu" -> "ખૂબ સરસ! તમે બરાબર ઓળખી લીધું! 🌸"
                                        else -> "Wonderful! You found it! 🌸"
                                    }
                                    feedbackMessage = successMsg
                                    isCelebration = true
                                    onSpeak(successMsg)
                                    onActivityCompleted()
                                } else {
                                    val correctOpt = shuffledOptions.firstOrNull { it.isCorrect }
                                    val guideMsg = when (lang) {
                                        "hi" -> "अच्छा प्रयास! चलिए साथ मिलकर देखते हैं — ${correctOpt?.emoji ?: ""} ${correctOpt?.getText(lang) ?: ""} को छुएं!"
                                        "gu" -> "સરસ પ્રયાસ! ચાલો સાથે મળીને જોઈએ — ${correctOpt?.emoji ?: ""} ${correctOpt?.getText(lang) ?: ""} ને અડો!"
                                        else -> "Good try! Let's look together — tap ${correctOpt?.emoji ?: ""} ${correctOpt?.getText(lang) ?: ""}!"
                                    }
                                    feedbackMessage = guideMsg
                                    isCelebration = false
                                    onSpeak(guideMsg)
                                }
                            }
                        )
                    }
                }
            }

            // Always-Positive Feedback Banner
            if (feedbackMessage != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isCelebration) BentoMoodBg else BentoTodayBg,
                    border = BorderStroke(2.dp, if (isCelebration) BentoMoodBorder else BentoTodayBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isCelebration) "✨" else "🌿",
                            fontSize = 28.sp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isCelebration) LocaleHelper.get("well_done_title", lang) else "Together",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isCelebration) BentoMoodText else BentoTodayText
                                )
                            )
                            Text(
                                text = feedbackMessage!!,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isLargeText) 18.sp else 16.sp,
                                    color = if (isCelebration) BentoMoodText else BentoTodayText
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Flow Buttons (Next Activity & Directory)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNextActivity,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoGreenAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_next_activity")
                ) {
                    Text(
                        text = "▶ " + LocaleHelper.get("next_activity", lang),
                        fontWeight = FontWeight.Black,
                        fontSize = if (isLargeText) 17.sp else 15.sp
                    )
                }

                OutlinedButton(
                    onClick = onBackToDirectory,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, BentoPlayBorder),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("btn_all_activities")
                ) {
                    Text(
                        text = LocaleHelper.get("all_activities", lang),
                        fontWeight = FontWeight.Bold,
                        color = BentoPlayText,
                        fontSize = if (isLargeText) 16.sp else 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryObservationTray(
    items: List<ActivityOption>,
    lang: String,
    isLargeText: Boolean,
    onReady: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = BentoMemoriesBg,
        border = BorderStroke(2.dp, BentoMemoriesBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "🪔 Puja Tray Observation",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = BentoMemoriesText
                )
            )

            // 3 Observation Items Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.5.dp, BentoMemoriesBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(text = item.emoji, fontSize = 36.sp)
                        Text(
                            text = item.getText(lang),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBackground,
                                fontSize = if (isLargeText) 15.sp else 13.sp
                            )
                        )
                    }
                }
            }

            Button(
                onClick = onReady,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoGreenAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_memory_ready")
            ) {
                Text(
                    text = LocaleHelper.get("i_remember_ready", lang),
                    fontWeight = FontWeight.Black,
                    fontSize = if (isLargeText) 18.sp else 15.sp
                )
            }
        }
    }
}

@Composable
fun HeroTargetFrame(
    activity: CognitiveActivity,
    lang: String,
    isLargeText: Boolean
) {
    val heroColor = if (activity.targetHeroColorHex != null) {
        Color(activity.targetHeroColorHex)
    } else {
        BentoPlayBg
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(heroColor)
            .border(2.dp, BentoPlayBorder, RoundedCornerShape(22.dp))
            .padding(vertical = 18.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = activity.targetHeroEmoji ?: "✨",
                fontSize = if (activity.type == ActivityType.COUNTING) 36.sp else 48.sp,
                textAlign = TextAlign.Center
            )

            val label = activity.getTargetLabel(lang)
            if (!label.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground,
                            fontSize = if (isLargeText) 17.sp else 14.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityOptionCard(
    option: ActivityOption,
    index: Int,
    lang: String,
    isLargeText: Boolean,
    isSelected: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && option.isCorrect -> BentoMoodBg
        isSelected && !option.isCorrect -> BentoTodayBg
        option.colorHex != null -> Color(option.colorHex)
        else -> BentoPlayBg.copy(alpha = 0.6f)
    }

    val borderColor = when {
        isSelected && option.isCorrect -> BentoMoodBorder
        isSelected && !option.isCorrect -> BentoTodayBorder
        else -> BentoPlayBorder
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("game_option_$index")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Visual Avatar: Photo or Large Emoji
            if (!option.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = option.photoUri,
                    contentDescription = option.getText(lang),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, BentoPlayBorder, CircleShape)
                )
            } else if (option.emoji.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BentoPlayBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = option.emoji, fontSize = 26.sp)
                }
            }

            // Option Text & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.getText(lang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = if (isLargeText) 20.sp else 17.sp,
                        color = BentoOnBackground
                    )
                )
                val sub = option.getSubtitle(lang)
                if (sub.isNotBlank()) {
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoOnBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLargeText) 14.sp else 12.sp
                        )
                    )
                }
            }

            // Selection Indicator
            if (isSelected) {
                Text(
                    text = if (option.isCorrect) "✓" else "•",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (option.isCorrect) BentoGreenAccent else BentoTodayText
                )
            }
        }
    }
}
