package at.robthered.plan_me.features.common.domain.main_scaffold

import kotlinx.coroutines.flow.StateFlow

interface MainScaffoldStateManager {
    val state: StateFlow<MainScaffoldState>
    fun handleAction(action: MainScaffoldStateAction)
}