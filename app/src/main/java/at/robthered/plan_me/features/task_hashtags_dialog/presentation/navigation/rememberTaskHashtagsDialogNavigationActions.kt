package at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs

@Composable
fun rememberTaskHashtagsDialogNavigationActions(
    navController: NavHostController,
): TaskHashtagsDialogNavigationActions {
    return remember {
        object : TaskHashtagsDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }

            override fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs) {
                navController
                    .navigate(
                        route = Route.UpdateHashtagNameDialog(
                            hashtagId = args.hashtagId
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