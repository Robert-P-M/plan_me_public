package at.robthered.plan_me.features.hashtag_picker_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.robthered.plan_me.features.common.domain.HashtagPickerEventBus
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.event_bus.clearEvent
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.data_source.data.local.mapper.toExistingHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.NewHashTagModel
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCase
import at.robthered.plan_me.features.data_source.domain.validation.new_hashtag.NewHashtagModelValidator
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event.HashtagPickerEvent
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.CanSaveNewHashtagModelChecker
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.DidAddHashtagModelsChangeChecker
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationEvent
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationEventDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class HashtagPickerDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val didAddHashtagModelsChangeChecker: DidAddHashtagModelsChangeChecker,
    private val hashtagPickerEventBus: HashtagPickerEventBus,
    private val getHashtagsByNameUseCase: GetHashtagsByNameUseCase,
    private val newHashtagModelValidator: NewHashtagModelValidator,
    private val canSaveNewHashtagModelChecker: CanSaveNewHashtagModelChecker,
    private val hashtagPickerDialogNavigationEventDispatcher: HashtagPickerDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
) : ViewModel() {

    private val _newHashtagModel: MutableStateFlow<NewHashTagModel> =
        savedStateHandle.getMutableStateFlow(
            key = SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL,
            initialValue = NewHashTagModel()
        )
    val newHashtagModel: StateFlow<NewHashTagModel> = _newHashtagModel.asStateFlow()

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

    val appNavigationEvent = hashtagPickerDialogNavigationEventDispatcher
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

    private val _initialHashtags =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_PICKED_INITIAL_HASHTAGS,
                initialValue = emptyList<UiHashtagModel>()
            )


    private val _newHashtags =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_PICKED_NEW_HASHTAGS,
                initialValue = emptyList<UiHashtagModel>()
            )

    val newHashtags = _newHashtags.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val foundHashtags = combine(
        _newHashtagModel
            .debounce(300L)
            .distinctUntilChanged(),
        _newHashtags
    ) { model: NewHashTagModel, tags: List<UiHashtagModel> ->
        Pair(model, tags)
    }
        .flatMapLatest { (model: NewHashTagModel, tags): Pair<NewHashTagModel, List<UiHashtagModel>> ->
            if (model.name.isBlank()) {
                flowOf(emptyList())
            } else {
                getHashtagsByNameUseCase(query = model.name)
                    .map { searchResult ->
                        val selectedExistingIds = tags.mapNotNull {
                            when (it) {
                                is UiHashtagModel.ExistingHashtagModel -> it.hashtagId
                                is UiHashtagModel.FoundHashtagModel -> it.hashtagId
                                is NewHashTagModel -> null
                            }
                        }.toSet()
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

    fun onAction(action: HashtagPickerDialogUiAction) {
        when (action) {
            HashtagPickerDialogUiAction.OnNavigateBack -> onNavigateBack()
            HashtagPickerDialogUiAction.OnResetState -> onResetState()
            HashtagPickerDialogUiAction.OnResetNewHashtagModel -> onResetNewHashtagModel()
            HashtagPickerDialogUiAction.OnSaveHashtags -> onSaveHashtags()
            is HashtagPickerDialogUiAction.OnChangeNewHashtagName -> onChangeNewHashtagName(action)
            HashtagPickerDialogUiAction.OnSaveNewHashtag -> onSaveNewHashtag()
            is HashtagPickerDialogUiAction.OnRemoveNewHashtag -> onRemoveNewHashtag(action)
            is HashtagPickerDialogUiAction.OnAddExistingHashtag -> onAddFoundHashtagModel(action)
            is HashtagPickerDialogUiAction.OnEditNewHashtagItem -> {
                _newHashtagModel.update { action.newHashtagModel }
            }
        }
    }

    private fun onRemoveNewHashtag(action: HashtagPickerDialogUiAction.OnRemoveNewHashtag) {
        _newHashtags.update {
            it.toMutableList().apply { removeAt(action.index) }
        }
    }

    private fun onSaveNewHashtag() {
        _newHashtagModel.value.index?.let {
            val newList = _newHashtags.value.toMutableList()
                .apply {
                    this[it] = _newHashtagModel.value
                }
            _newHashtags.update {
                newList
            }
        } ?: run {
            _newHashtags.update {
                it + newHashtagModel.value.copy(it.size)
            }
        }
        _newHashtagModel.value = NewHashtagModel()
    }

    private fun onAddFoundHashtagModel(action: HashtagPickerDialogUiAction.OnAddExistingHashtag) {
        _newHashtags.update {
            it + action.foundHashtagModel.toExistingHashtagModel()
        }
        _newHashtagModel.update { NewHashtagModel() }
    }

    private fun onChangeNewHashtagName(action: HashtagPickerDialogUiAction.OnChangeNewHashtagName) {
        _newHashtagModel
            .update {
                it.copy(
                    name = action.name
                )
            }
    }

    private fun onSaveHashtags() {
        viewModelScope.launch {
            hashtagPickerEventBus
                .publish(
                    event = HashtagPickerEvent
                        .NewHashtags(
                            hashtags = _newHashtags.value
                        )
                )
            hashtagPickerDialogNavigationEventDispatcher
                .dispatch(
                    event = HashtagPickerDialogNavigationEvent.OnNavigateBack
                )
        }
    }

    private fun onResetState() {
        _newHashtags.value = _initialHashtags.value
    }

    private fun onResetNewHashtagModel() {
        _newHashtagModel.value = NewHashtagModel()
    }

    private fun onNavigateBack() {
        viewModelScope
            .launch {
                hashtagPickerDialogNavigationEventDispatcher
                    .dispatch(
                        event = HashtagPickerDialogNavigationEvent.OnNavigateBack
                    )
            }
    }

    val didListChange = newHashtags
        .map { currentHashtags ->
            didAddHashtagModelsChangeChecker(
                initialList = _initialHashtags.value,
                currentList = currentHashtags
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val observeCurrentHashtags = hashtagPickerEventBus
        .subscribeOn<HashtagPickerEvent, HashtagPickerEvent.CurrentHashtags>(
            scope = viewModelScope
        ) { event ->
            _initialHashtags
                .update { event.hashtags }
            _newHashtags
                .update { event.hashtags }


            hashtagPickerEventBus.clearEvent(HashtagPickerEvent.ClearEvent)
        }

    companion object {
        const val SAVED_STATE_HANDLE_PICKED_INITIAL_HASHTAGS =
            "SAVED_STATE_HANDLE_PICKED_INITIAL_HASHTAGS"
        const val SAVED_STATE_HANDLE_PICKED_NEW_HASHTAGS = "SAVED_STATE_HANDLE_PICKED_NEW_HASHTAGS"
        const val SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL = "SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL"
        const val SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL_ERROR =
            "SAVED_STATE_HANDLE_NEW_HASHTAG_MODEL_ERROR"
    }

}