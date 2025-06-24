package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskHashtagsCrossRefRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject


/**
 * Instrumentation tests for [LocalTaskHashtagsCrossRefRepositoryImpl].
 *
 * This suite verifies all CRUD operations and, crucially, the behavior of the
 * two foreign key constraints with onDelete = CASCADE.
 */
@RunWith(AndroidJUnit4::class)
class LocalTaskHashtagsCrossRefRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val hashtagDao: HashtagDao by inject()
    private val repository: LocalTaskHashtagsCrossRefRepository by inject()

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }

    /**
     * GIVEN a parent task and a parent hashtag exist.
     * WHEN a cross-reference is inserted and then fetched.
     * THEN the fetched data should be correct.
     */
    @Test
    fun insertAndGet_shouldReturnCorrectData() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(1L, "My Hashtag"))
        val crossRefToInsert = TaskHashtagsCrossRefModel(taskId, hashtagId, Clock.System.now())

        // WHEN
        repository.insert(crossRefToInsert)
        val fetchedCrossRefs = repository.getCrossRefForTask(taskId).first()
        val fetchedTaskWithHashtags = repository.getForTaskId(taskId).first()

        // THEN
        assertThat(fetchedCrossRefs).hasSize(1)
        assertThat(fetchedCrossRefs.first().hashtagId).isEqualTo(hashtagId)

        assertThat(fetchedTaskWithHashtags).isNotNull()
        assertThat(fetchedTaskWithHashtags?.hashtags).hasSize(1)
        assertThat(fetchedTaskWithHashtags?.hashtags?.first()?.name).isEqualTo("My Hashtag")
    }

    /**
     * GIVEN a cross-reference exists.
     * WHEN deleteCrossRef is called.
     * THEN the cross-reference should be removed.
     */
    @Test
    fun deleteCrossRef_shouldRemoveTheLink() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(1L, "My Hashtag"))
        repository.insert(TaskHashtagsCrossRefModel(taskId, hashtagId, Clock.System.now()))
        assertThat(repository.getCrossRefForTask(taskId).first()).isNotEmpty()

        // WHEN
        val rowsAffected = repository.deleteCrossRef(taskId, hashtagId)
        val crossRefsAfterDelete = repository.getCrossRefForTask(taskId).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(crossRefsAfterDelete).isEmpty()
    }

    /**
     * GIVEN a parent task is deleted.
     * WHEN the onDelete=CASCADE rule is active.
     * THEN the associated cross-reference entry should also be deleted.
     */
    @Test
    fun deleteParentTask_shouldCascadeDeleteTheCrossRef() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(1L, "My Hashtag"))
        repository.insert(TaskHashtagsCrossRefModel(taskId, hashtagId, Clock.System.now()))
        assertThat(repository.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        taskDao.delete(taskId)

        // THEN
        val crossRefsAfterDelete = repository.getCrossRefForTask(taskId).first()
        assertThat(crossRefsAfterDelete).isEmpty()
    }

    /**
     * GIVEN a parent hashtag is deleted.
     * WHEN the onDelete=CASCADE rule is active.
     * THEN the associated cross-reference entry should also be deleted.
     */
    @Test
    fun deleteParentHashtag_shouldCascadeDeleteTheCrossRef() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(1L, "My Hashtag"))
        repository.insert(TaskHashtagsCrossRefModel(taskId, hashtagId, Clock.System.now()))
        assertThat(repository.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        hashtagDao.delete(hashtagId)

        // THEN
        val crossRefsAfterDelete = repository.getCrossRefForTask(taskId).first()
        assertThat(crossRefsAfterDelete).isEmpty()
    }

}