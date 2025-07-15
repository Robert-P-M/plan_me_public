package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberUpdateHashtagNameDialogNavigationActions(
    navController: NavHostController,
): UpdateHashtagNameDialogNavigationActions {
    return remember {
        object : UpdateHashtagNameDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}