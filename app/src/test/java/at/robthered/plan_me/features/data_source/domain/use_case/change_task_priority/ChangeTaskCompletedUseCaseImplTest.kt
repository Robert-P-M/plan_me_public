package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed.ChangeTaskCompletedUseCaseImpl
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("ChangeTaskCompletedUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChangeTaskCompletedUseCaseImplTest {
    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    @MockK
    private lateinit var getTaskModelUseCase: GetTaskModelUseCase

    @MockK
    private lateinit var localTaskRepository: LocalTaskRepository

    @MockK
    private lateinit var clock: Clock

    private lateinit var changeTaskCompletedUseCase: ChangeTaskCompletedUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(safeDatabaseResultCall, getTaskModelUseCase, localTaskRepository, clock)
        changeTaskCompletedUseCase = ChangeTaskCompletedUseCaseImpl(
            safeDatabaseResultCall,
            getTaskModelUseCase,
            localTaskRepository,
            clock
        )

        coEvery {
            safeDatabaseResultCall<Unit>(callerTag = any(), block = any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN the task exists")
    inner class TaskExists {

        /**
         * GIVEN an existing task is returned by getTaskModelUseCase.
         * WHEN invoke is called with a new completion status.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new completed status and timestamp")
        fun `updates task correctly on success`() = runTest {
            // GIVEN
            val taskId = 1L
            val newCompletedState = true
            val testTime = Instant.parse("2025-07-01T10:00:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "Initial Task",
                isCompleted = false,
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isArchived = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            every { clock.now() } returns testTime
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = changeTaskCompletedUseCase.invoke(taskId, newCompletedState)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            assertThat(capturedTask.taskId).isEqualTo(taskId)
            assertThat(capturedTask.title).isEqualTo(initialTask.title)
            assertThat(capturedTask.isCompleted).isEqualTo(newCompletedState)
            assertThat(capturedTask.updatedAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN the task does not exist")
    inner class TaskDoesNotExist {

        /**
         * GIVEN getTaskModelUseCase returns null.
         * WHEN invoke is called.
         * THEN it should immediately return a NO_TASK_FOUND error and not call the repository.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return NO_TASK_FOUND error")
        fun `returns error when task not found`() = runTest {
            // GIVEN
            val testTime = Instant.parse("2025-07-01T10:00:00Z")
            val taskId = 404L
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null
            every { clock.now() } returns testTime

            // WHEN
            val result = changeTaskCompletedUseCase.invoke(taskId, true)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}