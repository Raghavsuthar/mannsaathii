package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object MedicationNotificationHelper {

    const val CHANNEL_ID = "medication_reminders_channel"
    const val CHANNEL_NAME = "Medication Reminders"
    const val CHANNEL_DESCRIPTION = "Gentle alerts and spoken reminders for scheduled medications"

    const val EXTRA_DESTINATION = "EXTRA_DESTINATION"
    const val EXTRA_MED_ID = "EXTRA_MED_ID"
    const val DESTINATION_MEDICINE = "MEDICINE"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showMedicationNotification(
        context: Context,
        medicationId: Long,
        medicationName: String,
        dosage: String,
        scheduledTime: String,
        voicePrompt: String,
        instructions: String
    ) {
        createNotificationChannel(context)

        // Verify POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, cannot post system notification
                return
            }
        }

        // Tap action: open MainActivity directly to the Medicine screen
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, DESTINATION_MEDICINE)
            putExtra(EXTRA_MED_ID, medicationId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicationId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "💊 Time for $medicationName ($dosage)"
        val contentText = if (voicePrompt.isNotBlank()) {
            "\"$voicePrompt\" • Scheduled: $scheduledTime"
        } else if (instructions.isNotBlank()) {
            "$instructions • Scheduled: $scheduledTime"
        } else {
            "Please take $dosage of $medicationName scheduled for $scheduledTime"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        NotificationManagerCompat.from(context).notify(medicationId.toInt(), notification)
    }

    fun cancelNotification(context: Context, medicationId: Long) {
        NotificationManagerCompat.from(context).cancel(medicationId.toInt())
    }
}
