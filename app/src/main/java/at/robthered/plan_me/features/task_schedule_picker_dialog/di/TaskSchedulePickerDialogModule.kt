package at.robthered.plan_me.features.task_schedule_picker_dialog.di


import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.data_source.domain.validation.AddTaskScheduleEventModelValidator
import at.robthered.plan_me.features.data_source.domain.validation.AddTaskScheduleEventModelValidatorImpl
import at.robthered.plan_me.features.data_source.domain.validation.TaskScheduleStartDateEpochDaysValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskScheduleStartDateEpochDaysValidatorImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.data.DateTimeHelperImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.use_case.GetUpcomingDatesUseCase
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.use_case.GetUpcomingDatesUseCaseImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.TaskSchedulePickerDialogViewModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.helper.DidAddTaskScheduleEventModelChangeChecker
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.helper.DidAddTaskScheduleEventModelChangeCheckerImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerDialogNavigationEventDispatcher
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerDialogNavigationEventDispatcherImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging.GetPagedMonthUseCase
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging.GetPagedMonthUseCaseImpl
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBusImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskSchedulePickerDialogModule = module {
    single<TaskScheduleEventBus> {
        TaskScheduleEventBusImpl()
    }
    factory<DidAddTaskScheduleEventModelChangeChecker> {
        DidAddTaskScheduleEventModelChangeCheckerImpl()
    }
    factory<TaskScheduleStartDateEpochDaysValidator> {
        TaskScheduleStartDateEpochDaysValidatorImpl()
    }
    factory<AddTaskScheduleEventModelValidator> {
        AddTaskScheduleEventModelValidatorImpl(
            taskScheduleStartDateEpochDaysValidator = get<TaskScheduleStartDateEpochDaysValidator>()
        )
    }
    factory<DateTimeHelper> { DateTimeHelperImpl() }
    factory<GetPagedMonthUseCase> {
        GetPagedMonthUseCaseImpl(
            dateTimeHelper = get<DateTimeHelper>()
        )
    }
    factory<GetUpcomingDatesUseCase> {
        GetUpcomingDatesUseCaseImpl(
            dateTimeHelper = get<DateTimeHelper>()
        )
    }
    factory<TaskSchedulePickerDialogNavigationEventDispatcher> {
        TaskSchedulePickerDialogNavigationEventDispatcherImpl()
    }
    viewModel {
        TaskSchedulePickerDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            taskSchedulePickerDialogNavigationEventDispatcher = get<TaskSchedulePickerDialogNavigationEventDispatcher>(),
            getPagedMonthUseCase = get<GetPagedMonthUseCase>(),
            addTaskScheduleEventModelValidator = get<AddTaskScheduleEventModelValidator>(),
            didAddTaskScheduleEventModelChangeChecker = get<DidAddTaskScheduleEventModelChangeChecker>(),
            taskScheduleEventBus = get<TaskScheduleEventBus>(),
            getUpcomingDatesUseCase = get<GetUpcomingDatesUseCase>(),
            appAlarmScheduler = get<AppAlarmScheduler>(),
        )
    }
}