package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "section"
)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "section_id")
    val sectionId: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)