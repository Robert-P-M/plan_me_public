package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
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

@DisplayName("ChangeTaskPriorityHistoryUseCaseImpl Tests")
class ChangeTaskPriorityHistoryUseCaseImplTest : BaseKoinTest() {

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    private val clock: Clock by inject()
    private val changeTaskPriorityHistoryUseCase: ChangeTaskPriorityHistoryUseCase by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            getTaskModelUseCase,
            localTaskRepository,
            clock
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::ChangeTaskPriorityHistoryUseCaseImpl) {
                bind<ChangeTaskPriorityHistoryUseCase>()
            }
        }


    @Nested
    @DisplayName("GIVEN the task exists")
    inner class TaskExists {
        /**
         * GIVEN an existing task is found.
         * WHEN invoke is called with a new priority.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new priority and timestamp")
        fun `updates task correctly on success`() = runTest {
            // GIVEN
            val taskId = 1L
            val newPriority = PriorityEnum.HIGH
            val testTime = Instant.parse("2025-07-01T10:00:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "Initial Task",
                priorityEnum = PriorityEnum.LOW, // Ursprünglicher Zustand
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isCompleted = false,
                isArchived = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            every { clock.now() } returns testTime
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = changeTaskPriorityHistoryUseCase.invoke(taskId, newPriority)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            assertThat(capturedTask.priorityEnum).isEqualTo(newPriority) // Priorität sollte aktualisiert sein
            assertThat(capturedTask.updatedAt).isEqualTo(testTime) // Zeitstempel sollte aktualisiert sein
            assertThat(capturedTask.title).isEqualTo(initialTask.title) // Unveränderte Felder bleiben gleich
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
            val taskId = 404L
            val testTime = Instant.parse("2025-06-16T12:00:00Z")

            every { clock.now() } returns testTime
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null

            // WHEN
            val result = changeTaskPriorityHistoryUseCase.invoke(taskId, PriorityEnum.HIGH)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}