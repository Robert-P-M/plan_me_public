package at.robthered.plan_me.features.move_task_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberMoveTaskDialogNavigationActions(
    navController: NavHostController,
): MoveTaskDialogNavigationActions {
    return remember {
        object : MoveTaskDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}