package at.robthered.plan_me.features.data_source.domain.use_case.delete_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
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

@DisplayName("DeleteTaskUseCaseImpl Tests")
class DeleteTaskUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteTaskUseCaseImpl) {
                bind<DeleteTaskUseCase>()
            }
        }
    override val mockTransactionProvider: Boolean
        get() = true
    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    private val deleteTaskUseCase: DeleteTaskUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            transactionProvider,
            safeDatabaseResultCall,
        )
    }

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
        fun `returns Success when task is deleted`() = runTest {
            // GIVEN
            val taskId = 1L
            coEvery { localTaskRepository.delete(taskId) } returns 1

            // WHEN
            val result = deleteTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localTaskRepository.delete(taskId) }
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call 'fails'")
    inner class DeletionFails {
        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_TASK_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_TASK_FOUND error")
        fun `returns Error when task is not found`() = runTest {
            // GIVEN
            val taskId = 404L
            coEvery { localTaskRepository.delete(taskId) } returns 0

            // WHEN
            val result = deleteTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)
            coVerify(exactly = 1) { localTaskRepository.delete(taskId) }
        }

        /**
         * GIVEN the repository's delete method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall should handle it and return an Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns Error when repository throws exception`() = runTest {
            // GIVEN
            val taskId = 1L
            val exception = RuntimeException("Database offline")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)
            coEvery { localTaskRepository.delete(taskId) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = deleteTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}