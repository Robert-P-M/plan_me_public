package at.robthered.plan_me.features.inbox_screen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateSectionTitleDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

@Composable
fun rememberInboxScreenNavigationActions(
    navController: NavHostController,
): InboxScreenNavigationActions {
    return remember {
        object : InboxScreenNavigationActions {
            override fun onNavigateToAddTaskDialog(
                args: AddTaskDialogArgs,
            ) {
                navController.navigate(
                    route = Route.AddTaskDialog(
                        parentTaskId = args.parentTaskId,
                        sectionId = args.sectionId,
                    )
                )
            }

            override fun onNavigateToUpdateTaskDialog(
                args: UpdateTaskDialogArgs,
            ) {
                navController.navigate(
                    route = Route.UpdateTaskDialog(
                        taskId = args.taskId
                    )
                )
            }

            override fun onNavigateToAddSectionDialog() {
                navController.navigate(
                    route = Route.AddSectionDialog
                )
            }

            override fun onNavigateToUpdateSectionTitleDialog(
                args: UpdateSectionTitleDialogArgs,
            ) {
                navController.navigate(
                    route = Route.UpdateSectionTitleDialog(
                        sectionId = args.sectionId
                    )
                )
            }

            override fun onNavigateToPriorityPickerDialog() {
                navController.navigate(
                    route = Route.PriorityPickerDialog
                )
            }

            override fun onNavigateToTaskDetailsDialog(
                args: TaskDetailsDialogArgs,
            ) {
                navController.navigate(
                    route = Route.TaskDetailsDialog(
                        taskId = args.taskId
                    )
                )
            }

            override fun onNavigateToTaskStatisticsDialog(args: TaskStatisticsDialogArgs) {
                navController
                    .navigate(
                        route = Route.TaskStatisticsDialog(
                            taskId = args.taskId
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

        }
    }
}