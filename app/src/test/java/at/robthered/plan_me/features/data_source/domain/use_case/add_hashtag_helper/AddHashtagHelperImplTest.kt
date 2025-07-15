package at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper


import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject


@DisplayName("AddHashtagHelperImpl Tests")
class AddHashtagHelperImplTest : BaseKoinTest() {


    private val localHashtagRepository: LocalHashtagRepository by inject()
    private val localHashtagNameHistoryRepository: LocalHashtagNameHistoryRepository by inject()
    private val clock: Clock by inject()

    override val useCaseModule: Module = module {
        factoryOf(::AddHashtagHelperImpl) {
            bind<AddHashtagHelper>()
        }
    }

    override fun getMocks(): Array<Any> = arrayOf(
        localHashtagRepository,
        localHashtagNameHistoryRepository,
        clock,
    )


    private val addHashtagHelper: AddHashtagHelper by inject()

    @Nested
    @DisplayName("GIVEN a single NewHashTagModel")
    inner class SingleHashtag {
        /**
         * GIVEN a single hashtag model to add.
         * WHEN the single-item invoke method is called.
         * THEN it should insert one hashtag and one history entry with the correct data and return the new ID.
         */
        @Test
        @DisplayName("WHEN invoked THEN should insert hashtag and history and return new ID")
        fun `inserts single hashtag correctly`() = runTest {
            // GIVEN
            val newHashtag = UiHashtagModel.NewHashTagModel(name = "Kotlin")
            val expectedHashtagId = 1L
            val testTime = Instant.parse("2025-01-01T12:00:00Z")

            every { clock.now() } returns testTime
            coEvery { localHashtagRepository.insert(hashtagModel = any()) } returns expectedHashtagId
            coEvery { localHashtagNameHistoryRepository.insert(any()) } returns 1L // Return value is not used

            val hashtagSlot = slot<HashtagModel>()
            val historySlot = slot<HashtagNameHistoryModel>()

            // WHEN
            val resultId = addHashtagHelper.invoke(newHashtag)

            // THEN
            assertThat(resultId).isEqualTo(expectedHashtagId)

            coVerify(ordering = Ordering.ORDERED) {
                localHashtagRepository.insert(capture(hashtagSlot))
                localHashtagNameHistoryRepository.insert(capture(historySlot))
            }

            val capturedHashtag = hashtagSlot.captured
            assertThat(capturedHashtag.name).isEqualTo(newHashtag.name)
            assertThat(capturedHashtag.createdAt).isEqualTo(testTime)

            val capturedHistory = historySlot.captured
            assertThat(capturedHistory.hashtagId).isEqualTo(expectedHashtagId)
            assertThat(capturedHistory.text).isEqualTo(newHashtag.name)
            assertThat(capturedHistory.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN a list of NewHashTagModel")
    inner class ListOfHashtags {
        /**
         * GIVEN a list of new hashtags to add.
         * WHEN the list-based invoke method is called.
         * THEN it should insert each hashtag and its history and return all new IDs.
         */
        @Test
        @DisplayName("WHEN invoked with a non-empty list THEN should insert all items")
        fun `inserts list of hashtags correctly`() = runTest {
            // GIVEN
            val newHashtags = listOf(
                UiHashtagModel.NewHashTagModel(name = "Android"),
                UiHashtagModel.NewHashTagModel(name = "Compose")
            )
            val expectedIds = listOf(1L, 2L)
            val testTime = Instant.parse("2025-01-01T12:00:00Z")

            every { clock.now() } returns testTime

            coEvery { localHashtagRepository.insert(any<HashtagModel>()) }
                .returnsMany(expectedIds)
            coEvery { localHashtagNameHistoryRepository.insert(any()) } returns 1L

            // WHEN
            val resultIds = addHashtagHelper.invoke(newHashtags)

            // THEN
            assertThat(resultIds).isEqualTo(expectedIds)

            coVerify(exactly = 2) { localHashtagRepository.insert(any<HashtagModel>()) }
            coVerify(exactly = 2) { localHashtagNameHistoryRepository.insert(any()) }
        }

        /**
         * GIVEN an empty list of hashtags.
         * WHEN the list-based invoke method is called.
         * THEN it should do nothing and return an empty list.
         */
        @Test
        @DisplayName("WHEN invoked with an empty list THEN should do nothing")
        fun `does nothing for empty list`() = runTest {
            // GIVEN
            val emptyList = emptyList<UiHashtagModel.NewHashTagModel>()

            every { clock.now() } returns Instant.DISTANT_PAST
            // WHEN
            val resultIds = addHashtagHelper.invoke(emptyList)

            // THEN
            assertThat(resultIds).isEmpty()

            coVerify(exactly = 0) { localHashtagRepository.insert(hashtagModels = any()) }
            coVerify(exactly = 0) { localHashtagNameHistoryRepository.insert(any()) }
        }
    }
}