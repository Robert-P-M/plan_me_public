package at.robthered.plan_me.features.add_section_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberAddSectionDialogNavigationActions(
    navController: NavHostController,
): AddSectionDialogNavigationActions {
    return remember {
        object : AddSectionDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}