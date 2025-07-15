package at.robthered.plan_me.features.common.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.presentation.receivers.AppAlarmReceiver
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppAlarmSchedulerImplTest {

    private lateinit var context: Context
    private lateinit var appAlarmScheduler: AppAlarmScheduler

    /**
     * Sets up the test environment before each test, initializing a fresh context
     * and a new instance of the AppAlarmScheduler.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val appLogger = mockk<AppLogger>(relaxed = true)
        appAlarmScheduler = AppAlarmSchedulerImpl(context, appLogger)
    }

    /**
     * Helper function to check if a PendingIntent for a given notificationId exists.
     * It creates an Intent that perfectly matches the one in the production code
     * (including the data URI and extras) and uses FLAG_NO_CREATE to only check for existence.
     *
     * @param notificationId The unique ID used as the requestCode for the PendingIntent.
     * @param taskId The task ID included as an extra in the Intent.
     * @return True if a matching PendingIntent exists, false otherwise.
     */
    private fun doesPendingIntentExist(notificationId: Int, taskId: Long): Boolean {
        val intent = Intent(context, AppAlarmReceiver::class.java).apply {
            data = "plan-me://alarm/$notificationId".toUri()
            putExtra(AppAlarmSchedulerImpl.PENDING_INTENT_TASK_ID, taskId)
        }

        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            flags
        )
        return pendingIntent != null
    }

    /**
     * GIVEN a trigger time set in the future.
     * WHEN schedule() is called.
     * THEN a corresponding PendingIntent should exist in the system, indicating the alarm is set.
     */
    @Test
    fun schedule_whenTimeIsInFuture_schedulesAlarmSuccessfully() {
        // GIVEN
        val notificationId = 1
        val taskId = 123L
        val triggerAtMillis = System.currentTimeMillis() + 60 * 1000
        // Ensure the alarm does not exist before scheduling
        assertThat(doesPendingIntentExist(notificationId, taskId)).isFalse()

        // WHEN
        appAlarmScheduler.schedule(notificationId, taskId, triggerAtMillis)

        // THEN
        assertThat(doesPendingIntentExist(notificationId, taskId)).isTrue()

        // Cleanup to avoid leaving alarms on the test device
        appAlarmScheduler.cancel(notificationId, taskId)
    }

    /**
     * GIVEN a trigger time set in the past.
     * WHEN schedule() is called.
     * THEN no PendingIntent should be created, as the scheduler should exit early.
     */
    @Test
    fun schedule_whenTimeIsInPast_doesNotScheduleAlarm() {
        // GIVEN
        val notificationId = 2
        val taskId = 456L
        val triggerAtMillis = System.currentTimeMillis() - 60 * 1000

        // WHEN
        appAlarmScheduler.schedule(notificationId, taskId, triggerAtMillis)

        // THEN
        assertThat(doesPendingIntentExist(notificationId, taskId)).isFalse()
    }

    /**
     * GIVEN an alarm has been successfully scheduled and its PendingIntent exists.
     * WHEN cancel() is called with the same identifiers.
     * THEN the corresponding PendingIntent should no longer be findable in the system.
     */
    @Test
    fun cancel_whenAlarmExists_cancelsAlarmSuccessfully() {
        // GIVEN
        val notificationId = 3
        val taskId = 789L
        val triggerAtMillis = System.currentTimeMillis() + 60 * 1000
        appAlarmScheduler.schedule(notificationId, taskId, triggerAtMillis)
        // Verify that the alarm exists before trying to cancel it
        assertThat(doesPendingIntentExist(notificationId, taskId)).isTrue()

        // WHEN
        appAlarmScheduler.cancel(notificationId, taskId)

        // THEN
        // Verify that the alarm has been successfully removed and the PendingIntent is no longer findable.
        assertThat(doesPendingIntentExist(notificationId, taskId)).isFalse()
    }
}