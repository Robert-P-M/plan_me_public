package at.robthered.plan_me.features.common.presentation.helper

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun ObserveAppUiEvent(
    flow: Flow<AppUiEvent?>,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        flow
            .filterNotNull()
            .collect { event ->
                when (event) {
                    is AppUiEvent.Loading -> {
                        showSnackbarForMillis {
                            snackbarHostState
                                .showSnackbar(
                                    message = event.message.asString(context),
                                    duration = SnackbarDuration.Indefinite
                                )
                        }
                    }

                    is AppUiEvent.Success -> {
                        launch {
                            snackbarHostState
                                .showSnackbar(
                                    message = event.message.asString(context),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                )
                        }
                    }

                    is AppUiEvent.Error -> {
                        launch {
                            snackbarHostState
                                .showSnackbar(
                                    message = event.message.asString(context),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                )
                        }
                    }
                }
            }
    }
}