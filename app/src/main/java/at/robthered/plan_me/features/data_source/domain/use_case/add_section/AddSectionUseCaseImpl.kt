package at.robthered.plan_me.features.data_source.domain.use_case.add_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import kotlinx.datetime.Clock

class AddSectionUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localSectionRepository: LocalSectionRepository,
    private val localSectionTitleHistoryRepository: LocalSectionTitleHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : AddSectionUseCase {
    override suspend operator fun invoke(addSectionModel: AddSectionModel): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "AddSectionUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val createdAt = clock.now()
                val sectionModel = SectionModel(
                    title = addSectionModel.title,
                    createdAt = createdAt,
                    updatedAt = createdAt,
                )
                val newSectionId = localSectionRepository
                    .insert(
                        sectionModel = sectionModel
                    )
                val sectionTitleHistoryModel = SectionTitleHistoryModel(
                    sectionId = newSectionId,
                    text = addSectionModel.title,
                    createdAt = createdAt
                )
                localSectionTitleHistoryRepository
                    .insert(
                        sectionTitleHistoryModel = sectionTitleHistoryModel
                    )

                AppResult.Success(Unit)
            }
        }
    }
}