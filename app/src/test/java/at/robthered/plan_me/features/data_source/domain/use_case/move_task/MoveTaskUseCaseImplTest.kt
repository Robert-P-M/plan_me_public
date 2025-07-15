package at.robthered.plan_me.features.data_source.domain.use_case.move_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskUseCaseArgs
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
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("MoveTaskUseCaseImpl Tests")
class MoveTaskUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val getTaskModelUseCase: GetTaskModelUseCase by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true
    private val clock: Clock by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            transactionProvider,
            getTaskModelUseCase,
            safeDatabaseResultCall
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::MoveTaskUseCaseImpl){
                bind<MoveTaskUseCase>()
            }
        }

    private val moveTaskUseCase: MoveTaskUseCase by inject()


    @Nested
    @DisplayName("GIVEN the task to be moved exists")
    inner class TaskExists {
        /**
         * GIVEN an existing task is found.
         * WHEN invoke is called with new section and parent task IDs.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new location and timestamp")
        fun `updates task location successfully`() = runTest {
            // GIVEN
            val moveArgs = MoveTaskUseCaseArgs(taskId = 1L, sectionId = 10L, parentTaskId = 20L)

            val initialTask = TaskModel(
                taskId = 1L,
                title = "My Task",
                sectionId = 1L,
                parentTaskId = null,
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isCompleted = false,
                isArchived = false,
            )

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { getTaskModelUseCase.invoke(moveArgs.taskId) } returns initialTask
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = moveTaskUseCase.invoke(moveArgs)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured

            assertThat(capturedTask.sectionId).isEqualTo(moveArgs.sectionId)
            assertThat(capturedTask.parentTaskId).isEqualTo(moveArgs.parentTaskId)
            assertThat(capturedTask.updatedAt).isEqualTo(Instant.DISTANT_PAST)

            assertThat(capturedTask.title).isEqualTo(initialTask.title)
        }
    }

    @Nested
    @DisplayName("GIVEN the task to be moved does not exist")
    inner class TaskDoesNotExist {
        /**
         * GIVEN getTaskModelUseCase returns null.
         * WHEN invoke is called.
         * THEN it should return a NO_TASK_FOUND error and not perform any repository writes.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return NO_TASK_FOUND error")
        fun `returns error when task is not found`() = runTest {
            // GIVEN
            val moveArgs = MoveTaskUseCaseArgs(taskId = 404L, sectionId = 10L, parentTaskId = null)

            every { clock.now() } returns Instant.DISTANT_PAST

            coEvery { getTaskModelUseCase.invoke(moveArgs.taskId) } returns null

            // WHEN
            val result = moveTaskUseCase.invoke(moveArgs)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}