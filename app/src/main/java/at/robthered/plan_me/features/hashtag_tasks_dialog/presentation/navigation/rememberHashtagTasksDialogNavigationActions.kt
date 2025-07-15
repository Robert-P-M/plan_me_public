package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

@Composable
fun rememberHashtagTasksDialogNavigationActions(
    navController: NavHostController,
): HashtagTasksDialogNavigationActions {
    return remember {
        object : HashtagTasksDialogNavigationActions {
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

            override fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs) {
                navController
                    .navigate(
                        route = Route.UpdateHashtagNameDialog(
                            hashtagId = args.hashtagId
                        )
                    )
            }

            override fun onNavigateToUpdateTaskDialog(args: UpdateTaskDialogArgs) {
                navController
                    .navigate(
                        route = Route.UpdateTaskDialog(
                            taskId = args.taskId
                        )
                    )
            }
        }
    }
}