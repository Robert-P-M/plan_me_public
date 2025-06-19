package at.robthered.plan_me.features.data_source.domain.use_case.get_section_model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("GetSectionModelUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetSectionModelUseCaseImplTest {

    @MockK
    private lateinit var localSectionRepository: LocalSectionRepository

    private lateinit var getSectionModelUseCase: GetSectionModelUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localSectionRepository)
        getSectionModelUseCase = GetSectionModelUseCaseImpl(localSectionRepository)
    }

    @Nested
    @DisplayName("GIVEN the repository provides a result")
    inner class RepositoryResults {

        /**
         * GIVEN the repository returns a flow containing a valid SectionModel.
         * WHEN the use case is invoked.
         * THEN it should return that SectionModel object.
         */
        @Test
        @DisplayName("WHEN repository finds a section THEN it should return the section model")
        fun `returns section model when found`() = runTest {
            // GIVEN
            val sectionId = 1L
            val expectedSection = SectionModel(
                sectionId = sectionId,
                title = "Test Section",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            every { localSectionRepository.get(sectionId) } returns flowOf(expectedSection)

            // WHEN
            val result = getSectionModelUseCase.invoke(sectionId)

            // THEN
            assertThat(result).isNotNull()
            assertThat(result).isEqualTo(expectedSection)
            verify(exactly = 1) { localSectionRepository.get(sectionId) }
        }

        /**
         * GIVEN the repository returns a flow containing null.
         * WHEN the use case is invoked.
         * THEN it should return null.
         */
        @Test
        @DisplayName("WHEN repository finds no section THEN it should return null")
        fun `returns null when not found`() = runTest {
            // GIVEN
            val sectionId = 404L

            every { localSectionRepository.get(sectionId) } returns flowOf(null)

            // WHEN
            val result = getSectionModelUseCase.invoke(sectionId)

            // THEN
            assertThat(result).isNull()
            verify(exactly = 1) { localSectionRepository.get(sectionId) }
        }
    }
}