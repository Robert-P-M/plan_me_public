package at.robthered.plan_me.features.common.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import at.robthered.plan_me.features.common.data.notification.AppAlarmSchedulerImpl
import at.robthered.plan_me.features.common.domain.use_case.ShowNotificationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppAlarmReceiver(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BroadcastReceiver(), KoinComponent {
    private val showNotificationUseCase: ShowNotificationUseCase by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val taskId = intent.getLongExtra(AppAlarmSchedulerImpl.PENDING_INTENT_TASK_ID, -1L)
        if (taskId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(dispatcher).launch {
            try {
                showNotificationUseCase(taskId = taskId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}