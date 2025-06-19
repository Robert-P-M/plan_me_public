package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("LoadHashtagsForTaskUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoadHashtagsForTaskUseCaseImplTest {

    @MockK
    private lateinit var localHashtagRepository: LocalHashtagRepository

    private lateinit var loadHashtagsForTaskUseCase: LoadHashtagsForTaskUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localHashtagRepository)
        loadHashtagsForTaskUseCase = LoadHashtagsForTaskUseCaseImpl(localHashtagRepository)
    }

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