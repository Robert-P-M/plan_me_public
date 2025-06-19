package at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history.CreateTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("ToggleCompleteTaskUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToggleCompleteTaskUseCaseImplTest {


    @MockK
    private lateinit var transactionProvider: TransactionProvider


    @MockK
    private lateinit var createTaskCompletedHistoryUseCase: CreateTaskCompletedHistoryUseCase

    @MockK
    private lateinit var changeTaskCompletedUseCase: ChangeTaskCompletedUseCase

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    @MockK
    private lateinit var getTaskModelUseCase: GetTaskModelUseCase

    private lateinit var toggleCompleteTaskUseCase: ToggleCompleteTaskUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            transactionProvider,
            createTaskCompletedHistoryUseCase,
            changeTaskCompletedUseCase,
            safeDatabaseResultCall,
            getTaskModelUseCase
        )
        toggleCompleteTaskUseCase = ToggleCompleteTaskUseCaseImpl(
            transactionProvider = transactionProvider,
            createTaskCompletedHistoryUseCase = createTaskCompletedHistoryUseCase,
            changeTaskCompletedUseCase = changeTaskCompletedUseCase,
            safeDatabaseResultCall = safeDatabaseResultCall,
            getTaskModelUseCase = getTaskModelUseCase,
        )
        coEvery {
            transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
        coEvery {
            safeDatabaseResultCall<Unit>(callerTag = any(), block = captureCoroutine())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }

    }

    @Nested
    @DisplayName("GIVEN all dependencies succeed")
    inner class SuccessPath {
        @Test
        @DisplayName("invoke() with valid model should return Success and verify correct interactions")
        fun `invoke - successful insert - returns Success`() = runTest {

            val testTime = Instant.parse("2024-01-01T00:00:00Z")
            // GIVEN
            val taskId = 1L

            coEvery { getTaskModelUseCase.invoke(taskId) } returns TaskModel(
                taskId,
                title = "",
                createdAt = testTime,
                updatedAt = testTime,
                isCompleted = false,
                isArchived = false
            )
            coEvery { changeTaskCompletedUseCase.invoke(taskId, any()) } returns AppResult.Success(
                Unit
            )
            coEvery {
                createTaskCompletedHistoryUseCase.invoke(
                    taskId,
                    any()
                )
            } returns AppResult.Success(Unit)

            // WHEN
            val result = toggleCompleteTaskUseCase.invoke(taskId)

            // THEN
            assertAll(
                "Verify successful result and all interactions",
                { assertThat(result).isInstanceOf(AppResult.Success::class.java) },

                { coVerify(exactly = 1) { changeTaskCompletedUseCase.invoke(taskId, any()) } },
                {
                    coVerify(exactly = 1) {
                        createTaskCompletedHistoryUseCase.invoke(
                            taskId,
                            any()
                        )
                    }
                }
            )
        }
    }

    @Nested
    @DisplayName("GIVEN a subsequent dependency fails")
    inner class FailurePath {

        /**
         * GIVEN the first worker use case (`changeTaskCompletedUseCase`) returns an error.
         * WHEN the `invoke` method is called.
         * THEN the entire operation should fail immediately, returning the specific error,
         * and the subsequent worker use cases should not be called at all.
         */
        @Test
        @DisplayName("WHEN a worker use case fails THEN should return the error and stop execution")
        fun `invoke - subsequent operation fails - returns Error`() = runTest {
            // GIVEN
            val testTime = Instant.parse("2024-01-01T00:00:00Z")
            val taskId = 1L
            val expectedErrorResult = AppResult.Error(RoomDatabaseError.NO_TASK_FOUND)

            coEvery { getTaskModelUseCase.invoke(taskId) } returns TaskModel(
                taskId,
                title = "",
                createdAt = testTime,
                updatedAt = testTime,
                isCompleted = false,
                isArchived = false
            )
            coEvery { changeTaskCompletedUseCase.invoke(taskId, any()) } returns expectedErrorResult
            coEvery {
                createTaskCompletedHistoryUseCase.invoke(
                    taskId,
                    any()
                )
            } returns AppResult.Success(Unit)

            // WHEN
            val result = toggleCompleteTaskUseCase.invoke(taskId)

            // THEN
            assertAll(
                "Verify failing result and correct interaction chain",
                { assertThat(result).isEqualTo(expectedErrorResult) },
                { coVerify(exactly = 1) { changeTaskCompletedUseCase.invoke(taskId, any()) } },
                { coVerify(exactly = 0) { createTaskCompletedHistoryUseCase.invoke(any(), any()) } }
            )
        }
    }

}