package at.robthered.plan_me.features.data_source.domain.use_case.update_task_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("UpdateTaskTitleUseCaseImpl Tests")
class UpdateTaskTitleUseCaseImplTest: BaseKoinTest() {

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getTaskModelUseCase,
            localTaskRepository,
            safeDatabaseResultCall
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::UpdateTaskTitleUseCaseImpl){
                bind<UpdateTaskTitleUseCase>()
            }
        }

    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase by inject()



    @Nested
    @DisplayName("GIVEN the task exists")
    inner class TaskExists {
        /**
         * GIVEN an existing task is found.
         * WHEN invoke is called with a new title.
         * THEN it should call the repository's upsert method with a correctly updated TaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should upsert task with new title and timestamp")
        fun `updates task title successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val newTitle = "This is the updated title."
            val testTime = Instant.parse("2025-09-01T15:30:00Z")
            val initialTask = TaskModel(
                taskId = taskId,
                title = "Initial Title",
                description = "Some description",
                updatedAt = Instant.DISTANT_PAST,
                createdAt = Instant.DISTANT_PAST,
                isCompleted = false,
                isArchived = false
            )

            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            coEvery { localTaskRepository.upsert(any()) } returns Unit

            val taskModelSlot = slot<TaskModel>()

            // WHEN
            val result = updateTaskTitleUseCase.invoke(taskId, newTitle, testTime)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskRepository.upsert(capture(taskModelSlot)) }

            val capturedTask = taskModelSlot.captured
            assertThat(capturedTask.title).isEqualTo(newTitle)
            assertThat(capturedTask.updatedAt).isEqualTo(testTime)
            assertThat(capturedTask.description).isEqualTo(initialTask.description) // Unverändert
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
            val result = updateTaskTitleUseCase.invoke(taskId, "new title", Instant.DISTANT_PAST)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            coVerify(exactly = 0) { localTaskRepository.upsert(any()) }
        }
    }
}