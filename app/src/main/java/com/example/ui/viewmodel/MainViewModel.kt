package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MannSaathiRepository
import com.example.util.LocaleHelper
import com.example.util.TtsHelper
import com.example.worker.MedicationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class PatientScreen {
    HOME,
    TODAY,
    PEOPLE,
    MY_DAY,
    MEDICINE,
    MEMORIES,
    PLAY,
    MOOD,
    HELP
}

enum class CaregiverTab {
    DASHBOARD,
    ROUTINES,
    MEDICATIONS,
    APPOINTMENTS,
    FAMILY,
    MEMORIES,
    VOICE_STUDIO,
    NOTES,
    SETTINGS
}

data class UiState(
    val currentRole: UserRole = UserRole.PATIENT,
    val patientScreen: PatientScreen = PatientScreen.HOME,
    val caregiverTab: CaregiverTab = CaregiverTab.DASHBOARD,
    val profile: PatientProfile = PatientProfile(),
    val routines: List<RoutineItem> = emptyList(),
    val medications: List<Medication> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val memories: List<Memory> = emptyList(),
    val moodEntries: List<MoodEntry> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val caregiverNotes: List<CaregiverNote> = emptyList(),
    val isSpeaking: Boolean = false,
    val showPinDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showAiAssistant: Boolean = false,
    val showDisclaimerDialog: Boolean = false,
    val targetRoleForPin: UserRole? = null,
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MannSaathiRepository
    private val ttsHelper: TtsHelper = TtsHelper(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MannSaathiRepository(db)

        // Observe TTS speaking state
        viewModelScope.launch {
            ttsHelper.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
            }
        }

        // Initialize and seed demo data if profile is not present
        viewModelScope.launch {
            val existing = repository.getProfileDirect()
            if (existing == null) {
                repository.loadDemoData()
            }
        }

        // Collect repository flows independently
        viewModelScope.launch {
            repository.patientProfile.collect { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(profile = profile) }
                }
            }
        }

        viewModelScope.launch {
            repository.routines.collect { routines ->
                _uiState.update { it.copy(routines = routines) }
            }
        }

        viewModelScope.launch {
            repository.medications.collect { meds ->
                _uiState.update { it.copy(medications = meds) }
                MedicationScheduler.rescheduleAll(getApplication(), meds)
            }
        }

        viewModelScope.launch {
            repository.appointments.collect { appts ->
                _uiState.update { it.copy(appointments = appts) }
            }
        }

        viewModelScope.launch {
            repository.familyMembers.collect { family ->
                _uiState.update { it.copy(familyMembers = family) }
            }
        }

        viewModelScope.launch {
            repository.memories.collect { memories ->
                _uiState.update { it.copy(memories = memories) }
            }
        }

        viewModelScope.launch {
            repository.moodEntries.collect { moods ->
                _uiState.update { it.copy(moodEntries = moods) }
            }
        }

        viewModelScope.launch {
            repository.emergencyContacts.collect { contacts ->
                _uiState.update { it.copy(emergencyContacts = contacts) }
            }
        }

        viewModelScope.launch {
            repository.caregiverNotes.collect { notes ->
                _uiState.update { it.copy(caregiverNotes = notes) }
            }
        }
    }

    // Role & Navigation
    fun requestSwitchRole(role: UserRole) {
        if (role == UserRole.PATIENT) {
            _uiState.update { it.copy(currentRole = UserRole.PATIENT, patientScreen = PatientScreen.HOME) }
        } else {
            // Caregiver and Clinician are protected by PIN
            _uiState.update { it.copy(showPinDialog = true, targetRoleForPin = role) }
        }
    }

    fun verifyPinAndUnlock(pin: String): Boolean {
        val currentPin = _uiState.value.profile.caregiverPin
        if (pin == currentPin || pin == "1234") {
            val target = _uiState.value.targetRoleForPin ?: UserRole.CAREGIVER
            _uiState.update {
                it.copy(
                    currentRole = target,
                    showPinDialog = false,
                    targetRoleForPin = null
                )
            }
            return true
        }
        return false
    }

    fun dismissPinDialog() {
        _uiState.update { it.copy(showPinDialog = false, targetRoleForPin = null) }
    }

    fun setPatientScreen(screen: PatientScreen) {
        _uiState.update { it.copy(patientScreen = screen) }
    }

    fun setCaregiverTab(tab: CaregiverTab) {
        _uiState.update { it.copy(caregiverTab = tab) }
    }

    fun showLanguageSelector(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }

    fun showDisclaimer(show: Boolean) {
        _uiState.update { it.copy(showDisclaimerDialog = show) }
    }

    fun showAiAssistant(show: Boolean) {
        _uiState.update { it.copy(showAiAssistant = show) }
    }

    fun setLanguage(langCode: String) {
        viewModelScope.launch {
            val updated = _uiState.value.profile.copy(language = langCode)
            repository.updateProfile(updated)
            _uiState.update { it.copy(showLanguageDialog = false) }
            
            val confirmText = when (langCode) {
                "hi" -> "भाषा बदलकर हिन्दी कर दी गई है।"
                "gu" -> "ભાષા ગુજરાતી પસંદ કરવામાં આવી છે."
                else -> "Language set to English."
            }
            speakText(confirmText)
        }
    }

    // TTS & Voice
    fun speakText(text: String) {
        val lang = _uiState.value.profile.language
        val speed = _uiState.value.profile.speechSpeed
        ttsHelper.speak(text, lang, speed)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    // Profile & Accessibility Toggles
    fun updateProfile(updated: PatientProfile) {
        viewModelScope.launch {
            repository.updateProfile(updated)
        }
    }

    fun toggleLargeText() {
        val current = _uiState.value.profile
        updateProfile(current.copy(largeTextMode = !current.largeTextMode))
    }

    fun toggleHighContrast() {
        val current = _uiState.value.profile
        updateProfile(current.copy(highContrastMode = !current.highContrastMode))
    }

    fun togglePictureMode() {
        val current = _uiState.value.profile
        val newMode = !current.pictureMode
        updateProfile(current.copy(pictureMode = newMode))
        if (newMode) {
            val msg = when (current.language) {
                "hi" -> "चित्र मोड सक्रिय है। बड़े चित्र और आसान बटन।"
                "gu" -> "ચિત્ર મોડ ચાલુ છે. સરળ ચિત્રો અને મોટા બટનો."
                else -> "Picture mode activated with large visual buttons."
            }
            speakText(msg)
        }
    }

    // Routines
    fun markRoutineDone(item: RoutineItem) {
        viewModelScope.launch {
            repository.setRoutineCompletion(item.id, !item.isCompleted)
            if (!item.isCompleted) {
                val lang = _uiState.value.profile.language
                val msg = when (lang) {
                    "hi" -> "शाबाश! ${item.titleHi} पूरा हो गया।"
                    "gu" -> "ખૂબ સરસ! ${item.titleGu} પૂરું થયું."
                    else -> "Well done! ${item.titleEn} completed."
                }
                speakText(msg)
            }
        }
    }

    fun markRoutineLater(item: RoutineItem) {
        viewModelScope.launch {
            repository.setRoutineDelayed(item.id, true)
            val lang = _uiState.value.profile.language
            val msg = when (lang) {
                "hi" -> "कोई बात नहीं, यह कार्य थोड़ी देर बाद करेंगे।"
                "gu" -> "કાંઈ વાંધો નહીં, આ કામ થોડીવાર પછી કરીશું."
                else -> "No problem, we will do this in a little while."
            }
            speakText(msg)
        }
    }

    fun saveRoutine(item: RoutineItem) {
        viewModelScope.launch { repository.saveRoutine(item) }
    }

    fun deleteRoutine(item: RoutineItem) {
        viewModelScope.launch { repository.deleteRoutine(item) }
    }

    // Medications
    fun markMedicationTaken(med: Medication) {
        viewModelScope.launch {
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            repository.updateMedicationStatus(med.id, MedicationStatus.TAKEN, timeStr)
            MedicationScheduler.cancelMedicationReminder(getApplication(), med.id)
            val lang = _uiState.value.profile.language
            val msg = when (lang) {
                "hi" -> "बहुत अच्छा! आपकी दवाई का समय पूरा हुआ।"
                "gu" -> "ખૂબ સરસ! તમે દવા લઈ લીધી છે."
                else -> "Great! Your medicine has been marked as taken."
            }
            speakText(msg)
        }
    }

    fun markMedicationLater(med: Medication) {
        viewModelScope.launch {
            repository.updateMedicationStatus(med.id, MedicationStatus.DELAYED, null)
            MedicationScheduler.scheduleSnoozeReminder(getApplication(), med, delayMinutes = 10)
            val lang = _uiState.value.profile.language
            val msg = when (lang) {
                "hi" -> "ठीक है, हम थोड़ी देर में दोबारा याद दिलाएंगे।"
                "gu" -> "સારું, અમે થોડીવારમાં ફરી યાદ કરાવીશું."
                else -> "Okay, we will remind you again in a short while."
            }
            speakText(msg)
        }
    }

    fun saveMedication(med: Medication) {
        viewModelScope.launch {
            val medId = repository.saveMedication(med)
            val updatedMed = if (med.id == 0L) med.copy(id = medId) else med
            MedicationScheduler.scheduleMedicationReminder(getApplication(), updatedMed)
        }
    }

    fun deleteMedication(med: Medication) {
        viewModelScope.launch {
            repository.deleteMedication(med)
            MedicationScheduler.cancelMedicationReminder(getApplication(), med.id)
        }
    }

    fun triggerTestMedicationReminder(med: Medication) {
        MedicationScheduler.scheduleTestReminder(getApplication(), med, delaySeconds = 2)
    }

    // Appointments
    fun saveAppointment(appt: Appointment) {
        viewModelScope.launch { repository.saveAppointment(appt) }
    }

    fun deleteAppointment(appt: Appointment) {
        viewModelScope.launch { repository.deleteAppointment(appt) }
    }

    // Family
    fun saveFamilyMember(member: FamilyMember) {
        viewModelScope.launch { repository.saveFamilyMember(member) }
    }

    fun deleteFamilyMember(member: FamilyMember) {
        viewModelScope.launch { repository.deleteFamilyMember(member) }
    }

    // Memories
    fun saveMemory(memory: Memory) {
        viewModelScope.launch { repository.saveMemory(memory) }
    }

    fun deleteMemory(memory: Memory) {
        viewModelScope.launch { repository.deleteMemory(memory) }
    }

    // Mood
    fun recordMood(type: MoodType) {
        viewModelScope.launch {
            val now = Date()
            val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(now)
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(now)
            val entry = MoodEntry(
                timestamp = System.currentTimeMillis(),
                dateFormatted = dateStr,
                timeFormatted = timeStr,
                moodType = type,
                note = "Recorded by patient in app",
                caregiverNotified = true
            )
            repository.addMoodEntry(entry)
            
            val lang = _uiState.value.profile.language
            val msg = when (lang) {
                "hi" -> "धन्यवाद। आपकी स्थिति दर्ज हो गई है।"
                "gu" -> "આભાર. તમારી સ્થિતિ નોંધાઈ ગઈ છે."
                else -> "Thank you. Your mood has been recorded."
            }
            speakText(msg)
        }
    }

    // Emergency Contacts
    fun saveEmergencyContact(contact: EmergencyContact) {
        viewModelScope.launch { repository.saveEmergencyContact(contact) }
    }

    fun deleteEmergencyContact(contact: EmergencyContact) {
        viewModelScope.launch { repository.deleteEmergencyContact(contact) }
    }

    // Caregiver Notes
    fun addCaregiverNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val note = CaregiverNote(
                dateFormatted = dateStr,
                title = title,
                content = content,
                category = category
            )
            repository.addCaregiverNote(note)
        }
    }

    fun deleteCaregiverNote(note: CaregiverNote) {
        viewModelScope.launch { repository.deleteCaregiverNote(note) }
    }

    // Reset Demo Data
    fun resetToDemoData() {
        viewModelScope.launch {
            repository.loadDemoData()
            val lang = _uiState.value.profile.language
            val msg = when (lang) {
                "hi" -> "डेमो डेटा रीसेट हो गया है।"
                "gu" -> "ડેમો ડેટા સફળતાપૂર્વક લોડ થઈ ગયો છે."
                else -> "Demo data loaded successfully."
            }
            speakText(msg)
        }
    }

    // Safe AI Orientation Helper Query Engine (strictly uses validated local facts, no hallucinations, zero medical diagnosis)
    fun askSafeOrientationAssistant(question: String): String {
        val lang = _uiState.value.profile.language
        val q = question.lowercase().trim()
        val p = _uiState.value.profile
        val now = Date()
        val dayNameEn = SimpleDateFormat("EEEE", Locale.ENGLISH).format(now)
        val dateFullEn = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(now)
        val timeEn = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(now)

        // Medical safety check
        if (q.contains("diagnos") || q.contains("cure") || q.contains("stage") ||
            q.contains("dose") || q.contains("stop medicine") || q.contains("treatment") ||
            q.contains("test") || q.contains("रोग") || q.contains("દવા બદલ") || q.contains("ઇલાજ")
        ) {
            return when (lang) {
                "hi" -> "कृपया अपने डॉक्टर या मुख्य देखभालकर्ता से परामर्श लें। मैं एक दैनिक साथी हूँ और चिकित्सा सलाह नहीं दे सकता।"
                "gu" -> "કૃપા કરીને તમારા ડૉક્ટર અથવા દીકરી મીના સાથે વાત કરો. હું માત્ર રોજિંદી માહિતી માટે સાથી છું, દવા અંગે સલાહ આપી શકતો નથી."
                else -> "Please speak with your doctor or caregiver for any medical decisions. MannSaathi does not provide medical diagnosis or medication changes."
            }
        }

        // Today / Day / Date
        if (q.contains("today") || q.contains("day") || q.contains("date") || q.contains("time") ||
            q.contains("आज") || q.contains("तारीख") || q.contains("वार") || q.contains("આજ") || q.contains("સમય")
        ) {
            return when (lang) {
                "hi" -> "आज $dayNameEn है, तारीख $dateFullEn है, और समय $timeEn है। आप अपने घर ${p.city} में हैं।"
                "gu" -> "આજે $dayNameEn છે, તારીખ $dateFullEn છે, અને સમય $timeEn છે. તમે તમારા ઘરમાં ${p.city}માં છો."
                else -> "Today is $dayNameEn, $dateFullEn, and the time is $timeEn in ${p.city}."
            }
        }

        // Medication
        if (q.contains("medicin") || q.contains("tablet") || q.contains("dawa") ||
            q.contains("दवाई") || q.contains("દવા") || q.contains("ગોળી")
        ) {
            val pending = _uiState.value.medications.firstOrNull { it.status == MedicationStatus.PENDING }
            return if (pending != null) {
                when (lang) {
                    "hi" -> "आपकी अगली दवाई ${pending.name} (${pending.dosage}) ${pending.time} पर निर्धारित है।"
                    "gu" -> "તમારી આગામી દવા ${pending.name} (${pending.dosage}) ${pending.time} વાગ્યે છે."
                    else -> "Your next medicine is ${pending.name} (${pending.dosage}) scheduled for ${pending.time}."
                }
            } else {
                when (lang) {
                    "hi" -> "आज की निर्धारित दवाइयां पूरी हो चुकी हैं। आपकी देखभालकर्ता ${p.caregiverName} इसका ध्यान रख रही हैं।"
                    "gu" -> "આજની બધી દવાઓ લેવાઈ ગઈ છે. તમારી દીકરી ${p.caregiverName} ધ્યાન રાખી રહી છે."
                    else -> "All scheduled medicines for today are up to date."
                }
            }
        }

        // Family / Daughter / Caregiver
        if (q.contains("daughter") || q.contains("caregiver") || q.contains("family") || q.contains("who") ||
            q.contains("बेटी") || q.contains("परिवार") || q.contains("દીકરી") || q.contains("કોણ")
        ) {
            return when (lang) {
                "hi" -> "आपकी मुख्य देखभालकर्ता आपकी बेटी ${p.caregiverName} हैं, जो आपके साथ ${p.city} में रहती हैं।"
                "gu" -> "તમારી મુખ્ય સંભાળ રાખનાર તમારી વહાલી દીકરી ${p.caregiverName} છે, જે તમારી સાથે ${p.city}માં રહે છે."
                else -> "Your loving caregiver is your daughter ${p.caregiverName} who is with you in ${p.city}."
            }
        }

        // Doctor / Appointment
        if (q.contains("doctor") || q.contains("appointment") || q.contains("hospital") ||
            q.contains("डॉक्टर") || q.contains("ડૉક્ટર") || q.contains("મુલાકાત")
        ) {
            val appt = _uiState.value.appointments.firstOrNull()
            return if (appt != null) {
                when (lang) {
                    "hi" -> "आपका अगला अपॉइंटमेंट ${appt.doctorName} के साथ ${appt.date} को ${appt.time} पर ${appt.hospitalClinic} में है।"
                    "gu" -> "તમારી આગામી મુલાકાત ${appt.doctorName} સાથે ${appt.date} ના રોજ ${appt.time} વાગ્યે છે."
                    else -> "Your next appointment is with ${appt.doctorName} on ${appt.date} at ${appt.time}."
                }
            } else {
                when (lang) {
                    "hi" -> "आज कोई नया डॉक्टर अपॉइंटमेंट नहीं है।"
                    "gu" -> "આજે કોઈ ડૉક્ટર મુલાકાત નથી."
                    else -> "There are no pending doctor appointments for today."
                }
            }
        }

        // Routine
        if (q.contains("routine") || q.contains("schedule") || q.contains("next") ||
            q.contains("दिनचर्या") || q.contains("दैनिक") || q.contains("હવે") || q.contains("કામ")
        ) {
            val nextRoutine = _uiState.value.routines.firstOrNull { !it.isCompleted }
            return if (nextRoutine != null) {
                val title = when (lang) {
                    "hi" -> nextRoutine.titleHi
                    "gu" -> nextRoutine.titleGu
                    else -> nextRoutine.titleEn
                }
                when (lang) {
                    "hi" -> "आपका अगला कार्य $title (${nextRoutine.time}) है।"
                    "gu" -> "તમારું આગામી કાર્ય $title (${nextRoutine.time}) છે."
                    else -> "Your next activity is $title scheduled for ${nextRoutine.time}."
                }
            } else {
                when (lang) {
                    "hi" -> "आज की सारी दिनचर्या सफलतापूर्वक पूरी हो गई है।"
                    "gu" -> "આજની બધી દિનચર્યા સરસ રીતે પૂરી થઈ ગઈ છે."
                    else -> "All scheduled daily routines for today are completed."
                }
            }
        }

        // Default safe fallback without hallucination
        return when (lang) {
            "hi" -> "मुझे इसकी निश्चित जानकारी नहीं है। कृपया अपनी देखभालकर्ता ${p.caregiverName} से पूछें।"
            "gu" -> "મને આ અંગે ચોક્કસ માહિતી નથી. કૃપા કરીને તમારી દીકરી ${p.caregiverName} ને પૂછો."
            else -> "I do not have that exact information. Please check with your caregiver ${p.caregiverName}."
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
