package at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
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

@DisplayName("GetHashtagsByNameUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetHashtagsByNameUseCaseImplTest {

    @MockK
    private lateinit var localHashtagRepository: LocalHashtagRepository

    private lateinit var getHashtagsByNameUseCase: GetHashtagsByNameUseCase

    private lateinit var now: Instant

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localHashtagRepository)
        getHashtagsByNameUseCase = GetHashtagsByNameUseCaseImpl(localHashtagRepository)
        now = Clock.System.now()
    }

    @Test
    @DisplayName("GIVEN repository returns a flow of hashtags WHEN invoke is called THEN should return a flow of mapped UI models")
    fun `invoke - repository returns data - returns mapped flow`() = runTest {
        // GIVEN
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