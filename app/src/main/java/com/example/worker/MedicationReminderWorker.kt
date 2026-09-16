package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.model.MedicationStatus
import com.example.util.MedicationNotificationHelper

class MedicationReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_MED_ID = "KEY_MED_ID"
        const val KEY_MED_NAME = "KEY_MED_NAME"
        const val KEY_MED_DOSAGE = "KEY_MED_DOSAGE"
        const val KEY_MED_TIME = "KEY_MED_TIME"
        const val KEY_VOICE_PROMPT = "KEY_VOICE_PROMPT"
        const val KEY_INSTRUCTIONS = "KEY_INSTRUCTIONS"
    }

    override suspend fun doWork(): Result {
        val medId = inputData.getLong(KEY_MED_ID, -1L)
        if (medId == -1L) return Result.failure()

        val medName = inputData.getString(KEY_MED_NAME) ?: "Medicine"
        val dosage = inputData.getString(KEY_MED_DOSAGE) ?: ""
        val time = inputData.getString(KEY_MED_TIME) ?: ""
        val voicePrompt = inputData.getString(KEY_VOICE_PROMPT) ?: ""
        val instructions = inputData.getString(KEY_INSTRUCTIONS) ?: ""

        // Check if medication is already TAKEN in the local Room database
        try {
            val db = AppDatabase.getDatabase(applicationContext)
            val currentMed = db.medicationDao().getMedicationById(medId)
            if (currentMed != null && currentMed.status == MedicationStatus.TAKEN) {
                // Already taken, dismiss/skip notification
                return Result.success()
            }
        } catch (e: Exception) {
            // If DB query fails, still proceed to notify the patient for safety
        }

        // Trigger notification
        MedicationNotificationHelper.showMedicationNotification(
            context = applicationContext,
            medicationId = medId,
            medicationName = medName,
            dosage = dosage,
            scheduledTime = time,
            voicePrompt = voicePrompt,
            instructions = instructions
        )

        return Result.success()
    }
}
