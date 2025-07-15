package at.robthered.plan_me.features.data_source.domain.use_case.get_section_model

import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject

@DisplayName("GetSectionModelUseCaseImpl Tests")
class GetSectionModelUseCaseImplTest: BaseKoinTest() {

    private val localSectionRepository: LocalSectionRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localSectionRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetSectionModelUseCaseImpl) {
                bind<GetSectionModelUseCase>()
            }
        }
    private val getSectionModelUseCase: GetSectionModelUseCase by inject()

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