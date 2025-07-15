package at.robthered.plan_me.features.datasource.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskScheduleEventEntity
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.IOException


/**
 * Instrumentation tests for [TaskDao].
 *
 * This suite uses a real in-memory Room database to verify that all CRUD
 * (Create, Read, Update, Delete) operations and complex SQL queries for the Task table
 * work as expected.
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val sectionDao: SectionDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * GIVEN multiple root tasks are inserted in a non-alphabetical order.
     * WHEN getRootTaskEntitiesOrderDesc is called.
     * THEN it should return only root tasks (no sub-tasks or section-tasks), ordered by creation date descending.
     */
    @Test
    fun getRootTaskEntities_withDescSort_returnsCorrectlyFilteredAndSortedList() = runTest {
        // GIVEN
        val sectionId = sectionDao.insert(createSection(1L, "Test Section"))
        taskDao.insert(createTask(1L, "Root Task 1", createdAt = 100))
        taskDao.insert(createTask(2L, "Root Task 2", createdAt = 200))
        taskDao.insert(createTask(3L, "Sub-Task", parentId = 1, createdAt = 300))
        taskDao.insert(createTask(4L, "Section-Task", sectionId = sectionId, createdAt = 400))

        // WHEN
        val rootTasks =
            taskDao.getRootTaskEntitiesOrderDesc(showCompleted = null, showArchived = null).first()

        // THEN
        assertThat(rootTasks).hasSize(2)
        assertThat(rootTasks.map { it.taskId }).containsExactly(2L, 1L).inOrder()
        assertThat(rootTasks.all { it.parentTaskId == null && it.sectionId == null }).isTrue()
    }

    /**
     * GIVEN tasks exist in different sections and as root tasks.
     * WHEN getTaskEntitiesForSectionOrderAsc is called for a specific section.
     * THEN it should return only tasks belonging to that section, ordered by creation date ascending.
     */
    @Test
    fun getTaskEntitiesForSection_withAscSort_returnsCorrectlyFilteredAndSortedList() = runTest {
        // GIVEN
        val sectionId = sectionDao.insert(createSection(1L, "Test Section"))
        taskDao.insert(createTask(1L, "Section 1 Task 2", sectionId = sectionId, createdAt = 200))
        taskDao.insert(createTask(2L, "Section 1 Task 1", sectionId = sectionId, createdAt = 100))
        taskDao.insert(createTask(3L, "Root Task", createdAt = 300))

        // WHEN
        val sectionTasks = taskDao.getTaskEntitiesForSectionOrderAsc(
            sectionId = sectionId, showCompleted = null, showArchived = null
        ).first()

        // THEN
        assertThat(sectionTasks).hasSize(2)
        assertThat(sectionTasks.map { it.taskId }).containsExactly(2L, 1L).inOrder()
        assertThat(sectionTasks.all { it.sectionId == sectionId }).isTrue()
    }

    /**
     * GIVEN a parent task with multiple direct children and grandchildren exists.
     * WHEN getTaskEntitiesForParentOrderDesc is called for the parent.
     * THEN it should return only the direct children, ordered by creation date descending.
     */
    @Test
    fun getTaskEntitiesForParent_withDescSort_returnsOnlyDirectChildrenSorted() = runTest {
        // GIVEN
        val parentTask = createTask(1, "Parent")
        val child1 = createTask(2, "Child 1", parentId = 1, createdAt = 200)
        val child2 = createTask(3, "Child 2", parentId = 1, createdAt = 300)
        val grandChild = createTask(4, "Grandchild", parentId = 2, createdAt = 400)
        taskDao.insert(parentTask)
        taskDao.insert(child1)
        taskDao.insert(child2)
        taskDao.insert(grandChild)

        // WHEN
        val children = taskDao.getTaskEntitiesForParentOrderDesc(
            parentTaskId = 1, showCompleted = null, showArchived = null
        ).first()

        // THEN
        assertThat(children).hasSize(2)
        assertThat(children.map { it.taskId }).containsExactly(3L, 2L).inOrder()
    }

    /**
     * GIVEN a mix of tasks with different schedules.
     * WHEN getUpcomingTasksForAlarm is called for a specific day.
     * THEN it should return only tasks that are scheduled for today or the future and have notifications enabled.
     */
    @Test
    fun getUpcomingTasksForAlarm_returnsCorrectlyFilteredTasks() = runTest {
        // GIVEN
        val todayEpoch = 20000
        taskDao.insert(
            createTask(
                1,
                "Past",
                schedule = createSchedule(startDate = 19999, enabled = true)
            )
        )
        taskDao.insert(
            createTask(
                2,
                "Future Enabled",
                schedule = createSchedule(startDate = 20001, enabled = true)
            )
        )
        taskDao.insert(
            createTask(
                3,
                "Future Disabled",
                schedule = createSchedule(startDate = 20002, enabled = false)
            )
        )
        taskDao.insert(
            createTask(
                4,
                "Today Enabled",
                schedule = createSchedule(startDate = 20000, enabled = true)
            )
        )
        taskDao.insert(createTask(5, "No Schedule", schedule = null))

        // WHEN
        val upcomingTasks = taskDao.getUpcomingTasksForAlarm(dateInEpochDays = todayEpoch).first()

        // THEN
        assertThat(upcomingTasks).hasSize(2)
        assertThat(upcomingTasks.map { it.taskId }).containsExactly(2L, 4L)
    }

    // --- Helper Functions ---
    private fun createTask(
        id: Long,
        title: String = "Task $id",
        createdAt: Long = 0L,
        parentId: Long? = null,
        sectionId: Long? = null,
        schedule: TaskScheduleEventEntity? = null,
    ): TaskEntity {
        return TaskEntity(
            taskId = id,
            title = title,
            createdAt = createdAt,
            updatedAt = createdAt,
            isCompleted = false,
            isArchived = false,
            parentTaskId = parentId,
            sectionId = sectionId,
            taskSchedule = schedule
        )
    }

    private fun createSection(id: Long, title: String) =
        SectionEntity(sectionId = id, title = title, createdAt = 0L, updatedAt = 0L)

    private fun createSchedule(startDate: Int, enabled: Boolean): TaskScheduleEventEntity {
        return TaskScheduleEventEntity(
            taskId = 0, // Not needed for this embedded object
            startDateInEpochDays = startDate,
            isNotificationEnabled = enabled,
            createdAt = 0
        )
    }
}