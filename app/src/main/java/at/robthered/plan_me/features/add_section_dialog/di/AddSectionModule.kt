package at.robthered.plan_me.features.add_section_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.add_section_dialog.presentation.AddSectionDialogViewModel
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.AddSectionModelChangeChecker
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.AddSectionModelChangeCheckerImpl
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.CanSaveAddSectionChecker
import at.robthered.plan_me.features.add_section_dialog.presentation.helper.CanSaveAddSectionCheckerImpl
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationEventDispatcher
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationEventDispatcherImpl
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidator
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidatorImpl
import at.robthered.plan_me.features.data_source.domain.validation.add_section.AddSectionModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.add_section.AddSectionModelValidatorImpl
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addSectionModule = module {
    factory<SectionTitleValidator> {
        SectionTitleValidatorImpl()
    }
    factory<AddSectionModelValidator> {
        AddSectionModelValidatorImpl(
            sectionTitleValidator = get<SectionTitleValidator>()
        )
    }
    factory<CanSaveAddSectionChecker> {
        CanSaveAddSectionCheckerImpl()
    }
    factory<AddSectionModelChangeChecker> {
        AddSectionModelChangeCheckerImpl()
    }


    single<AddSectionUseCase> {
        AddSectionUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localSectionRepository = get<LocalSectionRepository>(),
            localSectionTitleHistoryRepository = get<LocalSectionTitleHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>()
        )
    }

    factory<AddSectionDialogNavigationEventDispatcher> {
        AddSectionDialogNavigationEventDispatcherImpl()
    }


    viewModel {
        AddSectionDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            addSectionModelValidator = get<AddSectionModelValidator>(),
            addSectionModelChangeChecker = get<AddSectionModelChangeChecker>(),
            canSaveAddSectionChecker = get<CanSaveAddSectionChecker>(),
            addSectionUseCase = get<AddSectionUseCase>(),
            addSectionDialogNavigationEventDispatcher = get<AddSectionDialogNavigationEventDispatcher>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            useCaseOperator = get<UseCaseOperator>(),
        )
    }

}