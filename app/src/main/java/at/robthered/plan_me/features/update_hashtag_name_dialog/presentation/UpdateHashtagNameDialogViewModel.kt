package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model.LoadUpdateHashtagModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameUseCase
import at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name.UpdateHashtagModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.CanSaveUpdateHashtagModelChecker
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper.DidUpdateHashtagModelChangeChecker
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationEvent
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationEventDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateHashtagNameDialogViewModel(
    savedStateHandler: SavedStateHandle,
    private val updateHashtagNameDialogNavigationEventDispatcher: UpdateHashtagNameDialogNavigationEventDispatcher,
    private val loadUpdateHashtagModelUseCase: LoadUpdateHashtagModelUseCase,
    private val didUpdateHashtagModelChangeChecker: DidUpdateHashtagModelChangeChecker,
    private val canSaveUpdateHashtagModelChecker: CanSaveUpdateHashtagModelChecker,
    private val updateHashtagModelValidator: UpdateHashtagModelValidator,
    private val useCaseOperator: UseCaseOperator,
    private val updateHashtagNameUseCase: UpdateHashtagNameUseCase,
    appUiEventDispatcher: AppUiEventDispatcher,
) : ViewModel() {

    private val updateHashtagNameDialogArgs = savedStateHandler
        .toRoute<Route.UpdateHashtagNameDialog>()

    private val hashtagId = savedStateHandler
        .getStateFlow(
            key = SAVED_STATE_HASHTAG_ID,
            initialValue = updateHashtagNameDialogArgs.hashtagId
        )


    val appNavigationEvent = updateHashtagNameDialogNavigationEventDispatcher
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

    private val _hashtagModel =
        savedStateHandler
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_INITIAL_HASHTAG_MODEL,
                initialValue = UpdateHashtagModel()
            )

    private val _updateHashtagModel =
        savedStateHandler
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL,
                initialValue = UpdateHashtagModel()
            )
    val updateHashtagModel = _updateHashtagModel.asStateFlow()

    private val _updateHashtagModelError =
        savedStateHandler
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL_ERROR,
                initialValue = UpdateHashtagModelError()
            )
    val updateHashtagModelError = _updateHashtagModelError.asStateFlow()

    private val observeUpdateHashtagModel = _updateHashtagModel
        .onEach { model ->
            _updateHashtagModelError
                .update {
                    updateHashtagModelValidator(
                        updateHashtagModel = model
                    )
                }
        }
        .launchIn(viewModelScope)

    val canSaveUpdateHashtag = updateHashtagModelError
        .map {
            canSaveUpdateHashtagModelChecker(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val didUpdateModelChange = updateHashtagModel
        .map { current ->
            didUpdateHashtagModelChangeChecker(
                initialModel = _hashtagModel.value,
                currentModel = current
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    fun onAction(action: UpdateHashtagNameDialogUiAction) {
        when (action) {
            UpdateHashtagNameDialogUiAction.OnNavigateBack -> onNavigateBack()
            is UpdateHashtagNameDialogUiAction.OnChangeName -> onChangeName(action)
            UpdateHashtagNameDialogUiAction.OnSaveHashtag -> onSaveHashtag()
        }
    }

    private fun onSaveHashtag() {
        viewModelScope
            .launch {
                useCaseOperator(
                    loadingStatus = UpdateHashtagNameLoadingStatus.STARTING.toUiText(),
                    successMessageProvider = { UpdateHashtagNameSuccessMessage.HASHTAG_UPDATED.toUiText() },
                    onSuccessAction = {
                        updateHashtagNameDialogNavigationEventDispatcher.dispatch(
                            UpdateHashtagNameDialogNavigationEvent.OnNavigateBack
                        )
                    }
                ) {
                    updateHashtagNameUseCase(updateHashtagModel = _updateHashtagModel.value)
                }
            }
    }

    private fun onChangeName(action: UpdateHashtagNameDialogUiAction.OnChangeName) {
        _updateHashtagModel.update {
            it.copy(
                name = action.name
            )
        }
    }

    private val observeHashtagId = hashtagId
        .onEach { id ->
            val model = loadUpdateHashtagModelUseCase(hashtagId = id).first()
            _hashtagModel.update {
                model
            }
            _updateHashtagModel.update {
                model
            }
        }
        .launchIn(viewModelScope)

    private fun onNavigateBack() {
        viewModelScope
            .launch {
                updateHashtagNameDialogNavigationEventDispatcher
                    .dispatch(
                        event = UpdateHashtagNameDialogNavigationEvent
                            .OnNavigateBack
                    )
            }
    }


    companion object {
        const val SAVED_STATE_HASHTAG_ID = "SAVED_STATE_HASHTAG_ID"
        const val SAVED_STATE_HANDLE_INITIAL_HASHTAG_MODEL =
            "SAVED_STATE_HANDLE_INITIAL_HASHTAG_MODEL"
        const val SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL =
            "SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL"
        const val SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL_ERROR =
            "SAVED_STATE_HANDLE_UPDATE_HASHTAG_MODEL_ERROR"
    }


    override fun onCleared() {
        super.onCleared()
    }

}