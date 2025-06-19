package at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("GetRootTaskModelsUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetRootTaskModelsUseCaseImplTest {

    @MockK
    private lateinit var localTaskRepository: LocalTaskRepository

    @MockK
    private lateinit var combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase

    private lateinit var getRootTaskModelsUseCase: GetRootTaskModelsUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskRepository, combineTasksWithHashtagsUseCase)
        getRootTaskModelsUseCase = GetRootTaskModelsUseCaseImpl(
            localTaskRepository,
            combineTasksWithHashtagsUseCase
        )
    }

    /**
     * GIVEN the repository emits a list of tasks.
     * WHEN the use case is invoked.
     * THEN it should first emit an empty list (from onStart), then call the combine use case,
     * and finally emit the combined list of tasks with hashtags.
     */
    @Test
    @DisplayName("WHEN repository emits tasks THEN should process and emit combined models")
    fun `correctly processes repository emissions`() = runTest {
        // GIVEN
        val sectionId = 1L
        val repoTasks = listOf(createTask(1L, "Task 1"))
        val finalCombinedTasks = listOf(createTaskWithHashtags(1L, "Task 1"))

        every { localTaskRepository.getRootTaskModels(any(), any(), any()) } returns flowOf(
            repoTasks
        )

        every { combineTasksWithHashtagsUseCase.invoke(emptyList()) } returns flowOf(emptyList())
        every { combineTasksWithHashtagsUseCase.invoke(repoTasks) } returns flowOf(
            finalCombinedTasks
        )

        // WHEN
        val resultFlow = getRootTaskModelsUseCase.invoke(null, null, SortDirection.ASC)

        // THEN
        resultFlow.test {
            assertThat(awaitItem()).isEmpty()

            assertThat(awaitItem()).isEqualTo(finalCombinedTasks)

            awaitComplete()
        }

        verify(exactly = 1) { combineTasksWithHashtagsUseCase.invoke(repoTasks) }
    }

    private fun createTask(id: Long, title: String) = TaskModel(
        taskId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
        isCompleted = false,
        isArchived = false
    )

    private fun createTaskWithHashtags(id: Long, title: String) = TaskWithUiHashtagsModel(
        task = createTask(id, title),
        hashtags = emptyList()
    )
}