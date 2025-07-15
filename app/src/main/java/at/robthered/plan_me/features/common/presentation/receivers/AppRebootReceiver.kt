package at.robthered.plan_me.features.common.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import at.robthered.plan_me.features.common.domain.use_case.RescheduleAlarmsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRebootReceiver(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BroadcastReceiver(), KoinComponent {
    private val rescheduleAlarmsUseCase: RescheduleAlarmsUseCase by inject()
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action == null) return

        val isBoot = intent.action == Intent.ACTION_BOOT_COMPLETED
        if (isBoot) {

            val pendingResult = goAsync()
            CoroutineScope(dispatcher).launch {
                try {
                    rescheduleAlarmsUseCase()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}