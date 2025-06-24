package at.robthered.plan_me.features.data_source.domain.model.add_section

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddSectionModel(
    val title: String = "",
) : Parcelable