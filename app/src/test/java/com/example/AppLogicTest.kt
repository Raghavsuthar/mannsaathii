package com.example

import com.example.data.model.Appointment
import com.example.data.model.CaregiverNote
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import com.example.data.model.MoodEntry
import com.example.data.model.MoodType
import com.example.data.model.PatientProfile
import com.example.data.model.RoutineItem
import com.example.util.ExportHelper
import com.example.util.LocaleHelper
import com.example.worker.MedicationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the safety-critical reminder logic: language-correct prompts,
 * scoped re-scheduling, locale mapping, and the offline care export.
 * Pure JVM — no device or emulator required.
 */
class AppLogicTest {

    private fun med(
        id: Long,
        status: MedicationStatus = MedicationStatus.PENDING,
        time: String = "08:00 AM"
    ) = Medication(
        id = id,
        name = "TestMed$id",
        dosage = "1 pill",
        time = time,
        period = "MORNING",
        instructionsEn = "Take with water",
        instructionsHi = "पानी के साथ लें",
        instructionsGu = "પાણી સાથે લો",
        status = status,
        caregiverVoicePromptEn = "Time for your medicine",
        caregiverVoicePromptHi = "दवाई का समय",
        caregiverVoicePromptGu = "દવાનો સમય"
    )

    // --- Language-correct reminder content ---

    @Test
    fun `voice prompt follows gujarati profile language`() {
        assertEquals("દવાનો સમય", MedicationScheduler.voicePromptFor(med(1), "gu"))
        assertEquals("પાણી સાથે લો", MedicationScheduler.instructionsFor(med(1), "gu"))
    }

    @Test
    fun `voice prompt follows hindi profile language`() {
        assertEquals("दवाई का समय", MedicationScheduler.voicePromptFor(med(1), "hi"))
        assertEquals("पानी के साथ लें", MedicationScheduler.instructionsFor(med(1), "hi"))
    }

    @Test
    fun `voice prompt falls back to english for unknown language`() {
        assertEquals("Time for your medicine", MedicationScheduler.voicePromptFor(med(1), "fr"))
        assertEquals("Take with water", MedicationScheduler.instructionsFor(med(1), "fr"))
    }

    @Test
    fun `blank regional prompt falls back to english`() {
        val m = med(1).copy(caregiverVoicePromptGu = "", instructionsGu = "")
        assertEquals("Time for your medicine", MedicationScheduler.voicePromptFor(m, "gu"))
        assertEquals("Take with water", MedicationScheduler.instructionsFor(m, "gu"))
    }

    // --- Scoped re-scheduling ---

    @Test
    fun `unchanged list produces empty sync plan`() {
        val list = listOf(med(1), med(2))
        val plan = MedicationScheduler.diffReminders(list, list.map { it.copy() })
        assertTrue(plan.toSchedule.isEmpty())
        assertTrue(plan.toCancel.isEmpty())
    }

    @Test
    fun `new medication is scheduled, taken one is cancelled`() {
        val old = listOf(med(1))
        val new = listOf(med(1), med(2), med(3, MedicationStatus.TAKEN))
        val plan = MedicationScheduler.diffReminders(old, new)
        assertEquals(listOf(2L), plan.toSchedule.map { it.id })
        assertEquals(listOf(3L), plan.toCancel)
    }

    @Test
    fun `edited dose time reschedules only that dose`() {
        val old = listOf(med(1), med(2))
        val new = listOf(med(1), med(2, time = "09:30 PM"))
        val plan = MedicationScheduler.diffReminders(old, new)
        assertEquals(listOf(2L), plan.toSchedule.map { it.id })
        assertTrue(plan.toCancel.isEmpty())
    }

    @Test
    fun `marking taken cancels reminder without rescheduling others`() {
        val old = listOf(med(1), med(2))
        val new = listOf(med(1), med(2, MedicationStatus.TAKEN))
        val plan = MedicationScheduler.diffReminders(old, new)
        assertTrue(plan.toSchedule.isEmpty())
        assertEquals(listOf(2L), plan.toCancel)
    }

    @Test
    fun `deleted medication cancels its reminder`() {
        val plan = MedicationScheduler.diffReminders(listOf(med(1), med(9)), listOf(med(1)))
        assertTrue(plan.toSchedule.isEmpty())
        assertEquals(listOf(9L), plan.toCancel)
    }

    // --- Locale mapping ---

    @Test
    fun `locale follows app language not device default`() {
        assertEquals("gu", LocaleHelper.localeFor("gu").language)
        assertEquals("hi", LocaleHelper.localeFor("hi").language)
        assertEquals("en", LocaleHelper.localeFor("en").language)
        assertEquals("en", LocaleHelper.localeFor("unknown").language)
    }

    // --- Offline care export ---

    @Test
    fun `care summary contains all sections`() {
        val summary = ExportHelper.buildCareSummary(
            profile = PatientProfile(),
            medications = listOf(med(1, MedicationStatus.TAKEN)),
            routines = listOf(
                RoutineItem(titleEn = "Morning tea", titleHi = "चाय", titleGu = "ચા", time = "07:30 AM", period = "MORNING", iconEmoji = "🍵", isCompleted = true)
            ),
            moods = listOf(
                MoodEntry(dateFormatted = "25 Sep", timeFormatted = "10:00 AM", moodType = MoodType.HAPPY)
            ),
            appointments = listOf(
                Appointment(doctorName = "Dr. Shah", specialty = "Neurologist", hospitalClinic = "Sterling", date = "26 Sep", time = "05:00 PM", purposeEn = "Review", purposeHi = "", purposeGu = "", location = "")
            ),
            notes = listOf(
                CaregiverNote(dateFormatted = "25 Sep 2026", title = "Sleep", content = "Slept well")
            )
        )
        assertTrue(summary.contains("Kamla Ben"))
        assertTrue(summary.contains("TestMed1"))
        assertTrue(summary.contains("Morning tea"))
        assertTrue(summary.contains("HAPPY"))
        assertTrue(summary.contains("Dr. Shah"))
        assertTrue(summary.contains("Slept well"))
        assertTrue(summary.contains("does not diagnose"))
    }

    @Test
    fun `care summary handles empty state`() {
        val summary = ExportHelper.buildCareSummary(
            profile = PatientProfile(),
            medications = emptyList(),
            routines = emptyList(),
            moods = emptyList(),
            appointments = emptyList(),
            notes = emptyList()
        )
        assertTrue(summary.contains("(none recorded)"))
    }
}
