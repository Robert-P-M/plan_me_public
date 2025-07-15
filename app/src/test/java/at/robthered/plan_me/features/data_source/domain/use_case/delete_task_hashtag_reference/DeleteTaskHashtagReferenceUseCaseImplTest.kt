package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
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

@DisplayName("DeleteTaskHashtagReferenceUseCaseImpl Tests")
class DeleteTaskHashtagReferenceUseCaseImplTest: BaseKoinTest() {

    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true
    private val deleteTaskHashtagReferenceUseCase: DeleteTaskHashtagReferenceUseCase by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteTaskHashtagReferenceUseCaseImpl){
                bind<DeleteTaskHashtagReferenceUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskHashtagsCrossRefRepository,
            safeDatabaseResultCall,
            transactionProvider,
        )
    }

    @Nested
    @DisplayName("GIVEN the repository delete call is successful")
    inner class DeletionSucceeds {
        /**
         * GIVEN the repository's deleteCrossRef method returns 1 (one row affected).
         * WHEN the use case is invoked.
         * THEN it should return AppResult.Success.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 1 THEN should return Success")
        fun `returns Success when cross-ref is deleted`() = runTest {
            // GIVEN
            val taskId = 1L
            val hashtagId = 10L
            coEvery {
                localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId,
                    hashtagId
                )
            } returns 1

            // WHEN
            val result = deleteTaskHashtagReferenceUseCase.invoke(taskId, hashtagId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) {
                localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId,
                    hashtagId
                )
            }
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call 'fails' or finds nothing")
    inner class DeletionFails {
        /**
         * GIVEN the repository's deleteCrossRef method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_TASK_HASHTAG_CROSS_REF_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_TASK_HASHTAG_CROSS_REF_FOUND error")
        fun `returns Error when cross-ref is not found`() = runTest {
            // GIVEN
            val taskId = 404L
            val hashtagId = 404L
            coEvery {
                localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId,
                    hashtagId
                )
            } returns 0

            // WHEN
            val result = deleteTaskHashtagReferenceUseCase.invoke(taskId, hashtagId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_HASHTAG_CROSS_REF_FOUND)
            coVerify(exactly = 1) {
                localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId,
                    hashtagId
                )
            }
        }

        /**
         * GIVEN the repository's deleteCrossRef method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall should handle it and return an Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns Error when repository throws exception`() = runTest {
            // GIVEN
            val taskId = 1L
            val hashtagId = 10L
            val exception = RuntimeException("Database offline")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)
            coEvery {
                localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId,
                    hashtagId
                )
            } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = deleteTaskHashtagReferenceUseCase.invoke(taskId, hashtagId)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}