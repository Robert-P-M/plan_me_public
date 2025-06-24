package at.robthered.plan_me.features.datasource.data.local.dao

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Instrumentation tests for [HashtagDao].
 *
 * This suite uses a real in-memory Room database to verify the correctness of all
 * SQL queries, insertions, deletions, and relational mappings defined in the DAO.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HashtagDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val hashtagDao: HashtagDao by inject()
    private val taskDao: TaskDao by inject()
    private val crossRefDao: TaskHashtagsCrossRefDao by inject()


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
     * GIVEN a new hashtag entity.
     * WHEN it is inserted and then retrieved by its new ID.
     * THEN the retrieved entity should match the inserted one.
     */
    @Test
    fun insertAndGetById_shouldInsertAndRetrieveHashtag() = runTest {
        // GIVEN
        val hashtag = HashtagEntity(name = "#test", createdAt = 1L, updatedAt = 1L)

        // WHEN
        val newId = hashtagDao.insert(hashtag)
        val retrieved = hashtagDao.get(newId).first()

        // THEN
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.name).isEqualTo("#test")
    }

    /**
     * GIVEN multiple hashtags inserted with different timestamps.
     * WHEN getAll() is called.
     * THEN it should return all hashtags, ordered by their creation time ascending.
     */
    @Test
    fun getAll_shouldReturnAllHashtagsOrderedByCreationAsc() = runTest {
        // GIVEN
        hashtagDao.insert(HashtagEntity(name = "#b", createdAt = 200L, updatedAt = 200L))
        hashtagDao.insert(HashtagEntity(name = "#a", createdAt = 100L, updatedAt = 100L))

        // WHEN
        val allHashtags = hashtagDao.getAll().first()

        // THEN
        assertThat(allHashtags).hasSize(2)
        assertThat(allHashtags.map { it.name }).containsExactly("#a", "#b").inOrder()
    }

    /**
     * GIVEN multiple hashtags exist.
     * WHEN getByName() is called with a search query.
     * THEN it should return all hashtags matching the query.
     */
    @Test
    fun getByName_withFTS_shouldReturnMatchingHashtags() = runTest {
        // GIVEN
        hashtagDao.insert(HashtagEntity(name = "#android", createdAt = 200L, updatedAt = 200L))
        hashtagDao.insert(HashtagEntity(name = "#apple", createdAt = 200L, updatedAt = 200L))
        hashtagDao.insert(HashtagEntity(name = "#androiddev", createdAt = 200L, updatedAt = 200L))

        // WHEN
        val results = hashtagDao.getByName("andr").first()

        // THEN
        assertThat(results).hasSize(2)
        assertThat(results.map { it.name }).containsExactly("#android", "#androiddev")
    }

    /**
     * GIVEN a task is linked to several hashtags.
     * WHEN getHashtagsForTask() is called with the task's ID.
     * THEN it should return only the correctly joined hashtags for that task.
     */
    @Test
    fun getHashtagsForTask_shouldReturnCorrectlyJoinedHashtags() = runTest {
        // GIVEN
        taskDao.insert(TaskEntity(taskId = 1, title = "Task 1", createdAt = 200L, updatedAt = 200L))
        taskDao.insert(TaskEntity(taskId = 2, title = "Task 2", createdAt = 300L, updatedAt = 300L))
        val h1 = hashtagDao.insert(HashtagEntity(name = "#a", createdAt = 200L, updatedAt = 200L))
        val h2 = hashtagDao.insert(HashtagEntity(name = "#b", createdAt = 300L, updatedAt = 300L))
        val h3 = hashtagDao.insert(HashtagEntity(name = "#c", createdAt = 400L, updatedAt = 400L))

        crossRefDao.insert(TaskHashtagsCrossRefEntity(taskId = 1, hashtagId = h1, createdAt = 200L))
        crossRefDao.insert(TaskHashtagsCrossRefEntity(taskId = 1, hashtagId = h2, createdAt = 200L))
        crossRefDao.insert(TaskHashtagsCrossRefEntity(taskId = 2, hashtagId = h3, createdAt = 200L))

        // WHEN
        val hashtagsForTask1 = hashtagDao.getHashtagsForTask(taskId = 1).first()

        // THEN
        assertThat(hashtagsForTask1).hasSize(2)
        assertThat(hashtagsForTask1.map { it.name }).containsExactly("#a", "#b")
    }

    /**
     * GIVEN a hashtag that is a parent in a cross-reference relationship.
     * WHEN the hashtag is deleted.
     * THEN the associated cross-reference entries should also be deleted due to CASCADE constraint.
     */
    @Test
    fun delete_whenHashtagIsParent_cascadesDeleteToCrossRef() = runTest {
        // GIVEN
        val taskId =
            taskDao.insert(TaskEntity(taskId = 1, title = "Task 1", createdAt = 1L, updatedAt = 1L))
        val hashtagIdToDelete =
            hashtagDao.insert(HashtagEntity(name = "#to_delete", createdAt = 1L, updatedAt = 1L))
        crossRefDao.insert(TaskHashtagsCrossRefEntity(taskId, hashtagIdToDelete, 1L))

        // Verify that the link exists before deletion
        assertThat(crossRefDao.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        hashtagDao.delete(hashtagIdToDelete)

        // THEN
        // Verify that the link has been automatically removed
        val crossRefsAfterDelete = crossRefDao.getCrossRefForTask(taskId).first()
        assertThat(crossRefsAfterDelete).isEmpty()
    }


}