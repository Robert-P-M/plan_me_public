package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived.ChangeTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("ToggleArchiveTaskUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToggleArchiveTaskUseCaseImplTest {
    @MockK
    private lateinit var transactionProvider: TransactionProvider
    @MockK
    private lateinit var getTaskModelUseCase: GetTaskModelUseCase
    @MockK
    private lateinit var changeTaskArchivedHistoryUseCase: ChangeTaskArchivedHistoryUseCase
    @MockK
    private lateinit var createTaskArchivedHistoryUseCase: CreateTaskArchivedHistoryUseCase
    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var toggleArchiveTaskUseCase: ToggleArchiveTaskUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            transactionProvider,
            getTaskModelUseCase,
            changeTaskArchivedHistoryUseCase,
            createTaskArchivedHistoryUseCase,
            safeDatabaseResultCall
        )
        toggleArchiveTaskUseCase = ToggleArchiveTaskUseCaseImpl(
            transactionProvider,
            getTaskModelUseCase,
            changeTaskArchivedHistoryUseCase,
            createTaskArchivedHistoryUseCase,
            safeDatabaseResultCall
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
        /**
         * GIVEN a task exists and all subsequent use cases return Success.
         * WHEN invoke is called.
         * THEN the result should be Success, and all dependent use cases should be called in the correct order with the correct parameters.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return Success and verify all interactions")
        fun `invoke successful`() = runTest {

            val testTime = Instant.parse("2024-01-01T00:00:00Z")
            // GIVEN
            val taskId = 1L
            val initialArchivedState = false
            val fakeTask = TaskModel(
                taskId = taskId,
                isArchived = initialArchivedState,
                title = "Task",
                createdAt = testTime,
                updatedAt = testTime,
                isCompleted = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns fakeTask
            coEvery {
                changeTaskArchivedHistoryUseCase.invoke(
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery {
                createTaskArchivedHistoryUseCase.invoke(
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)

            // WHEN
            val result = toggleArchiveTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                getTaskModelUseCase.invoke(taskId)
                changeTaskArchivedHistoryUseCase.invoke(
                    taskId,
                    !initialArchivedState
                ) // Der neue, umgekehrte Status
                createTaskArchivedHistoryUseCase.invoke(taskId, !initialArchivedState)
            }
        }
    }

    @Nested
    @DisplayName("GIVEN a dependency fails")
    inner class FailurePaths {
        /**
         * GIVEN the initial GetTaskModelUseCase returns null (task not found).
         * WHEN invoke is called.
         * THEN the result should be a NO_TASK_FOUND error and no other use cases should be called.
         */
        @Test
        @DisplayName("WHEN task is not found THEN should return NO_TASK_FOUND error")
        fun `task not found`() = runTest {
            // GIVEN
            val taskId = 404L
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null

            // WHEN
            val result = toggleArchiveTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { changeTaskArchivedHistoryUseCase.invoke(any(), any()) }
            coVerify(exactly = 0) { createTaskArchivedHistoryUseCase.invoke(any(), any()) }
        }

        /**
         * GIVEN GetTaskModelUseCase succeeds, but ChangeTaskArchivedHistoryUseCase fails.
         * WHEN invoke is called.
         * THEN the result should be the error from the failing use case, and the final use case should not be called.
         */
        @Test
        @DisplayName("WHEN changing archive state fails THEN should return the specific error")
        fun `change archived state fails`() = runTest {
            // GIVEN
            val testTime = Instant.parse("2024-01-01T00:00:00Z")
            val taskId = 1L
            val fakeTask = TaskModel(
                taskId = taskId,
                isArchived = false,
                title = "Task",
                isCompleted = false,
                updatedAt = testTime,
                createdAt = testTime
            )
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            coEvery { getTaskModelUseCase.invoke(taskId) } returns fakeTask
            coEvery { changeTaskArchivedHistoryUseCase.invoke(any(), any()) } returns expectedError

            // WHEN
            val result = toggleArchiveTaskUseCase.invoke(taskId)

            // THEN
            assertThat(result).isEqualTo(expectedError)

            coVerify(ordering = Ordering.ORDERED) {
                getTaskModelUseCase.invoke(taskId)
                changeTaskArchivedHistoryUseCase.invoke(any(), any())
            }
            coVerify(exactly = 0) { createTaskArchivedHistoryUseCase.invoke(any(), any()) }
        }
    }
}