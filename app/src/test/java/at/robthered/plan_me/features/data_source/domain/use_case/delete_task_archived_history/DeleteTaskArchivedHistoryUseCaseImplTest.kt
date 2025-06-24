package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("DeleteTaskArchivedHistoryUseCaseImpl Tests")
class DeleteTaskArchivedHistoryUseCaseImplTest: BaseKoinTest() {

    private val localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true
    private val deleteTaskArchivedHistoryUseCase: DeleteTaskArchivedHistoryUseCase by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteTaskArchivedHistoryUseCaseImpl) {
                bind<DeleteTaskArchivedHistoryUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskArchivedHistoryRepository,
            safeDatabaseResultCall,
            transactionProvider,
        )
    }

    @Nested
    @DisplayName("GIVEN repository interaction")
    inner class RepositoryInteraction {
        /**
         * GIVEN the repository's delete method returns 1 (one row affected).
         * WHEN the use case is invoked.
         * THEN it should return AppResult.Success.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 1 THEN should return Success")
        fun `returns Success when history is deleted`() = runTest {
            // GIVEN
            val historyId = 1L
            coEvery { localTaskArchivedHistoryRepository.delete(historyId) } returns 1

            // WHEN
            val result = deleteTaskArchivedHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localTaskArchivedHistoryRepository.delete(historyId) }
        }

        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a specific error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return specific not found error")
        fun `returns Error when history is not found`() = runTest {
            // GIVEN
            val historyId = 404L
            coEvery { localTaskArchivedHistoryRepository.delete(historyId) } returns 0

            // WHEN
            val result = deleteTaskArchivedHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_ARCHIVED_HISTORY_FOUND)
            coVerify(exactly = 1) { localTaskArchivedHistoryRepository.delete(historyId) }
        }
    }
}