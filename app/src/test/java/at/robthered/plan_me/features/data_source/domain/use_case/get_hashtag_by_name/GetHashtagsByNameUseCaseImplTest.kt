package at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("GetHashtagsByNameUseCaseImpl Tests")
class GetHashtagsByNameUseCaseImplTest: BaseKoinTest() {

    private val localHashtagRepository: LocalHashtagRepository by inject()
    private val clock: Clock by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetHashtagsByNameUseCaseImpl) {
                bind<GetHashtagsByNameUseCase>()
            }
        }
    private val getHashtagsByNameUseCase: GetHashtagsByNameUseCase by inject()

    val testTime = Instant.parse("2025-01-01T12:00:00Z")
    override fun additionalSetUp() {
        every { clock.now() } returns testTime
    }
    override fun getMocks(): Array<Any> {
        return arrayOf(
            localHashtagRepository,
            clock,
        )
    }


    @Test
    @DisplayName("GIVEN repository returns a flow of hashtags WHEN invoke is called THEN should return a flow of mapped UI models")
    fun `invoke - repository returns data - returns mapped flow`() = runTest {
        // GIVEN
        val now = clock.now()
        val searchQuery = "test"
        val fakeRepoData = listOf(
            HashtagModel(hashtagId = 1, name = "testing", createdAt = now, updatedAt = now),
            HashtagModel(hashtagId = 2, name = "test-driven", createdAt = now, updatedAt = now)
        )
        val expectedUiModels = listOf(
            UiHashtagModel.FoundHashtagModel(hashtagId = 1, name = "testing"),
            UiHashtagModel.FoundHashtagModel(hashtagId = 2, name = "test-driven")
        )

        every { localHashtagRepository.getByName(searchQuery) } returns flowOf(fakeRepoData)

        // WHEN
        val resultFlow = getHashtagsByNameUseCase.invoke(searchQuery)

        // THEN
        resultFlow.test {
            val emittedList = awaitItem()

            assertThat(emittedList).containsExactlyElementsIn(expectedUiModels).inOrder()

            awaitComplete()
        }
    }

}