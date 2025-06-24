package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items

import app.cash.turbine.test
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.data.local.exception.DatabaseOperationFailedException
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.mapper.toInboxItem
import at.robthered.plan_me.features.data_source.domain.model.SectionWithTasksModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_sections.GetInboxScreenSectionsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks.GetInboxScreenTasksUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.test.inject


@DisplayName("GetInboxScreenItemsUseCaseImpl Tests")
class GetInboxScreenItemsUseCaseImplTest: BaseKoinTest() {


    private val getInboxScreenTasksUseCase: GetInboxScreenTasksUseCase by inject()
    private val getInboxScreenSectionsUseCase: GetInboxScreenSectionsUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    override val useCaseModule: Module
        get() = module {
            single<CoroutineDispatcher> { testDispatcher}
            factoryOf(::GetInboxScreenItemsUseCaseImpl) {
                bind<GetInboxScreenItemsUseCase>()
            }
        }
    private val getInboxScreenItemsUseCase: GetInboxScreenItemsUseCase by inject()

    override fun getMocks(): Array<Any> {
        return arrayOf(
            getInboxScreenSectionsUseCase,
            getInboxScreenTasksUseCase,
        )
    }

    @Nested
    @DisplayName("GIVEN all sources behave as expected")
    inner class SuccessPath {
        /**
         * GIVEN both task and section sources will emit data.
         * WHEN the use case is invoked.
         * THEN it should emit Loading, followed by intermediate states, and finally a combined list.
         */
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        @DisplayName("WHEN both sources emit data THEN should combine them correctly")
        fun `combines flows successfully`() = runTest(testDispatcher) {
            // GIVEN
            val testTime = Instant.parse("2025-01-01T12:00:00Z")

            val fakeTaskData = listOf(
                TaskWithHashtagsAndChildrenModel(
                    taskWithUiHashtagsModel = TaskWithUiHashtagsModel(
                        task = TaskModel(
                            taskId = 1L,
                            title = "Task 1",
                            createdAt = testTime,
                            updatedAt = testTime,
                            isCompleted = false,
                            isArchived = false
                        )
                    ),
                    maxDepthReached = false
                )
            )
            val fakeSectionData = listOf(
                SectionWithTasksModel(
                    section = SectionModel(
                        sectionId = 1L,
                        title = "Section 1",
                        createdAt = testTime,
                        updatedAt = testTime
                    ),
                    tasks = emptyList()
                )
            )

            val expectedCombinedList = listOf(
                fakeTaskData.toInboxItem(),
                fakeSectionData.first().toInboxItem()
            )

            val tasksFlow = MutableSharedFlow<List<TaskWithHashtagsAndChildrenModel>>()
            val sectionsFlow = MutableSharedFlow<List<SectionWithTasksModel>>()

            every {
                getInboxScreenTasksUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns tasksFlow
            every {
                getInboxScreenSectionsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns sectionsFlow

            // WHEN & THEN
            getInboxScreenItemsUseCase.invoke(0, null, null, SortDirection.ASC).test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                tasksFlow.emit(fakeTaskData)
                sectionsFlow.emit(fakeSectionData)


                val finalState = awaitItem() as AppResource.Success

                assertThat(finalState.data).hasSize(2)
                assertThat(finalState.data).containsExactlyElementsIn(expectedCombinedList)
                    .inOrder()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN a source encounters an error")
    inner class FailurePath {
        /**
         * GIVEN one of the source flows throws an exception.
         * WHEN the use case is invoked.
         * THEN it should emit Loading, and then catch the exception and emit a corresponding AppResource.Error.
         */
        @Test
        @DisplayName("WHEN a source flow throws exception THEN should emit Error")
        fun `emits error when source fails`() = runTest(testDispatcher) {
            // GIVEN
            val dbError = RoomDatabaseError.UNKNOWN

            val failingFlow = flow<List<TaskWithHashtagsAndChildrenModel>> {
                throw DatabaseOperationFailedException(dbError)
            }

            every {
                getInboxScreenTasksUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns failingFlow
            every {
                getInboxScreenSectionsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns MutableSharedFlow()

            // WHEN & THEN
            getInboxScreenItemsUseCase.invoke(0, null, null, SortDirection.ASC).test {
                assertThat(awaitItem()).isInstanceOf(AppResource.Loading::class.java)

                val errorEmission = awaitItem()

                assertThat(errorEmission).isInstanceOf(AppResource.Error::class.java)
                assertThat((errorEmission as AppResource.Error).error).isEqualTo(dbError)

                awaitComplete()
            }
        }
    }
}