package at.robthered.plan_me.features.data_source.di

import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagNameHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagWithTasksRelationDao
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.dao.SectionTitleHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskArchivedHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskCompletedHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDescriptionHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskHashtagsCrossRefDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskPriorityHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskScheduleEventDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskTitleHistoryDao
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagNameHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagWithTasksRelationRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalSectionRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalSectionTitleHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskArchivedHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskCompletedHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskDescriptionHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskHashtagsCrossRefRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskPriorityHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskScheduleEventRepositoryImpl
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskTitleHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
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
import org.koin.dsl.module

val repositoryModule = module {

    single<LocalHashtagNameHistoryRepository> {
        LocalHashtagNameHistoryRepositoryImpl(
            hashtagNameHistoryDao = get<HashtagNameHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalHashtagRepository> {
        LocalHashtagRepositoryImpl(
            hashtagDao = get<HashtagDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalHashtagWithTasksRelationRepository> {
        LocalHashtagWithTasksRelationRepositoryImpl(
            hashtagWithTasksRelationDao = get<HashtagWithTasksRelationDao>()
        )
    }

    single<LocalSectionRepository> {
        LocalSectionRepositoryImpl(
            sectionDao = get<SectionDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }

    single<LocalSectionTitleHistoryRepository> {
        LocalSectionTitleHistoryRepositoryImpl(
            sectionTitleHistoryDao = get<SectionTitleHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalTaskArchivedHistoryRepository> {
        LocalTaskArchivedHistoryRepositoryImpl(
            taskArchivedHistoryDao = get<TaskArchivedHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalTaskCompletedHistoryRepository> {
        LocalTaskCompletedHistoryRepositoryImpl(
            taskCompletedHistoryDao = get<TaskCompletedHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }

    single<LocalTaskDescriptionHistoryRepository> {
        LocalTaskDescriptionHistoryRepositoryImpl(
            taskDescriptionHistoryDao = get<TaskDescriptionHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }

    single<LocalTaskHashtagsCrossRefRepository> {
        LocalTaskHashtagsCrossRefRepositoryImpl(
            taskHashtagsCrossRefDao = get<TaskHashtagsCrossRefDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalTaskPriorityHistoryRepository> {
        LocalTaskPriorityHistoryRepositoryImpl(
            taskPriorityHistoryDao = get<TaskPriorityHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }

    single<LocalTaskRepository> {
        LocalTaskRepositoryImpl(
            taskDao = get<TaskDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }
    single<LocalTaskScheduleEventRepository> {
        LocalTaskScheduleEventRepositoryImpl(
            taskScheduleEventDao = get<TaskScheduleEventDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>()
        )
    }

    single<LocalTaskTitleHistoryRepository> {
        LocalTaskTitleHistoryRepositoryImpl(
            taskTitleHistoryDao = get<TaskTitleHistoryDao>(),
            safeDatabaseExecutor = get<SafeDatabaseExecutor>(),
        )
    }

}