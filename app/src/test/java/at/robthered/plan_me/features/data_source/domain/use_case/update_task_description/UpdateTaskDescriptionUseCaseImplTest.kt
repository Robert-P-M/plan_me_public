package at.robthered.plan_me.features.data_source.domain.use_case.update_task_description

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("UpdateTaskDescriptionUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateTaskDescriptionUseCaseImplTest {

    @MockK
    private lateinit var getTaskModelUseCase: GetTaskModelUseCase

    @MockK
    private lateinit var localTaskRepository: LocalTaskRepository

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var updateTaskDescriptionUseCase: UpdateTaskDescriptionUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(getTaskModelUseCase, localTaskRepository, safeDatabaseResultCall)
        updateTaskDescriptionUseCase = UpdateTaskDescriptionUseCaseImpl(
            getTaskModelUseCase,
            localTaskRepository,
            safeDatabaseResultCall
        )

        coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN the task exists")
    inner class TaskExists {
        /**
         * GIVEN an existing task is found.
         * WHEN invoke is called with a new description.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new description and timestamp")
        fun `updates task description successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val newDescription = "This is the updated description."
            val testTime = Instant.parse("2025-09-01T15:00:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "My Task",
                description = "Initial description",
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isCompleted = false,
                isArchived = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = updateTaskDescriptionUseCase.invoke(taskId, newDescription, testTime)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            assertThat(capturedTask.description).isEqualTo(newDescription)
            assertThat(capturedTask.updatedAt).isEqualTo(testTime)
            assertThat(capturedTask.title).isEqualTo(initialTask.title)
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
        fun `returns error when task is not found`() = runTest {
            // GIVEN
            val taskId = 404L
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null

            // WHEN
            val result =
                updateTaskDescriptionUseCase.invoke(taskId, "new desc", Instant.DISTANT_PAST)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}