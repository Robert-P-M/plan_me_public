package at.robthered.plan_me.features.data_source.domain.use_case.get_sections

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
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
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("GetSectionsUseCaseImpl Tests")
class GetSectionsUseCaseImplTest: BaseKoinTest() {


    private val localSectionRepository: LocalSectionRepository by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            localSectionRepository
        )
    }

    override val useCaseModule: Module
        get()  = module {
            factoryOf(::GetSectionsUseCaseImpl){
                bind<GetSectionsUseCase>()
            }
        }
    private val getSectionsUseCase: GetSectionsUseCase by inject()

    @Nested
    @DisplayName("GIVEN the repository provides data")
    inner class RepositoryHasData {

        /**
         * GIVEN the repository returns a flow with a list of sections.
         * WHEN the use case is invoked.
         * THEN it should return the exact same flow of sections.
         */
        @Test
        @DisplayName("WHEN invoked THEN should forward the flow of sections from the repository")
        fun `forwards flow with data from repository`() = runTest {
            // GIVEN
            val now = Clock.System.now()
            val fakeSections = listOf(
                SectionModel(sectionId = 1L, title = "Work", createdAt = now, updatedAt = now),
                SectionModel(sectionId = 2L, title = "Private", createdAt = now, updatedAt = now)
            )
            val repositoryFlow = flowOf(fakeSections)

            every { localSectionRepository.get() } returns repositoryFlow

            // WHEN
            val resultFlow = getSectionsUseCase.invoke()

            // THEN
            resultFlow.test {
                val emittedList = awaitItem()
                assertThat(emittedList).isEqualTo(fakeSections)
                awaitComplete()
            }

            verify(exactly = 1) { localSectionRepository.get() }
        }

        /**
         * GIVEN the repository returns a flow with an empty list.
         * WHEN the use case is invoked.
         * THEN it should return a flow with an empty list.
         */
        @Test
        @DisplayName("WHEN no sections exist THEN should return a flow with an empty list")
        fun `forwards empty flow from repository`() = runTest {
            // GIVEN
            val emptyFlow = flowOf(emptyList<SectionModel>())
            every { localSectionRepository.get() } returns emptyFlow

            // WHEN
            val resultFlow = getSectionsUseCase.invoke()

            // THEN
            resultFlow.test {
                val emittedList = awaitItem()
                assertThat(emittedList).isEmpty()
                awaitComplete()
            }
        }
    }
}