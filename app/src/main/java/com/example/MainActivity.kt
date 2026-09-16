package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.caregiver.CaregiverMainScreen
import com.example.ui.caregiver.ClinicianReviewScreen
import com.example.ui.components.*
import com.example.ui.patient.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PatientScreen
import com.example.util.MedicationNotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MedicationNotificationHelper.createNotificationChannel(this)
        com.example.util.SecurityHelper.evaluateEnvironment(applicationContext)
        handleNotificationIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(highContrast = uiState.profile.highContrastMode) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentRole to uiState.patientScreen,
                            label = "MainScreenTransition"
                        ) { (role, pScreen) ->
                            when (role) {
                                UserRole.PATIENT -> {
                                    when (pScreen) {
                                        PatientScreen.HOME -> PatientHomeScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.TODAY -> PatientTodayScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.PEOPLE -> PatientPeopleScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.MY_DAY -> PatientMyDayScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.MEDICINE -> PatientMedicationScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.MEMORIES -> PatientMemoriesScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.PLAY -> PatientGamesScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.MOOD -> PatientMoodScreen(uiState = uiState, viewModel = viewModel)
                                        PatientScreen.HELP -> PatientHelpScreen(uiState = uiState, viewModel = viewModel)
                                    }
                                }
                                UserRole.CAREGIVER -> {
                                    CaregiverMainScreen(uiState = uiState, viewModel = viewModel)
                                }
                                UserRole.CLINICIAN -> {
                                    ClinicianReviewScreen(uiState = uiState, viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Dialogs
                    PinDialog(uiState = uiState, viewModel = viewModel)
                    LanguageDialog(uiState = uiState, viewModel = viewModel)
                    MedicalDisclaimerDialog(uiState = uiState, viewModel = viewModel)
                    SafeAiAssistantDialog(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val destination = intent?.getStringExtra(MedicationNotificationHelper.EXTRA_DESTINATION)
        if (destination == MedicationNotificationHelper.DESTINATION_MEDICINE) {
            viewModel.requestSwitchRole(UserRole.PATIENT)
            viewModel.setPatientScreen(PatientScreen.MEDICINE)
        }
    }
}
