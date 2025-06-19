package at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CreateTaskPriorityHistoryUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateTaskPriorityHistoryUseCaseImplTest {

    @MockK
    private lateinit var localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository

    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall

    @MockK
    private lateinit var clock: Clock

    private lateinit var createTaskPriorityHistoryUseCase: CreateTaskPriorityHistoryUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localTaskPriorityHistoryRepository, safeDatabaseResultCall, clock)
        createTaskPriorityHistoryUseCase = CreateTaskPriorityHistoryUseCaseImpl(
            localTaskPriorityHistoryRepository,
            safeDatabaseResultCall,
            clock
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SuccessPath {
        /**
         * GIVEN a taskId and a priority (which can be null).
         * WHEN the use case is invoked.
         * THEN it should create a history model with the correct data and timestamp,
         * and call the repository's insert method.
         */
        @ParameterizedTest(name = "[{index}] For priority = {0}, it should insert correctly")
        @MethodSource("providePriorityScenarios")
        fun `creates history model and inserts correctly for different priorities`(
            priority: PriorityEnum?,
            testName: String,
        ) = runTest {
            // GIVEN
            val taskId = 1L
            val testTime = Instant.parse("2025-08-10T20:30:00Z")

            every { clock.now() } returns testTime
            coEvery { localTaskPriorityHistoryRepository.insert(any()) } returns 1L

            val historyModelSlot = slot<TaskPriorityHistoryModel>()

            // WHEN
            val result = createTaskPriorityHistoryUseCase.invoke(taskId, priority)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(exactly = 1) {
                localTaskPriorityHistoryRepository.insert(
                    capture(
                        historyModelSlot
                    )
                )
            }

            val capturedModel = historyModelSlot.captured
            assertThat(capturedModel.taskId).isEqualTo(taskId)
            assertThat(capturedModel.priorityEnum).isEqualTo(priority)
            assertThat(capturedModel.createdAt).isEqualTo(testTime)
        }

        fun providePriorityScenarios(): Stream<Arguments> = Stream.of(
            Arguments.of(PriorityEnum.HIGH, "HIGH priority"),
            Arguments.of(null, "null priority")
        )
    }


    @Nested
    @DisplayName("GIVEN the repository fails")
    inner class FailurePath {
        /**
         * GIVEN the repository's insert method throws an exception.
         * WHEN the use case is invoked.
         * THEN the SafeDatabaseResultCall wrapper should handle it and return an AppResult.Error.
         */
        @Test
        @DisplayName("WHEN repository throws exception THEN should return Error")
        fun `returns error when repository fails`() = runTest {
            // GIVEN
            val exception = RuntimeException("Database connection lost")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { localTaskPriorityHistoryRepository.insert(any()) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = createTaskPriorityHistoryUseCase.invoke(
                taskId = 1L,
                priorityEnum = PriorityEnum.LOW
            )

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}