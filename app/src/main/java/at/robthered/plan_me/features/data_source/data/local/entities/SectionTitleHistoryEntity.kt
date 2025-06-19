package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "section_title_history",
    indices = [
        Index(
            value = ["section_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["section_id"],
            childColumns = ["section_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SectionTitleHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "section_title_history_id")
    val sectionTitleHistoryId: Long = 0L,
    @ColumnInfo(name = "section_id")
    val sectionId: Long,
    @ColumnInfo("text")
    val text: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)