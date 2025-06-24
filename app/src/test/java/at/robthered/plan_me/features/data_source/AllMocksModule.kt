package at.robthered.plan_me.features.data_source

import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_section.AddSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived.ChangeTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history.ChangeTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history.CreateTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader.FlattenedRecursiveTaskLoader
import at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name.GetHashtagsByNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items.GetInboxScreenItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections.GetInboxScreenSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks.GetInboxScreenTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models.GetRootTaskModelsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_sections.GetSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task.GetTaskDetailsDialogTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper.GetTaskHistoryFlowHelper
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task.GetTaskModelsForParentUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section.GetTaskModelsForSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task.LoadHashtagsForTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks.LoadHashtagWithTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items.LoadMoveTaskItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots.LoadTaskRootsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics.LoadTaskStatisticsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import at.robthered.plan_me.features.data_source.domain.use_case.task_statistics.TaskStatisticsBuilder
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCase
import io.mockk.mockk
import kotlinx.datetime.Clock
import org.koin.dsl.module

val allMocksModule = module {
    single { mockk<LocalHashtagNameHistoryRepository>() }
    single { mockk<LocalHashtagRepository>() }
    single { mockk<LocalHashtagWithTasksRelationRepository>() }
    single { mockk<LocalSectionRepository>() }
    single { mockk<LocalSectionTitleHistoryRepository>() }
    single { mockk<LocalTaskArchivedHistoryRepository>() }
    single { mockk<LocalTaskCompletedHistoryRepository>() }
    single { mockk<LocalTaskDescriptionHistoryRepository>() }
    single { mockk<LocalTaskHashtagsCrossRefRepository>() }
    single { mockk<LocalTaskPriorityHistoryRepository>() }
    single { mockk<LocalTaskRepository>() }
    single { mockk<LocalTaskScheduleEventRepository>() }
    single { mockk<LocalTaskTitleHistoryRepository>() }

    /*Utils*/
    single { mockk<TransactionProvider>() }
    single { mockk<SafeDatabaseResultCall>() }
    single { mockk<Clock>() }
    single { mockk<NotificationContentMapper>() }
    single { mockk<AppAlarmScheduler>() }

    /*use cases*/
    single { mockk<AddExistingHashtagUseCase>() }
    single { mockk<AddHashtagHelper>() }
    single { mockk<AddNewHashtagUseCase>() }
    single { mockk<AddSectionUseCase>() }
    single { mockk<AddTaskUseCase>() }
    single { mockk<AddTaskScheduleEventUseCase>() }
    single { mockk<ChangeTaskArchivedHistoryUseCase>() }
    single { mockk<ChangeTaskCompletedUseCase>() }
    single { mockk<ChangeTaskPriorityUseCase>() }
    single { mockk<ChangeTaskPriorityHistoryUseCase>() }
    single { mockk<CombineTasksWithHashtagsUseCase>() }
    single { mockk<CreateTaskArchivedHistoryUseCase>() }
    single { mockk<CreateTaskCompletedHistoryUseCase>() }
    single { mockk<CreateTaskDescriptionHistoryUseCase>() }
    single { mockk<CreateTaskHashtagCrossRefUseCase>() }
    single { mockk<CreateTaskPriorityHistoryUseCase>() }
    single { mockk<CreateTaskTitleHistoryUseCase>() }
    single { mockk<DeleteSectionUseCase>() }
    single { mockk<DeleteTaskUseCase>() }
    single { mockk<DeleteTaskArchivedHistoryUseCase>() }
    single { mockk<DeleteTaskCompletedHistoryUseCase>() }
    single { mockk<DeleteTaskDescriptionHistoryUseCase>() }
    single { mockk<DeleteTaskHashtagReferenceUseCase>() }
    single { mockk<DeleteTaskPriorityHistoryUseCase>() }
    single { mockk<DeleteTaskScheduleEventUseCase>() }
    single { mockk<DuplicateTaskUseCase>() }
    single { mockk<FlattenedRecursiveTaskLoader>() }
    single { mockk<GetHashtagsByNameUseCase>() }
    single { mockk<GetInboxScreenTasksUseCase>() }
    single { mockk<GetInboxScreenSectionsUseCase>() }
    single { mockk<GetInboxScreenItemsUseCase>() }
    single { mockk<GetRootTaskModelsUseCase>() }
    single { mockk<GetSectionModelUseCase>() }
    single { mockk<GetSectionsUseCase>()}
    single { mockk<GetTaskByIdUseCase>() }
    single { mockk<GetTaskDetailsDialogTaskUseCase>() }
    single { mockk<GetTaskHistoryFlowHelper>() }
    single { mockk<GetTaskModelUseCase>() }
    single { mockk<GetTaskModelsForParentUseCase>() }
    single { mockk<GetTaskModelsForSectionUseCase>() }
    single { mockk<LoadHashtagsForTaskUseCase>() }
    single { mockk<LoadHashtagWithTasksUseCase>() }
    single { mockk<LoadMoveTaskItemsUseCase>() }
    single { mockk<LoadTaskRootsUseCase>() }
    single { mockk<LoadTaskStatisticsUseCase>() }
    single { mockk<MoveTaskUseCase>() }
    single { mockk<RecursiveTaskWithHashtagsAndChildrenModelHelper>() }
    single { mockk<TaskStatisticsBuilder>() }
    single { mockk<ToggleArchiveTaskUseCase>() }
    single { mockk<ToggleCompleteTaskUseCase>() }
    single { mockk<UpdateHashtagNameUseCase>() }
    single { mockk<UpdateSectionTitleUseCase>() }
    single { mockk<UpdateTaskUseCase>() }
    single { mockk<UpdateTaskDescriptionUseCase>() }
    single { mockk<UpdateTaskPriorityUseCase>() }
    single { mockk<UpdateTaskTitleUseCase>() }
}