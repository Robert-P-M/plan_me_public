package at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task.GetTaskModelsForParentUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.first
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

@DisplayName("RecursiveTaskWithHashtagsAndChildrenModelHelperImpl Tests")
class RecursiveTaskWithHashtagsAndChildrenModelHelperImplTest: BaseKoinTest() {

    private val getTaskModelsForParentUseCase: GetTaskModelsForParentUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getTaskModelsForParentUseCase
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::RecursiveTaskWithHashtagsAndChildrenModelHelperImpl){
                bind<RecursiveTaskWithHashtagsAndChildrenModelHelper>()
            }
        }
    private val recursiveHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper by inject()


    @Nested
    @DisplayName("GIVEN a base case is met (recursion should stop)")
    inner class BaseCases {
        /**
         * GIVEN the current depth is negative.
         * WHEN the helper is invoked.
         * THEN it should immediately return an empty list without calling the use case.
         */
        @Test
        @DisplayName("WHEN depth is negative THEN should return empty list")
        fun `stops recursion when depth is exceeded`() = runTest {
            // WHEN
            val resultFlow = recursiveHelper.invoke(1L, -1, null, null, SortDirection.ASC)
            val result = resultFlow.first()
            // THEN
            assertThat(result).isEmpty()
            coVerify(exactly = 0) {
                getTaskModelsForParentUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

        /**
         * GIVEN the use case finds no children for a given parent task.
         * WHEN the helper is invoked.
         * THEN it should return an empty list.
         */
        @Test
        @DisplayName("WHEN no direct children are found THEN should return empty list")
        fun `stops recursion when no children are found`() = runTest {
            // GIVEN
            val parentTaskId = 1L
            coEvery {
                getTaskModelsForParentUseCase.invoke(
                    parentTaskId,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())
            // WHEN
            val resultFlow = recursiveHelper.invoke(parentTaskId, 1, null, null, SortDirection.ASC)
            val result = resultFlow.first()
            // THEN
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("GIVEN a recursive case")
    inner class RecursiveCases {
        /**
         * GIVEN a parent task has direct children, but those children have no grandchildren.
         * WHEN the helper is invoked.
         * THEN it should return the direct children with their own `children` list being empty.
         */
        @Test
        @DisplayName("WHEN tasks have one level of children THEN should build the hierarchy correctly")
        fun `builds one level of hierarchy`() = runTest {
            // GIVEN
            val parentId = 1L
            val child1 = createTaskWithHashtags(11L, "Child 1")
            val child2 = createTaskWithHashtags(12L, "Child 2")

            coEvery {
                getTaskModelsForParentUseCase.invoke(
                    parentId,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(listOf(child1, child2))

            coEvery {
                getTaskModelsForParentUseCase.invoke(
                    11L,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())
            coEvery {
                getTaskModelsForParentUseCase.invoke(
                    12L,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())

            // WHEN
            val resultFlow = recursiveHelper.invoke(parentId, 1, null, null, SortDirection.ASC)
            val result = resultFlow.first()

            // THEN
            assertThat(result).hasSize(2)

            val resultChild1 = result.find { it.taskWithUiHashtagsModel.task.taskId == 11L }
            assertThat(resultChild1).isNotNull()
            assertThat(resultChild1?.taskWithUiHashtagsModel).isEqualTo(child1)
            assertThat(resultChild1?.children).isEmpty()
            assertThat(resultChild1?.maxDepthReached).isFalse()

            val resultChild2 = result.find { it.taskWithUiHashtagsModel.task.taskId == 12L }
            assertThat(resultChild2).isNotNull()
            assertThat(resultChild2?.children).isEmpty()

            coVerify(exactly = 1) {
                getTaskModelsForParentUseCase.invoke(
                    parentId,
                    any(),
                    any(),
                    any()
                )
            }
            coVerify(exactly = 1) { getTaskModelsForParentUseCase.invoke(11L, any(), any(), any()) }
            coVerify(exactly = 1) { getTaskModelsForParentUseCase.invoke(12L, any(), any(), any()) }
        }
    }

    /**
     * GIVEN the helper is called with depth 0.
     * WHEN it finds children.
     * THEN it should return those children and their maxDepthReached flag should be true.
     */
    @Test
    @DisplayName("WHEN depth is 0 THEN should set maxDepthReached to true for children")
    fun `sets maxDepthReached correctly at the depth limit`() = runTest {
        // GIVEN
        val parentId = 1L
        val child1 = createTaskWithHashtags(11L, "Child 1")

        coEvery {
            getTaskModelsForParentUseCase.invoke(
                parentId,
                any(),
                any(),
                any()
            )
        } returns flowOf(listOf(child1))


        // WHEN
        val resultFlow = recursiveHelper.invoke(parentId, 0, null, null, SortDirection.ASC)
        val result = resultFlow.first()

        // THEN
        assertThat(result).hasSize(1)
        val resultChild1 = result.first()

        assertThat(resultChild1.maxDepthReached).isTrue()
    }

    private fun createTaskWithHashtags(id: Long, title: String) = TaskWithUiHashtagsModel(
        task = TaskModel(
            taskId = id,
            title = title,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            isCompleted = false,
            isArchived = false
        )
    )
}