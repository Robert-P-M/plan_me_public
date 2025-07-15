package at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberPriorityPickerDialogNavigationActions(
    navController: NavHostController,
): PriorityPickerDialogNavigationActions {
    return remember {
        object : PriorityPickerDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}