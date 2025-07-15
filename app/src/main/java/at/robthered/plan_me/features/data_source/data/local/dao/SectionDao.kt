package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    @Insert
    suspend fun insert(sectionEntity: SectionEntity): Long

    @Upsert
    suspend fun upsert(sectionEntity: SectionEntity)

    @Query("DELETE FROM section")
    suspend fun delete()

    @Query("DELETE FROM section WHERE section_id =:sectionId")
    suspend fun delete(sectionId: Long): Int

    @Query("DELETE FROM section WHERE section_id IN (:sectionIds)")
    suspend fun delete(sectionIds: List<Long>)

    @Query("SELECT * FROM section  ORDER BY created_at ASC")
    fun get(): Flow<List<SectionEntity>>

    @Query("SELECT * FROM section WHERE section_id =:sectionId")
    fun get(sectionId: Long): Flow<SectionEntity?>

}