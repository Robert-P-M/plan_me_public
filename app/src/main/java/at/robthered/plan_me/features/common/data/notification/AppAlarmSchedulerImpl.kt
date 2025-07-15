package at.robthered.plan_me.features.common.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.presentation.receivers.AppAlarmReceiver
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import kotlinx.datetime.Clock


class AppAlarmSchedulerImpl(
    private val context: Context,
    private val appLogger: AppLogger,
) : AppAlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(notificationId: Int, taskId: Long, triggerAtMillis: Long) {

        try {
            val triggerAtMillis = triggerAtMillis

            if (triggerAtMillis < Clock.System.now().toEpochMilliseconds()) {
                return
            }

            val pendingIntent = createPendingIntent(
                notificationId = notificationId,
                taskId = taskId
            )



            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            appLogger.e("AppAlarmSchedulerImpl", "Missing permission for exact alarms.", e)
        }

    }

    override fun cancel(notificationId: Int, taskId: Long) {
        val pendingIntent = createPendingIntent(
            notificationId = notificationId,
            taskId = taskId
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * Eine private Hilfsfunktion, um die Erstellung von PendingIntents zu zentralisieren.
     * Stellt sicher, dass für schedule und cancel der gleiche Intent erzeugt wird.
     *
     * @param notificationId Wird als eindeutiger requestCode für den PendingIntent verwendet.
     * @param taskId Wird als Extra in den Intent gepackt, damit der AlarmReceiver weiß, welcher Task gemeint ist.
     */
    private fun createPendingIntent(notificationId: Int, taskId: Long): PendingIntent {
        val intent = Intent(context, AppAlarmReceiver::class.java).apply {
            data = "plan-me://alarm/$notificationId".toUri()
            putExtra(PENDING_INTENT_TASK_ID, taskId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            flags
        )
    }

    override fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    companion object {
        const val PENDING_INTENT_TASK_ID = "PENDING_INTENT_TASK_ID"
    }

}