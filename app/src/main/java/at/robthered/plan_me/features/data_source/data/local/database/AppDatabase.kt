package at.robthered.plan_me.features.data_source.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagFtsEntity
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagNameHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.data_source.data.local.entities.SectionTitleHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskArchivedHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskCompletedHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskDescriptionHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskPriorityHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskScheduleEventEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskTitleHistoryEntity

@Database(
    entities = [
        SectionEntity::class,
        SectionTitleHistoryEntity::class,
        TaskEntity::class,
        TaskCompletedHistoryEntity::class,
        TaskArchivedHistoryEntity::class,
        TaskTitleHistoryEntity::class,
        TaskDescriptionHistoryEntity::class,
        TaskPriorityHistoryEntity::class,
        HashtagEntity::class,
        HashtagNameHistoryEntity::class,
        TaskHashtagsCrossRefEntity::class,
        HashtagFtsEntity::class,
        TaskScheduleEventEntity::class
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskTitleHistoryDao(): TaskTitleHistoryDao
    abstract fun taskDescriptionHistoryDao(): TaskDescriptionHistoryDao
    abstract fun taskCompletedHistoryDao(): TaskCompletedHistoryDao
    abstract fun taskArchivedHistoryDao(): TaskArchivedHistoryDao
    abstract fun taskPriorityHistoryDao(): TaskPriorityHistoryDao
    abstract fun sectionDao(): SectionDao
    abstract fun sectionTitleHistoryDao(): SectionTitleHistoryDao
    abstract fun hashtagDao(): HashtagDao
    abstract fun hashtagNameHistoryDao(): HashtagNameHistoryDao
    abstract fun hashtagWithTasksRelationDao(): HashtagWithTasksRelationDao
    abstract fun taskHashtagsCrossRefDao(): TaskHashtagsCrossRefDao

    abstract fun taskScheduleEventDao(): TaskScheduleEventDao

    companion object {
        const val DB_NAME = "plan_me.db"
    }
}