package com.example.util

import com.example.data.model.Appointment
import com.example.data.model.CaregiverNote
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import com.example.data.model.MoodEntry
import com.example.data.model.PatientProfile
import com.example.data.model.RoutineItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Builds a plain-text care summary for sharing with family members or
 * clinicians (e.g. via a share sheet). Pure function so the content is
 * unit-testable and stable across locales — the clinical summary is always
 * generated in English regardless of the patient display language.
 *
 * This is the offline answer to device loss / doctor visits: the family can
 * export the full care state without any account or cloud service.
 */
object ExportHelper {

    fun buildCareSummary(
        profile: PatientProfile,
        medications: List<Medication>,
        routines: List<RoutineItem>,
        moods: List<MoodEntry>,
        appointments: List<Appointment>,
        notes: List<CaregiverNote>,
        generatedAt: Date = Date()
    ): String {
        val stamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(generatedAt)
        val sb = StringBuilder()
        sb.appendLine("MannSaathi Care Summary — $stamp")
        sb.appendLine("=".repeat(40))
        sb.appendLine("Patient: ${profile.name} (${profile.preferredName}), ${profile.age}y")
        sb.appendLine("Stage: ${profile.stage}")
        sb.appendLine("Home: ${profile.homeAddress}, ${profile.city}")
        sb.appendLine("Caregiver: ${profile.caregiverName} (${profile.caregiverRelationship}) ${profile.caregiverPhone}")
        sb.appendLine("Doctor: ${profile.doctorName} ${profile.doctorPhone}")
        sb.appendLine()

        val taken = medications.count { it.status == MedicationStatus.TAKEN }
        sb.appendLine("Medications ($taken/${medications.size} taken):")
        if (medications.isEmpty()) {
            sb.appendLine("- (none recorded)")
        } else {
            medications.forEach { med ->
                val takenAt = med.takenTime?.let { " at $it" } ?: ""
                sb.appendLine("- ${med.name} ${med.dosage} @ ${med.time} [${med.status}]$takenAt")
            }
        }
        sb.appendLine()

        val done = routines.count { it.isCompleted }
        sb.appendLine("Routine ($done/${routines.size} completed):")
        if (routines.isEmpty()) {
            sb.appendLine("- (none recorded)")
        } else {
            routines.forEach { r ->
                val mark = if (r.isCompleted) "x" else " "
                sb.appendLine("[$mark] ${r.time} ${r.titleEn}")
            }
        }
        sb.appendLine()

        sb.appendLine("Recent mood (${moods.size} entries):")
        moods.takeLast(7).forEach { m ->
            val note = if (m.note.isNotBlank()) " — ${m.note}" else ""
            sb.appendLine("- ${m.dateFormatted} ${m.timeFormatted}: ${m.moodType}$note")
        }
        if (moods.isEmpty()) sb.appendLine("- (none recorded)")
        sb.appendLine()

        sb.appendLine("Upcoming appointments:")
        if (appointments.isEmpty()) {
            sb.appendLine("- (none recorded)")
        } else {
            appointments.forEach { a ->
                sb.appendLine("- ${a.date} ${a.time}: ${a.doctorName} (${a.specialty}), ${a.hospitalClinic}")
            }
        }
        sb.appendLine()

        sb.appendLine("Caregiver notes (${notes.size}):")
        notes.takeLast(10).forEach { n ->
            sb.appendLine("- [${n.dateFormatted}] ${n.title}: ${n.content}")
        }
        if (notes.isEmpty()) sb.appendLine("- (none recorded)")
        sb.appendLine()
        sb.appendLine("Note: family-recorded routine data for clinical review only. MannSaathi does not diagnose or prescribe.")
        return sb.toString()
    }
}
