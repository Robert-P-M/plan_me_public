package at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
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

@DisplayName("GetTaskByIdUseCaseImpl Tests")
class GetTaskByIdUseCaseImplTest: BaseKoinTest() {

    private val localTaskRepository: LocalTaskRepository by inject()
    private val localHashtagRepository: LocalHashtagRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskRepository,
            localHashtagRepository,
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetTaskByIdUseCaseImpl) {
                bind<GetTaskByIdUseCase>()
            }
        }

    private val getTaskByIdUseCase: GetTaskByIdUseCase by inject()

    @Nested
    @DisplayName("GIVEN a task is found")
    inner class TaskFound {
        @Test
        @DisplayName("WHEN invoked THEN should combine task and hashtags successfully in a single emission")
        fun `combines task with hashtags`() = runTest {
            // GIVEN
            val taskId = 1L
            val fakeTask = createTask(taskId, "Test Task")
            val fakeHashtags = listOf(createHashtag(10L, "test"))
            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(fakeTask)
            every { localHashtagRepository.getHashtagsForTask(taskId) } returns flowOf(fakeHashtags)

            // WHEN
            val resultFlow = getTaskByIdUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val finalResult = awaitItem()

                assertThat(finalResult).isNotNull()
                assertThat(finalResult?.task).isEqualTo(fakeTask)

                assertThat(finalResult?.hashtags).hasSize(1)

                val hashtag = finalResult?.hashtags?.first() as UiHashtagModel.ExistingHashtagModel
                assertThat(hashtag.name).isEqualTo("test")

                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN a task is not found")
    inner class TaskNotFound {
        /**
         * GIVEN the repository returns null for the task.
         * WHEN the use case is invoked.
         * THEN it should return a flow that emits null.
         */
        @Test
        @DisplayName("WHEN invoked THEN should emit null")
        fun `emits null when task not found`() = runTest {
            // GIVEN
            val taskId = 404L
            every { localTaskRepository.getTaskModelById(taskId) } returns flowOf(null)
            every { localHashtagRepository.getHashtagsForTask(taskId) } returns flowOf(emptyList())

            // WHEN
            val resultFlow = getTaskByIdUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val resultModel = awaitItem()
                assertThat(resultModel).isNull()
                awaitComplete()
            }
        }
    }

    private fun createTask(id: Long, title: String) = TaskModel(
        taskId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
        isCompleted = false,
        isArchived = false
    )

    private fun createHashtag(id: Long, name: String) = HashtagModel(
        hashtagId = id, name = name, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
    )
}