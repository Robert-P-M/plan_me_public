package at.robthered.plan_me.features.data_source.domain.use_case.delete_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository

class DeleteSectionUseCaseImpl(
    private val localSectionRepository: LocalSectionRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteSectionUseCase {
    override suspend operator fun invoke(sectionId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteSectionUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected = localSectionRepository.delete(sectionId = sectionId)
                if (rowsAffected > 0)
                    return@runAsTransaction AppResult.Success(Unit)

                AppResult.Error(error = RoomDatabaseError.NO_SECTION_FOUND)

            }
        }
    }
}