package at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("GetTaskHistoryFlowHelperImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTaskHistoryFlowHelperImplTest {

    @MockK
    private lateinit var localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository

    @MockK
    private lateinit var localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository

    @MockK
    private lateinit var localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository

    @MockK
    private lateinit var localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository

    @MockK
    private lateinit var localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository

    @MockK
    private lateinit var localTaskScheduleEventRepository: LocalTaskScheduleEventRepository

    private lateinit var getTaskHistoryFlowHelper: GetTaskHistoryFlowHelper

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            localTaskTitleHistoryRepository,
            localTaskDescriptionHistoryRepository,
            localTaskPriorityHistoryRepository,
            localTaskCompletedHistoryRepository,
            localTaskArchivedHistoryRepository,
            localTaskScheduleEventRepository
        )
        getTaskHistoryFlowHelper = GetTaskHistoryFlowHelperImpl(
            localTaskTitleHistoryRepository,
            localTaskDescriptionHistoryRepository,
            localTaskPriorityHistoryRepository,
            localTaskCompletedHistoryRepository,
            localTaskArchivedHistoryRepository,
            localTaskScheduleEventRepository
        )
    }

    /**
     * GIVEN each repository dependency returns a flow with its respective history list.
     * WHEN the helper is invoked.
     * THEN it should combine all lists into a single TaskHistoryModel and emit it.
     */
    @Test
    @DisplayName("WHEN invoked THEN should combine all history flows into one model")
    fun `combines all history flows correctly`() = runTest {
        // GIVEN
        val taskId = 1L

        val now = Clock.System.now()
        val fakeTitleHistory =
            listOf(TaskTitleHistoryModel(taskId = taskId, text = "Title", createdAt = now))
        val fakeDescriptionHistory = listOf(
            TaskDescriptionHistoryModel(
                taskId = taskId,
                text = "Description",
                createdAt = now
            )
        )
        val fakePriorityHistory =
            listOf(TaskPriorityHistoryModel(taskId = taskId, priorityEnum = null, createdAt = now))
        val fakeCompletedHistory =
            listOf(TaskCompletedHistoryModel(taskId = taskId, isCompleted = true, createdAt = now))
        val fakeArchivedHistory =
            listOf(TaskArchivedHistoryModel(taskId = taskId, isArchived = true, createdAt = now))
        val fakeTaskScheduleEventHistory = listOf(
            TaskScheduleEventModel(
                taskId = taskId,
                taskScheduleEventId = 1L,
                startDateInEpochDays = 1,
                createdAt = now,
            )
        )

        every { localTaskTitleHistoryRepository.getForTask(taskId) } returns flowOf(fakeTitleHistory)
        every { localTaskDescriptionHistoryRepository.getForTask(taskId) } returns flowOf(
            fakeDescriptionHistory
        )
        every { localTaskPriorityHistoryRepository.getForTask(taskId) } returns flowOf(
            fakePriorityHistory
        )
        every { localTaskCompletedHistoryRepository.getForTask(taskId) } returns flowOf(
            fakeCompletedHistory
        )
        every { localTaskArchivedHistoryRepository.getForTask(taskId) } returns flowOf(
            fakeArchivedHistory
        )
        every { localTaskScheduleEventRepository.getForTask(taskId) } returns flowOf(
            fakeTaskScheduleEventHistory
        )

        // WHEN
        val resultFlow = getTaskHistoryFlowHelper.invoke(taskId)

        // THEN
        resultFlow.test {
            val emittedModel = awaitItem()

            assertThat(emittedModel.title).isEqualTo(fakeTitleHistory)
            assertThat(emittedModel.description).isEqualTo(fakeDescriptionHistory)
            assertThat(emittedModel.priority).isEqualTo(fakePriorityHistory)
            assertThat(emittedModel.completed).isEqualTo(fakeCompletedHistory)
            assertThat(emittedModel.archived).isEqualTo(fakeArchivedHistory)
            assertThat(emittedModel.taskSchedules).isEqualTo(fakeTaskScheduleEventHistory)

            awaitComplete()
        }
    }
}