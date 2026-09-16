package com.example.ui.patient

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FamilyMember
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientPeopleScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode
    val isPictureMode = uiState.profile.pictureMode
    val context = LocalContext.current

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
                    modifier = Modifier.testTag("people_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "यहाँ आपके परिवार के सभी प्यारे सदस्य हैं। किसी को भी सुनने या फोन करने के लिए उनके कार्ड को छुएं।"
                            "gu" -> "અહીં તમારા પરિવારના બધા સ્નેહીજનો છે. સાંભળવા કે ફોન કરવા માટે તેમના કાર્ડને અડો."
                            else -> "Here are your loving family members. Tap any person to hear about them or make a call."
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
                color = BentoPeopleBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoPeopleBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "👨‍👩‍👧 " + LocaleHelper.get("people_title", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoPeopleText,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Tap on any photo to hear: \"Who is this?\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoPeopleText.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Family Members List
        items(uiState.familyMembers, key = { it.id }) { member ->
            val relationship = when (lang) {
                "hi" -> member.relationshipHi
                "gu" -> member.relationshipGu
                else -> member.relationshipEn
            }
            val intro = when (lang) {
                "hi" -> member.introHi
                "gu" -> member.introGu
                else -> member.introEn
            }

            FamilyMemberCard(
                member = member,
                relationship = relationship,
                intro = intro,
                isLargeText = isLargeText,
                isPictureMode = isPictureMode,
                lang = lang,
                onSpeak = {
                    val spoken = if (intro.isNotBlank()) intro else "$relationship, ${member.name}"
                    viewModel.speakText(spoken)
                },
                onCall = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${member.phone.replace(" ", "")}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        viewModel.speakText("Calling ${member.name} at ${member.phone}")
                    }
                }
            )
        }
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
    relationship: String,
    intro: String,
    isLargeText: Boolean,
    isPictureMode: Boolean,
    lang: String,
    onSpeak: () -> Unit,
    onCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, BentoHeaderBorder, RoundedCornerShape(28.dp))
            .clickable { onSpeak() }
            .testTag("family_card_${member.id}")
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar / Photo Container
                if (!member.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = member.photoUri,
                        contentDescription = member.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(if (isPictureMode) 80.dp else 68.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(2.dp, BentoPeopleBorder, RoundedCornerShape(22.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(if (isPictureMode) 80.dp else 68.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(BentoPeopleBg)
                            .border(2.dp, BentoPeopleBorder, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.iconEmoji,
                            fontSize = if (isPictureMode) 44.sp else 36.sp
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = if (isLargeText) 24.sp else 20.sp,
                            color = BentoOnBackground
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BentoGreenAccentLight,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = relationship,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoGreenAccent
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (intro.isNotBlank() && !isPictureMode) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = intro,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoOnBackground.copy(alpha = 0.75f),
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Interactive Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSpeak,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BentoHeaderBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_who_${member.id}")
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Who is this?", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleHelper.get("who_is_this", lang),
                        fontWeight = FontWeight.Bold,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 14.sp else 12.sp
                    )
                }

                Button(
                    onClick = onCall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoGreenAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_call_${member.id}")
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleHelper.get("btn_call", lang),
                        fontWeight = FontWeight.Black,
                        fontSize = if (isLargeText) 16.sp else 14.sp
                    )
                }
            }
        }
    }
}
