package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalSectionTitleHistoryRepository {

    suspend fun insert(sectionTitleHistoryModel: SectionTitleHistoryModel): Long

    fun get(sectionTitleHistoryId: Long): Flow<SectionTitleHistoryModel?>

    fun getForSection(sectionId: Long): Flow<List<SectionTitleHistoryModel>>

    suspend fun delete(sectionTitleHistoryId: Long): Unit

}