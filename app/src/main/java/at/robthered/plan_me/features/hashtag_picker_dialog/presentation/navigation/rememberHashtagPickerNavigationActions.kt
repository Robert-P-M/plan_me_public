package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

@Composable
fun rememberHashtagPickerNavigationActions(
    navController: NavHostController,
): HashtagPickerDialogNavigationActions {
    return remember {
        object : HashtagPickerDialogNavigationActions {
            override fun onNavigateBack() {
                navController.navigateUp()
            }
        }
    }
}