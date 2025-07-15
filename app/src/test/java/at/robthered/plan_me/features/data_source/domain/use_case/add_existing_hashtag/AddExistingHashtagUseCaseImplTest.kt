package at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
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


@DisplayName("AddExistingHashtagUseCaseImpl Tests")
class AddExistingHashtagUseCaseImplTest : BaseKoinTest() {


    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository by inject()
    private val clock: Clock by inject()
    private val addExistingHashtagUseCase: AddExistingHashtagUseCase by inject()

    override val mockSafeDatabaseResultCall: Boolean = true
    override val mockTransactionProvider: Boolean = true
    override val useCaseModule: Module = module {
        factoryOf(::AddExistingHashtagUseCaseImpl) {
            bind<AddExistingHashtagUseCase>()
        }
    }

    override fun getMocks(): Array<Any> = arrayOf(
        transactionProvider,
        localTaskHashtagsCrossRefRepository,
        safeDatabaseResultCall,
        clock
    )

    @Nested
    @DisplayName("GIVEN a valid request")
    inner class SuccessPath {
        /**
         * GIVEN a task ID and an existing hashtag model.
         * WHEN the use case is invoked.
         * THEN it should create a cross-reference model with the correct data and insert it.
         */
        @Test
        @DisplayName("WHEN invoked THEN should create and insert cross-reference correctly")
        fun `creates and inserts cross-ref successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val hashtagModel =
                UiHashtagModel.ExistingHashtagModel(hashtagId = 10L, name = "existing")
            val testTime = Instant.parse("2025-01-01T12:00:00Z")

            every { clock.now() } returns testTime

            coEvery { localTaskHashtagsCrossRefRepository.insert(crossRef = any()) } returns 1L

            val crossRefSlot = slot<TaskHashtagsCrossRefModel>()

            // WHEN
            val result = addExistingHashtagUseCase.invoke(taskId, hashtagModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskHashtagsCrossRefRepository.insert(capture(crossRefSlot)) }

            val capturedCrossRef = crossRefSlot.captured
            assertThat(capturedCrossRef.taskId).isEqualTo(taskId)
            assertThat(capturedCrossRef.hashtagId).isEqualTo(hashtagModel.hashtagId)
            assertThat(capturedCrossRef.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN the repository fails")
    inner class FailurePath {
        /**
         * GIVEN the repository throws an exception during insertion.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall wrapper should handle it and return an Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns error when repository fails`() = runTest {
            // GIVEN
            val exception = RuntimeException("Constraint failed")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { localTaskHashtagsCrossRefRepository.insert(crossRef = any()) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = addExistingHashtagUseCase.invoke(
                taskId = 1L,
                uiHashtagModel = UiHashtagModel.ExistingHashtagModel(10L, "test")
            )

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}