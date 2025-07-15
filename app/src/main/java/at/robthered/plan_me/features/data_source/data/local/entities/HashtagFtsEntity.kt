package at.robthered.plan_me.features.data_source.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "hashtag_fts")
@Fts4(contentEntity = HashtagEntity::class)
data class HashtagFtsEntity(
    @ColumnInfo(name = "name")
    val name: String,
)