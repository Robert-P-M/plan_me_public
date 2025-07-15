package at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("GetRootTaskModelsUseCaseImpl Tests")
class GetRootTaskModelsUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            combineTasksWithHashtagsUseCase,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetRootTaskModelsUseCaseImpl){
                bind<GetRootTaskModelsUseCase>()
            }
        }
    private val getRootTaskModelsUseCase: GetRootTaskModelsUseCase by inject()


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