package at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberUpdateSectionTitleDialogNavigationActions(
    navController: NavHostController,
): UpdateSectionTitleDialogNavigationActions {
    return remember {
        object : UpdateSectionTitleDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}