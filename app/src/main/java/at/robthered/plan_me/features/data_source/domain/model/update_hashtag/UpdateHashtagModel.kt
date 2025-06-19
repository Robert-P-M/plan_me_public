package at.robthered.plan_me.features.data_source.domain.model.update_hashtag

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateHashtagModel(
    val hashtagId: Long = 0,
    val name: String = "",
) : Parcelable