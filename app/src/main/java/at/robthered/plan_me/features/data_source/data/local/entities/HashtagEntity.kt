package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "hashtag"
)
data class HashtagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Long = 0L,
    val name: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)