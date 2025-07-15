package at.robthered.plan_me.features.data_source.domain.use_case.update_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history.CreateTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history.CreateTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history.CreateTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_description.UpdateTaskDescriptionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority.UpdateTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task_title.UpdateTaskTitleUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("UpdateTaskUseCaseImpl Tests")
class UpdateTaskUseCaseImplTest: BaseKoinTest() {

    override val mockTransactionProvider: Boolean
        get() = true
    private val getTaskModelUseCase: GetTaskModelUseCase by inject()
    private val createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase by inject()
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase by inject()
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase by inject()
    private val updateTaskDescriptionUseCase: UpdateTaskDescriptionUseCase by inject()
    private val createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase by inject()
    private val updateTaskPriorityUseCase: UpdateTaskPriorityUseCase by inject()
    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    private val clock: Clock by inject()
    override fun getMocks(): Array<Any> {
        return arrayOf(
            transactionProvider,
            getTaskModelUseCase,
            createTaskTitleHistoryUseCase,
            updateTaskTitleUseCase,
            createTaskDescriptionHistoryUseCase,
            updateTaskDescriptionUseCase,
            createTaskPriorityHistoryUseCase,
            updateTaskPriorityUseCase,
            safeDatabaseResultCall,
            clock
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::UpdateTaskUseCaseImpl){
                bind<UpdateTaskUseCase>()
            }
        }
    private val updateTaskUseCase: UpdateTaskUseCase by inject()


    @Nested
    @DisplayName("GIVEN initial task fetching fails")
    inner class TaskFetchFails {
        @Test
        @DisplayName("WHEN getTaskModelUseCase returns null THEN should return NO_TASK_FOUND error")
        fun `returns error when task not found`() = runTest {
            // GIVEN

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { getTaskModelUseCase.invoke(any()) } returns null
            // WHEN
            val result = updateTaskUseCase.invoke(UpdateTaskModel(taskId = 404L))
            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)
        }
    }

    @Nested
    @DisplayName("GIVEN initial task exists")
    inner class TaskExists {

        private val initialTask = TaskModel(
            taskId = 1L,
            title = "Original Title",
            description = "Original Desc",
            priorityEnum = PriorityEnum.LOW,
            isCompleted = false,
            isArchived = false,
            updatedAt = Instant.DISTANT_PAST,
            createdAt = Instant.DISTANT_PAST
        )

        @BeforeEach
        fun givenTaskExists() {

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { getTaskModelUseCase.invoke(1L) } returns initialTask
        }

        @Test
        @DisplayName("WHEN no data has changed THEN should do nothing and return Success")
        fun `does nothing when model is unchanged`() = runTest {
            // GIVEN
            val updateModel = UpdateTaskModel(
                taskId = 1L,
                title = "Original Title",
                description = "Original Desc",
                priorityEnum = PriorityEnum.LOW
            )
            // WHEN
            val result = updateTaskUseCase.invoke(updateModel)
            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 0) { updateTaskTitleUseCase.invoke(any(), any(), any()) }
            coVerify(exactly = 0) { updateTaskDescriptionUseCase.invoke(any(), any(), any()) }
            coVerify(exactly = 0) { updateTaskPriorityUseCase.invoke(any(), any(), any()) }
        }

        @Test
        @DisplayName("WHEN only title has changed THEN should only execute title-related use cases")
        fun `only executes title logic when only title changes`() = runTest {
            // GIVEN
            val updateModel = UpdateTaskModel(
                taskId = 1L,
                title = "Neuer Titel",
                description = "Original Desc", // unverändert
                priorityEnum = PriorityEnum.LOW // unverändert
            )
            coEvery {
                createTaskTitleHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery {
                updateTaskTitleUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)

            // WHEN
            val result = updateTaskUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) {
                createTaskTitleHistoryUseCase.invoke(
                    any(),
                    eq("Neuer Titel"),
                    any()
                )
            }
            coVerify(exactly = 1) { updateTaskTitleUseCase.invoke(any(), eq("Neuer Titel"), any()) }

            coVerify(exactly = 0) { updateTaskDescriptionUseCase.invoke(any(), any(), any()) }
            coVerify(exactly = 0) { updateTaskPriorityUseCase.invoke(any(), any(), any()) }
        }

        @Test
        @DisplayName("WHEN a title update fails THEN should stop execution and return the error")
        fun `stops execution if title update fails`() = runTest {
            // GIVEN
            val updateModel = UpdateTaskModel(taskId = 1L, title = "Neuer Titel" /*...*/)
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            coEvery {
                createTaskTitleHistoryUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns AppResult.Success(Unit)
            coEvery {
                updateTaskTitleUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns expectedError // Simuliere Fehler

            // WHEN
            val result = updateTaskUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isEqualTo(expectedError)

            coVerify(exactly = 0) { updateTaskDescriptionUseCase.invoke(any(), any(), any()) }
            coVerify(exactly = 0) { updateTaskPriorityUseCase.invoke(any(), any(), any()) }
        }
    }
}