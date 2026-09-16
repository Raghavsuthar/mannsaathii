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

    fun scheduleMedicationReminder(context: Context, medication: Medication) {
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
            MedicationReminderWorker.KEY_VOICE_PROMPT to medication.caregiverVoicePromptEn,
            MedicationReminderWorker.KEY_INSTRUCTIONS to medication.instructionsEn
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
     * Snoozes a reminder for [delayMinutes] (default 10 minutes)
     */
    fun scheduleSnoozeReminder(context: Context, medication: Medication, delayMinutes: Long = 10) {
        val inputData = workDataOf(
            MedicationReminderWorker.KEY_MED_ID to medication.id,
            MedicationReminderWorker.KEY_MED_NAME to medication.name,
            MedicationReminderWorker.KEY_MED_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_MED_TIME to medication.time,
            MedicationReminderWorker.KEY_VOICE_PROMPT to medication.caregiverVoicePromptEn,
            MedicationReminderWorker.KEY_INSTRUCTIONS to medication.instructionsEn
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
     */
    fun scheduleTestReminder(context: Context, medication: Medication, delaySeconds: Long = 3) {
        val inputData = workDataOf(
            MedicationReminderWorker.KEY_MED_ID to medication.id,
            MedicationReminderWorker.KEY_MED_NAME to medication.name,
            MedicationReminderWorker.KEY_MED_DOSAGE to medication.dosage,
            MedicationReminderWorker.KEY_MED_TIME to medication.time,
            MedicationReminderWorker.KEY_VOICE_PROMPT to medication.caregiverVoicePromptEn,
            MedicationReminderWorker.KEY_INSTRUCTIONS to medication.instructionsEn
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

    fun rescheduleAll(context: Context, medications: List<Medication>) {
        medications.forEach { med ->
            if (med.status != MedicationStatus.TAKEN) {
                scheduleMedicationReminder(context, med)
            }
        }
    }

    private fun calculateDelayToTime(timeStr: String): Long {
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
