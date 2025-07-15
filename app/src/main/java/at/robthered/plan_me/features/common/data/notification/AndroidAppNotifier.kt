package at.robthered.plan_me.features.common.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import at.robthered.plan_me.MainActivity
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.common.domain.notification.AppNotifier
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.common.utils.ext.toTimeDigits
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

fun Context.getPriorityContainerColor(priority: PriorityEnum?): Int {
    return when (priority) {
        PriorityEnum.LOW -> getColor(R.color.priority_low_container)
        PriorityEnum.NORMAL -> getColor(R.color.priority_normal_container)
        PriorityEnum.MEDIUM -> getColor(R.color.priority_medium_container)
        PriorityEnum.HIGH -> getColor(R.color.priority_high_container)
        null -> getColor(R.color.default_notification_color)
    }
}

class AndroidAppNotifier(
    private val context: Context,
    private val notificationContentMapper: NotificationContentMapper,
) : AppNotifier {

    private val notificationManager =
        context.getSystemService<NotificationManager>()!!

    companion object {
        private const val CHANNEL_ID = "task_reminder_channel"
        private const val CHANNEL_NAME = "Task Reminders"

        const val ACTION_SHOW_TASK = "at.robthered.plan_me.ACTION_SHOW_TASK"
        const val EXTRA_TASK_ID = "extra_task_id"
    }


    override suspend fun show(task: TaskModel) {
        if (task.taskSchedule == null) return
        val content = notificationContentMapper(task = task) ?: return

        createNotificationChannel()

        val notification = buildNotification(content = content)

        notificationManager.notify(content.notificationId, notification)

    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for scheduled task reminders."
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(content: NotificationContent): Notification {

        val clickIntent = Intent(
            context,
            MainActivity::class.java
        )
            .apply {
                action = ACTION_SHOW_TASK
                putExtra(EXTRA_TASK_ID, content.taskId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val clickPendingIntent = PendingIntent.getActivity(
            context,
            content.notificationId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val title = content.title
        val description = content.description ?: "You have a scheduled task."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(context.getPriorityContainerColor(content.priorityEnum))
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)

        when (content) {
            is NotificationContent.Default -> {
            }

            is NotificationContent.WithTime -> {
                if (!content.description.isNullOrBlank()) {
                    builder.setStyle(NotificationCompat.BigTextStyle().bigText(content.description))
                }
            }

            is NotificationContent.WithDuration -> {
                val startTimeFormatted =
                    "Start: ${content.scheduleStartTime.hour.toTimeDigits()}:${content.scheduleStartTime.minute.toTimeDigits()}"
                val endTimeFormatted =
                    "Ende: ${content.scheduleEndTime.hour.toTimeDigits()}:${content.scheduleEndTime.minute.toTimeDigits()}"

                builder.setStyle(
                    NotificationCompat.InboxStyle()
                        .addLine(startTimeFormatted)
                        .addLine(endTimeFormatted)
                        .setBigContentTitle(title)
                        .setSummaryText(description)
                )
            }
        }
        return builder.build()
    }
}