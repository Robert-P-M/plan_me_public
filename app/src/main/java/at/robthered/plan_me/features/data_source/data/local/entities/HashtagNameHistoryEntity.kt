package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hashtag_name_history",
    indices = [
        Index(
            value = ["hashtag_id"]
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = HashtagEntity::class,
            parentColumns = ["hashtag_id"],
            childColumns = ["hashtag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class HashtagNameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hashtag_name_history_id")
    val hashtagNameHistoryId: Long = 0L,
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Long,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)