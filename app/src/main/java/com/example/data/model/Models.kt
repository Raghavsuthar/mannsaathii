package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val flag: String) {
    ENGLISH("en", "English", "English", "🇬🇧"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "🇮🇳")
}

enum class UserRole {
    PATIENT,
    CAREGIVER,
    CLINICIAN
}

enum class PeriodOfDay(val defaultName: String, val iconEmoji: String) {
    MORNING("Morning", "🌅"),
    AFTERNOON("Afternoon", "☀️"),
    EVENING("Evening", "🌆"),
    NIGHT("Night", "🌙")
}

enum class MedicationStatus {
    PENDING,
    TAKEN,
    DELAYED,
    SKIPPED
}

enum class MoodType(val emoji: String, val colorHex: Long, val labelEn: String) {
    HAPPY("😀", 0xFF2E7D32, "Happy & Peaceful"),
    OKAY("🙂", 0xFF00897B, "Calm & Stable"),
    NOT_GOOD("😐", 0xFFF57C00, "A Bit Restless"),
    SAD("😢", 0xFF1565C0, "Low Mood"),
    TIRED("😴", 0xFF7B1FA2, "Fatigued / Sleepy"),
    ANGRY("😡", 0xFFC62828, "Agitated / Upset")
}

@Entity(tableName = "patient_profile")
data class PatientProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Kamla Ben",
    val preferredName: String = "Ba",
    val age: Int = 72,
    val stage: String = "Mild Cognitive Impairment",
    val homeAddress: String = "42, Shanti Kutir, Navrangpura",
    val city: String = "Ahmedabad",
    val caregiverName: String = "Meena",
    val caregiverRelationship: String = "Daughter",
    val caregiverPhone: String = "+91 98765 43210",
    val doctorName: String = "Dr. Rajesh Patel",
    val doctorPhone: String = "+91 98123 45678",
    val language: String = "gu",
    val caregiverPin: String = "1234",
    val isDemoMode: Boolean = true,
    val largeTextMode: Boolean = false,
    val extraLargeTextMode: Boolean = false,
    val highContrastMode: Boolean = false,
    val pictureMode: Boolean = false,
    val speechSpeed: Float = 0.85f, // Slightly slower for elderly
    val voiceAssistanceEnabled: Boolean = true
)

@Entity(tableName = "routine_items")
data class RoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleEn: String,
    val titleHi: String,
    val titleGu: String,
    val time: String, // e.g. "07:30 AM"
    val period: String, // MORNING, AFTERNOON, EVENING, NIGHT
    val iconEmoji: String,
    val isCompleted: Boolean = false,
    val isDelayed: Boolean = false,
    val caregiverVoiceTextEn: String = "",
    val caregiverVoiceTextHi: String = "",
    val caregiverVoiceTextGu: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val time: String, // "08:00 AM"
    val period: String = "MORNING", // MORNING, AFTERNOON, EVENING, NIGHT
    val instructionsEn: String,
    val instructionsHi: String,
    val instructionsGu: String,
    val status: MedicationStatus = MedicationStatus.PENDING,
    val takenTime: String? = null,
    val iconEmoji: String = "💊",
    val caregiverVoicePromptEn: String = "Ba, it is time for your morning medicine.",
    val caregiverVoicePromptHi: String = "बा, आपकी सुबह की दवाई का समय हो गया है।",
    val caregiverVoicePromptGu: String = "બા, તમારી સવારની દવાનો સમય થઈ ગયો છે."
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val doctorName: String,
    val specialty: String,
    val hospitalClinic: String,
    val date: String, // "Today", "22 August 2026", etc.
    val time: String, // "05:00 PM"
    val purposeEn: String,
    val purposeHi: String,
    val purposeGu: String,
    val location: String,
    val notes: String = "Routine checkup and cognitive status review",
    val iconEmoji: String = "👩‍⚕️"
)

@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationshipEn: String,
    val relationshipHi: String,
    val relationshipGu: String,
    val phone: String,
    val iconEmoji: String = "👩",
    val photoColorHex: Long = 0xFF00695C,
    val introEn: String,
    val introHi: String,
    val introGu: String,
    val sortPriority: Int = 1
)

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleEn: String,
    val titleHi: String,
    val titleGu: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val descriptionGu: String,
    val yearOrEra: String,
    val location: String,
    val iconEmoji: String = "🌸",
    val photoColorHex: Long = 0xFF9A5B00,
    val promptEn: String = "Do you remember this beautiful day?",
    val promptHi: String = "क्या आपको यह सुंदर दिन याद है?",
    val promptGu: String = "શું તમને આ સુંદર દિવસ યાદ છે?"
)

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String, // "22 Aug"
    val timeFormatted: String, // "10:30 AM"
    val moodType: MoodType,
    val note: String = "",
    val caregiverNotified: Boolean = true
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationshipEn: String,
    val relationshipHi: String,
    val relationshipGu: String,
    val phone: String,
    val priority: Int,
    val iconEmoji: String = "📞"
) {
    val isPriority: Boolean get() = priority <= 1
}

@Entity(tableName = "caregiver_notes")
data class CaregiverNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val title: String,
    val content: String,
    val category: String = "Observation" // Observation, Mood, Medication, Routine
)
