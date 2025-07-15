package at.robthered.plan_me.features.task_statistics_dialog.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.task_statistics_dialog.presentation.composables.TaskStatisticsItemCard
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationActions
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationEvent
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskStatisticsDialogRoot(
    modifier: Modifier = Modifier,
    taskStatisticsNavigationActions: TaskStatisticsDialogNavigationActions,
    taskStatisticsDialogViewModel: TaskStatisticsDialogViewModel = koinViewModel<TaskStatisticsDialogViewModel>(),
) {

    val taskStatisticsResource: AppResource<List<TaskStatisticsModel>> by taskStatisticsDialogViewModel.taskStatisticsResource.collectAsStateWithLifecycle()
    val isLoading by taskStatisticsDialogViewModel.isLoading.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = taskStatisticsDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            TaskStatisticsDialogNavigationEvent
                .OnNavigateBack,
                ->
                taskStatisticsNavigationActions
                    .onNavigateBack()

            is TaskStatisticsDialogNavigationEvent
            .OnNavigateToTaskDetailsDialog,
                ->
                taskStatisticsNavigationActions
                    .onNavigateToTaskDetailsDialog(
                        args = event.args
                    )

            is TaskStatisticsDialogNavigationEvent.OnNavigateToHashtagTasksDialog ->
                taskStatisticsNavigationActions
                    .onNavigateToHashtagTasksDialog(
                        args = event.args
                    )
        }
    }

    TaskStatisticsDialog(
        modifier = modifier,
        isLoading = isLoading,
        taskStatisticsResource = taskStatisticsResource,
        onAction = taskStatisticsDialogViewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TaskStatisticsDialog(
    modifier: Modifier = Modifier,
    onAction: (action: TaskStatisticsDialogUiAction) -> Unit,
    isLoading: Boolean,
    taskStatisticsResource: AppResource<List<TaskStatisticsModel>>,
) {


    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = false,
        onNavigateBack = {
            onAction(
                TaskStatisticsDialogUiAction.OnNavigateBack
            )
        }
    )




    ModalBottomSheet(
        modifier = modifier,
        sheetState = appSheetState.sheetState,
        onDismissRequest = {
            appSheetState.requestHide()
        },
        dragHandle = null,
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier,
            state = listState,
        ) {
            item {
                AppModalTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.task_statistics_dialog_modal_title),
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                appSheetState.requestHide()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
            when (val result = taskStatisticsResource) {
                is AppResource.Error -> Unit

                is AppResource.Loading -> {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                AppResource.Stale -> Unit

                is AppResource.Success -> {
                    items(
                        items = result.data,
                        key = { item -> item.hashCode() }) { taskStatisticsItem ->
                        TaskStatisticsItemCard(
                            taskStatisticsModel = taskStatisticsItem,
                            isLoading = isLoading,
                            onAction = onAction
                        )
                    }
                }
            }

        }


    }
}