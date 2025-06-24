package at.robthered.plan_me.features.datasource.data.local.dao


import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskArchivedHistoryDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskArchivedHistoryEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
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

/**
 * Instrumentation tests for [TaskArchivedHistoryDao].
 *
 * This suite uses a real in-memory Room database to verify all CRUD operations,
 * queries, and especially the foreign key constraints related to the task archived history.
 */
@RunWith(AndroidJUnit4::class)
class TaskArchivedHistoryDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val historyDao: TaskArchivedHistoryDao by inject()
    private val taskDao: TaskDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * GIVEN a valid parent task exists.
     * WHEN a new history entry is inserted and retrieved by its ID.
     * THEN the retrieved entity should contain the correct data.
     */
    @Test
    fun insertAndGetById_whenEntryIsValid_retrievesCorrectHistory() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(1L))
        val historyEntry = createHistory(taskId = parentTaskId, isArchived = true, createdAt = 100L)

        // WHEN
        val newHistoryId = historyDao.insert(historyEntry)
        val retrieved = historyDao.get(newHistoryId).first()

        // THEN
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.isArchived).isTrue()
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
        val parentTaskId = taskDao.insert(createTask(1L))
        historyDao.insert(
            createHistory(
                taskId = parentTaskId,
                isArchived = false,
                createdAt = 200L
            )
        )
        historyDao.insert(createHistory(taskId = parentTaskId, isArchived = true, createdAt = 100L))

        val otherTaskId = taskDao.insert(createTask(2L))
        historyDao.insert(createHistory(taskId = otherTaskId, isArchived = true, createdAt = 300L))

        // WHEN
        val historyForTask = historyDao.getForTask(taskId = parentTaskId).first()

        // THEN
        assertThat(historyForTask).hasSize(2)
        // Verifies ORDER BY createdAt ASC
        assertThat(historyForTask.map { it.isArchived }).containsExactly(true, false).inOrder()
    }

    /**
     * GIVEN a parent task and its history entry exist.
     * WHEN the parent task is deleted.
     * THEN the history entry should also be deleted due to the CASCADE constraint.
     */
    @Test
    fun delete_whenParentIsDeleted_cascadesAndDeletesHistory() = runTest {
        // GIVEN
        val parentTaskId = taskDao.insert(createTask(1L))
        historyDao.insert(createHistory(taskId = parentTaskId, isArchived = true, createdAt = 100L))
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
     * THEN the database should throw an SQLiteConstraintException, proving the foreign key is enforced.
     */
    @Test
    fun insert_withNonExistentParentTask_throwsConstraintException() = runTest {
        // GIVEN
        val nonExistentTaskId = 999L
        val invalidHistoryEntry =
            createHistory(taskId = nonExistentTaskId, isArchived = true, createdAt = 100L)
        var exception: Throwable? = null

        // WHEN
        try {
            historyDao.insert(invalidHistoryEntry)
        } catch (e: SQLiteConstraintException) {
            exception = e
        }

        // THEN
        assertThat(exception).isInstanceOf(SQLiteConstraintException::class.java)
    }

    // --- Helper Functions ---
    private fun createTask(id: Long) = TaskEntity(
        taskId = id,
        title = "Task $id",
        createdAt = 0,
        updatedAt = 0,
        isCompleted = false,
        isArchived = false
    )

    private fun createHistory(taskId: Long, isArchived: Boolean, createdAt: Long) =
        TaskArchivedHistoryEntity(taskId = taskId, isArchived = isArchived, createdAt = createdAt)
}