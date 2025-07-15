package at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs

@Composable
fun rememberTaskStatisticsNavigationActions(
    navController: NavHostController,
): TaskStatisticsDialogNavigationActions {
    return remember {
        object : TaskStatisticsDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }

            override fun onNavigateToTaskDetailsDialog(args: TaskDetailsDialogArgs) {
                navController
                    .navigate(
                        route = Route.TaskDetailsDialog(
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
        }
    }
}