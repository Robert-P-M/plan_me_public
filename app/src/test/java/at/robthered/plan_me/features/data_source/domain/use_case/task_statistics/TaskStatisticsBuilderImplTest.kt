package at.robthered.plan_me.features.data_source.domain.use_case.task_statistics

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
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

@DisplayName("TaskStatisticsBuilderImpl Tests")
class TaskStatisticsBuilderImplTest : BaseKoinTest(){

    override val useCaseModule: Module
        get() = module {
            factoryOf(::TaskStatisticsBuilderImpl){
                bind<TaskStatisticsBuilder>()
            }
        }

    override fun getMocks(): Array<Any> {
        return emptyArray()
    }
    private val taskStatisticsBuilder: TaskStatisticsBuilder by inject()


    @Nested
    @DisplayName("GIVEN history data")
    inner class HistoryDataTests {

        /**
         * GIVEN a list of title history entries.
         * WHEN the builder is invoked.
         * THEN it should create corresponding models and set `isLast` correctly only on the last item.
         */
        @Test
        @DisplayName("WHEN processing title history THEN should set isLast flag correctly")
        fun `builds title history with correct isLast flag`() {
            // GIVEN
            val history = listOf(
                createTitleHistory(1L, "Title 1", "2025-01-01T10:00:00Z"),
                createTitleHistory(1L, "Title 2", "2025-01-01T11:00:00Z")
            )
            val historyData = TaskHistoryModel(
                title = history,
                description = emptyList(),
                priority = emptyList(),
                completed = emptyList(),
                archived = emptyList(),
            )

            // WHEN
            val result =
                taskStatisticsBuilder.invoke(historyData, createTask(), emptyList(), emptyList())

            // THEN
            val titleHistoryResults =
                result.filterIsInstance<TaskStatisticsModel.TaskTitleHistory>()
            assertThat(titleHistoryResults).hasSize(2)
            assertThat(titleHistoryResults[0].isLast).isFalse()
            assertThat(titleHistoryResults[1].isLast).isTrue()
        }

        /**
         * GIVEN a list of priority history entries.
         * WHEN the builder is invoked.
         * THEN it should link each entry to its predecessor correctly.
         */
        @Test
        @DisplayName("WHEN processing priority history THEN should link to previous priority correctly")
        fun `builds priority history with correct previous link`() {
            // GIVEN
            val priority1 = createPriorityHistory(1L, PriorityEnum.LOW, "2025-01-01T10:00:00Z")
            val priority2 = createPriorityHistory(1L, PriorityEnum.MEDIUM, "2025-01-01T11:00:00Z")
            val priority3 = createPriorityHistory(1L, PriorityEnum.HIGH, "2025-01-01T12:00:00Z")
            val historyData = TaskHistoryModel(
                priority = listOf(priority1, priority2, priority3),
                title = emptyList(),
                description = emptyList(),
                completed = emptyList(),
                archived = emptyList(),
            )

            // WHEN
            val result =
                taskStatisticsBuilder.invoke(historyData, createTask(), emptyList(), emptyList())

            // THEN
            val priorityHistoryResults =
                result.filterIsInstance<TaskStatisticsModel.TaskPriorityHistory>()
            assertThat(priorityHistoryResults).hasSize(3)

            assertThat(priorityHistoryResults[0].previousTaskPriorityHistoryModel).isEqualTo(
                priority2
            )
            assertThat(priorityHistoryResults[1].previousTaskPriorityHistoryModel).isEqualTo(
                priority3
            )
            assertThat(priorityHistoryResults[2].previousTaskPriorityHistoryModel).isNull()
            assertThat(priorityHistoryResults[2].isLast).isTrue()
        }
    }

    @Nested
    @DisplayName("GIVEN hashtag data")
    inner class HashtagDataTests {
        /**
         * GIVEN hashtags with timestamps spanning across a 10-minute window.
         * WHEN the builder is invoked.
         * THEN it should group the hashtags correctly.
         */
        @Test
        @DisplayName("WHEN processing hashtags THEN should group them by 10-minute windows")
        fun `groups hashtags by time window correctly`() {
            // GIVEN
            val hashtag1 = createHashtag(1L, "t1", "2025-01-01T12:01:00Z")
            val hashtag2 = createHashtag(2L, "t2", "2025-01-01T12:09:00Z")
            val hashtag3 = createHashtag(3L, "t3", "2025-01-01T12:11:00Z")

            val hashtags = listOf(hashtag1, hashtag2, hashtag3)

            // WHEN
            val result = taskStatisticsBuilder.invoke(
                TaskHistoryModel(
                    title = emptyList(),
                    description = emptyList(),
                    priority = emptyList(),
                    completed = emptyList(),
                    archived = emptyList()
                ), createTask(), emptyList(), hashtags
            )

            // THEN
            val hashtagResults = result.filterIsInstance<TaskStatisticsModel.Hashtags>()
            assertThat(hashtagResults).hasSize(2)

            val firstGroup =
                hashtagResults.find { it.createdAt == Instant.parse("2025-01-01T12:00:00Z") }
            assertThat(firstGroup).isNotNull()
            assertThat(firstGroup?.hashtags).containsExactly(hashtag1, hashtag2).inOrder()

            val secondGroup =
                hashtagResults.find { it.createdAt == Instant.parse("2025-01-01T12:10:00Z") }
            assertThat(secondGroup).isNotNull()
            assertThat(secondGroup?.hashtags).containsExactly(hashtag3)
        }
    }

    private fun createTask() = TaskModel(
        taskId = 1L,
        title = "Task",
        isCompleted = false,
        isArchived = false,
        createdAt = Instant.DISTANT_PAST,
        updatedAt = Instant.DISTANT_PAST
    )

    private fun createTitleHistory(id: Long, text: String, time: String) =
        TaskTitleHistoryModel(taskId = id, text = text, createdAt = Instant.parse(time))

    private fun createPriorityHistory(id: Long, priority: PriorityEnum, time: String) =
        TaskPriorityHistoryModel(
            taskId = id,
            priorityEnum = priority,
            createdAt = Instant.parse(time)
        )

    private fun createHashtag(id: Long, name: String, time: String) = HashtagModel(
        hashtagId = id,
        name = name,
        createdAt = Instant.parse(time),
        updatedAt = Instant.parse(time)
    )
}