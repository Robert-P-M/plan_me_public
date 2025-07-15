package at.robthered.plan_me

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import at.robthered.plan_me.features.common.data.notification.AndroidAppNotifier
import at.robthered.plan_me.features.common.presentation.AppScaffold
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.navigation.MainNavigation
import at.robthered.plan_me.features.common.presentation.remember.rememberAppScaffoldStateManager
import at.robthered.plan_me.features.ui.presentation.theme.PlanMeTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private var initialTaskId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            KoinContext {
                PlanMeTheme {
                    val navController = rememberNavController()
                    val appScaffoldStateManager = rememberAppScaffoldStateManager()
                    val appUiEventDispatcher: AppUiEventDispatcher =
                        koinInject<AppUiEventDispatcher>()
                    AppScaffold(
                        appUiEvent = appUiEventDispatcher.appUiEvent,
                        scaffoldState = appScaffoldStateManager
                    ) {
                        MainNavigation(
                            navController = navController,
                            initialTaskId = initialTaskId,
                            scaffoldState = appScaffoldStateManager,
                            onInitialTaskHandled = { initialTaskId = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == AndroidAppNotifier.ACTION_SHOW_TASK) {
            val taskId = intent.getLongExtra(AndroidAppNotifier.EXTRA_TASK_ID, -1L)
            if (taskId != -1L) {
                initialTaskId = taskId
            }
        }
    }
}