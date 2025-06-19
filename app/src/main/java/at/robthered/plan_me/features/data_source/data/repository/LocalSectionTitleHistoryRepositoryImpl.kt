package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.SectionTitleHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalSectionTitleHistoryRepositoryImpl(
    private val sectionTitleHistoryDao: SectionTitleHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalSectionTitleHistoryRepository {
    override suspend fun insert(sectionTitleHistoryModel: SectionTitleHistoryModel): Long {
        return safeDatabaseExecutor {
            sectionTitleHistoryDao
                .insert(
                    sectionTitleHistoryEntity = sectionTitleHistoryModel.toEntity()
                )
        }
    }

    override fun get(sectionTitleHistoryId: Long): Flow<SectionTitleHistoryModel?> {
        return sectionTitleHistoryDao
            .get(
                sectionTitleHistoryId = sectionTitleHistoryId
            )
            .map { it?.toModel() }
    }

    override fun getForSection(sectionId: Long): Flow<List<SectionTitleHistoryModel>> {
        return sectionTitleHistoryDao
            .getForSection(
                sectionId = sectionId
            )
            .map { it.toModels() }
    }

    override suspend fun delete(sectionTitleHistoryId: Long) {
        return safeDatabaseExecutor {
            sectionTitleHistoryDao
                .delete(
                    sectionTitleHistoryId = sectionTitleHistoryId
                )
        }
    }
}