package at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task

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

@DisplayName("GetTaskModelsForParentUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTaskModelsForParentUseCaseImplTest {

    @MockK
    private lateinit var localTaskRepository: LocalTaskRepository

    @MockK
    private lateinit var combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase

    private lateinit var getTaskModelsForParentUseCase: GetTaskModelsForParentUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskRepository, combineTasksWithHashtagsUseCase)
        getTaskModelsForParentUseCase = GetTaskModelsForParentUseCaseImpl(
            localTaskRepository,
            combineTasksWithHashtagsUseCase
        )
    }

    /**
     * GIVEN the repository emits a list of tasks for a parent.
     * WHEN the use case is invoked.
     * THEN it should call the combine use case with the emitted tasks,
     * and finally emit the combined list of tasks with their hashtags.
     */
    @Test
    @DisplayName("WHEN repository emits tasks THEN should process and emit combined models")
    fun `correctly processes repository emissions`() = runTest {
        // GIVEN
        val parentTaskId = 1L
        val repoTasks = listOf(createTask(11L, "Subtask 1"))
        val finalCombinedTasks = listOf(createTaskWithHashtags(11L, "Subtask 1"))

        every {
            localTaskRepository.getTaskModelsForParent(
                parentTaskId,
                any(),
                any(),
                any()
            )
        } returns flowOf(repoTasks)

        every { combineTasksWithHashtagsUseCase.invoke(repoTasks) } returns flowOf(
            finalCombinedTasks
        )

        // WHEN
        val resultFlow =
            getTaskModelsForParentUseCase.invoke(parentTaskId, null, null, SortDirection.ASC)

        // THEN
        resultFlow.test {
            assertThat(awaitItem()).isEqualTo(finalCombinedTasks)

            awaitComplete()
        }

        verify(exactly = 1) { combineTasksWithHashtagsUseCase.invoke(repoTasks) }
        verify(exactly = 0) { combineTasksWithHashtagsUseCase.invoke(emptyList()) }
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