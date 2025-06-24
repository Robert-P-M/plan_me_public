package at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("FlattenedRecursiveTaskLoaderImpl Tests")
class FlattenedRecursiveTaskLoaderImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::FlattenedRecursiveTaskLoaderImpl){
                bind<FlattenedRecursiveTaskLoader>()
            }
        }
    private val flattenedRecursiveTaskLoader: FlattenedRecursiveTaskLoader by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository
        )
    }

    @Nested
    @DisplayName("GIVEN a base case is met (recursion should stop)")
    inner class BaseCases {
        /**
         * GIVEN the max recursion level is negative.
         * WHEN the use case is invoked.
         * THEN it should return an empty list immediately without querying the repository.
         */
        @Test
        @DisplayName("WHEN max recursion level is exceeded THEN returns empty list")
        fun `stops when max recursion level is exceeded`() = runTest {
            // WHEN
            val result = flattenedRecursiveTaskLoader.invoke(
                parentTaskId = 1L,
                currentVisualDepth = 0,
                maxRecursionLevels = -1
            )

            // THEN
            assertThat(result).isEmpty()
            coVerify(exactly = 0) {
                localTaskRepository.getTaskModelsForParent(
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

        /**
         * GIVEN a task has no children.
         * WHEN the use case is invoked.
         * THEN it should return an empty list.
         */
        @Test
        @DisplayName("WHEN a task has no children THEN returns empty list")
        fun `stops when no children are found`() = runTest {
            // GIVEN
            val parentTaskId = 1L
            coEvery {
                localTaskRepository.getTaskModelsForParent(
                    parentTaskId,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())

            // WHEN
            val result = flattenedRecursiveTaskLoader.invoke(
                parentTaskId = parentTaskId,
                currentVisualDepth = 0,
                maxRecursionLevels = 5
            )

            // THEN
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("GIVEN a nested task hierarchy")
    inner class RecursiveCases {
        /**
         * GIVEN a two-level hierarchy (Parent -> Child -> Grandchild).
         * WHEN the use case is invoked for the top parent.
         * THEN it should return a flattened list containing the Child and Grandchild in correct order with correct depth.
         */
        @Test
        @DisplayName("WHEN invoked on a hierarchy THEN returns correctly flattened and ordered list")
        fun `flattens hierarchy correctly`() = runTest {
            // GIVEN
            val parentId = 1L
            val child = createTask(11L, "Child")
            val grandchild = createTask(111L, "Grandchild")

            coEvery {
                localTaskRepository.getTaskModelsForParent(
                    parentId,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(listOf(child))
            coEvery {
                localTaskRepository.getTaskModelsForParent(
                    11L,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(listOf(grandchild))
            coEvery {
                localTaskRepository.getTaskModelsForParent(
                    111L,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())

            // WHEN
            val result = flattenedRecursiveTaskLoader.invoke(
                parentTaskId = parentId,
                currentVisualDepth = 0, // Start-Tiefe
                maxRecursionLevels = 5
            )

            // THEN
            assertThat(result).hasSize(2)

            val resultChild = result[0]
            assertThat(resultChild.taskId).isEqualTo(child.taskId)
            assertThat(resultChild.title).isEqualTo(child.title)
            assertThat(resultChild.depth).isEqualTo(0) // Korrekte Start-Tiefe

            val resultGrandchild = result[1]
            assertThat(resultGrandchild.taskId).isEqualTo(grandchild.taskId)
            assertThat(resultGrandchild.title).isEqualTo(grandchild.title)
            assertThat(resultGrandchild.depth).isEqualTo(1) // Korrekte, inkrementierte Tiefe

            coVerify(exactly = 1) {
                localTaskRepository.getTaskModelsForParent(
                    parentId,
                    any(),
                    any(),
                    any()
                )
            }
            coVerify(exactly = 1) {
                localTaskRepository.getTaskModelsForParent(
                    11L,
                    any(),
                    any(),
                    any()
                )
            }
            coVerify(exactly = 1) {
                localTaskRepository.getTaskModelsForParent(
                    111L,
                    any(),
                    any(),
                    any()
                )
            }
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