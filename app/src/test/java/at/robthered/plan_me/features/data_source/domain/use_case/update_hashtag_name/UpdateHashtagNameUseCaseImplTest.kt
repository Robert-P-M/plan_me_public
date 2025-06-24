package at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
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

@DisplayName("UpdateHashtagNameUseCaseImpl Tests")
class UpdateHashtagNameUseCaseImplTest: BaseKoinTest() {

    override val mockTransactionProvider: Boolean
        get() = true
    private val localHashtagRepository: LocalHashtagRepository by inject()
    private val localHashtagNameHistoryRepository: LocalHashtagNameHistoryRepository by inject()
    private val clock: Clock by inject()
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override fun getMocks(): Array<Any> {
        return arrayOf(
            transactionProvider,
            localHashtagRepository,
            localHashtagNameHistoryRepository,
            clock,
            safeDatabaseResultCall
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::UpdateHashtagNameUseCaseImpl){
                bind<UpdateHashtagNameUseCase>()
            }
        }

    private val updateHashtagNameUseCase: UpdateHashtagNameUseCase by inject()


    @Nested
    @DisplayName("GIVEN the hashtag does not exist")
    inner class HashtagNotFound {
        /**
         * GIVEN the repository returns an empty flow for the hashtagId.
         * WHEN the use case is invoked.
         * THEN it should return a NO_HASHTAG_FOUND error.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return NO_HASHTAG_FOUND error")
        fun `returns error when hashtag not found`() = runTest {
            // GIVEN
            val updateModel = UpdateHashtagModel(hashtagId = 404L, name = "New Name")
            every { clock.now() } returns Instant.DISTANT_PAST
            every { localHashtagRepository.get(updateModel.hashtagId) } returns flowOf(null)

            // WHEN
            val result = updateHashtagNameUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_HASHTAG_FOUND)
            coVerify(exactly = 0) { localHashtagRepository.update(any()) }
        }
    }

    @Nested
    @DisplayName("GIVEN the hashtag exists")
    inner class HashtagExists {
        private val initialHashtag = HashtagModel(
            hashtagId = 1L,
            name = "Initial Name",
            createdAt = Instant.DISTANT_PAST,
            updatedAt = Instant.DISTANT_PAST
        )
        private val testTime = Instant.parse("2025-01-01T12:00:00Z")

        @BeforeEach
        fun givenHashtagExists() {
            every { localHashtagRepository.get(1L) } returns flowOf(initialHashtag)
            every { clock.now() } returns testTime
        }

        /**
         * GIVEN the new name is identical to the old name.
         * WHEN the use case is invoked.
         * THEN it should return Success without performing any write operations.
         */
        @Test
        @DisplayName("WHEN name is unchanged THEN should do nothing and return Success")
        fun `does nothing when name is unchanged`() = runTest {
            // GIVEN
            val updateModel = UpdateHashtagModel(hashtagId = 1L, name = "Initial Name")

            // WHEN
            val result = updateHashtagNameUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 0) { localHashtagRepository.update(any()) }
            coVerify(exactly = 0) { localHashtagNameHistoryRepository.insert(any()) }
        }

        /**
         * GIVEN the new name is different from the old name.
         * WHEN the use case is invoked.
         * THEN it should update the hashtag and create a history entry with the correct new data.
         */
        @Test
        @DisplayName("WHEN name is changed THEN should update hashtag and create history")
        fun `updates and creates history when name has changed`() = runTest {
            // GIVEN
            val updateModel = UpdateHashtagModel(hashtagId = 1L, name = "Updated Name")
            coEvery { localHashtagRepository.update(any()) } returns Unit
            coEvery { localHashtagNameHistoryRepository.insert(any()) } returns 1L

            val hashtagSlot = slot<HashtagModel>()
            val historySlot = slot<HashtagNameHistoryModel>()

            // WHEN
            val result = updateHashtagNameUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                localHashtagRepository.update(capture(hashtagSlot))
                localHashtagNameHistoryRepository.insert(capture(historySlot))
            }

            val capturedHashtag = hashtagSlot.captured
            assertThat(capturedHashtag.name).isEqualTo(updateModel.name)
            assertThat(capturedHashtag.updatedAt).isEqualTo(testTime)

            val capturedHistory = historySlot.captured
            assertThat(capturedHistory.hashtagId).isEqualTo(updateModel.hashtagId)
            assertThat(capturedHistory.text).isEqualTo(updateModel.name)
            assertThat(capturedHistory.createdAt).isEqualTo(testTime)
        }
    }
}