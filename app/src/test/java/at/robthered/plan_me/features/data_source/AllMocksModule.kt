package at.robthered.plan_me.features.data_source

import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
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
    single { mockk<TransactionProvider>() }
    single { mockk<SafeDatabaseResultCall>() }
    single { mockk<Clock>() }
}