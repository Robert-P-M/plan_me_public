package at.robthered.plan_me.features.data_source.di

import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.executor.TransactionProviderImpl
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelperImpl
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived.ChangeTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived.ChangeTaskArchivedHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history.ChangeTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history.ChangeTaskPriorityHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history.CreateTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history.CreateTaskCompletedHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items.GetInboxScreenItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items.GetInboxScreenItemsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections.GetInboxScreenSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections.GetInboxScreenSectionsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks.GetInboxScreenTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks.GetInboxScreenTasksUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models.GetRootTaskModelsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models.GetRootTaskModelsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_sections.GetSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_sections.GetSectionsUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id.GetTaskByIdUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task.GetTaskDetailsDialogTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task.GetTaskDetailsDialogTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task.GetTaskModelsForParentUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task.GetTaskModelsForParentUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section.GetTaskModelsForSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section.GetTaskModelsForSectionUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks.LoadHashtagWithTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks.LoadHashtagWithTasksUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelperImpl
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCaseImpl
import kotlinx.datetime.Clock
import org.koin.dsl.module

val useCaseModule = module {


    single<TransactionProvider> {
        TransactionProviderImpl(
            database = get<AppDatabase>()
        )
    }

    single<GetTaskModelUseCase> {
        GetTaskModelUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>()
        )
    }

    single<CreateTaskTitleHistoryUseCase> {
        CreateTaskTitleHistoryUseCaseImpl(
            localTaskTitleHistoryRepository = get<LocalTaskTitleHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }

    single<CreateTaskCompletedHistoryUseCase> {
        CreateTaskCompletedHistoryUseCaseImpl(
            localTaskCompletedHistoryRepository = get<LocalTaskCompletedHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>(),
        )
    }

    single<UpdateTaskTitleUseCase> {
        UpdateTaskTitleUseCaseImpl(
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            localTaskRepository = get<LocalTaskRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }

    single<CreateTaskDescriptionHistoryUseCase> {
        CreateTaskDescriptionHistoryUseCaseImpl(
            localTaskDescriptionHistoryRepository = get<LocalTaskDescriptionHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }

    single<UpdateTaskDescriptionUseCase> {
        UpdateTaskDescriptionUseCaseImpl(
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            localTaskRepository = get<LocalTaskRepository>()
        )
    }

    single<UpdateTaskPriorityUseCase> {
        UpdateTaskPriorityUseCaseImpl(
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            localTaskRepository = get<LocalTaskRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
        )
    }

    single<ChangeTaskCompletedUseCase> {
        ChangeTaskCompletedUseCaseImpl(
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            localTaskRepository = get<LocalTaskRepository>(),
            clock = get<Clock>(),
        )
    }

    single<ToggleCompleteTaskUseCase> {
        ToggleCompleteTaskUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            createTaskCompletedHistoryUseCase = get<CreateTaskCompletedHistoryUseCase>(),
            changeTaskCompletedUseCase = get<ChangeTaskCompletedUseCase>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
        )
    }

    single<ToggleArchiveTaskUseCase> {
        ToggleArchiveTaskUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            changeTaskArchivedHistoryUseCase = get<ChangeTaskArchivedHistoryUseCase>(),
            createTaskArchivedHistoryUseCase = get<CreateTaskArchivedHistoryUseCase>(),
        )
    }

    single<ChangeTaskArchivedHistoryUseCase> {
        ChangeTaskArchivedHistoryUseCaseImpl(
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            localTaskRepository = get<LocalTaskRepository>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            clock = get<Clock>()
        )
    }

    single<CreateTaskArchivedHistoryUseCase> {
        CreateTaskArchivedHistoryUseCaseImpl(
            localTaskArchivedHistoryRepository = get<LocalTaskArchivedHistoryRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>()
        )
    }

    single<CreateTaskPriorityHistoryUseCase> {
        CreateTaskPriorityHistoryUseCaseImpl(
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            localTaskPriorityHistoryRepository = get<LocalTaskPriorityHistoryRepository>(),
            clock = get<Clock>(),
        )
    }

    single<ChangeTaskPriorityHistoryUseCase> {
        ChangeTaskPriorityHistoryUseCaseImpl(
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            localTaskRepository = get<LocalTaskRepository>(),
            clock = get<Clock>()
        )
    }

    single<ChangeTaskPriorityUseCase> {
        ChangeTaskPriorityUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            changeTaskPriorityHistoryUseCase = get<ChangeTaskPriorityHistoryUseCase>(),
            createTaskPriorityHistoryUseCase = get<CreateTaskPriorityHistoryUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>()
        )
    }

    single<DeleteTaskUseCase> {
        DeleteTaskUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    single<DuplicateTaskUseCase> {
        DuplicateTaskUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            localTaskRepository = get<LocalTaskRepository>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>(),
            createTaskTitleHistoryUseCase = get<CreateTaskTitleHistoryUseCase>(),
            createTaskDescriptionHistoryUseCase = get<CreateTaskDescriptionHistoryUseCase>(),
            createTaskPriorityHistoryUseCase = get<CreateTaskPriorityHistoryUseCase>(),
        )
    }

    single<GetSectionModelUseCase> {
        GetSectionModelUseCaseImpl(
            localSectionRepository = get<LocalSectionRepository>()
        )
    }

    single<DeleteSectionUseCase> {
        DeleteSectionUseCaseImpl(
            localSectionRepository = get<LocalSectionRepository>(),
            transactionProvider = get<TransactionProvider>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>()
        )
    }

    single<AddHashtagHelper> {
        AddHashtagHelperImpl(
            localHashtagRepository = get<LocalHashtagRepository>(),
            localHashtagNameHistoryRepository = get<LocalHashtagNameHistoryRepository>(),
            clock = get<Clock>()
        )
    }

    single<LoadHashtagWithTasksUseCase> {
        LoadHashtagWithTasksUseCaseImpl(
            localHashtagWithTasksRelationRepository = get<LocalHashtagWithTasksRelationRepository>()
        )
    }

    single<GetTaskByIdUseCase> {
        GetTaskByIdUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            localHashtagRepository = get<LocalHashtagRepository>()
        )
    }

    single<GetTaskDetailsDialogTaskUseCase> {
        GetTaskDetailsDialogTaskUseCaseImpl(
            getTaskByIdUseCase = get<GetTaskByIdUseCase>(),
            recursiveTaskWithHashtagsAndChildrenModelHelper = get<RecursiveTaskWithHashtagsAndChildrenModelHelper>(),
        )
    }

    single<GetRootTaskModelsUseCase> {
        GetRootTaskModelsUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            combineTasksWithHashtagsUseCase = get<CombineTasksWithHashtagsUseCase>()
        )
    }

    single<GetTaskModelsForSectionUseCase> {
        GetTaskModelsForSectionUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            combineTasksWithHashtagsUseCase = get<CombineTasksWithHashtagsUseCase>()
        )
    }

    single<GetTaskModelsForParentUseCase> {
        GetTaskModelsForParentUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            combineTasksWithHashtagsUseCase = get<CombineTasksWithHashtagsUseCase>()
        )
    }

    single<CombineTasksWithHashtagsUseCase> {
        CombineTasksWithHashtagsUseCaseImpl(
            localHashtagRepository = get<LocalHashtagRepository>()
        )
    }

    single<RecursiveTaskWithHashtagsAndChildrenModelHelper> {
        RecursiveTaskWithHashtagsAndChildrenModelHelperImpl(
            getTaskModelsForParentUseCase = get<GetTaskModelsForParentUseCase>()
        )
    }

    single<GetSectionsUseCase> {
        GetSectionsUseCaseImpl(
            localSectionRepository = get<LocalSectionRepository>()
        )
    }

    single<GetInboxScreenSectionsUseCase> {
        GetInboxScreenSectionsUseCaseImpl(
            getSectionsUseCase = get<GetSectionsUseCase>(),
            getTaskModelsForSectionUseCase = get<GetTaskModelsForSectionUseCase>(),
            recursiveTaskWithHashtagsAndChildrenModelHelper = get<RecursiveTaskWithHashtagsAndChildrenModelHelper>()
        )
    }

    single<GetInboxScreenTasksUseCase> {
        GetInboxScreenTasksUseCaseImpl(
            getRootTaskModelsUseCase = get<GetRootTaskModelsUseCase>(),
            recursiveTaskWithHashtagsAndChildrenModelHelper = get<RecursiveTaskWithHashtagsAndChildrenModelHelper>()
        )
    }

    single<GetInboxScreenItemsUseCase> {
        GetInboxScreenItemsUseCaseImpl(
            getInboxScreenTasksUseCase = get<GetInboxScreenTasksUseCase>(),
            getInboxScreenSectionsUseCase = get<GetInboxScreenSectionsUseCase>(),
        )
    }

    single<AddTaskScheduleEventUseCase> {
        AddTaskScheduleEventUseCaseImpl(
            transactionProvider = get<TransactionProvider>(),
            getTaskModelUseCase = get<GetTaskModelUseCase>(),
            localTaskRepository = get<LocalTaskRepository>(),
            localTaskScheduleEventRepository = get<LocalTaskScheduleEventRepository>(),
            safeDatabaseResultCall = get<SafeDatabaseResultCall>(),
            clock = get<Clock>(),
            appAlarmScheduler = get<AppAlarmScheduler>(),
            notificationContentMapper = get<NotificationContentMapper>()
        )
    }
}