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
import com.example.data.model.MoodType
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientMoodScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode

    var recordedMoodMsg by remember { mutableStateOf<String?>(null) }

    val moodOptions = listOf(
        Pair(MoodType.HAPPY, "mood_happy"),
        Pair(MoodType.OKAY, "mood_okay"),
        Pair(MoodType.NOT_GOOD, "mood_not_good"),
        Pair(MoodType.SAD, "mood_sad"),
        Pair(MoodType.TIRED, "mood_tired"),
        Pair(MoodType.ANGRY, "mood_angry")
    )

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
                    modifier = Modifier.testTag("mood_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = LocaleHelper.get("how_do_you_feel", lang)
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
                color = BentoMoodBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoMoodBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "😊 " + LocaleHelper.get("mood_checkin", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoMoodText,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = LocaleHelper.get("how_do_you_feel", lang),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = BentoMoodText.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLargeText) 20.sp else 16.sp
                        )
                    )
                }
            }
        }

        // Mood Faces Bento Grid
        items(moodOptions.chunked(2).size) { rowIdx ->
            val rowItems = moodOptions.chunked(2)[rowIdx]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowItems.forEach { (type, key) ->
                    val label = LocaleHelper.get(key, lang)
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(125.dp)
                            .border(
                                width = 2.5.dp,
                                color = BentoMoodBorder,
                                shape = RoundedCornerShape(28.dp)
                            )
                            .clickable {
                                viewModel.recordMood(type)
                                recordedMoodMsg = LocaleHelper.get("mood_saved", lang)
                            }
                            .testTag("mood_btn_${type.name}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = type.emoji, fontSize = 46.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoOnBackground,
                                    fontSize = if (isLargeText) 18.sp else 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Confirmation Banner
        if (recordedMoodMsg != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BentoMoodBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BentoMoodBorder, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "💚", fontSize = 24.sp)
                        Text(
                            text = recordedMoodMsg!!,
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
