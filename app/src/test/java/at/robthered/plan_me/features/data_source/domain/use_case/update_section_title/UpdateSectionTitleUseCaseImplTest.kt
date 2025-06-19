package at.robthered.plan_me.features.data_source.domain.use_case.update_section_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_section_model.GetSectionModelUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Ordering
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

@DisplayName("UpdateSectionTitleUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateSectionTitleUseCaseImplTest {

    @MockK
    private lateinit var transactionProvider: TransactionProvider
    @MockK
    private lateinit var localSectionRepository: LocalSectionRepository
    @MockK
    private lateinit var localSectionTitleHistoryRepository: LocalSectionTitleHistoryRepository
    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall
    @MockK
    private lateinit var getSectionModelUseCase: GetSectionModelUseCase
    @MockK
    private lateinit var clock: Clock

    private lateinit var updateSectionTitleUseCase: UpdateSectionTitleUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            transactionProvider, localSectionRepository, localSectionTitleHistoryRepository,
            safeDatabaseResultCall, getSectionModelUseCase, clock
        )
        updateSectionTitleUseCase = UpdateSectionTitleUseCaseImpl(
            transactionProvider, localSectionRepository, localSectionTitleHistoryRepository,
            safeDatabaseResultCall, getSectionModelUseCase, clock
        )

        coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
        coEvery { transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN the section to be updated exists")
    inner class SectionExists {
        /**
         * GIVEN an existing section is found.
         * WHEN invoke is called with a new title.
         * THEN it should upsert the section with the new title and timestamp,
         * create a new history entry, and return Success.
         */
        @Test
        @DisplayName("WHEN invoked THEN should update section and create history entry")
        fun `updates section and creates history successfully`() = runTest {
            // GIVEN
            val updateModel = UpdateSectionTitleModel(sectionId = 1L, title = "New Title")
            val testTime = Instant.parse("2025-08-20T10:00:00Z")
            val initialSection = SectionModel(
                sectionId = 1L,
                title = "Old Title",
                createdAt = testTime,
                updatedAt = testTime
            )

            coEvery { getSectionModelUseCase.invoke(updateModel.sectionId) } returns initialSection
            every { clock.now() } returns testTime
            coEvery { localSectionRepository.upsert(any()) } returns Unit
            coEvery { localSectionTitleHistoryRepository.insert(any()) } returns 1L

            val sectionSlot = slot<SectionModel>()
            val historySlot = slot<SectionTitleHistoryModel>()

            // WHEN
            val result = updateSectionTitleUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)

            coVerify(ordering = Ordering.ORDERED) {
                localSectionRepository.upsert(capture(sectionSlot))
                localSectionTitleHistoryRepository.insert(capture(historySlot))
            }

            val capturedSection = sectionSlot.captured
            assertThat(capturedSection.sectionId).isEqualTo(updateModel.sectionId)
            assertThat(capturedSection.title).isEqualTo(updateModel.title)
            assertThat(capturedSection.updatedAt).isEqualTo(testTime)

            val capturedHistory = historySlot.captured
            assertThat(capturedHistory.sectionId).isEqualTo(updateModel.sectionId)
            assertThat(capturedHistory.text).isEqualTo(updateModel.title)
            assertThat(capturedHistory.createdAt).isEqualTo(testTime)
        }
    }

    @Nested
    @DisplayName("GIVEN the section does not exist")
    inner class SectionDoesNotExist {
        /**
         * GIVEN getSectionModelUseCase returns null.
         * WHEN invoke is called.
         * THEN it should return a NO_SECTION_FOUND error and not perform any writes.
         */
        @Test
        @DisplayName("WHEN invoked THEN should return NO_SECTION_FOUND error")
        fun `returns error when section not found`() = runTest {
            // GIVEN
            val updateModel = UpdateSectionTitleModel(sectionId = 404L, title = "Does not matter")
            coEvery { getSectionModelUseCase.invoke(updateModel.sectionId) } returns null

            // WHEN
            val result = updateSectionTitleUseCase.invoke(updateModel)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_SECTION_FOUND)

            coVerify(exactly = 0) { localSectionRepository.upsert(any()) }
            coVerify(exactly = 0) { localSectionTitleHistoryRepository.insert(any()) }
        }
    }
}