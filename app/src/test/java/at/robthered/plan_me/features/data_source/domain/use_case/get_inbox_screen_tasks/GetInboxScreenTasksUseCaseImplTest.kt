package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models.GetRootTaskModelsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("GetInboxScreenTasksUseCaseImpl Tests")
class GetInboxScreenTasksUseCaseImplTest: BaseKoinTest() {

    private val getRootTaskModelsUseCase: GetRootTaskModelsUseCase by inject()
    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getRootTaskModelsUseCase,
            recursiveTaskWithHashtagsAndChildrenModelHelper,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetInboxScreenTasksUseCaseImpl) {
                bind<GetInboxScreenTasksUseCase>()
            }
        }

    private val getInboxScreenTasksUseCase: GetInboxScreenTasksUseCase by inject()

    /**
     * GIVEN the root task source provides a list of tasks.
     * WHEN the use case is invoked.
     * THEN it should fetch children for each root task in parallel and combine them into a final hierarchical list.
     */
    @Test
    @DisplayName("WHEN root tasks are emitted THEN should fetch children and combine them correctly")
    fun `fetches and combines children for root tasks`() = runTest {
        // GIVEN
        val depth = 2

        val rootTask1 = createTaskWithUiHashtags(1L, "Root Task 1")
        val rootTask2 = createTaskWithUiHashtags(2L, "Root Task 2")
        val childrenForTask1 = listOf(createTaskWithChildren(11L, "Child 1.1"))
        val childrenForTask2 = listOf(
            createTaskWithChildren(21L, "Child 2.1"),
            createTaskWithChildren(22L, "Child 2.2")
        )

        every { getRootTaskModelsUseCase.invoke(any(), any(), any()) } returns flowOf(
            listOf(
                rootTask1,
                rootTask2
            )
        )

        coEvery {
            recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                parentTaskId = 1L,
                currentDepth = depth - 1,
                any(),
                any(),
                any()
            )
        } returns flowOf(childrenForTask1)
        coEvery {
            recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                parentTaskId = 2L,
                currentDepth = depth - 1,
                any(),
                any(),
                any()
            )
        } returns flowOf(childrenForTask2)

        // WHEN
        val resultFlow = getInboxScreenTasksUseCase.invoke(depth, null, null, SortDirection.ASC)

        // THEN
        resultFlow.test {
            val finalList = awaitItem()

            assertThat(finalList).hasSize(2)

            val resultTask1 = finalList.find { it.taskWithUiHashtagsModel.task.taskId == 1L }
            assertThat(resultTask1).isNotNull()
            assertThat(resultTask1?.children).isEqualTo(childrenForTask1)
            assertThat(resultTask1?.maxDepthReached).isEqualTo((depth - 1) < 0)

            val resultTask2 = finalList.find { it.taskWithUiHashtagsModel.task.taskId == 2L }
            assertThat(resultTask2).isNotNull()
            assertThat(resultTask2?.children).isEqualTo(childrenForTask2)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                parentTaskId = 1L,
                any(),
                any(),
                any(),
                any()
            )
        }
        coVerify(exactly = 1) {
            recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                parentTaskId = 2L,
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    private fun createTaskWithUiHashtags(id: Long, title: String) = TaskWithUiHashtagsModel(
        task = TaskModel(
            taskId = id,
            title = title,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            isCompleted = false,
            isArchived = false
        )
    )

    private fun createTaskWithChildren(id: Long, title: String) = TaskWithHashtagsAndChildrenModel(
        taskWithUiHashtagsModel = createTaskWithUiHashtags(id, title),
        children = emptyList(),
        maxDepthReached = true
    )
}