package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("DeleteTaskPriorityHistoryUseCaseImpl Tests")
class DeleteTaskPriorityHistoryUseCaseImplTest: BaseKoinTest() {

    private val localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteTaskPriorityHistoryUseCaseImpl) {
                bind<DeleteTaskPriorityHistoryUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            transactionProvider,
            localTaskPriorityHistoryRepository
        )
    }
    private val deleteTaskPriorityHistoryUseCase: DeleteTaskPriorityHistoryUseCase by inject()

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
            coEvery { localTaskPriorityHistoryRepository.delete(historyId) } returns 1

            // WHEN
            val result = deleteTaskPriorityHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localTaskPriorityHistoryRepository.delete(historyId) }
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call 'fails' or finds nothing")
    inner class DeletionFails {
        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_TASK_PRIORITY_HISTORY_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_TASK_PRIORITY_HISTORY_FOUND error")
        fun `returns Error when history is not found`() = runTest {
            // GIVEN
            val historyId = 404L
            coEvery { localTaskPriorityHistoryRepository.delete(historyId) } returns 0

            // WHEN
            val result = deleteTaskPriorityHistoryUseCase.invoke(historyId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_PRIORITY_HISTORY_FOUND)
            coVerify(exactly = 1) { localTaskPriorityHistoryRepository.delete(historyId) }
        }
    }
}