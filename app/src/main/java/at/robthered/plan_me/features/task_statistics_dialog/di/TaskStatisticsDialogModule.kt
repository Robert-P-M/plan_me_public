package at.robthered.plan_me.features.task_statistics_dialog.di

import androidx.lifecycle.SavedStateHandle
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper.GetTaskHistoryFlowHelper
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper.GetTaskHistoryFlowHelperImpl
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics.LoadTaskStatisticsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics.LoadTaskStatisticsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.task_statistics.TaskStatisticsBuilder
import at.robthered.plan_me.features.data_source.domain.use_case.task_statistics.TaskStatisticsBuilderImpl
import at.robthered.plan_me.features.task_statistics_dialog.presentation.TaskStatisticsDialogViewModel
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationEventDispatcher
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationEventDispatcherImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskStatisticsDialogModule = module {

    factory<DeleteTaskTitleHistoryUseCase> {
        DeleteTaskTitleHistoryUseCaseImpl(
            localTaskTitleHistoryRepository = get<LocalTaskTitleHistoryRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }
    factory<DeleteTaskDescriptionHistoryUseCase> {
        DeleteTaskDescriptionHistoryUseCaseImpl(
            localTaskDescriptionHistoryRepository = get<LocalTaskDescriptionHistoryRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }
    factory<DeleteTaskPriorityHistoryUseCase> {
        DeleteTaskPriorityHistoryUseCaseImpl(
            localTaskPriorityHistoryRepository = get<LocalTaskPriorityHistoryRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }
    factory<DeleteTaskCompletedHistoryUseCase> {
        DeleteTaskCompletedHistoryUseCaseImpl(
            localTaskCompletedHistoryRepository = get<LocalTaskCompletedHistoryRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }
    factory<DeleteTaskArchivedHistoryUseCase> {
        DeleteTaskArchivedHistoryUseCaseImpl(
            localTaskArchivedHistoryRepository = get<LocalTaskArchivedHistoryRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }
    single<DeleteTaskScheduleEventUseCase> {
        DeleteTaskScheduleEventUseCaseImpl(
            localTaskScheduleEventRepository = get<LocalTaskScheduleEventRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    factory<GetTaskHistoryFlowHelper> {
        GetTaskHistoryFlowHelperImpl(
            localTaskTitleHistoryRepository = get<LocalTaskTitleHistoryRepository>(),
            localTaskDescriptionHistoryRepository = get<LocalTaskDescriptionHistoryRepository>(),
            localTaskPriorityHistoryRepository = get<LocalTaskPriorityHistoryRepository>(),
            localTaskCompletedHistoryRepository = get<LocalTaskCompletedHistoryRepository>(),
            localTaskArchivedHistoryRepository = get<LocalTaskArchivedHistoryRepository>(),
            localTaskScheduleEventRepository = get<LocalTaskScheduleEventRepository>()
        )
    }

    factory<TaskStatisticsBuilder> {
        TaskStatisticsBuilderImpl()
    }

    factory<LoadTaskStatisticsUseCase> {
        LoadTaskStatisticsUseCaseImpl(
            getTaskHistoryFlowHelper = get<GetTaskHistoryFlowHelper>(),
            localTaskRepository = get<LocalTaskRepository>(),
            localTaskHashtagsCrossRefRepository = get<LocalTaskHashtagsCrossRefRepository>(),
            taskStatisticsBuilder = get<TaskStatisticsBuilder>()
        )
    }

    factory<TaskStatisticsDialogNavigationEventDispatcher> {
        TaskStatisticsDialogNavigationEventDispatcherImpl()
    }

    viewModel<TaskStatisticsDialogViewModel> {
        TaskStatisticsDialogViewModel(
            savedStateHandle = get<SavedStateHandle>(),
            loadTaskStatisticsUseCase = get<LoadTaskStatisticsUseCase>(),
            useCaseOperator = get<UseCaseOperator>(),
            deleteTaskUseCase = get<DeleteTaskUseCase>(),
            deleteTaskTitleHistoryUseCase = get<DeleteTaskTitleHistoryUseCase>(),
            deleteTaskDescriptionHistoryUseCase = get<DeleteTaskDescriptionHistoryUseCase>(),
            deleteTaskPriorityHistoryUseCase = get<DeleteTaskPriorityHistoryUseCase>(),
            deleteTaskCompletedHistoryUseCase = get<DeleteTaskCompletedHistoryUseCase>(),
            deleteTaskArchivedHistoryUseCase = get<DeleteTaskArchivedHistoryUseCase>(),
            deleteTaskScheduleEventUseCase = get<DeleteTaskScheduleEventUseCase>(),
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            taskStatisticsDialogNavigationEventDispatcher = get<TaskStatisticsDialogNavigationEventDispatcher>()
        )
    }
}