package at.robthered.plan_me.features.common.presentation.remember.appSheetState

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppSheetState(
    shouldShowConfirmationDialog: Boolean,
    onNavigateBack: () -> Unit,
    skipPartiallyExpanded: Boolean = true,
): AppSheetState {
    val scope = rememberCoroutineScope()

    val showConfirmationDialog = remember { mutableStateOf(false) }

    val currentDidModelChange by rememberUpdatedState(shouldShowConfirmationDialog)
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    val materialSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { targetSheetValue ->
            if (targetSheetValue == SheetValue.Hidden) {
                if (currentDidModelChange) {
                    showConfirmationDialog.value = true
                    false
                } else {
                    currentOnNavigateBack()
                    true
                }
            } else {
                true
            }
        }
    )


    val appSheetState = remember(
        materialSheetState,
        showConfirmationDialog,
        scope,
        currentOnNavigateBack
    ) {
        AppSheetState(
            sheetState = materialSheetState,
            showConfirmationDialogState = showConfirmationDialog,
            scope = scope,
            onNavigateBackConfirmed = currentOnNavigateBack
        )
    }

    BackHandler(enabled = appSheetState.isVisible) {
        appSheetState.requestHide()
    }

    return appSheetState
}