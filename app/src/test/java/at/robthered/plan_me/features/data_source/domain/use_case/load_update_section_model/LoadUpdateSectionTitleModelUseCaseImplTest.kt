package at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateSectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("LoadUpdateSectionTitleModelUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoadUpdateSectionTitleModelUseCaseImplTest {

    @MockK
    private lateinit var localSectionRepository: LocalSectionRepository

    private lateinit var loadUpdateSectionTitleModelUseCase: LoadUpdateSectionTitleModelUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(localSectionRepository)
        loadUpdateSectionTitleModelUseCase =
            LoadUpdateSectionTitleModelUseCaseImpl(localSectionRepository)
    }

    @Nested
    @DisplayName("GIVEN the section exists in repository")
    inner class SectionFound {
        /**
         * GIVEN the repository returns a flow containing a valid SectionModel.
         * WHEN the use case is invoked.
         * THEN it should emit the correctly mapped UpdateSectionTitleModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should map and emit the correct UpdateSectionTitleModel")
        fun `maps existing section to UpdateSectionTitleModel`() = runTest {
            // GIVEN
            val sectionId = 1L
            val now = Clock.System.now()
            val sectionFromRepo = SectionModel(
                sectionId = sectionId,
                title = "My Section Title",
                createdAt = now,
                updatedAt = now
            )
            val expectedUpdateModel = sectionFromRepo.toUpdateSectionModel()

            every { localSectionRepository.get(sectionId) } returns flowOf(sectionFromRepo)

            // WHEN
            val resultFlow = loadUpdateSectionTitleModelUseCase.invoke(sectionId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedUpdateModel)
                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN the section does not exist in repository")
    inner class SectionNotFound {
        /**
         * GIVEN the repository returns a flow containing null.
         * WHEN the use case is invoked.
         * THEN it should emit a default, empty UpdateSectionTitleModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should emit a default UpdateSectionTitleModel")
        fun `emits default model when section not found`() = runTest {
            // GIVEN
            val sectionId = 404L
            val expectedDefaultModel = UpdateSectionTitleModel()

            every { localSectionRepository.get(sectionId) } returns flowOf(null)

            // WHEN
            val resultFlow = loadUpdateSectionTitleModelUseCase.invoke(sectionId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedDefaultModel)
                awaitComplete()
            }
        }
    }
}