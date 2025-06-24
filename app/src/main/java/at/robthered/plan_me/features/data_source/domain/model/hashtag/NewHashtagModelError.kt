package at.robthered.plan_me.features.data_source.domain.model.hashtag

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewHashtagModelError(
    val name: HashtagNameValidationError? = null,
) : Parcelable