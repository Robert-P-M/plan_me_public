package at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

interface AddHashtagHelper {
    suspend operator fun invoke(uiHashtagModel: UiHashtagModel.NewHashTagModel): Long
    suspend operator fun invoke(uiHashtagModels: List<UiHashtagModel.NewHashTagModel>): List<Long>
}