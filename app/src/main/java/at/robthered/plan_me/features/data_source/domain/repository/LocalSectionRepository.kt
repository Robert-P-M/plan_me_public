package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import kotlinx.coroutines.flow.Flow

interface LocalSectionRepository {

    suspend fun insert(sectionModel: SectionModel): Long
    suspend fun upsert(sectionModel: SectionModel)
    suspend fun delete()
    suspend fun delete(sectionId: Long): Int
    suspend fun delete(sectionIds: List<Long>)
    fun get(): Flow<List<SectionModel>>
    fun get(sectionId: Long): Flow<SectionModel?>


}