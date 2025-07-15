package at.robthered.plan_me.features.task_hashtags_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagReferenceLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagReferenceSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagWithReferenceLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagWithReferenceSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task.LoadHashtagsForTaskUseCase
import at.robthered.plan_me.features.data_source.domain.validation.new_hashtag.NewHashtagModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.CanSaveNewHashtagModelChecker
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationDispatcher
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskHashtagsDialogViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getHashtagsByNameUseCase: GetHashtagsByNameUseCase,
    private val loadHashtagsForTaskUseCase: LoadHashtagsForTaskUseCase,
    private val addExistingHashtagUseCase: AddExistingHashtagUseCase,
    private val addNewHashtagUseCase: AddNewHashtagUseCase,
    private val newHashtagModelValidator: NewHashtagModelValidator,
    private val canSaveNewHashtagModelChecker: CanSaveNewHashtagModelChecker,
    private val taskHashtagsDialogNavigationDispatcher: TaskHashtagsDialogNavigationDispatcher,
    private val deleteTaskHashtagReferenceUseCase: DeleteTaskHashtagReferenceUseCase,
    private val useCaseOperator: UseCaseOperator,
    appUiEventDispatcher: AppUiEventDispatcher,
) : ViewModel() {

    private val taskHashtagsDialogArgs = savedStateHandle.toRoute<Route.TaskHashtagsDialog>()
    val appNavigationEvent = taskHashtagsDialogNavigationDispatcher
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

    private val _taskId = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_HANDLE_TASK_ID,
            initialValue = taskHashtagsDialogArgs.taskId
        )


    private val _newHashtagModel: MutableStateFlow<UiHashtagModel.NewHashTagModel> =
        savedStateHandle.getMutableStateFlow(
            key = SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL,
            initialValue = UiHashtagModel.NewHashTagModel()
        )
    val newHashtagModel: StateFlow<UiHashtagModel.NewHashTagModel> = _newHashtagModel.asStateFlow()
    private val _newHashtagModelError =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL_ERROR,
                initialValue = NewHashtagModelError()
            )


    private val observeNewHashtagModel = _newHashtagModel
        .onEach { model ->
            _newHashtagModelError.update {
                newHashtagModelValidator(
                    newHashtagModel = model
                )
            }
        }
        .launchIn(viewModelScope)


    val newHashtagModelError = _newHashtagModelError
        .asStateFlow()


    val canSaveNewHashtag = newHashtagModelError
        .map {
            canSaveNewHashtagModelChecker(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    val taskHashtags: StateFlow<List<HashtagModel>> = _taskId
        .flatMapLatest { taskId ->
            loadHashtagsForTaskUseCase(taskId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = savedStateHandle[SAVED_STATE_HANDLE_TASK_HASHTAGS] ?: emptyList()
        ).also { flow ->
            viewModelScope.launch {
                flow.collectLatest {
                    savedStateHandle[SAVED_STATE_HANDLE_TASK_HASHTAGS] = it
                }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val foundHashtags = combine(
        _newHashtagModel
            .debounce(300L)
            .distinctUntilChanged(),
        taskHashtags
    ) { model: UiHashtagModel.NewHashTagModel, tags: List<HashtagModel> ->
        Pair(model, tags)
    }
        .flatMapLatest { (model: UiHashtagModel.NewHashTagModel, tags): Pair<UiHashtagModel.NewHashTagModel, List<HashtagModel>> ->
            if (model.name.isBlank()) {
                flowOf(emptyList())
            } else {
                getHashtagsByNameUseCase(query = model.name)
                    .map { searchResult ->
                        val selectedExistingIds = tags.map { it.hashtagId }.toSet()
                        searchResult.filterNot { foundHashtag ->
                            selectedExistingIds.contains(foundHashtag.hashtagId)
                        }
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    fun onAction(action: TaskHashtagsDialogUiAction) {
        when (action) {
            is TaskHashtagsDialogUiAction.OnDeleteTaskHashtagReference -> onDeleteTaskHashtagReference(
                action
            )

            TaskHashtagsDialogUiAction.OnNavigateBack -> onNavigateBack()
            is TaskHashtagsDialogUiAction.OnNavigateToUpdateHashtagNameDialog ->
                onNavigateToUpdateHashtagNameDialog(action)

            is TaskHashtagsDialogUiAction.OnChangeHashtagName -> onChangeHashtagName(action)
            TaskHashtagsDialogUiAction.OnSaveNewHashtag -> onSaveNewHashtag()
            is TaskHashtagsDialogUiAction.OnSaveExistingHashtag -> onSaveExistingHashtag(action)
            TaskHashtagsDialogUiAction.OnResetState -> {
                _newHashtagModel.value = UiHashtagModel.NewHashTagModel()
            }

            is TaskHashtagsDialogUiAction.OnNavigateToHashtagTasksDialog -> onNavigateToHashtagTasksDialog(
                action
            )
        }
    }

    private fun onNavigateToHashtagTasksDialog(action: TaskHashtagsDialogUiAction.OnNavigateToHashtagTasksDialog) {
        viewModelScope
            .launch {
                taskHashtagsDialogNavigationDispatcher
                    .dispatch(
                        event = TaskHashtagsDialogNavigationEvent
                            .OnNavigateToHashtagTasksDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onSaveExistingHashtag(action: TaskHashtagsDialogUiAction.OnSaveExistingHashtag) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = AddExistingHashtagReferenceLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { AddExistingHashtagReferenceSuccessMessage.REFERENCE_BETWEEN_HASHTAG_AND_TASK_CREATED.toUiText() },
                onSuccessAction = {
                    _newHashtagModel.value =
                        UiHashtagModel.NewHashTagModel()
                }
            ) {
                addExistingHashtagUseCase(
                    taskId = _taskId.value,
                    uiHashtagModel = action.existingHashtagModel
                )

            }
        }
    }

    private fun onSaveNewHashtag() {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = AddNewHashtagWithReferenceLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { AddNewHashtagWithReferenceSuccessMessage.NEW_HASHTAG_AND_REFERENCE_TO_TASK_CREATED.toUiText() },
                onSuccessAction = {
                    _newHashtagModel.value =
                        UiHashtagModel.NewHashTagModel()
                }
            ) {
                addNewHashtagUseCase(
                    taskId = _taskId.value,
                    uiHashtagModel = newHashtagModel.value
                )
            }
        }
    }

    private fun onNavigateToUpdateHashtagNameDialog(action: TaskHashtagsDialogUiAction.OnNavigateToUpdateHashtagNameDialog) {
        viewModelScope
            .launch {
                taskHashtagsDialogNavigationDispatcher
                    .dispatch(
                        event = TaskHashtagsDialogNavigationEvent
                            .OnNavigateToUpdateHashtagNameDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onChangeHashtagName(action: TaskHashtagsDialogUiAction.OnChangeHashtagName) {
        _newHashtagModel.update {
            it.copy(
                name = action.name
            )
        }
    }

    private fun onNavigateBack() {
        viewModelScope
            .launch {
                taskHashtagsDialogNavigationDispatcher
                    .dispatch(
                        event = TaskHashtagsDialogNavigationEvent
                            .OnNavigateBack
                    )
            }
    }

    private fun onDeleteTaskHashtagReference(action: TaskHashtagsDialogUiAction.OnDeleteTaskHashtagReference) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskHashtagReferenceLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskHashtagReferenceSuccessMessage.TASK_HASHTAG_REFERENCE_SUCCESS.toUiText() },
            ) {
                deleteTaskHashtagReferenceUseCase(
                    taskId = _taskId.value,
                    hashtagId = action.hashtagId
                )

            }
        }
    }


    companion object {
        const val SAVED_STATE_HANDLE_TASK_ID = "SAVED_STATE_HANDLE_TASK_ID"
        const val SAVED_STATE_HANDLE_TASK_HASHTAGS =
            "SAVED_STATE_HANDLE_TASK_HASHTAGS"
        const val SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL = "SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL"
        const val SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL_ERROR =
            "SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL_ERROR"
    }

}