package at.robthered.plan_me.features.data_source.domain.use_case.update_section_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCase
import kotlinx.datetime.Clock

class UpdateSectionTitleUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localSectionRepository: LocalSectionRepository,
    private val localSectionTitleHistoryRepository: LocalSectionTitleHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val getSectionModelUseCase: GetSectionModelUseCase,
    private val clock: Clock = Clock.System,
) : UpdateSectionTitleUseCase {
    override suspend fun invoke(updateSectionTitleModel: UpdateSectionTitleModel): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "UpdateSectionTitleUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {

                val sectionModel =
                    getSectionModelUseCase(sectionId = updateSectionTitleModel.sectionId)
                        ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_SECTION_FOUND)

                val createdAt = clock.now()
                val sectionToUpdate = sectionModel.copy(
                    title = updateSectionTitleModel.title,
                    updatedAt = createdAt,
                )
                localSectionRepository.upsert(sectionToUpdate)
                val sectionTitleHistoryEntry = SectionTitleHistoryModel(
                    sectionId = updateSectionTitleModel.sectionId,
                    text = updateSectionTitleModel.title,
                    createdAt = createdAt
                )
                localSectionTitleHistoryRepository.insert(sectionTitleHistoryEntry)
                AppResult.Success(Unit)

            }
        }
    }
}