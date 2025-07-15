package at.robthered.plan_me.features.data_source.domain.model.hashtag

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiHashtagModel : Parcelable {
    @Parcelize
    data class NewHashTagModel(
        val index: Int? = null,
        val name: String = "",
    ) : UiHashtagModel()

    @Parcelize
    data class ExistingHashtagModel(
        val hashtagId: Long,
        val name: String,
    ) : UiHashtagModel()

    @Parcelize
    data class FoundHashtagModel(
        val hashtagId: Long,
        val name: String,
    ) : UiHashtagModel()


}