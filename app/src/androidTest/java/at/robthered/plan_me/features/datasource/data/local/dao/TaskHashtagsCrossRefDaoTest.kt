package at.robthered.plan_me.features.datasource.data.local.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskHashtagsCrossRefDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
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
 * Instrumentation tests for [TaskHashtagsCrossRefDao].
 *
 * This suite uses a real in-memory Room database to verify all operations on the
 * many-to-many relationship table between Tasks and Hashtags, including foreign key
 * and cascade constraints.
 */
@RunWith(AndroidJUnit4::class)
class TaskHashtagsCrossRefDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val crossRefDao: TaskHashtagsCrossRefDao by inject()
    private val taskDao: TaskDao by inject()
    private val hashtagDao: HashtagDao by inject()


    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * GIVEN parent task and hashtag exist.
     * WHEN a list of cross-references is inserted.
     * THEN getCrossRefForTask should return all inserted references for that task.
     */
    @Test
    fun insertListAndGetForTask_shouldRetrieveAllRefsForTask() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTask(1L, "Task A"))
        val h1 = hashtagDao.insert(createHashtag(10L, "#a"))
        val h2 = hashtagDao.insert(createHashtag(20L, "#b"))
        val otherTaskId = taskDao.insert(createTask(2L, "Task B"))

        val refsToInsert = listOf(
            createCrossRef(taskId = taskId, hashtagId = h1),
            createCrossRef(taskId = taskId, hashtagId = h2)
        )
        // Insert a ref for another task that should be ignored
        crossRefDao.insert(createCrossRef(taskId = otherTaskId, hashtagId = h1))

        // WHEN
        crossRefDao.insert(refsToInsert)
        val retrievedRefs = crossRefDao.getCrossRefForTask(taskId = taskId).first()

        // THEN
        assertThat(retrievedRefs).hasSize(2)
        val retrievedHashtagIds = retrievedRefs.map { it.hashtagId }
        assertThat(retrievedHashtagIds).containsExactly(h1, h2)
    }

    /**
     * GIVEN two cross-references exist for a task.
     * WHEN deleteCrossRef is called for one of them.
     * THEN only the specified cross-reference should be removed.
     */
    @Test
    fun deleteCrossRef_shouldRemoveOnlySpecificLink() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTask(1L, "Task A"))
        val h1 = hashtagDao.insert(createHashtag(10L, "#a"))
        val h2 = hashtagDao.insert(createHashtag(20L, "#b"))
        crossRefDao.insert(createCrossRef(taskId = taskId, hashtagId = h1))
        crossRefDao.insert(createCrossRef(taskId = taskId, hashtagId = h2))

        // WHEN
        val deletedRows = crossRefDao.deleteCrossRef(taskId = taskId, hashtagId = h1)
        val remainingRefs = crossRefDao.getCrossRefForTask(taskId = taskId).first()

        // THEN
        assertThat(deletedRows).isEqualTo(1)
        assertThat(remainingRefs).hasSize(1)
        assertThat(remainingRefs.first().hashtagId).isEqualTo(h2)
    }

    /**
     * GIVEN a parent task is deleted.
     * WHEN the onDelete=CASCADE rule is active.
     * THEN the associated cross-reference entry should also be deleted.
     */
    @Test
    fun deleteParentTask_whenCascadeIsActive_deletesCrossRef() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTask(1L, "Task To Delete"))
        val hashtagId = hashtagDao.insert(createHashtag(10L, "#test"))
        crossRefDao.insert(createCrossRef(taskId, hashtagId))
        assertThat(crossRefDao.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        taskDao.delete(taskId)

        // THEN
        assertThat(crossRefDao.getCrossRefForTask(taskId).first()).isEmpty()
    }

    /**
     * GIVEN a parent hashtag is deleted.
     * WHEN the onDelete=CASCADE rule is active.
     * THEN the associated cross-reference entry should also be deleted.
     */
    @Test
    fun deleteParentHashtag_whenCascadeIsActive_deletesCrossRef() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTask(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(10L, "#to_delete"))
        crossRefDao.insert(createCrossRef(taskId, hashtagId))
        assertThat(crossRefDao.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        hashtagDao.delete(hashtagId)

        // THEN
        assertThat(crossRefDao.getCrossRefForTask(taskId).first()).isEmpty()
    }

    /**
     * GIVEN an attempt to insert a cross-ref for a non-existent task.
     * WHEN the insert operation is performed.
     * THEN the database should throw an SQLiteConstraintException.
     */
    @Test
    fun insert_withNonExistentParent_throwsConstraintException() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(10L, "#test"))
        val invalidCrossRef =
            createCrossRef(taskId = 999L, hashtagId = hashtagId) // Task 999 does not exist
        var exception: Throwable? = null

        // WHEN
        try {
            crossRefDao.insert(invalidCrossRef)
        } catch (e: SQLiteConstraintException) {
            exception = e
        }

        // THEN
        assertThat(exception).isInstanceOf(SQLiteConstraintException::class.java)
    }

    // --- Helper Functions ---
    private fun createTask(taskId: Long, title: String) =
        TaskEntity(
            taskId = taskId,
            title = title,
            createdAt = 0,
            updatedAt = 0,
            isCompleted = false,
            isArchived = false
        )

    private fun createHashtag(id: Long, name: String) =
        HashtagEntity(hashtagId = id, name = name, createdAt = 0, updatedAt = 0)

    private fun createCrossRef(taskId: Long, hashtagId: Long) = TaskHashtagsCrossRefEntity(
        taskId = taskId,
        hashtagId = hashtagId,
        createdAt = System.currentTimeMillis()
    )
}