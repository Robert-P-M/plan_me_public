package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadHashtagWithTasksUseCaseImpl Tests")
class LoadHashtagWithTasksUseCaseImplTest: BaseKoinTest() {
    private val localHashtagWithTasksRelationRepository: LocalHashtagWithTasksRelationRepository by inject()
    override fun getMocks(): Array<Any> {
        return arrayOf(
            localHashtagWithTasksRelationRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadHashtagWithTasksUseCaseImpl){
                bind<LoadHashtagWithTasksUseCase>()
            }
        }
    private val loadHashtagWithTasksUseCase: LoadHashtagWithTasksUseCase by inject()


    /**
     * GIVEN the repository returns a Flow containing a valid HashtagWithTasksModel.
     * WHEN the use case is invoked with a specific ID.
     * THEN it should return the exact same Flow, and verify that the repository was called correctly.
     */
    @Test
    @DisplayName("invoke() should return a flow with data when repository finds a match")
    fun `invoke - repository returns data - forwards flow correctly`() = runTest {
        // GIVEN
        val hashtagId = 1L
        val fakeData = HashtagWithTasksModel(
            hashtag = HashtagModel(hashtagId, "Test", Clock.System.now(), Clock.System.now()),
            tasks = emptyList()
        )
        val repositoryFlow = flowOf(fakeData)

        every { localHashtagWithTasksRelationRepository.getHashtagWithTasks(hashtagId) } returns repositoryFlow

        // WHEN
        val resultFlow = loadHashtagWithTasksUseCase.invoke(hashtagId)

        // THEN
        resultFlow.test {
            val emittedItem = awaitItem()
            assertThat(emittedItem).isEqualTo(fakeData)

            awaitComplete()
        }

        verify(exactly = 1) { localHashtagWithTasksRelationRepository.getHashtagWithTasks(hashtagId) }
    }

    /**
     * GIVEN the repository returns a Flow containing null (e.g., hashtag not found).
     * WHEN the use case is invoked.
     * THEN it should return the exact same Flow containing null.
     */
    @Test
    @DisplayName("invoke() should return a flow with null when repository finds no match")
    fun `invoke - repository returns null - forwards flow correctly`() = runTest {
        // GIVEN
        val hashtagId = 404L
        val repositoryFlowWithNull = flowOf(null)

        every { localHashtagWithTasksRelationRepository.getHashtagWithTasks(hashtagId) } returns repositoryFlowWithNull

        // WHEN
        val resultFlow = loadHashtagWithTasksUseCase.invoke(hashtagId)

        // THEN
        resultFlow.test {
            val emittedItem = awaitItem()
            assertThat(emittedItem).isNull()
            awaitComplete()
        }

        verify(exactly = 1) { localHashtagWithTasksRelationRepository.getHashtagWithTasks(hashtagId) }
    }
}