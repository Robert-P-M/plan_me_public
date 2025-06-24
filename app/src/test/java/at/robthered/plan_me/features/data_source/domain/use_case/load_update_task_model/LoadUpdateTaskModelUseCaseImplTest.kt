package at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadUpdateTaskModelUseCaseImpl Tests")
class LoadUpdateTaskModelUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadUpdateTaskModelUseCaseImpl){
                bind<LoadUpdateTaskModelUseCase>()
            }
        }
    private val loadUpdateTaskModelUseCase: LoadUpdateTaskModelUseCase by inject()


    @Nested
    @DisplayName("GIVEN the task exists in repository")
    inner class TaskFound {
        /**
         * GIVEN the repository returns a flow containing a valid TaskModel.
         * WHEN the use case is invoked.
         * THEN it should emit the correctly mapped UpdateTaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should map and emit the correct UpdateTaskModel")
        fun `maps existing task to UpdateTaskModel`() = runTest {
            // GIVEN
            val taskId = 1L
            val now = Clock.System.now()
            val taskFromRepo = TaskModel(
                taskId = taskId,
                title = "My Task Title",
                description = "My Task Description",
                priorityEnum = PriorityEnum.HIGH,
                createdAt = now,
                updatedAt = now,
                isArchived = false,
                isCompleted = false,
            )
            val expectedUpdateModel = taskFromRepo.toUpdateTaskModel()

            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(taskFromRepo)

            // WHEN
            val resultFlow = loadUpdateTaskModelUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedUpdateModel)

                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN the task does not exist in repository")
    inner class TaskNotFound {
        /**
         * GIVEN the repository returns a flow containing null.
         * WHEN the use case is invoked.
         * THEN it should emit a default, empty UpdateTaskModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should emit a default UpdateTaskModel")
        fun `emits default model when task not found`() = runTest {
            // GIVEN
            val taskId = 404L
            val expectedDefaultModel = UpdateTaskModel()

            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(null)

            // WHEN
            val resultFlow = loadUpdateTaskModelUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedDefaultModel)
                awaitComplete()
            }
        }
    }
}