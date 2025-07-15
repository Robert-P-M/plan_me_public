package at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
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


@DisplayName("ChangeTaskArchivedHistoryUseCaseImpl Tests")
class ChangeTaskArchivedHistoryUseCaseImplTest : BaseKoinTest() {

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    private val clock: Clock by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override val useCaseModule: Module
        get() = module {
            factoryOf(::ChangeTaskArchivedHistoryUseCaseImpl) {
                bind<ChangeTaskArchivedHistoryUseCase>()
            }
        }
    private val changeTaskArchivedHistoryUseCase: ChangeTaskArchivedHistoryUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            localTaskRepository,
            getTaskModelUseCase,
            clock,
        )
    }


    @Nested
    @DisplayName("GIVEN the task exists")
    inner class TaskExists {
        /**
         * GIVEN an existing task is found.
         * WHEN invoke is called with a new 'isArchived' status.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel
         * (new isArchived status and new updatedAt timestamp).
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new archived status and timestamp")
        fun `updates task correctly on success`() = runTest {
            // GIVEN
            val taskId = 1L
            val newArchivedState = true
            val testTime = Instant.parse("2025-07-01T10:00:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "Initial Task",
                isArchived = false,
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isCompleted = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            every { clock.now() } returns testTime
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = changeTaskArchivedHistoryUseCase.invoke(taskId, newArchivedState)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            assertThat(capturedTask.isArchived).isEqualTo(newArchivedState)
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
         * THEN it should immediately return a NO_TASK_FOUND error and not perform any repository writes.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return NO_TASK_FOUND error and not call repository")
        fun `returns error when task not found`() = runTest {
            // GIVEN
            val testTime = Instant.parse("2025-07-01T10:00:00Z")
            val taskId = 404L
            coEvery { getTaskModelUseCase.invoke(taskId) } returns null
            every { clock.now() } returns testTime

            // WHEN
            val result = changeTaskArchivedHistoryUseCase.invoke(taskId, true)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}