package at.robthered.plan_me.features.hashtag_picker_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.HashtagPickerEventBus
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidator
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidatorImpl
import at.robthered.plan_me.features.data_source.domain.validation.new_hashtag.NewHashtagModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.new_hashtag.NewHashtagModelValidatorImpl
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.HashtagPickerDialogViewModel
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event.HashtagPickerEventBusImpl
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.CanSaveNewHashtagModelChecker
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.CanSaveNewHashtagModelCheckerImpl
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.DidAddHashtagModelsChangeChecker
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper.DidAddHashtagModelsChangeCheckerImpl
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationEventDispatcher
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationEventDispatcherImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val hashtagPickerDialogModule = module {

    factory<HashtagNameValidator> {
        HashtagNameValidatorImpl()
    }

    factory<NewHashtagModelValidator> {
        NewHashtagModelValidatorImpl(
            hashtagNameValidator = get<HashtagNameValidator>()
        )
    }

    single<HashtagPickerEventBus> {
        HashtagPickerEventBusImpl()
    }

    factory<DidAddHashtagModelsChangeChecker> {
        DidAddHashtagModelsChangeCheckerImpl()
    }

    factory<HashtagPickerDialogNavigationEventDispatcher> {
        HashtagPickerDialogNavigationEventDispatcherImpl()
    }

    factory<CanSaveNewHashtagModelChecker> {
        CanSaveNewHashtagModelCheckerImpl()
    }

    factory<GetHashtagsByNameUseCase> {
        GetHashtagsByNameUseCaseImpl(
            localHashtagRepository = get<LocalHashtagRepository>()
        )
    }

    viewModel<HashtagPickerDialogViewModel> {
        HashtagPickerDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            didAddHashtagModelsChangeChecker = get<DidAddHashtagModelsChangeChecker>(),
            getHashtagsByNameUseCase = get<GetHashtagsByNameUseCase>(),
            hashtagPickerEventBus = get<HashtagPickerEventBus>(),
            newHashtagModelValidator = get<NewHashtagModelValidator>(),
            canSaveNewHashtagModelChecker = get<CanSaveNewHashtagModelChecker>(),
            hashtagPickerDialogNavigationEventDispatcher = get<HashtagPickerDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
        )
    }
}