package at.robthered.plan_me.features.data_source.domain.use_case.delete_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("DeleteSectionUseCaseImpl Tests")
class DeleteSectionUseCaseImplTest: BaseKoinTest() {
    private val localSectionRepository: LocalSectionRepository by inject()

    override val mockTransactionProvider: Boolean
        get() = true
    override val mockSafeDatabaseResultCall: Boolean
        get() = true

    override val useCaseModule: Module
        get() = module {
            factoryOf(::DeleteSectionUseCaseImpl) {
                bind<DeleteSectionUseCase>()
            }
        }


    override fun getMocks(): Array<Any> {
        return arrayOf(
            localSectionRepository,
            transactionProvider,
            safeDatabaseResultCall,
        )
    }
    private val deleteSectionUseCase: DeleteSectionUseCase by inject()

    @Nested
    @DisplayName("GIVEN repository indicates success")
    inner class DeletionSucceeds {
        /**
         * GIVEN the repository's delete method returns 1 (one row affected).
         * WHEN the use case is invoked.
         * THEN it should return AppResult.Success.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 1 THEN should return Success")
        fun `returns Success when section is deleted`() = runTest {
            // GIVEN
            val sectionId = 1L
            coEvery { localSectionRepository.delete(sectionId) } returns 1

            // WHEN
            val result = deleteSectionUseCase.invoke(sectionId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { localSectionRepository.delete(sectionId) }
        }
    }

    @Nested
    @DisplayName("GIVEN repository indicates failure or throws")
    inner class DeletionFails {
        /**
         * GIVEN the repository's delete method returns 0 (no row affected).
         * WHEN the use case is invoked.
         * THEN it should return a NO_SECTION_FOUND error.
         */
        @Test
        @DisplayName("WHEN rowsAffected is 0 THEN should return NO_SECTION_FOUND error")
        fun `returns Error when section is not found`() = runTest {
            // GIVEN
            val sectionId = 404L
            coEvery { localSectionRepository.delete(sectionId) } returns 0

            // WHEN
            val result = deleteSectionUseCase.invoke(sectionId)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_SECTION_FOUND)
            coVerify(exactly = 1) { localSectionRepository.delete(sectionId) }
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
            val sectionId = 1L
            val exception = RuntimeException("Database offline")
            val expectedError = AppResult.Error(RoomDatabaseError.UNKNOWN)
            coEvery { localSectionRepository.delete(sectionId) } throws exception

            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } returns expectedError

            // WHEN
            val result = deleteSectionUseCase.invoke(sectionId)

            // THEN
            assertThat(result).isEqualTo(expectedError)
        }
    }
}