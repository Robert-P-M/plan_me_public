package at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.Ordering
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

@DisplayName("DuplicateTaskUseCaseImpl Tests")
class DuplicateTaskUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()

    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase by inject()

    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase by inject()

    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true
    private val clock: Clock by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            transactionProvider,
                    localTaskRepository,
                    getTaskModelUseCase,
                    createTaskTitleHistoryUseCase,
                    createTaskDescriptionHistoryUseCase,
                    createTaskPriorityHistoryUseCase,
                    safeDatabaseResultCall,
                    clock,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DuplicateTaskUseCaseImpl) {
                bind<DuplicateTaskUseCase>()
            }
        }

    private val duplicateTaskUseCase: DuplicateTaskUseCase by inject()

    override fun additionalSetUp() {
        coEvery {
            createTaskTitleHistoryUseCase.invoke(
                any(),
                any(),
                any()
            )
        } returns AppResult.Success(Unit)
        coEvery {
            createTaskDescriptionHistoryUseCase.invoke(
                any(),
                any(),
                any()
            )
        } returns AppResult.Success(Unit)
        coEvery { createTaskPriorityHistoryUseCase.invoke(any(), any()) } returns AppResult.Success(
            Unit
        )
    }


    @Nested
    @DisplayName("GIVEN the original task exists")
    inner class TaskExists {

        /**
         * GIVEN the original task has a full set of data, including a description.
         * WHEN invoke is called.
         * THEN a new task should be inserted with modified data, and ALL relevant history use cases should be called.
         */
        @Test
        @DisplayName("WHEN task has description THEN should duplicate all data and create all history entries")
        fun `duplicates task with description`() = runTest {
            // GIVEN
            val originalTaskId = 1L
            val newTaskId = 2L
            val testTime = Instant.parse("2025-01-01T12:00:00Z")
            val originalTask = TaskModel(
                taskId = originalTaskId,
                title = "Original Task",
                description = "Original Description",
                priorityEnum = PriorityEnum.HIGH,
                isCompleted = false,
                isArchived = false,
                createdAt = Instant.DISTANT_PAST,
                updatedAt = Instant.DISTANT_PAST
            )
            val newTitle = "${originalTask.title} (duplicate)"

            coEvery { getTaskModelUseCase.invoke(originalTaskId) } returns originalTask
            every { clock.now() } returns testTime
            coEvery { localTaskRepository.insert(any()) } returns newTaskId

            val taskSlot = slot<TaskModel>()

            // WHEN
            val result = duplicateTaskUseCase.invoke(originalTaskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                getTaskModelUseCase.invoke(originalTaskId)
                localTaskRepository.insert(capture(taskSlot))
                createTaskTitleHistoryUseCase.invoke(newTaskId, newTitle, testTime)
                createTaskDescriptionHistoryUseCase.invoke(
                    newTaskId,
                    originalTask.description,
                    testTime
                )
                createTaskPriorityHistoryUseCase.invoke(newTaskId, originalTask.priorityEnum)
            }

            val capturedNewTask = taskSlot.captured
            assertThat(capturedNewTask.taskId).isEqualTo(0)
            assertThat(capturedNewTask.title).isEqualTo(newTitle)
            assertThat(capturedNewTask.createdAt).isEqualTo(testTime)
            assertThat(capturedNewTask.updatedAt).isEqualTo(testTime)
        }

        /**
         * GIVEN the original task does NOT have a description.
         * WHEN invoke is called.
         * THEN the use case for creating description history should NOT be called.
         */
        @Test
        @DisplayName("WHEN task has no description THEN should skip creating description history")
        fun `duplicates task without description`() = runTest {
            // GIVEN
            val originalTaskId = 1L
            val newTaskId = 2L
            val originalTask = TaskModel(
                taskId = originalTaskId,
                title = "Original Task",
                description = null,
                priorityEnum = PriorityEnum.LOW,
                isCompleted = false,
                isArchived = false,
                createdAt = Instant.DISTANT_PAST,
                updatedAt = Instant.DISTANT_PAST
            )

            coEvery { getTaskModelUseCase.invoke(originalTaskId) } returns originalTask
            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { localTaskRepository.insert(any()) } returns newTaskId

            // WHEN
            duplicateTaskUseCase.invoke(originalTaskId)

            // THEN
            coVerify(exactly = 0) {
                createTaskDescriptionHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            }

            coVerify(exactly = 1) { createTaskTitleHistoryUseCase.invoke(any(), any(), any()) }
            coVerify(exactly = 1) { createTaskPriorityHistoryUseCase.invoke(any(), any()) }
        }
    }

    @Nested
    @DisplayName("GIVEN the original task does not exist")
    inner class TaskDoesNotExist {
        /**
         * GIVEN getTaskModelUseCase returns null.
         * WHEN invoke is called.
         * THEN it should return a NO_TASK_FOUND error and no other operations should happen.
         */
        @Test
        @DisplayName("WHEN task is not found THEN should return error and do nothing")
        fun `returns error when task not found`() = runTest {
            // GIVEN
            val originalTaskId = 404L
            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { getTaskModelUseCase.invoke(originalTaskId) } returns null

            // WHEN
            val result = duplicateTaskUseCase.invoke(originalTaskId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.insert(any()) }
            coVerify(exactly = 0) { createTaskTitleHistoryUseCase.invoke(any(), any(), any()) }
        }
    }
}