package at.robthered.plan_me.features.data_source.domain.use_case.get_task_model

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("GetTaskModelUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTaskModelUseCaseImplTest {

    @MockK
    private lateinit var localTaskRepository: LocalTaskRepository

    private lateinit var getTaskModelUseCase: GetTaskModelUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskRepository)
        getTaskModelUseCase = GetTaskModelUseCaseImpl(localTaskRepository)
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