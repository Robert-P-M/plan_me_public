package at.robthered.plan_me.features.datasource.data.local.dao


import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskPriorityHistoryDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskPriorityHistoryEntity
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
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
 * Instrumentation tests for [TaskPriorityHistoryDao].
 *
 * This suite uses a real in-memory Room database to verify all CRUD operations,
 * queries, and foreign key constraints for the TaskPriorityHistory entity.
 */
@RunWith(AndroidJUnit4::class)
class TaskPriorityHistoryDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val historyDao: TaskPriorityHistoryDao by inject()
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
     * WHEN a new history entry is inserted and retrieved by its ID.
     * THEN the retrieved entity should contain the correct data.
     */
    @Test
    fun insertAndGetById_whenEntryIsValid_retrievesCorrectEntry() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1L))
        val historyEntry =
            createHistory(taskId = parentTaskId, priority = PriorityEnum.HIGH, createdAt = 100L)

        // WHEN
        val newHistoryId = historyDao.insert(historyEntry)
        val retrieved = historyDao.get(newHistoryId).first()

        // THEN
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.priorityEnum).isEqualTo(PriorityEnum.HIGH)
        assertThat(retrieved?.taskId).isEqualTo(parentTaskId)
    }

    /**
     * GIVEN multiple history entries for different tasks exist.
     * WHEN getForTask() is called for a specific task.
     * THEN it should return only the entries for that task, sorted by creation date.
     */
    @Test
    fun getForTask_whenEntriesExist_returnsFilteredAndSortedList() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1L))
        // Insert out of order to test sorting
        historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                priority = PriorityEnum.MEDIUM,
                createdAt = 200L
            )
        )
        historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                priority = PriorityEnum.HIGH,
                createdAt = 100L
            )
        )
        historyDao.insert(createHistory(taskId = parentTaskId, priority = null, createdAt = 300L))

        // Insert unrelated data that should be ignored
        val otherTaskId = taskDao.insert(createTask(id = 2L))
        historyDao.insert(
            createHistory(
                taskId = otherTaskId,
                priority = PriorityEnum.LOW,
                createdAt = 400L
            )
        )

        // WHEN
        val historyForTask = historyDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(historyForTask).hasSize(3)
        // Verifies ORDER BY createdAt ASC and correct mapping of nulls
        val priorities = historyForTask.map { it.priorityEnum }
        assertThat(priorities).containsExactly(PriorityEnum.HIGH, PriorityEnum.MEDIUM, null)
            .inOrder()
    }

    /**
     * GIVEN a history entry is deleted by its specific ID.
     * WHEN the remaining entries are fetched.
     * THEN the deleted entry should be gone, and others should remain.
     */
    @Test
    fun deleteById_shouldRemoveOnlyCorrectEntry() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1L))
        val idToDelete = historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                priority = PriorityEnum.LOW,
                createdAt = 100L
            )
        )
        val idToKeep = historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                priority = PriorityEnum.HIGH,
                createdAt = 200L
            )
        )

        // WHEN
        val deletedRows = historyDao.delete(taskPriorityHistoryId = idToDelete)
        val remainingHistory = historyDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(deletedRows).isEqualTo(1)
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().taskPriorityId).isEqualTo(idToKeep)
    }

    /**
     * GIVEN a parent task and its history entry exist.
     * WHEN the parent task is deleted.
     * THEN the history entry should also be deleted due to the CASCADE constraint.
     */
    @Test
    fun deleteParentTask_whenCascadeIsActive_deletesChildHistory() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(id = 1L))
        historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                priority = PriorityEnum.HIGH,
                createdAt = 100L
            )
        )
        assertThat(historyDao.getForTask(parentTaskId).first()).isNotEmpty()

        // WHEN
        taskDao.delete(taskId = parentTaskId)
        val history = historyDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(history).isEmpty()
    }

    /**
     * GIVEN an attempt to insert a history entry for a non-existent task.
     * WHEN the insert operation is performed.
     * THEN the database should throw an SQLiteConstraintException.
     */
    @Test
    fun insert_withNonExistentParent_throwsConstraintException() = runTest {
        // GIVEN
        val invalidHistoryEntry =
            createHistory(taskId = 999L, priority = PriorityEnum.MEDIUM, createdAt = 100L)
        var capturedException: Throwable? = null

        // WHEN
        try {
            historyDao.insert(invalidHistoryEntry)
        } catch (e: SQLiteConstraintException) {
            capturedException = e
        }

        // THEN
        assertThat(capturedException).isInstanceOf(SQLiteConstraintException::class.java)
    }

    // --- Helper Functions ---
    private fun createTask(id: Long, title: String = "Task $id") =
        TaskEntity(
            taskId = id,
            title = title,
            createdAt = 0,
            updatedAt = 0,
            isCompleted = false,
            isArchived = false
        )

    private fun createHistory(taskId: Long, priority: PriorityEnum?, createdAt: Long) =
        TaskPriorityHistoryEntity(taskId = taskId, priorityEnum = priority, createdAt = createdAt)
}