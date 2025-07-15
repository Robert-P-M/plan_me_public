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
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.exception.RoomDatabaseErrorMapperImpl
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import org.koin.dsl.module

val localDataSourceModule = module {

    single<HashtagDao> {
        get<AppDatabase>().hashtagDao()
    }

    single<HashtagNameHistoryDao> {
        get<AppDatabase>().hashtagNameHistoryDao()
    }

    single<HashtagWithTasksRelationDao> {
        get<AppDatabase>().hashtagWithTasksRelationDao()
    }

    single<SectionDao> {
        get<AppDatabase>().sectionDao()
    }

    single<SectionTitleHistoryDao> {
        get<AppDatabase>().sectionTitleHistoryDao()
    }

    single<TaskArchivedHistoryDao> {
        get<AppDatabase>().taskArchivedHistoryDao()
    }

    single<TaskCompletedHistoryDao> {
        get<AppDatabase>().taskCompletedHistoryDao()
    }

    single<TaskDao> {
        get<AppDatabase>().taskDao()
    }

    // TODO: Test for [TaskDescriptionHistoryDao]
    single<TaskDescriptionHistoryDao> {
        get<AppDatabase>().taskDescriptionHistoryDao()
    }

    single<TaskHashtagsCrossRefDao> {
        get<AppDatabase>().taskHashtagsCrossRefDao()
    }

    single<TaskPriorityHistoryDao> {
        get<AppDatabase>().taskPriorityHistoryDao()
    }

    single<TaskScheduleEventDao> {
        get<AppDatabase>().taskScheduleEventDao()
    }

    single<TaskTitleHistoryDao> {
        get<AppDatabase>().taskTitleHistoryDao()
    }

    single<RoomDatabaseErrorMapper> {
        RoomDatabaseErrorMapperImpl(
            appLogger = get<AppLogger>()
        )
    }

}