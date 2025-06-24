package at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics

import app.cash.turbine.test
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper.GetTaskHistoryFlowHelper
import at.robthered.plan_me.features.data_source.domain.use_case.task_statistics.TaskStatisticsBuilder
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadTaskStatisticsUseCaseImpl Tests")
class LoadTaskStatisticsUseCaseImplTest: BaseKoinTest() {

    private val taskStatisticsBuilder: TaskStatisticsBuilder by inject()
    private val getTaskHistoryFlowHelper: GetTaskHistoryFlowHelper by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            taskStatisticsBuilder,
            getTaskHistoryFlowHelper,
            localTaskRepository,
            localTaskHashtagsCrossRefRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadTaskStatisticsUseCaseImpl){
                bind<LoadTaskStatisticsUseCase>()
            }
        }

    private val loadTaskStatisticsUseCase: LoadTaskStatisticsUseCase by inject()


    @Nested
    @DisplayName("GIVEN a successful data load")
    inner class SuccessPath {
        /**
         * GIVEN all underlying flows emit valid data.
         * WHEN the use case is invoked.
         * THEN it should emit Loading, then Success with a list of statistics items that are correctly built and sorted.
         */
        @Test
        @DisplayName("WHEN all sources provide data THEN should emit Loading and then sorted Success data")
        fun `builds and sorts statistics on success`() = runTest {
            // GIVEN
            val taskId = 1L
            val now = Instant.parse("2025-01-01T12:00:00Z")
            val fakeHistory =
                TaskHistoryModel(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
            val fakeTask = TaskModel(
                taskId = 1L,
                title = "My Task",
                createdAt = now,
                updatedAt = now,
                isCompleted = false,
                isArchived = false
            )
            val fakeSubTasks = listOf(
                TaskModel(
                    taskId = 2L,
                    title = "Subtask",
                    createdAt = now,
                    updatedAt = now,
                    isCompleted = false,
                    isArchived = false
                )
            )
            val fakeHashtags = listOf(
                HashtagModel(
                    hashtagId = 1L,
                    name = "test",
                    createdAt = now,
                    updatedAt = now
                )
            )
            val fakeCrossRefs =
                listOf(TaskHashtagsCrossRefModel(taskId = 1L, hashtagId = 1L, createdAt = now))

            val unsortedBuilderResult = listOf(
                TaskStatisticsModel.TaskInfo(
                    fakeTask,
                    createdAt = Instant.parse("2025-01-01T10:00:00Z")
                ),
                TaskStatisticsModel.SubTask(
                    fakeSubTasks.first(),
                    createdAt = Instant.parse("2025-01-01T11:00:00Z")
                )
            )
            val expectedSortedList = unsortedBuilderResult.sortedByDescending { it.createdAt }

            every { getTaskHistoryFlowHelper.invoke(taskId) } returns flowOf(fakeHistory)
            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(fakeTask)
            every {
                localTaskRepository.getTaskModelsForParent(
                    taskId,
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(fakeSubTasks)
            every { localTaskHashtagsCrossRefRepository.getForTaskId(taskId) } returns flowOf(
                TaskWithHashtagsModel(task = fakeTask, hashtags = fakeHashtags)
            )
            every { localTaskHashtagsCrossRefRepository.getCrossRefForTask(taskId) } returns flowOf(
                fakeCrossRefs
            )

            every {
                taskStatisticsBuilder.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns unsortedBuilderResult

            // WHEN
            val resultFlow = loadTaskStatisticsUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val successResult = awaitItem()
                assertThat(successResult).isInstanceOf(AppResource.Success::class.java)

                val finalData = (successResult as AppResource.Success).data
                assertThat(finalData).hasSize(expectedSortedList.size)
                assertThat(finalData).isEqualTo(expectedSortedList)
                val correctlySortedList = finalData.sortedByDescending { it.createdAt }
                assertThat(finalData).isEqualTo(correctlySortedList)

                awaitComplete()
            }

            verify(exactly = 1) {
                taskStatisticsBuilder.invoke(
                    fakeHistory,
                    fakeTask,
                    fakeSubTasks,
                    any()
                )
            }
        }
    }

    @Nested
    @DisplayName("GIVEN a data loading error")
    inner class FailurePath {
        /**
         * GIVEN the main task cannot be found.
         * WHEN the use case is invoked.
         * THEN it should emit Loading, then a NO_TASK_FOUND error.
         */
        @Test
        @DisplayName("WHEN task is not found THEN should emit Loading and then NO_TASK_FOUND error")
        fun `emits error when task not found`() = runTest {
            // GIVEN
            val taskId = 404L
            val now = Instant.parse("2025-01-01T12:00:00Z")

            val fakeHistory =
                TaskHistoryModel(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
            val fakeTask = TaskModel(
                taskId = 1L,
                title = "My Task",
                createdAt = now,
                updatedAt = now,
                isCompleted = false,
                isArchived = false
            )
            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(null)

            every { getTaskHistoryFlowHelper.invoke(taskId) } returns flowOf(fakeHistory)
            every {
                localTaskRepository.getTaskModelsForParent(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())
            every { localTaskHashtagsCrossRefRepository.getForTaskId(any()) } returns flowOf(
                TaskWithHashtagsModel(task = fakeTask)
            )
            every { localTaskHashtagsCrossRefRepository.getCrossRefForTask(any()) } returns flowOf(
                emptyList()
            )

            // WHEN
            val resultFlow = loadTaskStatisticsUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val errorResult = awaitItem()
                assertThat(errorResult).isInstanceOf(AppResource.Error::class.java)
                assertThat((errorResult as AppResource.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

                awaitComplete()
            }

            verify(exactly = 0) { taskStatisticsBuilder.invoke(any(), any(), any(), any()) }
        }
    }
}