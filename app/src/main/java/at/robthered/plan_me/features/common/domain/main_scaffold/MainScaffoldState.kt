package at.robthered.plan_me.features.common.domain.main_scaffold

import androidx.compose.runtime.Composable

data class MainScaffoldState(
    val topAppBar: TopAppBarComposable? = null,
    val floatingActionButton: FloatingActionButtonComposable? = null,
)
typealias TopAppBarComposable = @Composable () -> Unit
typealias FloatingActionButtonComposable = @Composable () -> Unit