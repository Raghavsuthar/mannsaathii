package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.example.util.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    uiState: UiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.profile.language
    val appName = LocaleHelper.get("app_name", lang)

    Surface(
        color = BentoHeaderBg,
        contentColor = BentoOnBackground,
        shadowElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BentoHeaderBorder,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Branding with Icon & Role
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, BentoHeaderBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "❤️",
                            fontSize = 22.sp
                        )
                    }

                    Column {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBackground,
                                fontSize = if (uiState.profile.largeTextMode) 24.sp else 20.sp
                            )
                        )
                        Text(
                            text = when (uiState.currentRole) {
                                UserRole.PATIENT -> LocaleHelper.get("patient_mode", lang)
                                UserRole.CAREGIVER -> LocaleHelper.get("caregiver_mode", lang)
                                UserRole.CLINICIAN -> LocaleHelper.get("clinician_mode", lang)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoGreenAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                // Top Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Audio Speaking indicator / Stop Button
                    if (uiState.isSpeaking) {
                        IconButton(
                            onClick = { viewModel.stopSpeaking() },
                            modifier = Modifier
                                .testTag("stop_audio_button")
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoHelpRed)
                                .size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeOff,
                                contentDescription = "Stop Audio",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Picture Mode quick toggle
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                        modifier = Modifier
                            .height(38.dp)
                            .clickable { viewModel.togglePictureMode() }
                            .testTag("picture_mode_toggle")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (uiState.profile.pictureMode) "🖼️" else "🔤",
                                fontSize = 18.sp
                            )
                        }
                    }

                    // Language Selector Button
                    Button(
                        onClick = { viewModel.showLanguageSelector(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BentoOnBackground
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeaderBorder),
                        modifier = Modifier
                            .testTag("language_selector_button")
                            .height(38.dp)
                    ) {
                        val currentLangEnum = AppLanguage.values().find { it.code == lang } ?: AppLanguage.GUJARATI
                        Text(
                            text = "${currentLangEnum.flag} ${currentLangEnum.nativeName}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBackground
                            )
                        )
                    }

                    // Role Switcher / Menu Button
                    IconButton(
                        onClick = {
                            if (uiState.currentRole == UserRole.PATIENT) {
                                viewModel.requestSwitchRole(UserRole.CAREGIVER)
                            } else {
                                viewModel.requestSwitchRole(UserRole.PATIENT)
                            }
                        },
                        modifier = Modifier
                            .testTag("switch_role_button")
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, BentoHeaderBorder, RoundedCornerShape(14.dp))
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.currentRole == UserRole.PATIENT) Icons.Default.Lock else Icons.Default.AccountCircle,
                            contentDescription = LocaleHelper.get("switch_mode", lang),
                            tint = BentoGreenAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Medical disclaimer icon
                    IconButton(
                        onClick = { viewModel.showDisclaimer(true) },
                        modifier = Modifier
                            .testTag("disclaimer_button")
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Medical Disclaimer",
                            tint = BentoOnBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
