package at.robthered.plan_me.features.common.domain.main_scaffold

sealed interface MainScaffoldStateAction {
    data class OnSetTopAppBar(val topAppBar: TopAppBarComposable?) : MainScaffoldStateAction
    data class OnSetFloatingActionButton(val floatingActionButton: FloatingActionButtonComposable) :
        MainScaffoldStateAction

    data object OnClearScaffoldElements : MainScaffoldStateAction
}