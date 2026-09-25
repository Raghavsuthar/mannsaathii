package com.example.worker

import android.content.Context
import androidx.work.*
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object MedicationScheduler {

    private const val WORK_NAME_PREFIX = "med_reminder_"

    /** Set on one-shot scheduled reminders so the worker chains the next day. */
    const val KEY_REPEATING = "KEY_REPEATING"

    /** Voice prompt in the patient's display language. */
    fun voicePromptFor(medication: Medication, lang: String): String {
        return when (lang.lowercase()) {
            "hi" -> medication.caregiverVoicePromptHi.ifBlank { medication.caregiverVoicePromptEn }
            "gu" -> medication.caregiverVoicePromptGu.ifBlank { medication.caregiverVoicePromptEn }
            else -> medication.caregiverVoicePromptEn
        }
    }

    /** Dosage instructions in the patient's display language. */
    fun instructionsFor(medication: Medication, lang: String): String {
        return when (lang.lowercase()) {
            "hi" -> medication.instructionsHi.ifBlank { medication.instructionsEn }
            "gu" -> medication.instructionsGu.ifBlank { medication.instructionsEn }
            else -> medication.instructionsEn
        }
    }

    /**
     * Pure diff between the previously scheduled list and the current list.
     * Only new/changed/removed medications produce work — status flips and
     * edits no longer reset every timer in the app.
     */
    data class SyncPlan(
        val toSchedule: List<Medication>,
        val toCancel: List<Long>
    )

    fun diffReminders(old: List<Medication>, new: List<Medication>): SyncPlan {
        val oldById = old.associateBy { it.id }
        val newById = new.associateBy { it.id }

        val toSchedule = new.filter { med ->
            if (med.status == MedicationStatus.TAKEN) return@filter false
            val prev = oldById[med.id]
            prev == null ||
                prev.status != med.status ||
                prev.time != med.time ||
                prev.name != med.name ||
                prev.dosage != med.dosage
        }
        val toCancel = newById.values
            .filter { it.status == MedicationStatus.TAKEN && oldById[it.id]?.status != MedicationStatus.TAKEN }
            .map { it.id } + (oldById.keys - newById.keys)
        return SyncPlan(toSchedule, toCancel.distinct())
    }

    /**
     * Applies [diffReminders] against WorkManager. Call with the last synced
     * snapshot and the fresh list; returns the new snapshot to store.
     */
    fun syncReminders(
        context: Context,
        old: List<Medication>,
        new: List<Medication>,
        lang: String
    ): List<Medication> {
        val plan = diffReminders(old, new)
        plan.toSchedule.forEach { scheduleMedicationReminder(context, it, lang) }
        plan.toCancel.forEach { cancelMedicationReminder(context, it) }
        return new
    }

    fun scheduleMedicationReminder(context: Context, medication: Medication, lang: String = "en") {
        // If already taken, no need to schedule today's reminder
        if (medication.status == MedicationStatus.TAKEN) {
            cancelMedicationReminder(context, medication.id)
            return
        }

        val initialDelayMs = calculateDelayToTime(medication.time)

        val inputData = workDataOf(
            MedicationReminderWorker.KEY_MED_ID to medication.id,
            MedicationReminderWorker.KEY_MED_NAME to medication.name,
            MedicationReminderWorker.KEY_MED_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_MED_TIME to medication.time,
            MedicationReminderWorker.KEY_VOICE_PROMPT to voicePromptFor(medication, lang),
            MedicationReminderWorker.KEY_INSTRUCTIONS to instructionsFor(medication, lang),
            KEY_REPEATING to true
        )

        val workRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("medication_reminder")
            .addTag("medication_${medication.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "$WORK_NAME_PREFIX${medication.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Chains the next daily occurrence after a reminder fires, reusing the
     * worker's own input. Called from [MedicationReminderWorker] so doses
     * keep reminding every day without any app restart.
     */
    fun scheduleNextDay(context: Context, inputData: Data) {
        val time = inputData.getString(MedicationReminderWorker.KEY_MED_TIME) ?: return
        val medId = inputData.getLong(MedicationReminderWorker.KEY_MED_ID, -1L)
        if (medId == -1L) return

        val workRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInitialDelay(calculateDelayToTime(time), TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("medication_reminder")
            .addTag("medication_$medId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "$WORK_NAME_PREFIX$medId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Snoozes a reminder for [delayMinutes] (default 10 minutes).
     * Snoozes never chain into daily repeats.
     */
    fun scheduleSnoozeReminder(
        context: Context,
        medication: Medication,
        lang: String = "en",
        delayMinutes: Long = 10
    ) {
        val inputData = workDataOf(
            MedicationReminderWorker.KEY_MED_ID to medication.id,
            MedicationReminderWorker.KEY_MED_NAME to medication.name,
            MedicationReminderWorker.KEY_MED_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_MED_TIME to medication.time,
            MedicationReminderWorker.KEY_VOICE_PROMPT to voicePromptFor(medication, lang),
            MedicationReminderWorker.KEY_INSTRUCTIONS to instructionsFor(medication, lang),
            KEY_REPEATING to false
        )

        val workRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag("medication_reminder")
            .addTag("medication_snooze_${medication.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "$WORK_NAME_PREFIX${medication.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Immediately triggers a test reminder within [delaySeconds] (default 3 seconds)
     * using WorkManager, allowing immediate verification of the notification pipeline.
     * Test reminders never chain into daily repeats.
     */
    fun scheduleTestReminder(
        context: Context,
        medication: Medication,
        lang: String = "en",
        delaySeconds: Long = 3
    ) {
        val inputData = workDataOf(
            MedicationReminderWorker.KEY_MED_ID to medication.id,
            MedicationReminderWorker.KEY_MED_NAME to medication.name,
            MedicationReminderWorker.KEY_MED_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_MED_TIME to medication.time,
            MedicationReminderWorker.KEY_VOICE_PROMPT to voicePromptFor(medication, lang),
            MedicationReminderWorker.KEY_INSTRUCTIONS to instructionsFor(medication, lang),
            KEY_REPEATING to false
        )

        val workRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag("medication_reminder_test")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "test_med_reminder_${medication.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelMedicationReminder(context: Context, medicationId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("$WORK_NAME_PREFIX$medicationId")
        WorkManager.getInstance(context).cancelUniqueWork("test_med_reminder_$medicationId")
    }

    fun rescheduleAll(context: Context, medications: List<Medication>, lang: String = "en") {
        medications.forEach { med ->
            if (med.status != MedicationStatus.TAKEN) {
                scheduleMedicationReminder(context, med, lang)
            }
        }
    }

    internal fun calculateDelayToTime(timeStr: String): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance()

        var parsed = false
        val formats = listOf(
            SimpleDateFormat("hh:mm a", Locale.US),
            SimpleDateFormat("h:mm a", Locale.US),
            SimpleDateFormat("HH:mm", Locale.US),
            SimpleDateFormat("H:mm", Locale.US)
        )

        for (format in formats) {
            try {
                val parsedDate = format.parse(timeStr.trim())
                if (parsedDate != null) {
                    val calTemp = Calendar.getInstance().apply { time = parsedDate }
                    target.set(Calendar.HOUR_OF_DAY, calTemp.get(Calendar.HOUR_OF_DAY))
                    target.set(Calendar.MINUTE, calTemp.get(Calendar.MINUTE))
                    target.set(Calendar.SECOND, 0)
                    target.set(Calendar.MILLISECOND, 0)
                    parsed = true
                    break
                }
            } catch (e: Exception) {
                // Try next pattern
            }
        }

        if (!parsed) {
            // Default: 1 hour from now
            return TimeUnit.HOURS.toMillis(1)
        }

        // If scheduled time has already passed today, schedule for tomorrow
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val diff = target.timeInMillis - now.timeInMillis
        return if (diff > 0) diff else TimeUnit.MINUTES.toMillis(1)
    }
}
