package at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModelRootEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader.FlattenedRecursiveTaskLoader
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

@DisplayName("LoadMoveTaskItemsUseCaseImpl Tests")
class LoadMoveTaskItemsUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val localSectionRepository: LocalSectionRepository by inject()
    private val flattenedRecursiveTaskLoader: FlattenedRecursiveTaskLoader by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            localSectionRepository,
            flattenedRecursiveTaskLoader
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadMoveTaskItemsUseCaseImpl){
                bind<LoadMoveTaskItemsUseCase>()
            }
        }
    private val loadMoveTaskItemsUseCase: LoadMoveTaskItemsUseCase by inject()


    /**
     * GIVEN both repositories return empty lists.
     * WHEN the use case is invoked.
     * THEN it should only emit a list containing the INBOX root.
     */
    @Test
    @DisplayName("WHEN database is empty THEN returns only INBOX root")
    fun `handles empty state`() = runTest {
        // GIVEN
        every { localSectionRepository.get() } returns flowOf(emptyList())
        every { localTaskRepository.getRootTaskModels(any(), any(), any()) } returns flowOf(
            emptyList()
        )

        // WHEN
        val resultFlow = loadMoveTaskItemsUseCase.invoke(1)

        // THEN
        resultFlow.test {
            val result = awaitItem()
            assertThat(result).hasSize(1)
            assertThat(result.first()).isEqualTo(MoveTaskModel.Root(MoveTaskModelRootEnum.INBOX))
            awaitComplete()
        }
    }

    @Nested
    @DisplayName("GIVEN data exists in repository")
    inner class DataExists {

        /**
         * GIVEN only sections with tasks exist (no root tasks).
         * WHEN the use case is invoked.
         * THEN the final list should contain the Root, followed by the Section and its Task.
         */
        @Test
        @DisplayName("WHEN only sections with tasks exist THEN builds correct hierarchy")
        fun `builds hierarchy for sections only`() = runTest {
            // GIVEN
            val section = createSection(1L, "My Section")
            val taskInSection = createTaskWithHashtags(11L, "Task in Section")

            // Setup mocks
            every { localSectionRepository.get() } returns flowOf(listOf(section))
            every {
                localTaskRepository.getTaskModelsForSection(
                    eq(1L),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(listOf(taskInSection))
            every { localTaskRepository.getRootTaskModels(any(), any(), any()) } returns flowOf(
                emptyList()
            )

            coEvery { flattenedRecursiveTaskLoader.invoke(any(), any(), any()) } returns emptyList()

            // WHEN
            val resultFlow = loadMoveTaskItemsUseCase.invoke(1)

            // THEN
            resultFlow.test {
                val result = awaitItem()
                assertThat(result).hasSize(3)
                assertThat(result[0]).isEqualTo(MoveTaskModel.Root(MoveTaskModelRootEnum.INBOX))
                assertThat(result[1]).isEqualTo(MoveTaskModel.Section("My Section", 1L))
                assertThat(result[2]).isEqualTo(MoveTaskModel.Task("Task in Section", 11L, 1))
                awaitComplete()
            }
        }

        /**
         * GIVEN only root tasks exist (no sections).
         * WHEN the use case is invoked.
         * THEN the final list should contain the Root, followed by the root Task.
         */
        @Test
        @DisplayName("WHEN only root tasks exist THEN builds correct hierarchy")
        fun `builds hierarchy for root tasks only`() = runTest {
            // GIVEN
            val rootTask = createTaskWithHashtags(101L, "Root Task")

            // Setup mocks
            every { localSectionRepository.get() } returns flowOf(emptyList())
            every { localTaskRepository.getRootTaskModels(any(), any(), any()) } returns flowOf(
                listOf(rootTask)
            )
            coEvery { flattenedRecursiveTaskLoader.invoke(any(), any(), any()) } returns emptyList()

            // WHEN
            val resultFlow = loadMoveTaskItemsUseCase.invoke(1)

            // THEN
            resultFlow.test {
                val result = awaitItem()
                assertThat(result).hasSize(2)
                assertThat(result[0]).isEqualTo(MoveTaskModel.Root(MoveTaskModelRootEnum.INBOX))
                assertThat(result[1]).isEqualTo(MoveTaskModel.Task("Root Task", 101L, 0))
                awaitComplete()
            }
        }
    }

    private fun createSection(id: Long, title: String) = SectionModel(
        sectionId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    private fun createTaskWithHashtags(id: Long, title: String) = TaskModel(
        taskId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
        isCompleted = false,
        isArchived = false
    )
}