package at.robthered.plan_me.features.add_task_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs

@Composable
fun rememberAddTaskDialogNavigationActions(
    navController: NavHostController,
): AddTaskDialogNavigationActions {
    return remember {
        object : AddTaskDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }

            override fun onNavigateToPriorityPickerDialog() {
                navController.navigate(
                    Route.PriorityPickerDialog
                )
            }

            override fun onNavigateToHashtagPickerDialog() {
                navController.navigate(
                    route = Route.HashtagPickerDialog
                )
            }

            override fun onNavigateToTaskSchedulePickerDialog(args: TaskSchedulePickerDialogArgs) {
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