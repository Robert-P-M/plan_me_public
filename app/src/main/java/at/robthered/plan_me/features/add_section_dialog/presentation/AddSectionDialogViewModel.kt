package at.robthered.plan_me.features.add_section_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.AddSectionModelChangeChecker
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.CanSaveAddSectionChecker
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationEvent
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationEventDispatcher
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionUseCase
import at.robthered.plan_me.features.data_source.domain.validation.add_section.AddSectionModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AddSectionDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val addSectionModelValidator: AddSectionModelValidator,
    private val addSectionModelChangeChecker: AddSectionModelChangeChecker,
    private val canSaveAddSectionChecker: CanSaveAddSectionChecker,
    private val addSectionUseCase: AddSectionUseCase,
    private val addSectionDialogNavigationEventDispatcher: AddSectionDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val useCaseOperator: UseCaseOperator,
) : ViewModel() {

    private val lazyAddSectionModel by lazy {
        AddSectionModel()
    }

    private val lazyAddSectionModelError by lazy {
        AddSectionModelError()
    }

    val appNavigationEvent = addSectionDialogNavigationEventDispatcher
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


    private val _addSectionModel =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_ADD_SECTION_MODEL,
                initialValue = lazyAddSectionModel
            )

    val addSectionModel: StateFlow<AddSectionModel> =
        _addSectionModel.asStateFlow()

    private val _addSectionModelError = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_ADD_SECTION_MODEL_ERROR,
            initialValue = lazyAddSectionModelError
        )
    val addSectionModelError: StateFlow<AddSectionModelError> =
        _addSectionModelError.asStateFlow()

    private val observeAddSectionModelForError = _addSectionModel
        .onEach { currentModel ->
            _addSectionModelError.update {
                addSectionModelValidator(currentModel)
            }
        }
        .launchIn(viewModelScope)

    val canSave = addSectionModelError
        .map {
            canSaveAddSectionChecker(addSectionModelError = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val didModelChange = addSectionModel
        .map { currentModel ->
            addSectionModelChangeChecker(
                initialModel = AddSectionModel(),
                currentModel = currentModel
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun onAction(action: AddSectionDialogAction) {
        when (action) {
            is AddSectionDialogAction.OnChangeTitle -> onChangeTitle(action)
            AddSectionDialogAction.OnResetState -> Unit
            AddSectionDialogAction.OnSaveSection -> onSaveSection()
            AddSectionDialogAction.OnNavigateBack -> onNavigateBack()
        }
    }

    private fun onChangeTitle(action: AddSectionDialogAction.OnChangeTitle) {
        _addSectionModel.update { it.copy(title = action.title) }
    }


    private fun onNavigateBack() {
        viewModelScope.launch {
            addSectionDialogNavigationEventDispatcher.dispatch(
                event = AddSectionDialogNavigationEvent.OnNavigateBack
            )
        }
    }

    private fun onSaveSection() {
        viewModelScope
            .launch {
                useCaseOperator(
                    loadingStatus = AddSectionLoadingStatus.STARTING.toUiText(),
                    successMessageProvider = {
                        AddSectionSuccessMessage.SECTION_SAVED.toUiText()
                    },
                    onSuccessAction = {
                        addSectionDialogNavigationEventDispatcher.dispatch(
                            event = AddSectionDialogNavigationEvent.OnNavigateBack
                        )
                    }
                ) {
                    addSectionUseCase(addSectionModel = addSectionModel.value)
                }
            }
    }


    companion object {
        const val SAVED_STATE_ADD_SECTION_MODEL = "SAVED_STATE_ADD_SECTION_MODEL"
        const val SAVED_STATE_ADD_SECTION_MODEL_ERROR = "SAVED_STATE_ADD_SECTION_MODEL_ERROR"
    }

}