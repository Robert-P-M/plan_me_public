package at.robthered.plan_me.features.common.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.features.common.presentation.composables.AppSnackbar
import at.robthered.plan_me.features.common.presentation.helper.ObserveAppUiEvent
import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun AppScaffold(
    scaffoldState: AppScaffoldStateManagerImpl,
    appUiEvent: Flow<AppUiEvent?>,
    content: @Composable () -> Unit,
) {

    val state by scaffoldState.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAppUiEvent(
        flow = appUiEvent,
        snackbarHostState = snackbarHostState,
    )

    val isTopAppBarVisible by remember {
        derivedStateOf {
            state.topAppBar != null
        }
    }

    val isFloatingActionButtonVisible by remember {
        derivedStateOf {
            state.floatingActionButton != null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = isTopAppBarVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.topAppBar?.invoke()
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFloatingActionButtonVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.floatingActionButton?.invoke()
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { snackbarData ->
                AppSnackbar(snackbarData = snackbarData)
            }
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                content()
            }
        }
    )
}