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

    var selectedGameIndex by remember { mutableStateOf<Int?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back and Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (selectedGameIndex != null) {
                            selectedGameIndex = null
                            feedbackMessage = null
                        } else {
                            viewModel.setPatientScreen(PatientScreen.HOME)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoHeaderBg,
                        contentColor = BentoOnBackground
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                    modifier = Modifier.testTag("games_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (selectedGameIndex != null) "All Activities" else "Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "यहाँ मनोरंजक और हल्की गतिविधियां हैं। कोई भी अभ्यास चुनकर आनंद लें।"
                            "gu" -> "અહીં મનોરંજક અને સરળ રમતો છે. કોઈપણ પ્રવૃત્તિ પસંદ કરીને આનંદ માણો."
                            else -> "Gentle mind activities. No scores, pure enjoyment."
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

        if (selectedGameIndex == null) {
            // Games Directory
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
                        verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            val gamesList = listOf(
                Triple(1, "game_1_name", "🌸"),
                Triple(2, "game_2_name", "🍎"),
                Triple(3, "game_3_name", "🟢"),
                Triple(4, "game_4_name", "🍲"),
                Triple(5, "game_5_name", "🌅"),
                Triple(6, "game_6_name", "☕"),
                Triple(7, "game_7_name", "👀"),
                Triple(8, "game_8_name", "🔢"),
                Triple(9, "game_9_name", "⭐"),
                Triple(10, "game_10_name", "👨‍👩‍👧")
            )

            items(gamesList.size) { idx ->
                val (num, key, icon) = gamesList[idx]
                val gameTitle = LocaleHelper.get(key, lang)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BentoPlayBorder, RoundedCornerShape(24.dp))
                        .clickable {
                            selectedGameIndex = num
                            feedbackMessage = null
                            viewModel.speakText(gameTitle)
                        }
                        .testTag("game_item_$num")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BentoPlayBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon, fontSize = 26.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVITY $num",
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

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = BentoGreenAccent
                        )
                    }
                }
            }
        } else {
            // Active Game Interactive Screen
            item {
                ActiveGameViewer(
                    gameIndex = selectedGameIndex!!,
                    lang = lang,
                    isLargeText = isLargeText,
                    feedbackMessage = feedbackMessage,
                    onOptionSelected = { isRight, promptText ->
                        val encouraging = when (lang) {
                            "hi" -> "बहुत सुंदर! चलिए साथ मिलकर देखते हैं।"
                            "gu" -> "ખૂબ સુંદર! ચાલો સાથે મળીને જોઈએ."
                            else -> "Wonderful! Let's explore together."
                        }
                        feedbackMessage = encouraging
                        viewModel.speakText(encouraging)
                    },
                    onSpeak = { text -> viewModel.speakText(text) }
                )
            }
        }
    }
}

@Composable
fun ActiveGameViewer(
    gameIndex: Int,
    lang: String,
    isLargeText: Boolean,
    feedbackMessage: String?,
    onOptionSelected: (Boolean, String) -> Unit,
    onSpeak: (String) -> Unit
) {
    val prompt = when (gameIndex) {
        1 -> when (lang) {
            "hi" -> "इस कमल के फूल जैसा दूसरा फूल चुनें:"
            "gu" -> "આ કમળના ફૂલ જેવું બીજું ફૂલ પસંદ કરો:"
            else -> "Match the identical Lotus flower:"
        }
        2 -> when (lang) {
            "hi" -> "इनमें से मीठा लाल सेब (Apple) कौन सा है?"
            "gu" -> "આમાંથી મીઠું લાલ સફરજન કયું છે?"
            else -> "Which one is the red sweet Apple?"
        }
        3 -> when (lang) {
            "hi" -> "हरे रंग की चाय का कप छुएं:"
            "gu" -> "લીલા રંગનો ચાનો કપ અડો:"
            else -> "Tap the Green color teacup:"
        }
        4 -> when (lang) {
            "hi" -> "इनमें से खाने की वस्तु कौन सी है?"
            "gu" -> "આમાંથી ખાવાની વસ્તુ કઈ છે?"
            else -> "Which item is delicious food?"
        }
        5 -> when (lang) {
            "hi" -> "सुबह उठने के बाद हम क्या करते हैं?"
            "gu" -> "સવારે જાગ્યા પછી આપણે શું કરીએ છીએ?"
            else -> "What do we do in the morning first?"
        }
        6 -> when (lang) {
            "hi" -> "समय देखने के लिए हम क्या देखते हैं?"
            "gu" -> "સમય જોવા માટે આપણે શું જોઈએ છીએ?"
            else -> "What do we use to see the time?"
        }
        7 -> when (lang) {
            "hi" -> "इन 3 सुंदर चीजों को ध्यान से देखें: 🌸 🍵 📖"
            "gu" -> "આ 3 સુંદર વસ્તુઓને જુઓ: 🌸 🍵 📖"
            else -> "Look at these 3 items: Flower 🌸, Tea 🍵, Book 📖"
        }
        8 -> when (lang) {
            "hi" -> "यहाँ कितने गेंदे के फूल हैं? 🌼 🌼 🌼"
            "gu" -> "અહીં કેટલા ગલગોટાના ફૂલ છે? 🌼 🌼 🌼"
            else -> "How many marigold flowers are here? 🌼 🌼 🌼"
        }
        9 -> when (lang) {
            "hi" -> "चमकते सुनहरे सितारे को छुएं:"
            "gu" -> "ચમકતા સોનેરી તારાને અડો:"
            else -> "Tap the glowing golden Star:"
        }
        else -> when (lang) {
            "hi" -> "आपकी प्यारी बेटी मीना का चित्र कौन सा है?"
            "gu" -> "તમારી વહાલી દીકરી મીનાનો ફોટો કયો છે?"
            else -> "Which photo is your daughter Meena?"
        }
    }

    val options = when (gameIndex) {
        1 -> listOf("🌸 Lotus", "🌻 Sunflower", "🌹 Rose")
        2 -> listOf("🍎 Apple", "🥕 Carrot", "🥥 Coconut")
        3 -> listOf("🟢 Green Tea", "🔴 Red Bowl", "🔵 Blue Mug")
        4 -> listOf("🍛 Dal Rotli", "👗 Bandhani Saree", "🪑 Wooden Chair")
        5 -> listOf("🪥 Brush & Tea", "🌙 Go to Sleep", "🚶 Evening Walk")
        6 -> listOf("⏰ Wall Clock", "👓 Reading Glasses", "📻 Old Radio")
        7 -> listOf("🍵 Warm Tea", "🚗 Red Car", "✈️ Aeroplane")
        8 -> listOf("3 Flowers", "5 Flowers", "1 Flower")
        9 -> listOf("⭐ Star", "🔷 Diamond", "⭕ Circle")
        else -> listOf("👩 Meena (Daughter)", "👨 Raj (Son)", "👧 Pooja (Granddaughter)")
    }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACTIVITY $gameIndex",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BentoPlayText,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )

                IconButton(
                    onClick = { onSpeak(prompt) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoPlayBg)
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }
            }

            Text(
                text = prompt,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (isLargeText) 24.sp else 20.sp,
                    color = BentoOnBackground
                )
            )

            // Options Buttons (Large touch targets)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEachIndexed { i, opt ->
                    Button(
                        onClick = { onOptionSelected(i == 0, opt) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoPlayBg,
                            contentColor = BentoPlayText
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .border(1.5.dp, BentoPlayBorder, RoundedCornerShape(18.dp))
                            .testTag("game_option_$i")
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = if (isLargeText) 20.sp else 18.sp
                            )
                        )
                    }
                }
            }

            // Gentle Positive Feedback Banner
            if (feedbackMessage != null) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BentoMoodBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BentoMoodBorder, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "✨", fontSize = 24.sp)
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = BentoMoodText,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
