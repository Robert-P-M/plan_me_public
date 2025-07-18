package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskDescriptionHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject


/**
 * Instrumentation tests for [LocalTaskDescriptionHistoryRepositoryImpl].
 *
 * This suite verifies all CRUD operations and the foreign key cascade behavior using a
 * real in-memory Room database.
 */
@RunWith(AndroidJUnit4::class)
class LocalTaskDescriptionHistoryRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")
    private val parentTask = createTaskEntity(1L, "Parent Task")

    /**
     * Creates a fresh in-memory database and inserts a parent task before each test
     * to satisfy the foreign key constraint of the TaskDescriptionHistoryEntity.
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
     * GIVEN a new description history model for an existing task.
     * WHEN it is inserted via the repository and then fetched with get().
     * THEN the returned model should match the inserted data.
     */
    @Test
    fun getById_afterInsert_returnsCorrectlyMappedModel() = runTest {
        // GIVEN
        val historyToInsert =
            TaskDescriptionHistoryModel(taskId = 1L, text = "New Description", createdAt = testTime)

        // WHEN
        val newHistoryId = localTaskDescriptionHistoryRepository.insert(historyToInsert)
        val fetchedHistory = localTaskDescriptionHistoryRepository.get(newHistoryId).first()

        // THEN
        assertThat(fetchedHistory).isNotNull()
        assertThat(fetchedHistory?.taskDescriptionHistoryId).isEqualTo(newHistoryId)
        assertThat(fetchedHistory?.text).isEqualTo("New Description")
        assertThat(fetchedHistory?.taskId).isEqualTo(1L)
    }

    /**
     * GIVEN multiple history entries for one task exist.
     * WHEN getForTask() is called with the parent task's ID.
     * THEN a list containing all associated history entries should be returned.
     */
    @Test
    fun getForTask_whenMultipleEntriesExist_returnsCompleteList() = runTest {
        // GIVEN
        localTaskDescriptionHistoryRepository.insert(
            TaskDescriptionHistoryModel(
                taskId = 1L,
                text = "Desc 1",
                createdAt = testTime
            )
        )
        localTaskDescriptionHistoryRepository.insert(
            TaskDescriptionHistoryModel(
                taskId = 1L,
                text = "Desc 2",
                createdAt = testTime
            )
        )

        // WHEN
        val fetchedHistory = localTaskDescriptionHistoryRepository.getForTask(1L).first()

        // THEN
        assertThat(fetchedHistory).hasSize(2)
    }

    /**
     * GIVEN multiple history entries exist.
     * WHEN delete() is called with a specific history ID.
     * THEN only that specific entry should be removed from the database.
     */
    @Test
    fun deleteById_removesOnlySpecifiedEntry() = runTest {
        // GIVEN
        val historyIdToDelete = localTaskDescriptionHistoryRepository.insert(
            TaskDescriptionHistoryModel(
                taskId = 1L,
                text = "History to delete",
                createdAt = testTime
            )
        )
        val historyIdToKeep = localTaskDescriptionHistoryRepository.insert(
            TaskDescriptionHistoryModel(
                taskId = 1L,
                text = "History to keep",
                createdAt = testTime
            )
        )

        // WHEN
        val rowsAffected = localTaskDescriptionHistoryRepository.delete(historyIdToDelete)
        val remainingHistory = localTaskDescriptionHistoryRepository.getForTask(1L).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().taskDescriptionHistoryId).isEqualTo(historyIdToKeep)
    }

    /**
     * GIVEN a parent task and its history entries exist.
     * WHEN the PARENT task is deleted.
     * THEN all associated history entries should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteParentTask_whenCascadeIsActive_deletesChildHistory() = runTest {
        // GIVEN
        localTaskDescriptionHistoryRepository.insert(
            TaskDescriptionHistoryModel(
                taskId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        assertThat(localTaskDescriptionHistoryRepository.getForTask(1L).first()).hasSize(1)

        // WHEN
        taskDao.delete(taskId = 1L)

        // THEN
        val historyAfterDelete = localTaskDescriptionHistoryRepository.getForTask(1L).first()
        assertThat(historyAfterDelete).isEmpty()
    }

}