package at.robthered.plan_me.features.common.presentation.remember

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import at.robthered.plan_me.features.common.presentation.AppScaffoldStateManagerImpl

@Composable
fun rememberAppScaffoldStateManager(): AppScaffoldStateManagerImpl {

    val scope = rememberCoroutineScope()
    return AppScaffoldStateManagerImpl(scope)
}