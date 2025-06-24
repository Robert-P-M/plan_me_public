package at.robthered.plan_me.features.data_source.domain.use_case.get_task_model

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("GetTaskModelUseCaseImpl Tests")
class GetTaskModelUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository
        )
    }

    private val getTaskModelUseCase: GetTaskModelUseCase by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetTaskModelUseCaseImpl) {
                bind<GetTaskModelUseCase>()
            }
        }


    @Nested
    @DisplayName("GIVEN the repository provides a result")
    inner class RepositoryProvidesResult {

        /**
         * GIVEN the repository returns a flow containing a valid TaskModel.
         * WHEN the use case is invoked.
         * THEN it should return that specific TaskModel object after taking the first emission.
         */
        @Test
        @DisplayName("WHEN repository finds a task THEN should return the task model")
        fun `returns task model when found`() = runTest {
            // GIVEN
            val taskId = 1L
            val expectedTask = createTask(taskId, "Found Task")

            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(expectedTask)

            // WHEN
            val result = getTaskModelUseCase.invoke(taskId)

            // THEN
            assertThat(result).isNotNull()
            assertThat(result).isEqualTo(expectedTask)
            verify(exactly = 1) { localTaskRepository.getTaskModelById(taskId) }
        }

        /**
         * GIVEN the repository returns a flow containing null.
         * WHEN the use case is invoked.
         * THEN it should return null.
         */
        @Test
        @DisplayName("WHEN repository finds no task THEN should return null")
        fun `returns null when not found`() = runTest {
            // GIVEN
            val taskId = 404L

            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(null)

            // WHEN
            val result = getTaskModelUseCase.invoke(taskId)

            // THEN
            assertThat(result).isNull()
            verify(exactly = 1) { localTaskRepository.getTaskModelById(taskId) }
        }

        @Test
        @DisplayName("WHEN repository finds task THEN should return TaskModel")
        fun `invoke returns TaskWithChildrenModel when task is found`() = runTest {

            val taskId = 1L
            val expectedTaskModel = TaskModel(
                taskId = taskId,
                parentTaskId = null,
                sectionId = null,
                title = "Test Task Title",
                description = "Test Task Description",
                isCompleted = false,
                isArchived = false,
                priorityEnum = PriorityEnum.NORMAL,
                taskSchedule = null,
                createdAt = Instant.DISTANT_PAST,
                updatedAt = Instant.DISTANT_PAST
            )

            val expectedTaskWithChildrenModel = expectedTaskModel

            coEvery { localTaskRepository.getTaskModelById(taskId = taskId) } returns flowOf(
                expectedTaskWithChildrenModel
            )


            val actualTaskWithChildrenModel = getTaskModelUseCase(taskId = taskId)


            assertEquals(
                expectedTaskWithChildrenModel, actualTaskWithChildrenModel,
                "The returned TaskWithChildrenModel should match the expected one."
            )
            assertEquals(
                taskId, actualTaskWithChildrenModel?.taskId,
                "The returned Task's ID should match the requested ID."
            )
        }
    }

    private fun createTask(id: Long, title: String) = TaskModel(
        taskId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
        isCompleted = false,
        isArchived = false
    )
}