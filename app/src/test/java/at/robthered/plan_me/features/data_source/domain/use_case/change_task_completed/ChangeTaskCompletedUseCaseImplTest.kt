package at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("ChangeTaskCompletedUseCaseImpl Tests")
class ChangeTaskCompletedUseCaseImplTest : BaseKoinTest() {

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    private val clock: Clock by inject()
    private val changeTaskCompletedUseCase: ChangeTaskCompletedUseCase by inject()

    override val useCaseModule: Module = module {
        factoryOf(::ChangeTaskCompletedUseCaseImpl) {
            bind<ChangeTaskCompletedUseCase>()
        }
    }

    override fun getMocks(): Array<Any> = arrayOf(
        safeDatabaseResultCall,
        getTaskModelUseCase,
        localTaskRepository,
        clock
    )

    override val mockSafeDatabaseResultCall: Boolean = true

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
            val testTime = Instant.Companion.parse("2025-07-01T10:00:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "Initial Task",
                isCompleted = false,
                updatedAt = Instant.Companion.DISTANT_PAST,
                createdAt = Instant.Companion.DISTANT_PAST,
                isArchived = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            every { clock.now() } returns testTime
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = changeTaskCompletedUseCase.invoke(taskId, newCompletedState)

            // THEN
            Truth.assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            Truth.assertThat(capturedTask.taskId).isEqualTo(taskId)
            Truth.assertThat(capturedTask.title).isEqualTo(initialTask.title)
            Truth.assertThat(capturedTask.isCompleted).isEqualTo(newCompletedState)
            Truth.assertThat(capturedTask.updatedAt).isEqualTo(testTime)
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
            val testTime = Instant.Companion.parse("2025-07-01T10:00:00Z")
            val taskId = 404L
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null
            every { clock.now() } returns testTime

            // WHEN
            val result = changeTaskCompletedUseCase.invoke(taskId, true)

            // THEN
            Truth.assertThat(result).isInstanceOf(AppResult.Error::class.java)
            Truth.assertThat((result as AppResult.Error).error)
                .isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}