package at.robthered.plan_me.features.data_source.domain.use_case.add_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
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
import org.junit.jupiter.api.assertAll
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject


/**
 * Test suite for [AddSectionUseCaseImpl].
 *
 * This class tests the business logic for adding a new section. It focuses on verifying
 * the interactions with its dependencies (repositories, providers) rather than the logic
 * within those dependencies. Mocks are provided by MockK.
 */
@DisplayName("AddSectionUseCaseImpl Tests")
class AddSectionUseCaseImplTest : BaseKoinTest() {

    private val localSectionRepository: LocalSectionRepository by inject()

    private val localSectionTitleHistoryRepository: LocalSectionTitleHistoryRepository by inject()


    private val clock: Clock by inject()

    private val addSectionUseCase: AddSectionUseCase by inject()

    override val mockTransactionProvider: Boolean
        get() = true
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override val useCaseModule: Module
        get() = module {
            factoryOf(::AddSectionUseCaseImpl) {
                bind<AddSectionUseCase>()
            }
        }

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localSectionRepository,
            localSectionTitleHistoryRepository,
            clock,
        )
    }

    @Nested
    @DisplayName("GIVEN all dependencies succeed")
    inner class SuccessPath {

        /**

         * GIVEN a valid [AddSectionModel] and all dependencies are mocked for a successful outcome.
         * WHEN the `invoke` method is called.
         * THEN the result should be [AppResult.Success], and it should verify that:
         * 1. The operation runs within a transaction.
         * 2. The section and history repositories are called exactly once to insert data.
         * 3. The data passed to the repositories is constructed correctly with the proper title and timestamp.
         *
         * @see AddSectionUseCaseImpl.invoke
         */
        @Test
        @DisplayName("invoke() with valid model should return Success and verify correct interactions")
        fun `invoke - successful insert - returns Success`() = runTest {
            // GIVEN
            val addSectionModel = AddSectionModel(title = "New Section")
            val expectedSectionId = 123L
            val expectedHistoryInsertId = 1L
            val testTime = Instant.parse("2024-01-01T00:00:00Z")

            every { clock.now() } answers { testTime }

            coEvery { localSectionRepository.insert(any()) } coAnswers { expectedSectionId }
            coEvery { localSectionTitleHistoryRepository.insert(any()) } coAnswers { expectedHistoryInsertId }

            val sectionSlot = slot<SectionModel>()
            val historySlot = slot<SectionTitleHistoryModel>()

            coEvery {
                safeDatabaseResultCall<Unit>(callerTag = any(), block = captureCoroutine())
            } coAnswers {
                val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
                block()
            }
            // WHEN
            val result = addSectionUseCase.invoke(addSectionModel)
            // THEN
            assertAll(
                "Verify successful result and all interactions",
                { assertThat(result).isInstanceOf(AppResult.Success::class.java) },
                {
                    coVerify(exactly = 1) {
                        localSectionRepository.insert(capture(sectionSlot))
                    }
                    val capturedSection = sectionSlot.captured
                    assertThat(capturedSection.title).isEqualTo(addSectionModel.title)
                    assertThat(capturedSection.createdAt).isEqualTo(testTime)
                    assertThat(capturedSection.updatedAt).isEqualTo(testTime)
                },
                {
                    coVerify(exactly = 1) {
                        localSectionTitleHistoryRepository.insert(capture(historySlot))
                    }

                    val capturedHistory = historySlot.captured
                    assertThat(capturedHistory.sectionId).isEqualTo(expectedSectionId)
                    assertThat(capturedHistory.text).isEqualTo(addSectionModel.title)
                    assertThat(capturedHistory.createdAt).isEqualTo(testTime)
                }
            )
        }
    }

    @Nested
    @DisplayName("GIVEN a subsequent dependency fails")
    inner class FailurePath {

        /**
         * GIVEN the `SafeDatabaseResultCall` dependency is mocked to return a specific error.
         * WHEN the `invoke` method is called.
         * THEN the use case should not proceed with the transaction or repository calls,
         * and should directly return the [AppResult.Error] provided by the mock.
         *
         * @see AddSectionUseCaseImpl.invoke
         */
        @Test
        @DisplayName("invoke() should return Error when the database call wrapper fails")
        fun `invoke - error from SafeDatabaseCall - returns Error`() = runTest {
            // GIVEN
            val addSectionModel = AddSectionModel(title = "Faulty Section")
            val expectedError = RoomDatabaseError.UNKNOWN

            coEvery {
                safeDatabaseResultCall<Unit>(callerTag = any(), block = any())
            } returns AppResult.Error(expectedError)

            // WHEN
            val result = addSectionUseCase.invoke(addSectionModel)

            // THEN
            assertAll(
                "Verify failing result and correct interaction chain",
                { assertThat((result as AppResult.Error).error).isEqualTo(expectedError) },
                {
                    assertThat(result).isInstanceOf(AppResult.Error::class.java)
                    coVerify(exactly = 0) { transactionProvider.runAsTransaction(any()) }
                    coVerify(exactly = 0) { localSectionRepository.insert(any()) }
                }
            )

        }
    }


}