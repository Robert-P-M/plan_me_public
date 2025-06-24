package at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
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


@DisplayName("AddNewHashtagUseCaseImpl Tests")
class AddNewHashtagUseCaseImplTest : BaseKoinTest() {


    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository by inject()
    private val addHashtagHelper: AddHashtagHelper by inject()
    private val clock: Clock by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::AddNewHashtagUseCaseImpl) {
                bind<AddNewHashtagUseCase>()
            }
        }
    private val addNewHashtagUseCase: AddNewHashtagUseCase by inject()
    override val mockSafeDatabaseResultCall: Boolean
        get() = true
    override val mockTransactionProvider: Boolean
        get() = true

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            transactionProvider,
            localTaskHashtagsCrossRefRepository,
            addHashtagHelper,
            clock
        )
    }

    @Nested
    @DisplayName("GIVEN all dependencies succeed")
    inner class SuccessPath {
        /**
         * GIVEN the AddHashtagHelper returns a new ID.
         * WHEN the use case is invoked.
         * THEN it should create a cross-reference with that new ID and insert it.
         */
        @Test
        @DisplayName("WHEN invoked THEN should create and insert cross-reference correctly")
        fun `creates and inserts cross-ref successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val newHashtagModel = UiHashtagModel.NewHashTagModel(name = "new")
            val expectedNewHashtagId = 10L
            val testTime = Instant.parse("2025-01-01T12:00:00Z")

            every { clock.now() } returns testTime
            coEvery { addHashtagHelper.invoke(newHashtagModel) } returns expectedNewHashtagId
            coEvery { localTaskHashtagsCrossRefRepository.insert(crossRef = any()) } returns 1L

            val crossRefSlot = slot<TaskHashtagsCrossRefModel>()

            // WHEN
            val result = addNewHashtagUseCase.invoke(taskId, newHashtagModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                addHashtagHelper.invoke(newHashtagModel)
                localTaskHashtagsCrossRefRepository.insert(capture(crossRefSlot))
            }

            // Überprüfe das erstellte CrossRef-Objekt
            val capturedCrossRef = crossRefSlot.captured
            assertThat(capturedCrossRef.taskId).isEqualTo(taskId)
            assertThat(capturedCrossRef.hashtagId).isEqualTo(expectedNewHashtagId) // Wichtigster Check!
            assertThat(capturedCrossRef.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN a dependency fails")
    inner class FailurePath {
        /**
         * GIVEN the AddHashtagHelper throws an exception.
         * WHEN the use case is invoked.
         * THEN it should be handled by the SafeDatabaseResultCall wrapper and return an Error.
         */
        @Test
        @DisplayName("WHEN helper throws exception THEN should return error")
        fun `returns error when helper fails`() = runTest {
            // GIVEN
            val taskId = 1L
            val newHashtagModel = UiHashtagModel.NewHashTagModel(name = "fail")
            val exception = RuntimeException("Failed to create hashtag")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { addHashtagHelper.invoke(newHashtagModel) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = addNewHashtagUseCase.invoke(taskId, newHashtagModel)

            // THEN
            assertThat(result).isEqualTo(expectedError)

            coVerify(exactly = 0) { localTaskHashtagsCrossRefRepository.insert(crossRef = any()) }
        }
    }
}