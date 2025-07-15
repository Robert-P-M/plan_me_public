package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskTitleHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Instrumentation tests for [LocalTaskTitleHistoryRepositoryImpl].
 *
 * This test suite uses a real in-memory Room database to verify the correctness
 * of the repository's interaction with its DAO, including data mapping,
 * queries, and foreign key constraints.
 */
@RunWith(AndroidJUnit4::class)
class LocalTaskTitleHistoryRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )

    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")
    private val parentTask = TaskEntity(
        taskId = 1L,
        title = "Parent Task",
        createdAt = testTime.toEpochMilliseconds(),
        updatedAt = testTime.toEpochMilliseconds(),
        isCompleted = false,
        isArchived = false
    )


    /**
     * Creates a fresh in-memory database and the repository instance before each test.
     */
    @Before
    fun setUp() {
        runBlocking {
            taskDao.insert(taskEntity = parentTask)
        }
    }

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }


    /**
     * GIVEN a history entry is inserted into the database.
     * WHEN get() is called with the specific ID of that entry.
     * THEN the correct, fully mapped [TaskTitleHistoryModel] should be returned.
     */
    @Test
    fun getById_whenHistoryExists_returnsCorrectModel() = runTest {
        // GIVEN
        val historyToInsert =
            TaskTitleHistoryModel(taskId = 1L, text = "Specific Title", createdAt = testTime)
        val newHistoryId = localTaskTitleHistoryRepository.insert(historyToInsert)

        // WHEN
        val fetchedHistory = localTaskTitleHistoryRepository.get(newHistoryId).first()

        // THEN
        assertThat(fetchedHistory).isNotNull()
        assertThat(fetchedHistory?.taskTitleHistoryId).isEqualTo(newHistoryId)
        assertThat(fetchedHistory?.text).isEqualTo("Specific Title")
        assertThat(fetchedHistory?.taskId).isEqualTo(1L)
    }

    /**
     * GIVEN multiple history entries for the same task exist.
     * WHEN getForTask() is called with the parent task's ID.
     * THEN a list containing all associated history entries should be returned.
     */
    @Test
    fun getForTask_whenMultipleEntriesExist_returnsCompleteList() = runTest {
        // GIVEN
        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History 2",
                createdAt = testTime
            )
        )

        // WHEN
        val fetchedHistory = localTaskTitleHistoryRepository.getForTask(1L).first()

        // THEN
        assertThat(fetchedHistory).hasSize(2)
    }

    /**
     * GIVEN multiple history entries exist.
     * WHEN delete() is called with a specific history ID.
     * THEN only that specific entry should be removed from the database.
     */
    @Test
    fun deleteById_whenEntryExists_removesOnlyThatEntry() = runTest {
        // GIVEN
        val historyIdToDelete = localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History to delete",
                createdAt = testTime
            )
        )
        val historyIdToKeep = localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History to keep",
                createdAt = testTime
            )
        )

        // WHEN
        val rowsAffected = localTaskTitleHistoryRepository.delete(historyIdToDelete)
        val remainingHistory = localTaskTitleHistoryRepository.getForTask(1L).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().taskTitleHistoryId).isEqualTo(historyIdToKeep)
    }

    /**
     * GIVEN a parent task and its history entries exist.
     * WHEN the PARENT task is deleted.
     * THEN all associated history entries should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteParentTask_whenCascadeIsActive_deletesAllChildHistory() = runTest {
        // GIVEN
        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = 1L,
                text = "History 2",
                createdAt = testTime
            )
        )
        assertThat(localTaskTitleHistoryRepository.getForTask(1L).first()).hasSize(2)

        // WHEN
        // Delete the parent task directly via its DAO
        taskDao.delete(taskId = 1L)

        // THEN
        // Verify that the history for that task is now empty
        val historyAfterDelete = localTaskTitleHistoryRepository.getForTask(1L).first()
        assertThat(historyAfterDelete).isEmpty()
    }

    /**
     * GIVEN a parent task exists in the database.
     * WHEN a new history entry for that task is inserted and then fetched via getForTask.
     * THEN the returned list should contain the correctly mapped history model.
     */
    @Test
    fun insertAndGetForTask_shouldReturnCorrectHistoryForTask() = runTest {
        // GIVEN


        val historyToInsert =
            TaskTitleHistoryModel(
                taskId = parentTask.taskId,
                text = "New Title",
                createdAt = Clock.System.now()
            )

        // WHEN
        localTaskTitleHistoryRepository.insert(historyToInsert)
        val fetchedHistory = localTaskTitleHistoryRepository.getForTask(parentTask.taskId).first()

        // THEN
        assertThat(fetchedHistory).hasSize(1)
        assertThat(fetchedHistory.first().text).isEqualTo("New Title")
        assertThat(fetchedHistory.first().taskId).isEqualTo(parentTask.taskId)
    }

    /**
     * GIVEN a parent task and its history entries exist.
     * WHEN the PARENT task is deleted.
     * THEN all associated history entries should be deleted automatically due to CASCADE constraint.
     */
    @Test
    fun delete_onCascade_shouldDeleteHistoryWhenParentTaskIsDeleted() = runTest {
        // GIVEN

        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = parentTask.taskId,
                text = "History 1",
                createdAt = testTime
            )
        )
        localTaskTitleHistoryRepository.insert(
            TaskTitleHistoryModel(
                taskId = parentTask.taskId,
                text = "History 2",
                createdAt = testTime
            )
        )

        val historyBeforeDelete =
            localTaskTitleHistoryRepository.getForTask(parentTask.taskId).first()
        assertThat(historyBeforeDelete).hasSize(2)

        // WHEN
        taskDao.delete(taskId = parentTask.taskId)

        // THEN
        val historyAfterDelete =
            localTaskTitleHistoryRepository.getForTask(parentTask.taskId).first()
        assertThat(historyAfterDelete).isEmpty()
    }
}