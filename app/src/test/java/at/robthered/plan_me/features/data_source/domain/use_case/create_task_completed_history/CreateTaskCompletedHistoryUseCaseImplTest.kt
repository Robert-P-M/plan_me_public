package at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
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

@DisplayName("CreateTaskCompletedHistoryUseCaseImpl Tests")
class CreateTaskCompletedHistoryUseCaseImplTest: BaseKoinTest() {

    private val localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    private val clock: Clock by inject()

    override val useCaseModule: Module
        get() = module {
            factoryOf(::CreateTaskCompletedHistoryUseCaseImpl) {
                bind<CreateTaskCompletedHistoryUseCase>()
            }
        }

    private val createTaskCompletedHistoryUseCase: CreateTaskCompletedHistoryUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            safeDatabaseResultCall,
            localTaskCompletedHistoryRepository,
            clock,
        )
    }


    /**
     * GIVEN a taskId and a completion status.
     * WHEN the use case is invoked.
     * THEN it should create a correct TaskCompletedHistoryModel and call the repository's insert method.
     */
    @Test
    @DisplayName("invoke() should correctly create a history model and call insert")
    fun `invoke - success case`() = runTest {
        // GIVEN
        val taskId = 1L
        val isCompleted = true
        val testTime = Instant.Companion.parse("2025-06-16T12:00:00Z")

        every { clock.now() } returns testTime
        coEvery { localTaskCompletedHistoryRepository.insert(any()) } returns 1L

        val historyModelSlot = slot<TaskCompletedHistoryModel>()

        // WHEN
        val result = createTaskCompletedHistoryUseCase.invoke(taskId, isCompleted)

        // THEN
        Truth.assertThat(result).isInstanceOf(AppResult.Success::class.java)

        // Verify that insert was called and capture the argument
        coVerify(exactly = 1) { localTaskCompletedHistoryRepository.insert(capture(historyModelSlot)) }

        // Assert on the captured object's properties
        val capturedModel = historyModelSlot.captured
        Truth.assertThat(capturedModel.taskId).isEqualTo(taskId)
        Truth.assertThat(capturedModel.isCompleted).isEqualTo(isCompleted)
        Truth.assertThat(capturedModel.createdAt).isEqualTo(testTime)
    }

    /**
     * GIVEN the repository throws an exception during insertion.
     * WHEN the use case is invoked.
     * THEN the SafeDatabaseResultCall wrapper should catch it and return an AppResult.Error.
     */
    @Test
    @DisplayName("invoke() should return Error when repository throws exception")
    fun `invoke - repository fails - returns Error`() = runTest {
        // GIVEN
        val exception = RuntimeException("Database insertion failed")
        val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

        every { clock.now() } returns Instant.Companion.DISTANT_PAST
        coEvery { localTaskCompletedHistoryRepository.insert(any()) } throws exception

        coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

        // WHEN
        val result = createTaskCompletedHistoryUseCase.invoke(taskId = 1L, isCompleted = true)

        // THEN
        Truth.assertThat(result).isEqualTo(expectedError)
    }

}