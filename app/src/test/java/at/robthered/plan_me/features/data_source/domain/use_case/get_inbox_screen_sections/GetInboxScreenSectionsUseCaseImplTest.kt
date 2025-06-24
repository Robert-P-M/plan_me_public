package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_sections.GetSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section.GetTaskModelsForSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
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


@DisplayName("GetInboxScreenSectionsUseCaseImpl Tests")
class GetInboxScreenSectionsUseCaseImplTest: BaseKoinTest() {

    private val getSectionsUseCase: GetSectionsUseCase by inject()

    private val getTaskModelsForSectionUseCase: GetTaskModelsForSectionUseCase by inject()

    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getSectionsUseCase,
            getTaskModelsForSectionUseCase,
            recursiveTaskWithHashtagsAndChildrenModelHelper
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::GetInboxScreenSectionsUseCaseImpl) {
                bind<GetInboxScreenSectionsUseCase>()
            }
        }

    private val getInboxScreenSectionsUseCase: GetInboxScreenSectionsUseCase by inject()


    @Nested
    @DisplayName("GIVEN sections source")
    inner class SectionSource {

        /**
         * GIVEN the getSectionsUseCase returns an empty list.
         * WHEN the use case is invoked.
         * THEN it should take the `if (sections.isEmpty())` branch and return a flow of an empty list.
         */
        @Test
        @DisplayName("WHEN no sections are found THEN should emit an empty list")
        fun `emits empty list when no sections`() = runTest {
            // GIVEN
            every { getSectionsUseCase.invoke() } returns flowOf(emptyList())

            // WHEN
            val resultFlow = getInboxScreenSectionsUseCase.invoke(1, null, null, SortDirection.ASC)

            // THEN
            resultFlow.test {
                assertThat(awaitItem()).isEmpty()
                awaitComplete()
            }
        }

        /**
         * GIVEN the getSectionsUseCase returns multiple sections.
         * WHEN the use case is invoked.
         * THEN it should fetch tasks for each section and combine them into a final list.
         */
        @Test
        @DisplayName("WHEN sections are found THEN should combine them with their tasks")
        fun `combines sections with their respective tasks`() = runTest {
            // GIVEN
            val section1 = createSection(1L, "Sektion 1")
            val section2 = createSection(2L, "Sektion 2")
            val tasksForSection1 = listOf(createTaskWithHashtags(11L, "Task 1.1"))
            val tasksForSection2 = listOf(
                createTaskWithHashtags(21L, "Task 2.1"),
                createTaskWithHashtags(22L, "Task 2.2")
            )

            every { getSectionsUseCase.invoke() } returns flowOf(listOf(section1, section2))

            every {
                getTaskModelsForSectionUseCase.invoke(
                    eq(1L),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(tasksForSection1)
            every {
                getTaskModelsForSectionUseCase.invoke(
                    eq(2L),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(tasksForSection2)

            coEvery {
                recursiveTaskWithHashtagsAndChildrenModelHelper.invoke(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(emptyList())

            // WHEN
            val resultFlow = getInboxScreenSectionsUseCase.invoke(1, null, null, SortDirection.ASC)

            // THEN
            resultFlow.test {
                val resultList = awaitItem()

                assertThat(resultList).hasSize(2)

                val resultSection1 = resultList.find { it.section.sectionId == 1L }
                assertThat(resultSection1).isNotNull()
                assertThat(resultSection1?.tasks).hasSize(1)
                assertThat(resultSection1?.tasks?.first()?.taskWithUiHashtagsModel).isEqualTo(
                    tasksForSection1.first()
                )

                val resultSection2 = resultList.find { it.section.sectionId == 2L }
                assertThat(resultSection2).isNotNull()
                assertThat(resultSection2?.tasks).hasSize(2)

                awaitComplete()
            }
        }
    }

    private fun createSection(id: Long, title: String) = SectionModel(
        sectionId = id,
        title = title,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    private fun createTaskWithHashtags(id: Long, title: String) = TaskWithUiHashtagsModel(
        task = TaskModel(
            taskId = id,
            title = title,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            isCompleted = false,
            isArchived = false
        )
    )
}