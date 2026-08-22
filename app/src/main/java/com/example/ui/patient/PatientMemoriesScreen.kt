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
import com.example.data.model.Memory
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientMemoriesScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode

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
                    onClick = { viewModel.setPatientScreen(PatientScreen.HOME) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoHeaderBg,
                        contentColor = BentoOnBackground
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                    modifier = Modifier.testTag("memories_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "आपकी सुंदर यादें। किसी भी याद की कहानी सुनने के लिए उस पर छुएं।"
                            "gu" -> "તમારી સુંદર યાદો. કોઈપણ યાદગીરીની વાર્તા સાંભળવા માટે તેના પર અડો."
                            else -> "Your cherished memories. Tap any memory to hear the story narrated."
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

        // Section Title
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BentoMemoriesBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoMemoriesBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🎞️ " + LocaleHelper.get("memories_title", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoMemoriesText,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Gentle family stories and nostalgic moments.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoMemoriesText.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Memories List
        items(uiState.memories, key = { it.id }) { memory ->
            val title = when (lang) {
                "hi" -> memory.titleHi
                "gu" -> memory.titleGu
                else -> memory.titleEn
            }
            val description = when (lang) {
                "hi" -> memory.descriptionHi
                "gu" -> memory.descriptionGu
                else -> memory.descriptionEn
            }
            val prompt = when (lang) {
                "hi" -> memory.promptHi
                "gu" -> memory.promptGu
                else -> memory.promptEn
            }

            MemoryCard(
                memory = memory,
                title = title,
                description = description,
                prompt = prompt,
                isLargeText = isLargeText,
                lang = lang,
                onSpeak = {
                    val fullNarrative = "$title. $description. $prompt"
                    viewModel.speakText(fullNarrative)
                }
            )
        }
    }
}

@Composable
fun MemoryCard(
    memory: Memory,
    title: String,
    description: String,
    prompt: String,
    isLargeText: Boolean,
    lang: String,
    onSpeak: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.5.dp, BentoMemoriesBorder, RoundedCornerShape(28.dp))
            .clickable { onSpeak() }
            .testTag("memory_card_${memory.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Photo Simulation Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BentoMemoriesBg)
                    .border(1.5.dp, BentoMemoriesBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = memory.iconEmoji, fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 ${memory.location} • 📅 ${memory.yearOrEra}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = BentoMemoriesText,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }

            // Title & Description
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 24.sp else 20.sp
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = BentoOnBackground.copy(alpha = 0.8f),
                        lineHeight = 24.sp,
                        fontSize = if (isLargeText) 18.sp else 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Gentle Prompt Box
            if (prompt.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BentoGreenAccentLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "💭", fontSize = 22.sp)
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoGreenAccent
                            )
                        )
                    }
                }
            }

            // Listen Story Action Button
            Button(
                onClick = onSpeak,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoGreenAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_narrate_${memory.id}")
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Listen")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "▶ " + LocaleHelper.get("listen_story", lang),
                    fontWeight = FontWeight.Black,
                    fontSize = if (isLargeText) 16.sp else 14.sp
                )
            }
        }
    }
}
