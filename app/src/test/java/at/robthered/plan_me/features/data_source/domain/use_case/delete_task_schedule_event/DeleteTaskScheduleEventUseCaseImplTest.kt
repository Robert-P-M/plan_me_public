package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("DeleteTaskScheduleEventUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteTaskScheduleEventUseCaseImplTest {

    @MockK
    private lateinit var localTaskScheduleEventRepository: LocalTaskScheduleEventRepository

    @MockK
    private lateinit var transactionProvider: TransactionProvider

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    private lateinit var deleteTaskScheduleEventUseCase: DeleteTaskScheduleEventUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskScheduleEventRepository, transactionProvider, safeDatabaseResultCall)
        deleteTaskScheduleEventUseCase = DeleteTaskScheduleEventUseCaseImpl(
            localTaskScheduleEventRepository,
            transactionProvider,
            safeDatabaseResultCall
        )


        coEvery {
            safeDatabaseResultCall<Unit>(callerTag = any(), block = any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
        coEvery {
            transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any())
        } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call is successful")
    inner class DeletionSucceeds {
        /**
         * GIVEN the repository's delete method returns 1 (one row affected).
         * WHEN the use case is invoked.
         * THEN it should return AppResult.Success.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 1 THEN should return Success")
        fun `returns Success when event is deleted`() = runTest {
            // GIVEN
            val scheduleEventId = 1L
            coEvery { localTaskScheduleEventRepository.delete(scheduleEventId) } returns 1

            // WHEN
            val result = deleteTaskScheduleEventUseCase.invoke(scheduleEventId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localTaskScheduleEventRepository.delete(scheduleEventId) }
        }
    }

    @Nested
    @DisplayName("GIVEN the repository delete call 'fails' or finds nothing")
    inner class DeletionFails {
        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_TASK_SCHEDULE_EVENT_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_TASK_SCHEDULE_EVENT_FOUND error")
        fun `returns Error when event is not found`() = runTest {
            // GIVEN
            val scheduleEventId = 404L
            coEvery { localTaskScheduleEventRepository.delete(scheduleEventId) } returns 0

            // WHEN
            val result = deleteTaskScheduleEventUseCase.invoke(scheduleEventId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_SCHEDULE_EVENT_FOUND)
            coVerify(exactly = 1) { localTaskScheduleEventRepository.delete(scheduleEventId) }
        }

        /**
         * GIVEN the repository's delete method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall should handle it and return an Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns Error when repository throws exception`() = runTest {
            // GIVEN
            val scheduleEventId = 1L
            val exception = RuntimeException("Database offline")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)
            coEvery { localTaskScheduleEventRepository.delete(scheduleEventId) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = deleteTaskScheduleEventUseCase.invoke(scheduleEventId)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}