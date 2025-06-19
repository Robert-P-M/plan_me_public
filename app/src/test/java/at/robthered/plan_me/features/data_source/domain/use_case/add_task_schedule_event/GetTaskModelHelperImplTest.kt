package at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNull


@DisplayName("Test suite for GetTaskModelHelperImpl")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTaskModelHelperImplTest {
    private lateinit var mockLocalTaskRepository: LocalTaskRepository
    private lateinit var getTaskModelHelper: GetTaskModelUseCaseImpl

    @BeforeEach
    fun setUp() {
        mockLocalTaskRepository = mockk()
        getTaskModelHelper = GetTaskModelUseCaseImpl(mockLocalTaskRepository)
    }

    @Test
    fun `invoke returns null when task is not found`() = runTest {

        val taskId = 1L

        coEvery { mockLocalTaskRepository.getTaskModelById(taskId = taskId) } returns flowOf(null)

        val result = getTaskModelHelper(taskId = taskId)
        assertNull(result)


    }

    @Test
    fun `invoke returns TaskWithChildrenModel when task is found`() = runTest {

        val taskId = 1L
        val expectedTaskModel = TaskModel(
            taskId = taskId,
            parentTaskId = null,
            sectionId = null,
            title = "Test Task Title",
            description = "Test Task Description",
            isCompleted = false,
            isArchived = false,
            priorityEnum = PriorityEnum.NORMAL,
            taskSchedule = null,
            createdAt = Instant.DISTANT_PAST,
            updatedAt = Instant.DISTANT_PAST
        )

        val expectedTaskWithChildrenModel = expectedTaskModel

        coEvery { mockLocalTaskRepository.getTaskModelById(taskId = taskId) } returns flowOf(
            expectedTaskWithChildrenModel
        )


        val actualTaskWithChildrenModel = getTaskModelHelper(taskId = taskId)


        assertEquals(
            expectedTaskWithChildrenModel, actualTaskWithChildrenModel,
            "The returned TaskWithChildrenModel should match the expected one."
        )
        assertEquals(
            taskId, actualTaskWithChildrenModel?.taskId,
            "The returned Task's ID should match the requested ID."
        )
    }

}