package at.robthered.plan_me.features.task_details_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

@Composable
fun rememberTaskDetailsDialogNavigationActions(
    navController: NavHostController,
): TaskDetailsDialogNavigationActions {
    return remember {
        object : TaskDetailsDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }

            override fun onNavigateToTaskDetailsDialog(taskId: Long) {
                navController.navigate(
                    route = Route.TaskDetailsDialog(
                        taskId = taskId
                    )
                )
            }

            override fun onNavigateToUpdateTaskDialog(args: UpdateTaskDialogArgs) {
                navController.navigate(
                    route = Route.UpdateTaskDialog(
                        taskId = args.taskId
                    )
                )
            }

            override fun onNavigateToSectionDetails(sectionId: Long) {
                // TODO: Navigate to Section Details
            }

            override fun onNavigateToAddTaskDialog(args: AddTaskDialogArgs) {
                navController.navigate(
                    route = Route.AddTaskDialog(
                        parentTaskId = args.parentTaskId,
                    )
                )
            }

            override fun onNavigateToPriorityPickerDialog() {
                navController.navigate(
                    route = Route.PriorityPickerDialog
                )
            }

            override fun onNavigateToTaskStatisticsDialog(args: TaskStatisticsDialogArgs) {
                navController.navigate(
                    route = Route.TaskStatisticsDialog(
                        taskId = args.taskId
                    )
                )
            }

            override fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs) {
                navController
                    .navigate(
                        route = Route.UpdateHashtagNameDialog(
                            hashtagId = args.hashtagId
                        )
                    )
            }

            override fun onNavigateToTaskHashtagsDialog(args: TaskHashtagsDialogArgs) {
                navController
                    .navigate(
                        route = Route.TaskHashtagsDialog(
                            taskId = args.taskId
                        )
                    )
            }

            override fun onNavigateToHashtagTasksDialog(args: HashtagTasksDialogArgs) {
                navController
                    .navigate(
                        route = Route.HashtagTasksDialog(
                            hashtagId = args.hashtagId
                        )
                    )
            }

            override fun onNavigateToMoveTaskDialog(args: MoveTaskDialogArgs) {
                navController
                    .navigate(
                        route = Route.MoveTaskDialog(
                            taskId = args.taskId,
                            parentTaskId = args.parentTaskId,
                            sectionId = args.sectionId
                        )
                    )
            }

            override fun onNavigateToTaskScheduleEventDialog(args: TaskSchedulePickerDialogArgs) {
                navController
                    .navigate(
                        route = Route.TaskSchedulePickerDialog(
                            taskId = args.taskId,
                            startDateInEpochDays = args.startDateInEpochDays,
                            timeOfDayInMinutes = args.timeOfDayInMinutes,
                            isNotificationEnabled = args.isNotificationEnabled,
                            durationInMinutes = args.durationInMinutes,
                            isFullDay = args.isFullDay,
                        )
                    )
            }

        }
    }
}