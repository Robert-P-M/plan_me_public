package at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject
import java.util.stream.Stream

@DisplayName("CreateTaskDescriptionHistoryUseCaseImpl Tests")
class CreateTaskDescriptionHistoryUseCaseImplTest : BaseKoinTest() {

    private val localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository by inject()
    private val clock: Clock by inject()

    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override val useCaseModule: Module
        get() = module {
            factoryOf(::CreateTaskDescriptionHistoryUseCaseImpl) {
                bind<CreateTaskDescriptionHistoryUseCase>()
            }
        }
    private val createTaskDescriptionHistoryUseCase: CreateTaskDescriptionHistoryUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localTaskDescriptionHistoryRepository,
            clock,
        )
    }


    @Nested
    @DisplayName("GIVEN a valid request")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SuccessPath {
        /**
         * GIVEN a taskId, a description (which can be null), and a creation time.
         * WHEN the use case is invoked.
         * THEN it should create a history model with the correct data and call the repository's insert method.
         */
        @ParameterizedTest(name = "[{index}] For description = {1}, it should insert correctly")
        @MethodSource("provideDescriptionScenarios")
        fun `creates history model and inserts correctly`(description: String?, testName: String) =
            runTest {
                // GIVEN
                val taskId = 1L
                val testTime = Instant.parse("2025-06-17T11:00:00Z")

                coEvery { localTaskDescriptionHistoryRepository.insert(any()) } returns 1L

                val historyModelSlot = slot<TaskDescriptionHistoryModel>()

                // WHEN
                val result =
                    createTaskDescriptionHistoryUseCase.invoke(taskId, description, testTime)

                // THEN
                assertThat(result).isInstanceOf(AppResult.Success::class.java)

                coVerify(exactly = 1) {
                    localTaskDescriptionHistoryRepository.insert(
                        capture(
                            historyModelSlot
                        )
                    )
                }

                val capturedModel = historyModelSlot.captured
                assertThat(capturedModel.taskId).isEqualTo(taskId)
                assertThat(capturedModel.text).isEqualTo(description)
                assertThat(capturedModel.createdAt).isEqualTo(testTime)
            }

        fun provideDescriptionScenarios(): Stream<Arguments> = Stream.of(
            Arguments.of("A valid description", "with non-null description"),
            Arguments.of(null, "with null description")
        )
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
            val exception = RuntimeException("Database constraint failed")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)

            coEvery { localTaskDescriptionHistoryRepository.insert(any()) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = createTaskDescriptionHistoryUseCase.invoke(
                1L,
                "some description",
                Instant.DISTANT_PAST
            )

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}