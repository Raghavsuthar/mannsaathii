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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@Composable
fun PatientHelpScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val isLargeText = uiState.profile.largeTextMode
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back and Header — Stitch p9: back square + title + profile
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { viewModel.setPatientScreen(PatientScreen.HOME) }
                            .testTag("help_back_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "←", fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(
                        text = when (lang) {
                            "hi" -> "Emergency Assistance"
                            "gu" -> "Emergency Assistance"
                            else -> "Emergency Assistance"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground,
                            fontSize = if (isLargeText) 22.sp else 19.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BentoGreenAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 22.sp)
                }
            }
        }

        // Title Header — Immediate Help intro (light green, Stitch p9)
        item {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = BentoTodayBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoTodayBorder, RoundedCornerShape(32.dp))
                    .clickable {
                        val speech = when (lang) {
                            "hi" -> "घबराएं नहीं। मदद के लिए नीचे दिए किसी भी बटन को दबाएं या अपनी बेटी मीना को फोन करें।"
                            "gu" -> "ચિંતા ન કરો. મદદ માટે નીચે આપેલ કોઈપણ બટન દબાવો."
                            else -> "Stay calm. Tap any button below to connect immediately with your family or doctor."
                        }
                        viewModel.speakText(speech)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = when (lang) {
                            "hi" -> "✳ Immediate Help • मदद"
                            "gu" -> "✳ Immediate Help • મદદ"
                            else -> "✳ Immediate Help • મદદ"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground,
                            fontSize = if (isLargeText) 24.sp else 21.sp
                        )
                    )
                    Text(
                        text = when (lang) {
                            "hi" -> "Help is right here. Tap any button to speak to family or a doctor immediately."
                            "gu" -> "Help is right here. Tap any button to speak to family or a doctor immediately."
                            else -> "Help is right here. Tap any button to speak to family or a doctor immediately."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoOnBackground.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "ચિંતા કરશો નહીં, અમે તમારી સાથે છીએ.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoTodayText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }

        // Primary Caregiver Giant Call Button — solid red, Stitch p9
        item {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = BentoHelpRed,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoHelpRedBorder, RoundedCornerShape(32.dp))
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${uiState.profile.caregiverPhone.replace(" ", "")}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.speakText("Calling ${uiState.profile.caregiverName}")
                        }
                    }
                    .testTag("btn_call_primary_caregiver")
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
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📞", fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Call ${uiState.profile.caregiverName}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = if (isLargeText) 21.sp else 18.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = when (lang) {
                                "hi" -> "बेटी को फोन करो • Im..."
                                "gu" -> "દીકરીને ફોન કરો • Im..."
                                else -> "દીકરીને ફોન કરો • Im..."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "→", fontSize = 22.sp, fontWeight = FontWeight.Black, color = BentoHelpRed)
                    }
                }
            }
        }

        // Section label — Emergency Contacts • પરિવાર + Tap to call
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (lang) {
                        "hi" -> "📋 Emergency Contacts • परिवार"
                        "gu" -> "📋 Emergency Contacts • પરિવાર"
                        else -> "📋 Emergency Contacts • પરિવાર"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = BentoOnBackground,
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Tap to call",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BentoOnBackground.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Emergency Contacts List — Stitch p9 cards
        items(uiState.emergencyContacts, key = { it.id }) { contact ->
            val relationship = when (lang) {
                "hi" -> contact.relationshipHi
                "gu" -> contact.relationshipGu
                else -> contact.relationshipEn
            }
            val isAmbulance = contact.name.contains("108", ignoreCase = true) ||
                contact.name.contains("Ambulance", ignoreCase = true)
            val cardBg = when {
                isAmbulance -> Color(0xFFFFE3E3)
                contact.isPriority -> BentoTodayBg
                else -> Color.White
            }
            val cardBorder = when {
                isAmbulance -> Color(0xFFF3B4B4)
                contact.isPriority -> BentoTodayBorder
                else -> BentoHeaderBorder
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = cardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = cardBorder,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.phone.replace(" ", "")}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.speakText("Calling ${contact.name}")
                        }
                    }
                    .testTag("contact_item_${contact.id}")
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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAmbulance) BentoHelpRed else BentoGreenAccent.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = contact.iconEmoji, fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isAmbulance) BentoHelpRedDark else BentoOnBackground,
                                    fontSize = if (isLargeText) 20.sp else 16.sp
                                ),
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (contact.isPriority && !isAmbulance) {
                                Surface(shape = CircleShape, color = BentoGreenAccentLight) {
                                    Text(
                                        text = "Main",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = BentoGreenAccent,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "$relationship • ${contact.phone}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isAmbulance) BentoHelpRedDark.copy(alpha = 0.85f)
                                else BentoOnBackground.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            maxLines = 2
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAmbulance) BentoHelpRed else BentoGreenAccent
                            )
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${contact.phone.replace(" ", "")}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.speakText("Calling ${contact.name}")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📞", fontSize = 22.sp)
                    }
                }
            }
        }

        // Voice Guide card — Tap here to hear spoken help (Stitch p9)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BentoMoodBg,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoMoodBorder, RoundedCornerShape(28.dp))
                    .clickable {
                        val speech = when (lang) {
                            "hi" -> "घबराएं नहीं। गहरी सांस लें। आपकी देखभालकर्ता पास में हैं। मदद आ रही है।"
                            "gu" -> "ગભરાશો નહીં. ઊંડો શ્વાસ લો. તમારી સંભાળ રાખનાર નજીક છે. મદદ આવી રહી છે."
                            else -> "Do not worry. Take a deep breath. Your caregiver is nearby. Help is on the way."
                        }
                        viewModel.speakText(speech)
                    }
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BentoGreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔊", fontSize = 22.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (lang) {
                                "hi" -> "Voice Guide • आवाज़ सुनो"
                                "gu" -> "Voice Guide • અવાજ સાંભળો"
                                else -> "Voice Guide • અવાજ સાંભળો"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoMoodText
                            )
                        )
                        Text(
                            text = "Tap here to hear spoken help",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoMoodText.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Text(text = "🔈", fontSize = 22.sp)
                }
            }
        }

        // Safe Location & Home Address Bento Card — YOU ARE SAFE AT HOME (Stitch p9 gray)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFE8E9E2),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFFC9CBC2), RoundedCornerShape(28.dp))
                    .clickable {
                        val speech = when (lang) {
                            "hi" -> "आपका घर ${uiState.profile.homeAddress}, ${uiState.profile.city} में है।"
                            "gu" -> "તમારું ઘર ${uiState.profile.homeAddress}, ${uiState.profile.city}માં છે."
                            else -> "Your home address is ${uiState.profile.homeAddress}, ${uiState.profile.city}."
                        }
                        viewModel.speakText(speech)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BentoGreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📍", fontSize = 24.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOU ARE SAFE AT HOME •",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoGreenAccent,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${uiState.profile.homeAddress}, ${uiState.profile.city}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoOnBackground,
                                fontSize = if (isLargeText) 18.sp else 15.sp
                            )
                        )
                        Text(
                            text = "${uiState.profile.city}, Gujarat • શાંતિ કુટીર, નવરંગપુરા",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoOnBackground.copy(alpha = 0.65f),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
