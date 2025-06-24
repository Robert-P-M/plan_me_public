package at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task

import app.cash.turbine.test
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id.GetTaskByIdUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


@DisplayName("GetTaskDetailsDialogTaskUseCaseImpl Tests")
class GetTaskDetailsDialogTaskUseCaseImplTest: BaseKoinTest() {

    private val getTaskByIdUseCase: GetTaskByIdUseCase by inject()

    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getTaskByIdUseCase,
            recursiveTaskWithHashtagsAndChildrenModelHelper,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetTaskDetailsDialogTaskUseCaseImpl) {
                bind<GetTaskDetailsDialogTaskUseCase>()
            }
        }
    private val getTaskDetailsDialogTaskUseCase: GetTaskDetailsDialogTaskUseCase by inject()

    @Nested
    @DisplayName("GIVEN the main task exists")
    inner class TaskExists {
        /**
         * GIVEN getTaskByIdUseCase finds a task and the recursive helper finds its children.
         * WHEN the use case is invoked.
         * THEN it should emit Loading, followed by a Success state containing the fully assembled task with its children.
         */
        @Test
        @DisplayName("WHEN invoked THEN should emit Loading and then Success with hydrated model")
        fun `successfully hydrates task with children`() = runTest {
            // GIVEN
            val taskId = 1L
            val mainTask = createTaskWithHashtags(taskId, "Main Task")
            val children = listOf(createTaskWithChildren(11L, "Child Task"))

            every { getTaskByIdUseCase.invoke(taskId) } returns flowOf(mainTask)
            coEvery {
                recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                    taskId,
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(children)

            // WHEN
            val resultFlow =
                getTaskDetailsDialogTaskUseCase.invoke(taskId, 1, null, null, SortDirection.ASC)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val successResult = awaitItem()
                assertThat(successResult).isInstanceOf(AppResource.Success::class.java)

                val data = (successResult as AppResource.Success).data
                assertThat(data.taskWithUiHashtagsModel).isEqualTo(mainTask)
                assertThat(data.children).isEqualTo(children)
                assertThat(data.maxDepthReached).isFalse()

                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN an error condition occurs")
    inner class FailurePaths {
        /**
         * GIVEN getTaskByIdUseCase returns a flow with null.
         * WHEN the use case is invoked.
         * THEN it should throw TaskNotFoundException, which is caught and mapped to a NO_TASK_FOUND error.
         */
        @Test
        @DisplayName("WHEN task is not found THEN should emit Loading and then NO_TASK_FOUND error")
        fun `emits not found error`() = runTest {
            // GIVEN
            val taskId = 404L
            every { getTaskByIdUseCase.invoke(taskId) } returns flowOf(null)

            // WHEN
            val resultFlow =
                getTaskDetailsDialogTaskUseCase.invoke(taskId, 1, null, null, SortDirection.ASC)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val errorResult = awaitItem()
                assertThat(errorResult).isInstanceOf(AppResource.Error::class.java)
                assertThat((errorResult as AppResource.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

                awaitComplete()
            }
        }

        /**
         * GIVEN a downstream dependency throws a generic exception.
         * WHEN the use case is invoked.
         * THEN it should be caught and mapped to an UNKNOWN error.
         */
        @Test
        @DisplayName("WHEN a generic exception is thrown THEN should emit Loading and then UNKNOWN error")
        fun `emits unknown error on generic exception`() = runTest {
            // GIVEN
            val taskId = 1L
            val failingFlow = flow<TaskWithUiHashtagsModel?> {
                throw RuntimeException("Unexpected database error")
            }
            every { getTaskByIdUseCase.invoke(taskId) } returns failingFlow

            // WHEN
            val resultFlow =
                getTaskDetailsDialogTaskUseCase.invoke(taskId, 1, null, null, SortDirection.ASC)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val errorResult = awaitItem()
                assertThat(errorResult).isInstanceOf(AppResource.Error::class.java)
                assertThat((errorResult as AppResource.Error).error).isEqualTo(RoomDatabaseError.UNKNOWN)

                awaitComplete()
            }
        }
    }

    private fun createTaskWithHashtags(id: Long, title: String) = TaskWithUiHashtagsModel(
        task = TaskModel(
            taskId = id,
            title = title,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            isCompleted = false,
            isArchived = false
        )
    )

    private fun createTaskWithChildren(id: Long, title: String) = TaskWithHashtagsAndChildrenModel(
        taskWithUiHashtagsModel = createTaskWithHashtags(id, title),
        children = emptyList(),
        maxDepthReached = false
    )
}