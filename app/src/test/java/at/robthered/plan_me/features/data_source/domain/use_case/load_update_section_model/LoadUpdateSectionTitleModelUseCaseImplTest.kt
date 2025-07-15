package at.robthered.plan_me.features.data_source.domain.use_case.load_update_section_model

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateSectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadUpdateSectionTitleModelUseCaseImpl Tests")
class LoadUpdateSectionTitleModelUseCaseImplTest: BaseKoinTest() {

    private val localSectionRepository: LocalSectionRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localSectionRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadUpdateSectionTitleModelUseCaseImpl){
                bind<LoadUpdateSectionTitleModelUseCase>()
            }
        }
    private val loadUpdateSectionTitleModelUseCase: LoadUpdateSectionTitleModelUseCase by inject()


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