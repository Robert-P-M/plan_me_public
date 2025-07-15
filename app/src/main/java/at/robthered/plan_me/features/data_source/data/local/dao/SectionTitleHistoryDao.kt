package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.SectionTitleHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionTitleHistoryDao {

    @Insert
    suspend fun insert(sectionTitleHistoryEntity: SectionTitleHistoryEntity): Long

    @Query("SELECT * FROM section_title_history WHERE section_title_history_id =:sectionTitleHistoryId")
    fun get(sectionTitleHistoryId: Long): Flow<SectionTitleHistoryEntity?>

    @Query("SELECT * FROM section_title_history WHERE section_id =:sectionId ORDER BY created_at ASC")
    fun getForSection(sectionId: Long): Flow<List<SectionTitleHistoryEntity>>

    @Query("DELETE FROM section_title_history WHERE section_title_history_id =:sectionTitleHistoryId")
    suspend fun delete(sectionTitleHistoryId: Long)

}