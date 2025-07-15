package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberTaskSchedulePickerNavigationActions(
    navController: NavHostController,
): TaskSchedulePickerNavigationActions {
    return remember {
        object : TaskSchedulePickerNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}