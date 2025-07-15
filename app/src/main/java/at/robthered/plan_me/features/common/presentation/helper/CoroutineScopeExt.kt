package at.robthered.plan_me.features.common.presentation.helper

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

fun CoroutineScope.showSnackbarForMillis(
    durationMillis: Long = 1000L,
    execute: suspend CoroutineScope.() -> Unit,
) {
    if (durationMillis <= 0) {
        launch {
            execute()
        }
        return
    }
    val displayJob = launch {
        try {
            execute()
        } catch (e: CancellationException) {

            Log.d("CoroutineScope.showSnackbarForMillis", "Snackbar scope was canceled / aborted.")
        }
    }
    launch {
        delay(durationMillis)
        if (displayJob.isActive) {
            displayJob.cancel()
        }
    }
}