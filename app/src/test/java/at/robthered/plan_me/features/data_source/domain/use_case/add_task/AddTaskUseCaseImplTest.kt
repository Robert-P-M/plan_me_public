package at.robthered.plan_me.features.data_source.domain.use_case.add_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref.CreateTaskHashtagCrossRefUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject


@DisplayName("AddTaskUseCase Tests")
class AddTaskUseCaseImplTest : BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val createTaskHashtagCrossRefUseCase: CreateTaskHashtagCrossRefUseCase by inject()
    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase by inject()
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase by inject()
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase by inject()
    private val addTaskScheduleEventUseCase: AddTaskScheduleEventUseCase by inject()
    private val clock: Clock by inject()

    private val addTaskUseCase: AddTaskUseCase by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::AddTaskUseCaseImpl) {
                bind<AddTaskUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            transactionProvider,
            localTaskRepository,
            createTaskHashtagCrossRefUseCase,
            createTaskTitleHistoryUseCase,
            createTaskDescriptionHistoryUseCase,
            createTaskPriorityHistoryUseCase,
            addTaskScheduleEventUseCase,
            clock
        )
    }

    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override val mockTransactionProvider: Boolean
        get() = true


    @Nested
    @DisplayName("GIVEN all dependencies succeed")
    inner class SuccessPath {

        @Test
        @DisplayName("invoke() with valid model should return Success and verify correct interactions")
        fun `invoke - successful insert - returns Success`() = runTest {
            // Given
            val addTaskModel = AddTaskModel(title = "New Task")
            val expectedTaskId = 123L
            val testTime = Instant.parse("2024-01-01T00:00:00Z")

            every { clock.now() } returns testTime



            coEvery { localTaskRepository.insert(any()) } coAnswers { expectedTaskId }

            coEvery {
                createTaskTitleHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery {
                createTaskDescriptionHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery {
                createTaskPriorityHistoryUseCase.invoke(
                    any(),
                    any(),
                )
            } returns AppResult.Success(Unit)
            coEvery {
                createTaskHashtagCrossRefUseCase.invoke(
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery { addTaskScheduleEventUseCase.invoke(any(), any()) } returns AppResult.Success(
                Unit
            )

            val taskSlot = slot<TaskModel>()

            val result = addTaskUseCase.invoke(addTaskModel)

            assertAll(
                "Verify success result and all interactions.",
                { assertThat(result).isInstanceOf(AppResult.Success::class.java) },
                {
                    coVerify(exactly = 1) {
                        localTaskRepository.insert(capture(taskSlot))
                    }
                    coVerify(exactly = 1) {
                        createTaskTitleHistoryUseCase.invoke(
                            taskId = expectedTaskId,
                            title = addTaskModel.title,
                            createdAt = testTime
                        )
                    }
                    coVerify(exactly = 1) {
                        createTaskDescriptionHistoryUseCase.invoke(
                            taskId = expectedTaskId,
                            description = addTaskModel.description,
                            createdAt = testTime
                        )
                    }
                    coVerify(exactly = 1) {
                        createTaskPriorityHistoryUseCase.invoke(
                            taskId = expectedTaskId,
                            priorityEnum = addTaskModel.priorityEnum,
                        )
                    }
                    coVerify(exactly = 1) {
                        createTaskHashtagCrossRefUseCase.invoke(
                            taskId = expectedTaskId,
                            hashtags = addTaskModel.hashtags
                        )
                    }
                    coVerify(exactly = 1) {
                        addTaskScheduleEventUseCase.invoke(
                            taskId = expectedTaskId,
                            addTaskScheduleEventModel = addTaskModel.taskSchedule
                        )
                    }

                }
            )


        }
    }

    @Nested
    @DisplayName("GIVEN a subsequent dependency fails")
    inner class FailurePath {

        /**
         * GIVEN that the initial task insertion succeeds, but a subsequent operation within the transaction fails.
         * WHEN the `invoke` method is called.
         * THEN the entire operation should fail and return an [AppResult.Error],
         * and no further operations should be executed after the point of failure.
         */
        @Test
        @DisplayName("invoke() should return Error if a subsequent use case fails within the transaction")
        fun `invoke - subsequent operation fails - returns Error`() = runTest {
            // GIVEN
            val addTaskModel = AddTaskModel(title = "New Task")
            val expectedTaskId = 123L
            val testTime = Instant.parse("2024-01-01T00:00:00Z")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            every { clock.now() } returns testTime
            coEvery { localTaskRepository.insert(any()) } returns expectedTaskId

            coEvery {
                createTaskTitleHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns expectedError


            // WHEN
            val result = addTaskUseCase.invoke(addTaskModel)

            // THEN
            assertAll(
                "Verify failing result and correct interaction chain.",
                { assertThat(result).isEqualTo(expectedError) },
                {
                    coVerify(ordering = Ordering.ORDERED) {
                        localTaskRepository.insert(any())
                        createTaskTitleHistoryUseCase.invoke(any(), any(), any())
                    }

                    coVerify(exactly = 0) {
                        createTaskDescriptionHistoryUseCase.invoke(
                            any(),
                            any(),
                            any()
                        )
                    }
                    coVerify(exactly = 0) {
                        createTaskPriorityHistoryUseCase.invoke(
                            any(),
                            any(),
                        )
                    }
                }
            )


        }
    }
}