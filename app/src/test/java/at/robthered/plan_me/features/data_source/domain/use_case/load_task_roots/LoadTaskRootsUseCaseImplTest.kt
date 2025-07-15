package at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModel
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModelRootEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
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

@DisplayName("LoadTaskRootsUseCaseImpl Tests")
class LoadTaskRootsUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val localSectionRepository: LocalSectionRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            localSectionRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadTaskRootsUseCaseImpl){
                bind<LoadTaskRootsUseCase>()
            }
        }
    private val loadTaskRootsUseCase: LoadTaskRootsUseCase by inject()

    @Nested
    @DisplayName("GIVEN task does not exist")
    inner class TaskNotFound {
        /**
         * GIVEN the initial task cannot be found in the repository.
         * WHEN the use case is invoked.
         * THEN it should return a default path containing only the INBOX root.
         */
        @Test
        @DisplayName("WHEN invoked with non-existent ID THEN returns default INBOX root path")
        fun `returns default path when task not found`() = runTest {
            // GIVEN
            val nonExistentTaskId = 404L
            every { localTaskRepository.getTaskModelById(nonExistentTaskId) } returns flowOf(null)

            // WHEN
            val resultFlow = loadTaskRootsUseCase.invoke(nonExistentTaskId)

            // THEN
            resultFlow.test {
                val path = awaitItem()
                assertThat(path).hasSize(1)
                assertThat(path.first()).isEqualTo(TaskTreeModel.Root(TaskTreeModelRootEnum.INBOX))
                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN task exists")
    inner class TaskFound {

        /**
         * GIVEN a task exists but has no parent and no section (is in INBOX).
         * WHEN the use case is invoked.
         * THEN it should return a path of [INBOX, Task].
         */
        @Test
        @DisplayName("WHEN task is a root task in INBOX THEN returns correct simple path")
        fun `builds correct path for root task in inbox`() = runTest {
            // GIVEN
            val task = createTask(1L, "Root Task", parentTaskId = null, sectionId = null)
            every { localTaskRepository.getTaskModelById(1L) } returns flowOf(task)

            // WHEN
            val resultFlow = loadTaskRootsUseCase.invoke(1L)

            // THEN
            resultFlow.test {
                val path = awaitItem()
                assertThat(path).hasSize(2)
                assertThat(path[0]).isEqualTo(TaskTreeModel.Root(TaskTreeModelRootEnum.INBOX))
                assertThat(path[1]).isEqualTo(TaskTreeModel.Task("Root Task", 1L))
                awaitComplete()
            }
        }

        /**
         * GIVEN a task has a parent, which is in a section.
         * WHEN the use case is invoked.
         * THEN it should traverse the hierarchy and return the full path [INBOX, Section, Parent, Task].
         */
        @Test
        @DisplayName("WHEN task is deeply nested THEN returns correct full path")
        fun `builds correct path for nested task with section`() = runTest {
            // GIVEN
            val task = createTask(1L, "My Task", parentTaskId = 11L, sectionId = null)
            val parent = createTask(11L, "Parent Task", parentTaskId = null, sectionId = 101L)
            val section = createSection(101L, "My Section")

            every { localTaskRepository.getTaskModelById(1L) } returns flowOf(task)

            every { localTaskRepository.getTaskModelById(11L) } returns flowOf(parent)

            every { localSectionRepository.get(101L) } returns flowOf(section)

            // WHEN
            val resultFlow = loadTaskRootsUseCase.invoke(1L)

            // THEN
            resultFlow.test {
                val path = awaitItem()
                assertThat(path).hasSize(4)
                assertThat(path[0]).isEqualTo(TaskTreeModel.Root(TaskTreeModelRootEnum.INBOX))
                assertThat(path[1]).isEqualTo(TaskTreeModel.Section("My Section", 101L))
                assertThat(path[2]).isEqualTo(TaskTreeModel.Task("Parent Task", 11L))
                assertThat(path[3]).isEqualTo(TaskTreeModel.Task("My Task", 1L))
                awaitComplete()
            }
        }
    }

    private fun createTask(id: Long, title: String, parentTaskId: Long?, sectionId: Long?) =
        TaskModel(
            taskId = id,
            title = title,
            parentTaskId = parentTaskId,
            sectionId = sectionId,
            isCompleted = false,
            isArchived = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

    private fun createSection(id: Long, title: String) = SectionModel(
        sectionId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )
}