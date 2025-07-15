package at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

@DisplayName("GetTaskModelsForSectionUseCaseImpl Tests")
class GetTaskModelsForSectionUseCaseImplTest: BaseKoinTest() {

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
            factoryOf(::GetTaskModelsForSectionUseCaseImpl){
                bind<GetTaskModelsForSectionUseCase>()
            }
        }
    private val getTaskModelsForSectionUseCase: GetTaskModelsForSectionUseCase by inject()


    /**
     * GIVEN the repository emits a list of tasks.
     * WHEN the use case is invoked.
     * THEN it should call the combine use case with the emitted tasks,
     * and finally emit the combined list of tasks with hashtags.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("WHEN repository emits tasks THEN should process and emit combined models")
    fun `correctly processes repository emissions`() = runTest {
        // GIVEN
        val sectionId = 1L
        val repoTasks = listOf(createTask(1L, "Task 1"), createTask(2L, "Task 2"))
        val finalCombinedTasks =
            listOf(createTaskWithHashtags(1L, "Task 1"), createTaskWithHashtags(2L, "Task 2"))

        every {
            localTaskRepository.getTaskModelsForSection(
                eq(sectionId),
                any(),
                any(),
                any()
            )
        } returns flowOf(repoTasks)

        every { combineTasksWithHashtagsUseCase.invoke(repoTasks) } returns flowOf(
            finalCombinedTasks
        )

        // WHEN & THEN
        getTaskModelsForSectionUseCase.invoke(sectionId, null, null, SortDirection.ASC).test {
            val finalList = awaitItem()
            assertThat(finalList).isEqualTo(finalCombinedTasks)

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