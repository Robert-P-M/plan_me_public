package at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("CreateTaskTitleHistoryUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateTaskTitleHistoryUseCaseImplTest {

    @MockK
    private lateinit var localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var createTaskTitleHistoryUseCase: CreateTaskTitleHistoryUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskTitleHistoryRepository, safeDatabaseResultCall)
        createTaskTitleHistoryUseCase = CreateTaskTitleHistoryUseCaseImpl(
            localTaskTitleHistoryRepository,
            safeDatabaseResultCall
        )

        coEvery {
            safeDatabaseResultCall<Unit>(callerTag = any(), block = any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN a valid request")
    inner class SuccessPath {
        /**
         * GIVEN a taskId, title and creation time.
         * WHEN the use case is invoked.
         * THEN it should create a TaskTitleHistoryModel with the correct data and call the repository's insert method.
         */
        @Test
        @DisplayName("WHEN invoked THEN should create correct history model and call insert")
        fun `creates and inserts history model successfully`() = runTest {
            // GIVEN
            val taskId = 1L
            val title = "New Task Title"
            val testTime = Instant.parse("2025-06-17T09:00:00Z")

            coEvery { localTaskTitleHistoryRepository.insert(any()) } returns 1L

            val historyModelSlot = slot<TaskTitleHistoryModel>()

            // WHEN
            val result = createTaskTitleHistoryUseCase.invoke(taskId, title, testTime)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) { localTaskTitleHistoryRepository.insert(capture(historyModelSlot)) }

            val capturedModel = historyModelSlot.captured
            assertThat(capturedModel.taskId).isEqualTo(taskId)
            assertThat(capturedModel.text).isEqualTo(title)
            assertThat(capturedModel.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN the repository fails")
    inner class FailurePath {
        /**
         * GIVEN the repository's insert method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall wrapper should handle it and return an Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns error when repository fails`() = runTest {
            // GIVEN
            val exception = RuntimeException("Database constraint violation")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            coEvery { localTaskTitleHistoryRepository.insert(any()) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result =
                createTaskTitleHistoryUseCase.invoke(1L, "some title", Instant.DISTANT_PAST)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}