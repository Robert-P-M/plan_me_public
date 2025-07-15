package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskHashtagsCrossRefDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject


/**
 * Instrumentation tests for [LocalHashtagRepositoryImpl].
 *
 * Verifies all CRUD operations and the foreign key cascade behavior on its child entities.
 */
@RunWith(AndroidJUnit4::class)
class LocalHashtagRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )

    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val hashtagDao: HashtagDao by inject()
    private val taskDao: TaskDao by inject()
    private val taskHashtagsCrossRefDao: TaskHashtagsCrossRefDao by inject()

    private val localHashtagRepository: LocalHashtagRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }

    /**
     * GIVEN a new hashtag model.
     * WHEN it is inserted and then fetched by its ID.
     * THEN the fetched model should match the inserted data.
     */
    @Test
    fun insertAndGetById_shouldReturnCorrectData() = runTest {
        // GIVEN
        val hashtagToInsert =
            HashtagModel(name = "test", createdAt = testTime, updatedAt = testTime)

        // WHEN
        val newId = localHashtagRepository.insert(hashtagToInsert)
        val fetchedHashtag = localHashtagRepository.get(newId).first()

        // THEN
        assertThat(fetchedHashtag).isNotNull()
        assertThat(fetchedHashtag?.hashtagId).isEqualTo(newId)
        assertThat(fetchedHashtag?.name).isEqualTo("test")
    }

    /**
     * GIVEN multiple hashtags are inserted.
     * WHEN getAll() is called.
     * THEN it should return a list containing all inserted hashtags.
     */
    @Test
    fun getAll_shouldReturnAllInsertedHashtags() = runTest {
        // GIVEN
        localHashtagRepository.insert(
            HashtagModel(
                name = "kotlin",
                createdAt = testTime,
                updatedAt = testTime
            )
        )
        localHashtagRepository.insert(
            HashtagModel(
                name = "android",
                createdAt = testTime,
                updatedAt = testTime
            )
        )

        // WHEN
        val allHashtags = localHashtagRepository.getAll().first()

        // THEN
        assertThat(allHashtags).hasSize(2)
    }

    /**
     * GIVEN multiple hashtags related to a specific task.
     * WHEN getHashtagsForTask() is called with the task's ID.
     * THEN it should return only the hashtags associated with that task.
     */
    @Test
    fun getHashtagsForTask_shouldReturnCorrectlyJoinedHashtags() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId1 = hashtagDao.insert(createHashtag(0L, "relevant"))
        val hashtagId2 = hashtagDao.insert(createHashtag(0L, "irrelevant"))

        taskHashtagsCrossRefDao.insert(TaskHashtagsCrossRefEntity(taskId, hashtagId1, 0L))

        // WHEN
        val hashtagsForTask = localHashtagRepository.getHashtagsForTask(taskId).first()

        // THEN
        assertThat(hashtagsForTask).hasSize(1)
        assertThat(hashtagsForTask.first().name).isEqualTo("relevant")
    }

    /**
     * GIVEN a parent hashtag is deleted.
     * WHEN the onDelete=CASCADE rule is active.
     * THEN all associated cross-reference entries should also be deleted.
     */
    @Test
    fun delete_whenCascadeIsActive_deletesChildCrossRefs() = runTest {
        // GIVEN
        val taskId = taskDao.insert(createTaskEntity(1L, "My Task"))
        val hashtagId = hashtagDao.insert(createHashtag(0L, "To be deleted"))
        taskHashtagsCrossRefDao.insert(TaskHashtagsCrossRefEntity(taskId, hashtagId, 0L))

        assertThat(taskHashtagsCrossRefDao.getCrossRefForTask(taskId).first()).hasSize(1)

        // WHEN
        val rowsAffected = localHashtagRepository.delete(hashtagId)

        // THEN
        assertThat(rowsAffected).isEqualTo(1)

        val crossRefsAfterDelete = taskHashtagsCrossRefDao.getCrossRefForTask(taskId).first()
        assertThat(crossRefsAfterDelete).isEmpty()
    }

}