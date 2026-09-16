package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.data.model.Medication
import com.example.data.model.MedicationStatus
import com.example.util.MedicationNotificationHelper
import com.example.worker.MedicationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MedicationSchedulerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun `notification channel is created properly`() {
        MedicationNotificationHelper.createNotificationChannel(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(MedicationNotificationHelper.CHANNEL_ID)
        assertNotNull(channel)
        assertEquals(MedicationNotificationHelper.CHANNEL_NAME, channel.name)
    }

    @Test
    fun `schedule medication reminder enqueues WorkManager job`() {
        val testMed = Medication(
            id = 42L,
            name = "Metformin",
            dosage = "500mg",
            time = "08:00 AM",
            period = "MORNING",
            instructionsEn = "Take after breakfast with water",
            instructionsHi = "नाश्ते के बाद पानी के साथ लें",
            instructionsGu = "નાસ્તા પછી પાણી સાથે લો",
            status = MedicationStatus.PENDING
        )

        MedicationScheduler.scheduleMedicationReminder(context, testMed)

        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork("med_reminder_42")
            .get()

        assertEquals(1, workInfos.size)
        val info = workInfos.first()
        assertEquals(WorkInfo.State.ENQUEUED, info.state)
    }

    @Test
    fun `schedule snooze reminder enqueues WorkManager snooze job`() {
        val testMed = Medication(
            id = 43L,
            name = "Donepezil",
            dosage = "5mg",
            time = "09:00 PM",
            period = "NIGHT",
            instructionsEn = "Take before bedtime",
            instructionsHi = "सोने से पहले लें",
            instructionsGu = "સૂતા પહેલાં લો",
            status = MedicationStatus.DELAYED
        )

        MedicationScheduler.scheduleSnoozeReminder(context, testMed, delayMinutes = 10)

        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork("med_reminder_43")
            .get()

        assertEquals(1, workInfos.size)
        assertEquals(WorkInfo.State.ENQUEUED, workInfos.first().state)
    }

    @Test
    fun `cancelling medication reminder cancels WorkManager job`() {
        val testMed = Medication(
            id = 44L,
            name = "Amlodipine",
            dosage = "5mg",
            time = "10:00 AM",
            period = "MORNING",
            instructionsEn = "Take morning",
            instructionsHi = "सुबह लें",
            instructionsGu = "સવારે લો",
            status = MedicationStatus.PENDING
        )

        MedicationScheduler.scheduleMedicationReminder(context, testMed)
        MedicationScheduler.cancelMedicationReminder(context, 44L)

        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork("med_reminder_44")
            .get()

        assertEquals(1, workInfos.size)
        assertEquals(WorkInfo.State.CANCELLED, workInfos.first().state)
    }
}
