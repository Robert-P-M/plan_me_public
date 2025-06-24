package at.robthered.plan_me.features.update_section_title_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model.LoadUpdateSectionTitleModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleUseCase
import at.robthered.plan_me.features.data_source.domain.validation.update_section.UpdateSectionTitleModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.CanSaveUpdateSectionTitleChecker
import at.robthered.plan_me.features.update_section_title_dialog.presentation.helper.UpdateSectionTitleModelChangeChecker
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationEvent
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationEventDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateSectionTitleDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val loadUpdateSectionTitleModelUseCase: LoadUpdateSectionTitleModelUseCase,
    private val updateSectionTitleModelChangeChecker: UpdateSectionTitleModelChangeChecker,
    private val canSaveUpdateSectionTitleChecker: CanSaveUpdateSectionTitleChecker,
    private val updateSectionTitleModelValidator: UpdateSectionTitleModelValidator,
    private val updateSectionTitleUseCase: UpdateSectionTitleUseCase,
    private val useCaseOperator: UseCaseOperator,
    private val updateSectionTitleDialogNavigationEventDispatcher: UpdateSectionTitleDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
) : ViewModel() {

    private val sectionIdFromArgs =
        savedStateHandle.toRoute<Route.UpdateSectionTitleDialog>().sectionId

    val appNavigationEvent = updateSectionTitleDialogNavigationEventDispatcher
        .navigationEvents
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 0
        )


    val isLoading = appUiEventDispatcher
        .isLoading
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )


    private val sectionId: StateFlow<Long> = savedStateHandle
        .getStateFlow(
            key = SAVED_STATE_SECTION_ID,
            initialValue = sectionIdFromArgs
        )

    private val initialUpdateSectionTitleModel: MutableStateFlow<UpdateSectionTitleModel> =
        MutableStateFlow(
            UpdateSectionTitleModel(
                sectionId = sectionIdFromArgs
            )
        )

    private val _updateSectionTitleModel: MutableStateFlow<UpdateSectionTitleModel> =
        savedStateHandle.getMutableStateFlow(
            key = SAVED_STATE_EDIT_SECTION_MODEL,
            initialValue = UpdateSectionTitleModel(
                sectionId = sectionIdFromArgs
            )
        )
    val updateSectionTitleModel: StateFlow<UpdateSectionTitleModel> =
        _updateSectionTitleModel.asStateFlow()

    private val _updateSectionTitleModelError = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_EDIT_SECTION_MODEL_ERROR,
            initialValue = UpdateSectionTitleModelError()
        )

    val updateSectionTitleModelError: StateFlow<UpdateSectionTitleModelError> =
        _updateSectionTitleModelError.asStateFlow()

    private val observeEditSectionModelForError = _updateSectionTitleModel
        .onEach { currentModel ->
            _updateSectionTitleModelError.update {
                updateSectionTitleModelValidator(currentModel)
            }
        }
        .launchIn(viewModelScope)

    val canSave = updateSectionTitleModelError
        .map {
            canSaveUpdateSectionTitleChecker(updateSectionTitleModelError = it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val observeSectionId = sectionId
        .onEach { id ->
            val model = loadUpdateSectionTitleModelUseCase(sectionId = id).first()
            initialUpdateSectionTitleModel.update {
                model
            }
            _updateSectionTitleModel.update {
                model
            }
        }
        .launchIn(viewModelScope)

    val didModelChange = updateSectionTitleModel
        .map { currentModel ->
            updateSectionTitleModelChangeChecker(
                initialModel = initialUpdateSectionTitleModel.value,
                currentModel = currentModel
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun onAction(action: UpdateSectionTitleDialogUiAction) {
        when (action) {
            UpdateSectionTitleDialogUiAction.OnNavigateBack -> onNavigateBack()
            UpdateSectionTitleDialogUiAction.OnResetState -> onResetState()
            is UpdateSectionTitleDialogUiAction.OnChangeTitle -> onChangeTitle(action)
            is UpdateSectionTitleDialogUiAction.OnUpdateSection -> onUpdateSection()
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            updateSectionTitleDialogNavigationEventDispatcher.dispatch(
                event = UpdateSectionTitleDialogNavigationEvent.OnNavigateBack
            )
        }
    }

    private fun onResetState() {
        _updateSectionTitleModel.value = initialUpdateSectionTitleModel.value
    }

    private fun onChangeTitle(action: UpdateSectionTitleDialogUiAction.OnChangeTitle) {
        _updateSectionTitleModel.update {
            it.copy(
                title = action.title
            )
        }
    }

    private fun onUpdateSection() {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = UpdateSectionTitleLoadingStatus.STARTING.toUiText(),
                successMessageProvider = {
                    UpdateSectionTitleSuccessMessage.SECTION_TITLE_UPDATED.toUiText()
                },
                onSuccessAction = {
                    updateSectionTitleDialogNavigationEventDispatcher.dispatch(
                        event = UpdateSectionTitleDialogNavigationEvent.OnNavigateBack
                    )
                }
            ) {
                updateSectionTitleUseCase(updateSectionTitleModel = _updateSectionTitleModel.value)
            }
        }
    }


    companion object {
        const val SAVED_STATE_SECTION_ID = "SAVED_STATE_SECTION_ID"
        const val SAVED_STATE_EDIT_SECTION_MODEL = "SAVED_STATE_EDIT_SECTION_MODEL"
        const val SAVED_STATE_EDIT_SECTION_MODEL_ERROR = "SAVED_STATE_EDIT_SECTION_MODEL_ERROR"
    }
}