package at.robthered.plan_me.features.data_source.domain.model.update_hashtag

import android.os.Parcelable
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateHashtagModelError(
    val name: HashtagNameValidationError? = null,
) : Parcelable