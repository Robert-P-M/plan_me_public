package at.robthered.plan_me.features.update_task_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import at.robthered.plan_me.features.common.presentation.navigation.Route

@Composable
fun rememberUpdateTaskDialogNavigationActions(
    navController: NavHostController,
): UpdateTaskDialogNavigationActions {
    return remember {
        object : UpdateTaskDialogNavigationActions {
            override fun onNavigateBack() {
                navController
                    .navigateUp()
            }

            override fun onNavigateToPriorityPickerDialog() {
                navController
                    .navigate(
                        route = Route.PriorityPickerDialog
                    )
            }
        }
    }
}