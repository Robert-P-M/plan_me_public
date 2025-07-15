package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.dao.TaskScheduleEventDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskScheduleEventRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
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
 * Instrumentation tests for [LocalTaskScheduleEventRepositoryImpl].
 *
 * This suite uses a real in-memory Room database to verify that the repository correctly
 * interacts with its DAO, respecting foreign key constraints and correctly mapping data
 * between entities and domain models.
 */
@RunWith(AndroidJUnit4::class)
class LocalTaskScheduleEventRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.

    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val taskScheduleEventDao: TaskScheduleEventDao by inject()
    private val localTaskScheduleEventRepository: LocalTaskScheduleEventRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")
    private val parentTask = TaskEntity(
        taskId = 1L,
        title = "Parent Task For Schedule Events",
        createdAt = testTime.toEpochMilliseconds(),
        updatedAt = testTime.toEpochMilliseconds(),
        isCompleted = false,
        isArchived = false
    )

    /**
     * Creates a fresh in-memory database and inserts a parent task before each test
     * to satisfy the foreign key constraint of the TaskScheduleEventEntity.
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
     * GIVEN a new schedule event model for an existing task.
     * WHEN it is inserted via the repository and then fetched with getForTask.
     * THEN the returned list should contain the correctly mapped model.
     */
    @Test
    fun insertAndGetForTask_shouldReturnCorrectData() = runTest {
        // GIVEN
        val scheduleEventToInsert = TaskScheduleEventModel(
            taskId = parentTask.taskId,
            startDateInEpochDays = 12345,
            isNotificationEnabled = true,
            createdAt = testTime
        )

        // WHEN
        val newId = localTaskScheduleEventRepository.insert(scheduleEventToInsert)
        val fetchedEvents = localTaskScheduleEventRepository.getForTask(parentTask.taskId).first()

        // THEN
        assertThat(fetchedEvents).hasSize(1)
        val fetchedEvent = fetchedEvents.first()

        assertThat(fetchedEvent.taskScheduleEventId).isEqualTo(newId)
        assertThat(fetchedEvent.taskId).isEqualTo(parentTask.taskId)
        assertThat(fetchedEvent.isNotificationEnabled).isTrue()
    }

    /**
     * GIVEN a schedule event exists in the database.
     * WHEN the delete method is called with the event's ID.
     * THEN the entry should be removed from the database.
     */
    @Test
    fun delete_shouldRemoveTheCorrectEntry() = runTest {
        // GIVEN
        val scheduleEventToInsert = TaskScheduleEventModel(
            taskId = parentTask.taskId,
            startDateInEpochDays = 5,
            createdAt = testTime
        )
        val newId = localTaskScheduleEventRepository.insert(scheduleEventToInsert)
        // Verify it exists before deleting
        assertThat(
            localTaskScheduleEventRepository.getForTask(parentTask.taskId).first()
        ).hasSize(1)

        // WHEN
        val rowsAffected = localTaskScheduleEventRepository.delete(newId)
        val eventsAfterDelete =
            localTaskScheduleEventRepository.getForTask(parentTask.taskId).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(eventsAfterDelete).isEmpty()
    }

    /**
     * GIVEN a parent task and its associated schedule events exist.
     * WHEN the PARENT task is deleted from the database.
     * THEN all associated schedule events should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteParentTask_whenCascadeIsActive_deletesChildScheduleEvents() = runTest {
        // GIVEN

        val event1 = TaskScheduleEventModel(
            taskId = parentTask.taskId,
            startDateInEpochDays = 1,
            createdAt = testTime
        )
        localTaskScheduleEventRepository.insert(event1)

        val eventsBeforeDelete =
            localTaskScheduleEventRepository.getForTask(parentTask.taskId).first()
        assertThat(eventsBeforeDelete).hasSize(1)

        // WHEN
        taskDao.delete(taskId = parentTask.taskId)

        // THEN
        val eventsAfterDelete =
            localTaskScheduleEventRepository.getForTask(parentTask.taskId).first()
        assertThat(eventsAfterDelete).isEmpty()
    }
}