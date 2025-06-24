package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalSectionRepositoryImpl(
    private val sectionDao: SectionDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalSectionRepository {
    override suspend fun insert(sectionModel: SectionModel): Long {
        return safeDatabaseExecutor {
            sectionDao
                .insert(
                    sectionEntity = sectionModel.toEntity()
                )
        }
    }

    override suspend fun upsert(sectionModel: SectionModel) {
        return safeDatabaseExecutor {
            sectionDao
                .upsert(
                    sectionEntity = sectionModel.toEntity()
                )
        }
    }

    override suspend fun delete() {
        return safeDatabaseExecutor {
            sectionDao
                .delete()
        }
    }

    override suspend fun delete(sectionId: Long): Int {
        return safeDatabaseExecutor {
            sectionDao
                .delete(
                    sectionId = sectionId
                )
        }
    }

    override suspend fun delete(sectionIds: List<Long>) {
        return safeDatabaseExecutor {
            sectionDao
                .delete(
                    sectionIds = sectionIds
                )
        }
    }

    override fun get(): Flow<List<SectionModel>> {
        return sectionDao.get().map { it.toModels() }

    }

    override fun get(sectionId: Long): Flow<SectionModel?> {
        return sectionDao
            .get(
                sectionId = sectionId
            )
            .map { it?.toModel() }
    }

}