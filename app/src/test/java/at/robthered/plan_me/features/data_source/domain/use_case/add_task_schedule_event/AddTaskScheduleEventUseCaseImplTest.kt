package at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("AddTaskScheduleEventUseCaseImpl Tests")
class AddTaskScheduleEventUseCaseImplTest : BaseKoinTest() {
    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val localTaskRepository: LocalTaskRepository by inject()
    private val localTaskScheduleEventRepository: LocalTaskScheduleEventRepository by inject()
    private val notificationContentMapper: NotificationContentMapper by inject()
    private val appAlarmScheduler: AppAlarmScheduler by inject()
    private val clock: Clock by inject()


    override val useCaseModule: Module
        get() = module {
            factoryOf(::AddTaskScheduleEventUseCaseImpl) {
                bind<AddTaskScheduleEventUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            transactionProvider,
            getTaskModelUseCase,
            localTaskRepository,
            localTaskScheduleEventRepository,
            notificationContentMapper,
            appAlarmScheduler,
            clock,
        )
    }

    override val mockTransactionProvider: Boolean
        get() = true
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    private val useCase: AddTaskScheduleEventUseCase by inject()

    @Nested
    @DisplayName("GIVEN initial task fetching fails")
    inner class TaskNotFound {
        @Test
        @DisplayName("WHEN task is not found THEN should return NO_TASK_FOUND error")
        fun `returns error if task not found`() = runTest {
            // GIVEN
            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { getTaskModelUseCase.invoke(any()) } returns null
            // WHEN
            val result = useCase.invoke(1L, null)
            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)
        }
    }

    @Nested
    @DisplayName("GIVEN initial task exists")
    inner class TaskExists {

        private val taskId = 1L
        val now = Instant.DISTANT_PAST
        private val initialTask = TaskModel(
            taskId = taskId,
            title = "My Task",
            createdAt = now,
            updatedAt = now,
            isCompleted = false,
            isArchived = false
        )

        @BeforeEach
        fun givenTask() {
            coEvery { getTaskModelUseCase.invoke(taskId) } returns initialTask
            every { clock.now() } returns Instant.parse("2025-01-01T12:00:00Z")
        }

        @Test
        @DisplayName("WHEN input model is null THEN should remove schedule from task")
        fun `removes schedule when input is null`() = runTest {
            // GIVEN
            val taskSlot = slot<TaskModel>()
            coEvery { localTaskRepository.upsert(capture(taskSlot)) } returns Unit

            // WHEN
            val result = useCase.invoke(taskId, null)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            assertThat(taskSlot.captured.taskSchedule).isNull()
            assertThat(taskSlot.captured.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("WHEN new schedule with notification is added THEN should insert, upsert and schedule alarm")
        fun `adds schedule and alarm`() = runTest {
            // GIVEN
            val scheduleModel =
                AddTaskScheduleEventModel(startDateInEpochDays = 1, isNotificationEnabled = true)
            val newScheduleId = 10L
            val now = Instant.DISTANT_PAST
            val notificationContent = NotificationContent.Default(
                notificationId = 1,
                taskId = 1L,
                title = "Title",
                description = "Description",
                scheduledDate = LocalDate(year = 2025, monthNumber = 6, dayOfMonth = 17),
                triggerInstant = now,
                priorityEnum = PriorityEnum.HIGH
            )

            coEvery { localTaskScheduleEventRepository.insert(any()) } returns newScheduleId
            coEvery { localTaskRepository.upsert(any()) } returns Unit
            every { notificationContentMapper.invoke(any()) } returns notificationContent
            every { appAlarmScheduler.schedule(any(), any(), any()) } just runs

            // WHEN
            val result = useCase.invoke(taskId, scheduleModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskScheduleEventRepository.insert(any()) }
            coVerify(exactly = 1) { localTaskRepository.upsert(any()) }
            verify(exactly = 1) { notificationContentMapper.invoke(any()) }
            verify(exactly = 1) { appAlarmScheduler.schedule(any(), any(), any()) }
            verify(exactly = 0) { appAlarmScheduler.cancel(any(), any()) }
        }

        @Test
        @DisplayName("WHEN input is invalid THEN should return UNKNOWN error")
        fun `returns error for invalid input`() = runTest {
            // GIVEN
            val invalidScheduleModel = AddTaskScheduleEventModel(startDateInEpochDays = null)

            // WHEN
            val result = useCase.invoke(taskId, invalidScheduleModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.UNKNOWN)
        }
    }
}