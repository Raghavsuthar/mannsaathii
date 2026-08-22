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
                    modifier = Modifier.testTag("help_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", fontWeight = FontWeight.Black, color = BentoOnBackground)
                }

                IconButton(
                    onClick = {
                        val speech = when (lang) {
                            "hi" -> "घबराएं नहीं। मदद के लिए नीचे दिए किसी भी बटन को दबाएं या अपनी बेटी मीना को फोन करें।"
                            "gu" -> "ચિંતા ન કરો. મદદ માટે નીચે આપેલ કોઈપણ બટન દબાવો અથવા તમારી દીકરી મીનાને ફોન કરો."
                            else -> "Stay calm. Tap any button below to connect immediately with your family or doctor."
                        }
                        viewModel.speakText(speech)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoHelpRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak Help Guidance",
                        tint = Color.White
                    )
                }
            }
        }

        // Title Header
        item {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = BentoHelpRed,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoHelpRedDark, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🆘 " + LocaleHelper.get("help_title", lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = if (isLargeText) 26.sp else 22.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = LocaleHelper.get("help_subtitle", lang),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Primary Caregiver Giant Call Button
        item {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = BentoPeopleBg,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoPeopleBorder, RoundedCornerShape(32.dp))
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
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👩", fontSize = 34.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${LocaleHelper.get("call_caregiver", lang)} (${uiState.profile.caregiverName})",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoPeopleText,
                                fontSize = if (isLargeText) 22.sp else 18.sp
                            )
                        )
                        Text(
                            text = uiState.profile.caregiverPhone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoPeopleText.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${uiState.profile.caregiverPhone.replace(" ", "")}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.speakText("Calling ${uiState.profile.caregiverName}")
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = BentoGreenAccent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Caregiver",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Emergency Contacts List
        items(uiState.emergencyContacts, key = { it.id }) { contact ->
            val relationship = when (lang) {
                "hi" -> contact.relationshipHi
                "gu" -> contact.relationshipGu
                else -> contact.relationshipEn
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = if (contact.isPriority) BentoMedicineBg else BentoMyDayBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = if (contact.isPriority) BentoMedicineBorder else BentoMyDayBorder,
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
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = contact.iconEmoji, fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoOnBackground,
                                fontSize = if (isLargeText) 20.sp else 16.sp
                            )
                        )
                        Text(
                            text = "$relationship • ${contact.phone}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoOnBackground.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone.replace(" ", "")}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.speakText("Calling ${contact.name}")
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = BentoGreenAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call Contact")
                    }
                }
            }
        }

        // Safe Location & Home Address Bento Card
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BentoTodayBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, BentoTodayBorder, RoundedCornerShape(28.dp))
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏡", fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SAFE HOME ADDRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoTodayText.copy(alpha = 0.75f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${uiState.profile.homeAddress}, ${uiState.profile.city}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoTodayText,
                                fontSize = if (isLargeText) 18.sp else 15.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak Address",
                        tint = BentoTodayText
                    )
                }
            }
        }
    }
}
