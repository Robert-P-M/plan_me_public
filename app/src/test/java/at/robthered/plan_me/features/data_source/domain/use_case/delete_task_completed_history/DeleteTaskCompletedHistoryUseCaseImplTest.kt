package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("DeleteTaskCompletedHistoryUseCaseImpl Tests")
class DeleteTaskCompletedHistoryUseCaseImplTest: BaseKoinTest() {

    private val localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository by inject()

    override val mockTransactionProvider: Boolean
        get() = true
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskCompletedHistoryRepository,
            transactionProvider,
            safeDatabaseResultCall,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteTaskCompletedHistoryUseCaseImpl) {
                bind<DeleteTaskCompletedHistoryUseCase>()
            }
        }

    private val deleteTaskCompletedHistoryUseCase: DeleteTaskCompletedHistoryUseCase by inject()


    @Nested
    @DisplayName("GIVEN the repository delete call is successful")
    inner class DeletionSucceeds {
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
            coEvery { localTaskCompletedHistoryRepository.delete(historyId) } returns 1

            // WHEN
            val result = deleteTaskCompletedHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localTaskCompletedHistoryRepository.delete(historyId) }
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call 'fails' or finds nothing")
    inner class DeletionFails {
        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_TASK_TITLE_HISTORY_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_TASK_TITLE_HISTORY_FOUND error")
        fun `returns Error when history is not found`() = runTest {
            // GIVEN
            val historyId = 404L
            coEvery { localTaskCompletedHistoryRepository.delete(historyId) } returns 0

            // WHEN
            val result = deleteTaskCompletedHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)

            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_TITLE_HISTORY_FOUND)
            coVerify(exactly = 1) { localTaskCompletedHistoryRepository.delete(historyId) }
        }
    }
}