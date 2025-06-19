package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority


import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history.ChangeTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
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

@DisplayName("ChangeTaskPriorityUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChangeTaskPriorityUseCaseImplTest {

    @MockK
    private lateinit var transactionProvider: TransactionProvider

    @MockK
    private lateinit var changeTaskPriorityHistoryUseCase: ChangeTaskPriorityHistoryUseCase

    @MockK
    private lateinit var createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase

    @MockK
    private lateinit var getTaskModelUseCase: GetTaskModelUseCase

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var changeTaskPriorityUseCase: ChangeTaskPriorityUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            transactionProvider,
            changeTaskPriorityHistoryUseCase,
            createTaskPriorityHistoryUseCase,
            safeDatabaseResultCall,
            getTaskModelUseCase
        )
        changeTaskPriorityUseCase = ChangeTaskPriorityUseCaseImpl(
            transactionProvider,
            changeTaskPriorityHistoryUseCase = changeTaskPriorityHistoryUseCase,
            createTaskPriorityHistoryUseCase = createTaskPriorityHistoryUseCase,
            safeDatabaseResultCall = safeDatabaseResultCall,
            getTaskModelUseCase = getTaskModelUseCase
        )

        coEvery {
            safeDatabaseResultCall<Unit>(callerTag = any(), block = any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
        coEvery {
            transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN all dependent use cases succeed")
    inner class SuccessPath {
        /**
         * GIVEN all dependent use cases are mocked to return Success.
         * WHEN the use case is invoked.
         * THEN it should return Success and verify that all dependent use cases were called in the correct order.
         */
        @Test
        @DisplayName("WHEN invoked THEN should call all child use cases in order and return Success")
        fun `orchestrates all use cases successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val priority = PriorityEnum.HIGH

            coEvery { getTaskModelUseCase.invoke(taskId) } returns TaskModel(
                taskId = taskId,
                sectionId = null,
                parentTaskId = null,
                createdAt = Instant.DISTANT_PAST,
                updatedAt = Instant.DISTANT_PAST,
                isCompleted = false,
                isArchived = false,
                title = "Title"
            )
            coEvery {
                changeTaskPriorityHistoryUseCase.invoke(
                    taskId,
                    priority
                )
            } returns AppResult.Success(Unit)
            coEvery {
                createTaskPriorityHistoryUseCase.invoke(
                    taskId,
                    priority
                )
            } returns AppResult.Success(Unit)

            // WHEN
            val result = changeTaskPriorityUseCase.invoke(priority, taskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                getTaskModelUseCase.invoke(taskId)
                changeTaskPriorityHistoryUseCase.invoke(taskId, priority)
                createTaskPriorityHistoryUseCase.invoke(taskId, priority)
            }
        }
    }

    @Nested
    @DisplayName("GIVEN a dependent use case fails")
    inner class FailurePath {
        /**
         * GIVEN the first dependent use case fails.
         * WHEN the use case is invoked.
         * THEN it should return the error from the first use case and not call any subsequent use cases.
         */
        @Test
        @DisplayName("WHEN first child use case fails THEN should return its error and stop execution")
        fun `stops execution on first failure`() = runTest {
            // GIVEN
            val taskId = 1L
            val priority = PriorityEnum.MEDIUM
            val expectedError = AppResult.Error(RoomDatabaseError.NO_TASK_FOUND)

            coEvery { getTaskModelUseCase.invoke(taskId) } returns null
            coEvery {
                changeTaskPriorityHistoryUseCase.invoke(
                    taskId,
                    priority
                )
            } returns expectedError

            // WHEN
            val result = changeTaskPriorityUseCase.invoke(priority, taskId)

            // THEN
            assertThat(result).isEqualTo(expectedError)

            coVerify(exactly = 1) { getTaskModelUseCase.invoke(taskId) }
            coVerify(exactly = 0) { changeTaskPriorityHistoryUseCase.invoke(taskId, priority) }
            coVerify(exactly = 0) { createTaskPriorityHistoryUseCase.invoke(any(), any()) }
        }
    }
}