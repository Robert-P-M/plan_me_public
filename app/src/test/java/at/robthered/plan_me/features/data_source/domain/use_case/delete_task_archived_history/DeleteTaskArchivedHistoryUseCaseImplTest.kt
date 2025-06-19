package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
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

@DisplayName("DeleteTaskArchivedHistoryUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteTaskArchivedHistoryUseCaseImplTest {

    @MockK
    private lateinit var localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository

    @MockK
    private lateinit var transactionProvider: TransactionProvider

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var deleteTaskArchivedHistoryUseCase: DeleteTaskArchivedHistoryUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskArchivedHistoryRepository, transactionProvider, safeDatabaseResultCall)
        deleteTaskArchivedHistoryUseCase = DeleteTaskArchivedHistoryUseCaseImpl(
            localTaskArchivedHistoryRepository,
            transactionProvider,
            safeDatabaseResultCall
        )

        coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
        coEvery { transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
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