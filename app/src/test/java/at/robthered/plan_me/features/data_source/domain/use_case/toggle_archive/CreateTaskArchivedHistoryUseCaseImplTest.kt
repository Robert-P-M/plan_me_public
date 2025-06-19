package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history.CreateTaskArchivedHistoryUseCaseImpl
import com.google.common.truth.Truth.assertThat
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@DisplayName("CreateTaskArchivedHistoryUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateTaskArchivedHistoryUseCaseImplTest {

    @MockK
    private lateinit var localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    @MockK
    private lateinit var clock: Clock

    private lateinit var createTaskArchivedHistoryUseCase: CreateTaskArchivedHistoryUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskArchivedHistoryRepository, safeDatabaseResultCall, clock)
        createTaskArchivedHistoryUseCase = CreateTaskArchivedHistoryUseCaseImpl(
            localTaskArchivedHistoryRepository,
            safeDatabaseResultCall,
            clock
        )

        coEvery {
            safeDatabaseResultCall.invoke<Unit>(any(), any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN a valid request")
    inner class SuccessPath {
        /**
         * GIVEN a taskId and an 'isArchived' status.
         * WHEN the use case is invoked.
         * THEN it should create a TaskArchivedHistoryModel with the correct data and timestamp,
         * and call the repository's insert method with it.
         */
        @Test
        @DisplayName("WHEN invoked THEN should create correct history model and call insert")
        fun `creates history model and inserts correctly`() = runTest {
            // GIVEN
            val taskId = 123L
            val isArchived = true
            val testTime = Instant.parse("2025-08-10T20:00:00Z")

            every { clock.now() } returns testTime
            coEvery { localTaskArchivedHistoryRepository.insert(any()) } returns 1L

            val historyModelSlot = slot<TaskArchivedHistoryModel>()

            // WHEN
            val result = createTaskArchivedHistoryUseCase.invoke(taskId, isArchived)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) {
                localTaskArchivedHistoryRepository.insert(
                    capture(
                        historyModelSlot
                    )
                )
            }

            val capturedModel = historyModelSlot.captured
            assertThat(capturedModel.taskId).isEqualTo(taskId)
            assertThat(capturedModel.isArchived).isEqualTo(isArchived)
            assertThat(capturedModel.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN the repository fails")
    inner class FailurePath {
        /**
         * GIVEN the repository's insert method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall wrapper should handle the exception and return an AppResult.Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns error when repository fails`() = runTest {
            // GIVEN
            val exception = RuntimeException("Database is down")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { localTaskArchivedHistoryRepository.insert(any()) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = createTaskArchivedHistoryUseCase.invoke(taskId = 1L, isArchived = false)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }

}