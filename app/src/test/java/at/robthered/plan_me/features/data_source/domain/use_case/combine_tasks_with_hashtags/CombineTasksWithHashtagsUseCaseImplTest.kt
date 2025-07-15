package at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("CombineTasksWithHashtagsUseCaseImpl Tests")
class CombineTasksWithHashtagsUseCaseImplTest : BaseKoinTest() {

    private val localHashtagRepository: LocalHashtagRepository by inject()
    private val combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::CombineTasksWithHashtagsUseCaseImpl) {
                bind<CombineTasksWithHashtagsUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localHashtagRepository,
        )
    }

    @Nested
    @DisplayName("GIVEN an input list of tasks")
    inner class InputListScenarios {
        /**
         * GIVEN the input list of tasks is empty.
         * WHEN the use case is invoked.
         * THEN it should return a flow that emits a single empty list and completes.
         */
        @Test
        @DisplayName("WHEN input list is empty THEN should return flow of empty list")
        fun `handles empty list correctly`() = runTest {
            // GIVEN
            val emptyTaskList = emptyList<TaskModel>()

            // WHEN
            val resultFlow = combineTasksWithHashtagsUseCase.invoke(emptyTaskList)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isEmpty()
                awaitComplete()
            }

            verify(exactly = 0) { localHashtagRepository.getHashtagsForTask(any()) }
        }

        /**
         * GIVEN a list of tasks.
         * WHEN the use case is invoked.
         * THEN it should call the repository for each task and combine the results correctly.
         */
        @Test
        @DisplayName("WHEN list is not empty THEN should combine tasks with their hashtags")
        fun `combines tasks with hashtags successfully`() = runTest {
            // GIVEN
            val task1 = createTask(1L, "Task mit 2 Hashtags")
            val task2 = createTask(2L, "Task mit 1 Hashtag")
            val hashtagsForTask1 = listOf(createHashtag(10L, "kotlin"), createHashtag(11L, "test"))
            val hashtagsForTask2 = listOf(createHashtag(12L, "android"))

            every { localHashtagRepository.getHashtagsForTask(1L) } returns flowOf(hashtagsForTask1)
            every { localHashtagRepository.getHashtagsForTask(2L) } returns flowOf(hashtagsForTask2)

            // WHEN
            val resultFlow = combineTasksWithHashtagsUseCase.invoke(listOf(task1, task2))

            // THEN
            resultFlow.test {
                val resultList = awaitItem()
                awaitComplete()
                val resultTask1 = resultList.find { it.task.taskId == 1L }
                assertThat(resultTask1).isNotNull()
                assertThat(resultTask1!!.hashtags).hasSize(2)

                val firstHashtag = resultTask1.hashtags.first()
                assertThat(firstHashtag).isInstanceOf(UiHashtagModel.ExistingHashtagModel::class.java)

                val firstHashtagName = (firstHashtag as UiHashtagModel.ExistingHashtagModel).name
                assertThat(firstHashtagName).isEqualTo("kotlin")

                val resultTask2 = resultList.find { it.task.taskId == 2L }
                assertThat(resultTask2).isNotNull()
                assertThat(resultTask2!!.hashtags).hasSize(1)

                val secondTaskHashtag = resultTask2.hashtags.first()
                assertThat(secondTaskHashtag).isInstanceOf(UiHashtagModel.ExistingHashtagModel::class.java)
                val secondTaskHashtagName =
                    (secondTaskHashtag as UiHashtagModel.ExistingHashtagModel).name
                assertThat(secondTaskHashtagName).isEqualTo("android")
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