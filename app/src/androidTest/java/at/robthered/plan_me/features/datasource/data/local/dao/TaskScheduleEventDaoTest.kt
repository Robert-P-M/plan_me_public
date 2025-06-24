package at.robthered.plan_me.features.datasource.data.local.dao


import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskScheduleEventDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
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
 * Instrumentation tests for [TaskScheduleEventDao].
 *
 * This suite uses a real in-memory Room database to verify all CRUD operations,
 * queries, and foreign key constraints for the TaskScheduleEvent entity.
 */
@RunWith(AndroidJUnit4::class)
class TaskScheduleEventDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )


    private val db: AppDatabase by inject()
    private val eventDao: TaskScheduleEventDao by inject()
    private val taskDao: TaskDao by inject()


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
     * GIVEN a valid parent task exists.
     * WHEN a new schedule event is inserted.
     * THEN it should be retrievable via the getForTask method.
     */
    @Test
    fun insert_whenEntryIsValid_canBeRetrievedViaGetForTask() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1))
        val scheduleEvent = createScheduleEvent(
            taskId = parentTaskId,
            startDate = 20000,
            isNotificationEnabled = true,
            createdAt = 100L
        )

        // WHEN
        val newEventId = eventDao.insert(scheduleEvent)

        val retrieved = eventDao.getForTask(parentTaskId).first()

        // THEN
        assertThat(retrieved).hasSize(1)
        assertThat(retrieved.first().taskScheduleEventId).isEqualTo(newEventId)
        assertThat(retrieved.first().startDateInEpochDays).isEqualTo(20000)
        assertThat(retrieved.first().isNotificationEnabled).isTrue()
    }

    /**
     * GIVEN multiple schedule events for different tasks exist.
     * WHEN getForTask() is called for a specific task.
     * THEN it should return only the entries for that task, sorted by creation date.
     */
    @Test
    fun getForTask_whenEntriesExist_returnsFilteredAndSortedList() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1))

        // Insert out of order to test sorting
        eventDao.insert(
            createScheduleEvent(
                taskId = parentTaskId,
                startDate = 20002,
                createdAt = 200L
            )
        )
        eventDao.insert(
            createScheduleEvent(
                taskId = parentTaskId,
                startDate = 20001,
                createdAt = 100L
            )
        )

        // Insert unrelated data
        val otherTaskId = taskDao.insert(createTask(id = 2))
        eventDao.insert(
            createScheduleEvent(
                taskId = otherTaskId,
                startDate = 30000,
                createdAt = 300L
            )
        )

        // WHEN
        val eventsForTask = eventDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(eventsForTask).hasSize(2)
        // Verifies ORDER BY createdAt ASC
        assertThat(eventsForTask.map { it.startDateInEpochDays }).containsExactly(20001, 20002)
            .inOrder()
    }

    @Test
    fun deleteById_shouldRemoveOnlyCorrectEntry() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1))
        val event1 = eventDao.insert(
            createScheduleEvent(
                taskId = parentTaskId,
                startDate = 1,
                createdAt = 100L
            )
        )
        val event2 = eventDao.insert(
            createScheduleEvent(
                taskId = parentTaskId,
                startDate = 2,
                createdAt = 200L
            )
        )

        // WHEN
        val deletedRows = eventDao.delete(taskScheduleEventId = event1)
        val remainingEvents = eventDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(deletedRows).isEqualTo(1)
        assertThat(remainingEvents).hasSize(1)
        assertThat(remainingEvents.first().taskScheduleEventId).isEqualTo(event2)
    }

    /**
     * GIVEN a parent task and its schedule event exist.
     * WHEN the parent task is deleted.
     * THEN the schedule event should also be deleted due to the CASCADE constraint.
     */
    @Test
    fun deleteParentTask_cascadesDeleteToScheduleEvents() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1))
        eventDao.insert(createScheduleEvent(taskId = parentTaskId, startDate = 1, createdAt = 100L))
        eventDao.insert(createScheduleEvent(taskId = parentTaskId, startDate = 2, createdAt = 200L))

        // WHEN
        taskDao.delete(taskId = parentTaskId)
        val events = eventDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(events).isEmpty()
    }

    @Test
    fun insert_withNonExistentTask_throwsForeignKeyConstraintException() = runTest {
        // GIVEN
        val invalidEvent = createScheduleEvent(taskId = 999L, startDate = 1, createdAt = 100L)

        // WHEN/THEN
        var exception: Throwable? = null
        try {
            eventDao.insert(invalidEvent)
        } catch (e: SQLiteConstraintException) {
            exception = e
        }
        assertThat(exception).isInstanceOf(SQLiteConstraintException::class.java)
    }

    // --- Helper Functions ---

    private fun createTask(id: Long, title: String = "Task $id") =
        TaskEntity(taskId = id, title = title, createdAt = 0, updatedAt = 0)

    private fun createScheduleEvent(
        taskId: Long,
        startDate: Int,
        isNotificationEnabled: Boolean = false,
        createdAt: Long,
    ) = TaskScheduleEventEntity(
        taskId = taskId,
        startDateInEpochDays = startDate,
        isNotificationEnabled = isNotificationEnabled,
        createdAt = createdAt
    )

}