package at.robthered.plan_me.features.common.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import at.robthered.plan_me.features.add_section_dialog.presentation.AddSectionDialogRoot
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.rememberAddSectionDialogNavigationActions
import at.robthered.plan_me.features.add_task_dialog.presentation.AddTaskDialogRoot
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.rememberAddTaskDialogNavigationActions
import at.robthered.plan_me.features.common.presentation.AppScaffoldStateManagerImpl
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.HashtagPickerDialogRoot
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.rememberHashtagPickerNavigationActions
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.HashtagTasksDialogRoot
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.rememberHashtagTasksDialogNavigationActions
import at.robthered.plan_me.features.inbox_screen.presentation.InboxScreenRoot
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.rememberInboxScreenNavigationActions
import at.robthered.plan_me.features.move_task_dialog.presentation.MoveTaskDialogRoot
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.rememberMoveTaskDialogNavigationActions
import at.robthered.plan_me.features.priority_picker_dialog.presentation.PriorityPickerDialogRoot
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.rememberPriorityPickerDialogNavigationActions
import at.robthered.plan_me.features.task_details_dialog.presentation.TaskDetailsDialogRoot
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.rememberTaskDetailsDialogNavigationActions
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.TaskHashtagsDialogRoot
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.rememberTaskHashtagsDialogNavigationActions
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.TaskSchedulePickerDialogRoot
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.rememberTaskSchedulePickerNavigationActions
import at.robthered.plan_me.features.task_statistics_dialog.presentation.TaskStatisticsDialogRoot
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.rememberTaskStatisticsNavigationActions
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.UpdateHashtagNameDialogRoot
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.rememberUpdateHashtagNameDialogNavigationActions
import at.robthered.plan_me.features.update_section_title_dialog.presentation.UpdateSectionTitleDialogRoot
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.rememberUpdateSectionTitleDialogNavigationActions
import at.robthered.plan_me.features.update_task_dialog.presentation.UpdateTaskDialogRoot
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.rememberUpdateTaskDialogNavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavHostController,
    scaffoldState: AppScaffoldStateManagerImpl,
    initialTaskId: Long?,
    onInitialTaskHandled: () -> Unit,
) {

    LaunchedEffect(
        key1 = initialTaskId
    ) {
        if (initialTaskId != null) {
            navController
                .navigate(
                    route = Route.TaskDetailsDialog(
                        taskId = initialTaskId
                    )
                )

            onInitialTaskHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.InboxScreen
    ) {
        composable<Route.InboxScreen> {

            val inboxScreenNavigationActions = rememberInboxScreenNavigationActions(
                navController = navController
            )
            InboxScreenRoot(
                inboxScreenNavigationActions = inboxScreenNavigationActions,
                scaffoldState = scaffoldState,
            )
        }

        dialog<Route.AddTaskDialog> {

            val addTaskDialogNavigationActions = rememberAddTaskDialogNavigationActions(
                navController = navController,
            )

            AddTaskDialogRoot(
                addTaskDialogNavigationActions = addTaskDialogNavigationActions,
            )
        }

        dialog<Route.PriorityPickerDialog> {

            val priorityPickerDialogNavigationActions =
                rememberPriorityPickerDialogNavigationActions(
                    navController = navController
                )

            PriorityPickerDialogRoot(
                priorityPickerDialogNavigationActions = priorityPickerDialogNavigationActions,
            )


        }

        dialog<Route.UpdateTaskDialog> {

            val updateTaskDialogNavigationActions = rememberUpdateTaskDialogNavigationActions(
                navController = navController
            )
            UpdateTaskDialogRoot(
                updateTaskDialogNavigationActions = updateTaskDialogNavigationActions
            )
        }

        dialog<Route.AddSectionDialog> {

            val addSectionDialogNavigationActions = rememberAddSectionDialogNavigationActions(
                navController = navController
            )

            AddSectionDialogRoot(
                addSectionDialogNavigationActions = addSectionDialogNavigationActions,
            )
        }

        dialog<Route.UpdateSectionTitleDialog> {

            val updateSectionTitleDialogNavigationActions =
                rememberUpdateSectionTitleDialogNavigationActions(
                    navController = navController
                )
            UpdateSectionTitleDialogRoot(
                updateSectionTitleDialogNavigationActions = updateSectionTitleDialogNavigationActions,
            )
        }


        dialog<Route.TaskDetailsDialog> {

            val taskDetailsDialogNavigationActions = rememberTaskDetailsDialogNavigationActions(
                navController = navController
            )
            TaskDetailsDialogRoot(
                taskDetailsDialogNavigationActions = taskDetailsDialogNavigationActions,
            )
        }

        dialog<Route.TaskStatisticsDialog> {

            val taskStatisticsNavigationActions = rememberTaskStatisticsNavigationActions(
                navController = navController
            )

            TaskStatisticsDialogRoot(
                taskStatisticsNavigationActions = taskStatisticsNavigationActions,
            )
        }
        dialog<Route.HashtagPickerDialog> {

            val hashtagNavigationActions = rememberHashtagPickerNavigationActions(
                navController = navController
            )

            HashtagPickerDialogRoot(
                hashtagPickerDialogNavigationActions = hashtagNavigationActions,
            )
        }

        dialog<Route.UpdateHashtagNameDialog> {

            val updateHashtagNameDialogNavigationActions =
                rememberUpdateHashtagNameDialogNavigationActions(
                    navController = navController
                )

            UpdateHashtagNameDialogRoot(
                updateHashtagNameDialogNavigationActions = updateHashtagNameDialogNavigationActions,
            )
        }

        dialog<Route.TaskHashtagsDialog> {
            val taskHashtagsNavigationActions =
                rememberTaskHashtagsDialogNavigationActions(
                    navController = navController
                )

            TaskHashtagsDialogRoot(
                taskHashtagsNavigationActions = taskHashtagsNavigationActions,
            )
        }

        dialog<Route.HashtagTasksDialog> {

            val hashtagTasksDialogNavigationActions = rememberHashtagTasksDialogNavigationActions(
                navController = navController
            )

            HashtagTasksDialogRoot(
                hashtagTasksDialogNavigationActions = hashtagTasksDialogNavigationActions,
            )

        }

        dialog<Route.MoveTaskDialog> {

            val moveTaskDialogNavigationActions = rememberMoveTaskDialogNavigationActions(
                navController = navController
            )

            MoveTaskDialogRoot(
                moveTaskDialogNavigationActions = moveTaskDialogNavigationActions,
            )
        }

        dialog<Route.TaskSchedulePickerDialog> {
            val taskScheduleNavigationActions = rememberTaskSchedulePickerNavigationActions(
                navController = navController
            )

            TaskSchedulePickerDialogRoot(
                taskSchedulePickerNavigationActions = taskScheduleNavigationActions,
            )
        }
    }
}