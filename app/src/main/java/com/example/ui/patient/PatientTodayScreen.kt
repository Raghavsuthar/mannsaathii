package com.example.ui.patient

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    val now = remember { Date() }
    val timeFormatted = remember(lang) { SimpleDateFormat("hh:mm a", LocaleHelper.localeFor(lang)).format(now) }
    val dayFormatted = remember(lang) { SimpleDateFormat("EEEE", LocaleHelper.localeFor(lang)).format(now) }
    val dateFormatted = remember(lang) { SimpleDateFormat("dd MMMM yyyy", LocaleHelper.localeFor(lang)).format(now) }

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
        // Top Back & Title Bar — Stitch p2: Home pill + speaker circle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.setPatientScreen(PatientScreen.HOME) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BentoOnBackground
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                    modifier = Modifier.testTag("today_back_button")
                ) {
                    Text(text = "←", fontWeight = FontWeight.Black, color = BentoGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (lang) {
                            "hi" -> "Home घर"
                            "gu" -> "Home ઘર"
                            else -> "Home ઘર"
                        },
                        fontWeight = FontWeight.Black,
                        color = BentoOnBackground
                    )
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
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BentoHeaderBorder, CircleShape)
                ) {
                    Text(text = "🔊", fontSize = 22.sp)
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (lang) {
                        "hi" -> "☀️ Today आज"
                        "gu" -> "☀️ Today આજ"
                        else -> "☀️ Today આજ"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = BentoOnBackground,
                        fontSize = if (isLargeText) 28.sp else 24.sp
                    ),
                    modifier = Modifier.weight(1f, fill = false)
                )
                Surface(
                    shape = CircleShape,
                    color = BentoTodayBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoTodayBorder)
                ) {
                    Text(
                        text = when (lang) {
                            "hi" -> "Live Clock • सक्रिय"
                            "gu" -> "Live Clock • સક્રિય"
                            else -> "Live Clock • સક્રિય"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTodayText,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (lang) {
                    "hi" -> "Daily Cognitive Orientation • दैनिक दिशा-सूचन"
                    "gu" -> "Daily Cognitive Orientation • દૈનિક દિશા-સૂચન"
                    else -> "Daily Cognitive Orientation • દૈનિક દિશા-સૂચન"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BentoOnBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Audio hint banner — Stitch: green hearing icon + two-line text
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoHeaderBorder, RoundedCornerShape(20.dp))
                    .clickable {
                        viewModel.speakText(
                            when (lang) {
                                "hi" -> "किसी भी कार्ड को छूकर सुनें।"
                                "gu" -> "સાંભળવા માટે કોઈપણ કાર્ડ પર સ્પર્શ કરો."
                                else -> "Tap any card to hear it aloud."
                            }
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BentoGreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👂", fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (lang) {
                                "hi" -> "👉 Tap any card to hear it aloud"
                                "gu" -> "👉 Tap any card to hear it aloud"
                                else -> "👉 Tap any card to hear it aloud"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoGreenAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = when (lang) {
                                "hi" -> "सुनने के लिए किसी भी कार्ड को छुएं"
                                "gu" -> "સાંભળવા માટે કોઈપણ કાર્ડ પર સ્પર્શ કરો"
                                else -> "સાંભળવા માટે કોઈપણ કાર્ડ પર સ્પર્શ કરો"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoOnBackground.copy(alpha = 0.6f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            )
        }

        // 1. DAY & TIME Bento Card — CURRENT TIME • સમય
        item {
            OrientationBentoCard(
                icon = "⏰",
                label = when (lang) {
                    "hi" -> "CURRENT TIME • समय"
                    "gu" -> "CURRENT TIME • સમય"
                    else -> "CURRENT TIME • સમય"
                },
                value = timeFormatted,
                subValue = when (lang) {
                    "hi" -> "$dayFormatted • शुभ दिन"
                    "gu" -> "$dayFormatted • શુભ દિવસ"
                    else -> "$dayFormatted • શુભ દિવસ"
                },
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

        // 2. DATE Bento Card — TODAY'S DATE • આજની તારીખ
        item {
            OrientationBentoCard(
                icon = "📅",
                label = when (lang) {
                    "hi" -> "TODAY'S DATE • आज की तारीख"
                    "gu" -> "TODAY'S DATE • આજની તારીખ"
                    else -> "TODAY'S DATE • આજની તારીખ"
                },
                value = dateFormatted,
                subValue = "Year: Two Thousand Twenty-Six • ૨૦૨૬",
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

        // 3. PLACE Bento Card — WHERE YOU ARE • તમે ક્યાં છો
        item {
            OrientationBentoCard(
                icon = "📍",
                label = when (lang) {
                    "hi" -> "WHERE YOU ARE • आप कहाँ हैं"
                    "gu" -> "WHERE YOU ARE • તમે ક્યાં છો"
                    else -> "WHERE YOU ARE • તમે ક્યાં છો"
                },
                value = uiState.profile.city,
                subValue = when (lang) {
                    "hi" -> "अपने घर में, प्यार से घिरे • At home, surrounded by love"
                    "gu" -> "પોતાના ઘરમાં શાંતિથી • At home, surrounded by love"
                    else -> "At home, surrounded by love • પોતાના ઘરમાં શાંતિથી"
                },
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

        // 4. YOU ARE (Patient Identity Card) — YOUR IDENTITY • તમારી ઓળખ
        item {
            OrientationBentoCard(
                icon = "👵",
                label = when (lang) {
                    "hi" -> "YOUR IDENTITY • आपकी पहचान"
                    "gu" -> "YOUR IDENTITY • તમારી ઓળખ"
                    else -> "YOUR IDENTITY • તમારી ઓળખ"
                },
                value = "${uiState.profile.name} (${uiState.profile.preferredName})",
                subValue = when (lang) {
                    "hi" -> "उम्र ${uiState.profile.age} • प्यारी माँ और दादी"
                    "gu" -> "ઉંમર ${uiState.profile.age} • વહાલા મા અને દાદી • કમળા બા"
                    else -> "Age ${uiState.profile.age} • Loved mother & grandmother • કમળા બા"
                },
                backgroundColor = BentoPeopleBg,
                borderColor = BentoPeopleBorder,
                textColor = BentoPeopleText,
                verifiedStripText = when (lang) {
                    "hi" -> "Respected Elder of Patel Family • पटेल परिवार की मुखिया"
                    "gu" -> "Respected Elder of Patel Family • પટેલ પરિવારના મુખિયા"
                    else -> "Respected Elder of Patel Family • પટેલ પરિવારના મુખિયા"
                },
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

        // 5. CAREGIVER Card — PRIMARY CAREGIVER • સંભાળ + in-card Call button (Stitch)
        item {
            OrientationBentoCard(
                icon = "👩",
                label = when (lang) {
                    "hi" -> "PRIMARY CAREGIVER • देखभालकर्ता"
                    "gu" -> "PRIMARY CAREGIVER • સંભાળ"
                    else -> "PRIMARY CAREGIVER • સંભાળ"
                },
                value = "${uiState.profile.caregiverName} (${uiState.profile.caregiverRelationship})",
                subValue = when (lang) {
                    "hi" -> "तुम्हारे साथ ही घर में है • In the home"
                    "gu" -> "તમારી સાથે જ ઘરમાં છે • In the home"
                    else -> "તમારી સાથે જ ઘરમાં છે • In the home"
                },
                backgroundColor = BentoMoodBg,
                borderColor = BentoMoodBorder,
                textColor = BentoMoodText,
                showCallButton = true,
                callButtonText = when (lang) {
                    "hi" -> "📞 Call ${uiState.profile.caregiverName} • फोन करो"
                    "gu" -> "📞 Call ${uiState.profile.caregiverName} • ફોન કરો"
                    else -> "📞 Call ${uiState.profile.caregiverName} • ફોન કરો"
                },
                onCallClick = {
                    try {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:${uiState.profile.caregiverPhone.replace(" ", "")}")
                            )
                        )
                    } catch (e: Exception) {
                        viewModel.speakText("Calling ${uiState.profile.caregiverName}")
                    }
                },
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

        // 6. NEXT IMPORTANT EVENT Card — UPCOMING STEP • હવે પછીનું કામ
        item {
            OrientationBentoCard(
                icon = "💊",
                label = when (lang) {
                    "hi" -> "UPCOMING STEP • अगला काम"
                    "gu" -> "UPCOMING STEP • હવે પછીનું કામ"
                    else -> "UPCOMING STEP • હવે પછીનું કામ"
                },
                value = nextEventTitle,
                subValue = when (lang) {
                    "hi" -> "गर्म पानी के साथ, चाय के बाद • Take with warm water after tea"
                    "gu" -> "ચા પછી ગરમ પાણી સાથે • Take with warm water after tea"
                    else -> "Take with warm water after tea • ચા પછી ગરમ પાણી સાથે"
                },
                backgroundColor = BentoTodayBg,
                borderColor = BentoTodayBorder,
                textColor = BentoTodayText,
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

        // 7. Reassurance banner — Everything is okay • બધું બરાબર છે (Stitch p2)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoTodayBorder, RoundedCornerShape(28.dp))
                    .clickable {
                        val speech = when (lang) {
                            "hi" -> "सब कुछ ठीक है। आप सुरक्षित हैं और आपकी देखभालकर्ता पास में हैं। आराम से बैठें।"
                            "gu" -> "બધું બરાબર છે. તમે સુરક્ષિત છો અને તમારી સંભાળ રાખનાર નજીક છે. આરામથી બેસો."
                            else -> "Everything is okay. You are safe, loved, and your caregiver is nearby. Please relax."
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BentoTodayBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 24.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (lang) {
                                "hi" -> "Everything is okay • सब ठीक है"
                                "gu" -> "Everything is okay • બધું બરાબર છે"
                                else -> "Everything is okay • બધું બરાબર છે"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoOnBackground
                            )
                        )
                        Text(
                            text = when (lang) {
                                "hi" -> "You are safe & deeply loved"
                                "gu" -> "You are safe & deeply loved"
                                else -> "You are safe & deeply loved"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoTodayText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = when (lang) {
                                "hi" -> "आपकी देखभालकर्ता पास में हैं। गहरी सांस लें, चाय का आनंद लें और आराम करें। चिंता की कोई जरूरत नहीं।"
                                "gu" -> "Radha is nearby and looking after you. Take a gentle breath, enjoy your tea, and relax. આરામથી બેસો બા, ચિંતા કરવાની કોઈ જરૂર નથી."
                                else -> "Radha is nearby and looking after you. Take a gentle breath, enjoy your tea, and relax. આરામથી બેસો બા, ચિંતા કરવાની કોઈ જરૂર નથી."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoOnBackground.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // SOS footer — one-tap emergency from Today (Stitch bottom bar)
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BentoHelpRed,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BentoHelpRedBorder, RoundedCornerShape(24.dp))
                    .clickable { viewModel.setPatientScreen(PatientScreen.HELP) }
                    .testTag("today_sos_footer")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "HELP / મદદ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "तुरंत सहायता प्राप्त करें",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Surface(shape = CircleShape, color = Color.White) {
                        Text(
                            text = "SOS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoHelpRed
                            ),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                        )
                    }
                }
            }
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
    verifiedStripText: String? = null,
    showCallButton: Boolean = false,
    callButtonText: String = "",
    onCallClick: () -> Unit = {},
    onClick: () -> Unit
) {
    // New Stitch: calm white cards with hard tactile shelf + categorical accent bar.
    // backgroundColor/borderColor/textColor now drive the accent + verified strip,
    // keeping bento identity while matching the white-card orientation look.
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = BentoHeaderBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Categorical accent bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(borderColor)
                )
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 28.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground.copy(alpha = 0.55f),
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoOnBackground,
                            fontSize = 20.sp
                        )
                    )
                    if (subValue.isNotBlank()) {
                        Text(
                            text = subValue,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textColor.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoHeaderBg.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }
            }

            if (verifiedStripText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✅", fontSize = 16.sp)
                    Text(
                        text = verifiedStripText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (showCallButton) {
                Button(
                    onClick = onCallClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoGreenAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .height(56.dp)
                ) {
                    Text(
                        text = callButtonText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
