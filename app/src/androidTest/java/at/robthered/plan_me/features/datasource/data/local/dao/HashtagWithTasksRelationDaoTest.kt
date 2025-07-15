package at.robthered.plan_me.features.datasource.data.local.dao


import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagWithTasksRelationDao
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


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HashtagWithTasksRelationDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val relationDao: HashtagWithTasksRelationDao by inject()
    private val taskDao: TaskDao by inject()
    private val hashtagDao: HashtagDao by inject()
    private val crossRefDao: TaskHashtagsCrossRefDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun closeDb() {
        db.clearAllTables()
    }

    private fun createTask(id: Long, title: String) =
        TaskEntity(taskId = id, title = title, createdAt = 0, updatedAt = 0)

    private fun createHashtag(id: Long, name: String) =
        HashtagEntity(hashtagId = id, name = name, createdAt = 0, updatedAt = 0)

    private fun createCrossRef(taskId: Long, hashtagId: Long) =
        TaskHashtagsCrossRefEntity(taskId = taskId, hashtagId = hashtagId, createdAt = 0)

    /**
     * GIVEN a hashtag is linked to multiple tasks, and other unrelated tasks exist.
     * WHEN getHashtagWithTasks is called for that specific hashtag.
     * THEN it should return a model containing the correct hashtag and only its associated tasks.
     */
    @Test
    fun getHashtagWithTasks_whenRelationsExist_returnsCorrectlyPopulatedModel() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(0L, "#work"))
        val taskId1 = taskDao.insert(createTask(0L, "make report"))
        val taskId2 = taskDao.insert(createTask(0L, "setup fo rmeeting"))
        taskDao.insert(createTask(0L, "Unrelated Task")) // This task should not be in the result

        crossRefDao.insert(createCrossRef(taskId = taskId1, hashtagId = hashtagId))
        crossRefDao.insert(createCrossRef(taskId = taskId2, hashtagId = hashtagId))

        // WHEN
        val result = relationDao.getHashtagWithTasks(hashtagId = hashtagId).first()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.hashtag?.name).isEqualTo("#work")
        assertThat(result?.tasks).hasSize(2)
        val taskTitles = result?.tasks?.map { it.title }
        assertThat(taskTitles).containsExactly("make report", "setup fo rmeeting")
    }

    /**
     * GIVEN a hashtag exists but has no associated tasks.
     * WHEN getHashtagWithTasks is called.
     * THEN it should return the hashtag model with an empty list of tasks.
     */
    @Test
    fun getHashtagWithTasks_whenHashtagHasNoTasks_returnsModelWithEmptyTaskList() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(0L, "#unused"))

        // WHEN
        val result = relationDao.getHashtagWithTasks(hashtagId).first()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.hashtag?.name).isEqualTo("#unused")
        assertThat(result?.tasks).isEmpty()
    }

    /**
     * GIVEN a hashtag ID that does not exist in the database.
     * WHEN getHashtagWithTasks is called.
     * THEN the flow should emit null.
     */
    @Test
    fun getHashtagWithTasks_whenHashtagIsNotFound_returnsNull() = runTest {
        // GIVEN: an empty database
        // WHEN
        val result = relationDao.getHashtagWithTasks(hashtagId = 999L).first()

        // THEN
        assertThat(result).isNull()
    }

    /**
     * GIVEN a hashtag is linked to multiple tasks.
     * WHEN a linked task is deleted (which also deletes the cross-ref due to cascade).
     * THEN the relation should update and return a list with one less task.
     */
    @Test
    fun getHashtagWithTasks_whenLinkedTaskIsDeleted_updatesRelationCorrectly() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(1, "#work"))
        val taskIdToDelete = taskDao.insert(createTask(10, "make report"))
        val taskIdToKeep = taskDao.insert(createTask(30, "setup fo rmeeting"))
        crossRefDao.insert(createCrossRef(taskId = taskIdToDelete, hashtagId = hashtagId))
        crossRefDao.insert(createCrossRef(taskId = taskIdToKeep, hashtagId = hashtagId))

        // WHEN
        taskDao.delete(taskId = taskIdToDelete) // Deleting the task will cascade-delete the cross-ref
        val result = relationDao.getHashtagWithTasks(hashtagId = 1L).first()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.tasks).hasSize(1)
        assertThat(result?.tasks?.first()?.title).isEqualTo("setup fo rmeeting")
    }
}