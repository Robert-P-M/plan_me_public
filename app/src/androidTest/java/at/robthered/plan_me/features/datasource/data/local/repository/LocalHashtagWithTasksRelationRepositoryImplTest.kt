package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskHashtagsCrossRefDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagWithTasksRelationRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
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
 * Instrumentation tests for [LocalHashtagWithTasksRelationRepositoryImpl].
 *
 * This suite's primary goal is to verify the correctness of the complex Room @Relation
 * that joins hashtags, tasks, and their cross-reference table.
 */
@RunWith(AndroidJUnit4::class)
class LocalHashtagWithTasksRelationRepositoryImplTest : KoinTest {

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

    private val repository: LocalHashtagWithTasksRelationRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }

    /**
     * GIVEN a hashtag is linked to multiple tasks via the cross-ref table.
     * WHEN getHashtagWithTasks is called for that hashtag.
     * THEN it should return a model containing the correct hashtag and a list of all associated tasks.
     */
    @Test
    fun getHashtagWithTasks_whenRelationsExist_returnsCorrectlyPopulatedModel() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(0L, "testing"))
        val taskId1 = taskDao.insert(createTaskEntity(0L, "First Task"))
        val taskId2 = taskDao.insert(createTaskEntity(0L, "Second Task"))
        // unrelated to test the query
        taskDao.insert(createTaskEntity(0L, "Unrelated Task"))

        taskHashtagsCrossRefDao.insert(
            TaskHashtagsCrossRefEntity(
                taskId1,
                hashtagId,
                testTime.toEpochMilliseconds()
            )
        )
        taskHashtagsCrossRefDao.insert(
            TaskHashtagsCrossRefEntity(
                taskId2,
                hashtagId,
                testTime.toEpochMilliseconds()
            )
        )

        // WHEN
        val result = repository.getHashtagWithTasks(hashtagId).first()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.hashtag?.hashtagId).isEqualTo(hashtagId)
        assertThat(result?.hashtag?.name).isEqualTo("testing")

        assertThat(result?.tasks).hasSize(2)
        val taskTitles = result?.tasks?.map { it.title }
        assertThat(taskTitles).containsExactly("First Task", "Second Task")
    }

    /**
     * GIVEN a hashtag exists but has no associated tasks.
     * WHEN getHashtagWithTasks is called.
     * THEN it should return the hashtag model with an empty list of tasks.
     */
    @Test
    fun getHashtagWithTasks_whenHashtagHasNoTasks_returnsModelWithEmptyTaskList() = runTest {
        // GIVEN
        val hashtagId = hashtagDao.insert(createHashtag(0L, "lonely_hashtag"))

        // WHEN
        val result = repository.getHashtagWithTasks(hashtagId).first()

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.hashtag?.hashtagId).isEqualTo(hashtagId)
        assertThat(result?.tasks).isEmpty()
    }

    /**
     * GIVEN a hashtag ID that does not exist in the database.
     * WHEN getHashtagWithTasks is called.
     * THEN the flow should emit null.
     */
    @Test
    fun getHashtagWithTasks_whenHashtagNotFound_returnsNull() = runTest {
        // GIVEN
        val nonExistentId = 404L

        // WHEN
        val result = repository.getHashtagWithTasks(nonExistentId).first()

        // THEN
        assertThat(result).isNull()
    }


}