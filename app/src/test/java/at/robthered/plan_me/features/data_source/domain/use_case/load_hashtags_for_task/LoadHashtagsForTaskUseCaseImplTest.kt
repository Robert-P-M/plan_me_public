package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
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
import org.junit.jupiter.api.TestInstance
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadHashtagsForTaskUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoadHashtagsForTaskUseCaseImplTest: BaseKoinTest() {

    private val localHashtagRepository: LocalHashtagRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localHashtagRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadHashtagsForTaskUseCaseImpl){
                bind<LoadHashtagsForTaskUseCase>()
            }
        }
    private val loadHashtagsForTaskUseCase: LoadHashtagsForTaskUseCase by inject()

    @Nested
    @DisplayName("GIVEN repository provides a flow of hashtags")
    inner class RepositoryProvidesFlow {

        /**
         * GIVEN the repository returns a flow with a list of hashtags.
         * WHEN the use case is invoked.
         * THEN it should return the exact same flow of hashtags.
         */
        @Test
        @DisplayName("WHEN repository returns data THEN it should forward the data")
        fun `forwards flow with data from repository`() = runTest {
            // GIVEN
            val taskId = 1L
            val now = Clock.System.now()
            val fakeHashtags = listOf(
                HashtagModel(1L, "kotlin", now, now),
                HashtagModel(2L, "android", now, now)
            )
            val repositoryFlow = flowOf(fakeHashtags)

            every { localHashtagRepository.getHashtagsForTask(taskId) } returns repositoryFlow

            // WHEN
            val resultFlow = loadHashtagsForTaskUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val emittedList = awaitItem()
                assertThat(emittedList).isEqualTo(fakeHashtags)
                awaitComplete()
            }

            verify(exactly = 1) { localHashtagRepository.getHashtagsForTask(taskId) }
        }

        /**
         * GIVEN the repository returns a flow with an empty list.
         * WHEN the use case is invoked.
         * THEN it should return a flow with an empty list.
         */
        @Test
        @DisplayName("WHEN repository returns empty list THEN it should forward the empty list")
        fun `forwards empty flow from repository`() = runTest {
            // GIVEN
            val taskId = 2L
            val emptyFlow = flowOf(emptyList<HashtagModel>())
            every { localHashtagRepository.getHashtagsForTask(taskId) } returns emptyFlow

            // WHEN
            val resultFlow = loadHashtagsForTaskUseCase.invoke(taskId)

            // THEN
            resultFlow.test {
                val emittedList = awaitItem()
                assertThat(emittedList).isEmpty()
                awaitComplete()
            }
        }
    }
}