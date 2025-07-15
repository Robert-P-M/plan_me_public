package at.robthered.plan_me.features.common.presentation.remember.appSheetState

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class AppSheetState(
    val sheetState: SheetState,
    private val showConfirmationDialogState: MutableState<Boolean>,
    private val scope: CoroutineScope,
    private val onNavigateBackConfirmed: () -> Unit,
) {

    val showConfirmationDialog: Boolean by showConfirmationDialogState

    val isVisible: Boolean
        @Composable get() = sheetState.isVisible

    fun requestHide() {
        scope.launch {
            sheetState.hide()
        }
    }

    fun show() {
        scope.launch {
            sheetState.show()
        }
    }

    fun confirmDiscardAndNavigateBack() {
        showConfirmationDialogState.value = false
        onNavigateBackConfirmed()
    }

    fun cancelDiscardChanges() {
        showConfirmationDialogState.value = false
    }
}