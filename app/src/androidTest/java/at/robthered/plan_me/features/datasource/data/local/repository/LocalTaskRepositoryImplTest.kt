package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.repository.LocalTaskRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
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
 * Instrumentation tests for [LocalTaskRepositoryImpl].
 * This suite uses a real in-memory Room database to verify that the repository correctly
 * interacts with the DAO, that SQL queries are valid, and that data is mapped correctly.
 */
@RunWith(AndroidJUnit4::class)
class LocalTaskRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.

    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val localTaskRepository: LocalTaskRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }

    /**
     * GIVEN a new TaskModel to insert.
     * WHEN it is inserted via the repository and then fetched by its new ID.
     * THEN the fetched task should not be null and should match the inserted data.
     */
    @Test
    fun insertAndGetById_shouldReturnCorrectlyMappedModel() = runTest {
        // GIVEN
        val taskModelToInsert = createTaskModel(0L, "New Task")

        // WHEN
        val newId = localTaskRepository.insert(taskModelToInsert)
        val fetchedTask = localTaskRepository.getTaskModelById(newId).first()

        // THEN
        assertThat(fetchedTask).isNotNull()
        assertThat(fetchedTask?.taskId).isEqualTo(newId)
        assertThat(fetchedTask?.title).isEqualTo("New Task")
    }

    /**
     * GIVEN an existing task in the database.
     * WHEN the repository's update method is called with a modified version of the task.
     * THEN the fetched task should reflect the updated data.
     */
    @Test
    fun update_shouldModifyExistingTask() = runTest {
        // GIVEN
        val initialTask = createTaskModel(1L, "Initial Title")
        localTaskRepository.insert(initialTask)

        // WHEN
        val updatedTask = initialTask.copy(title = "Updated Title")
        localTaskRepository.update(updatedTask)
        val fetchedTask = localTaskRepository.getTaskModelById(1L).first()

        // THEN
        assertThat(fetchedTask?.title).isEqualTo("Updated Title")
    }

    /**
     * GIVEN a task exists in the database.
     * WHEN the delete method is called with the task's ID.
     * THEN the task should no longer be found in the database.
     */
    @Test
    fun deleteById_shouldRemoveTaskFromDatabase() = runTest {
        // GIVEN
        val newId = localTaskRepository.insert(createTaskModel(0L, "To be deleted"))
        assertThat(localTaskRepository.getTaskModelById(newId).first()).isNotNull()

        // WHEN
        val rowsAffected = localTaskRepository.delete(newId)
        val resultAfterDelete = localTaskRepository.getTaskModelById(newId).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(resultAfterDelete).isNull()
    }

    /**
     * GIVEN multiple tasks exist.
     * WHEN the delete method is called with a list of specific IDs.
     * THEN only the specified tasks should be removed.
     */
    @Test
    fun deleteByIds_shouldRemoveOnlySpecifiedTasks() = runTest {
        // GIVEN
        val id1 = localTaskRepository.insert(createTaskModel(0L, "Task 1"))
        val id2 = localTaskRepository.insert(createTaskModel(0L, "Task 2"))
        val id3 = localTaskRepository.insert(createTaskModel(0L, "Task 3"))

        // WHEN
        localTaskRepository.delete(listOf(id1, id3))
        val allTasks = localTaskRepository.getRootTaskModels(null, null, SortDirection.ASC).first()

        // THEN
        assertThat(allTasks).hasSize(1)
        assertThat(allTasks.first().taskId).isEqualTo(id2)
    }


    /**
     * GIVEN multiple tasks are inserted in a non-alphabetical order.
     * WHEN getRootTaskModels is called with SortDirection.ASC.
     * THEN the returned list should be sorted alphabetically by title.
     */
    @Test
    fun getRootTaskModels_withAscDirection_returnsListSortedAscending() = runTest {
        // GIVEN
        taskDao.insert(createTaskEntity(1L, "B Task").copy(createdAt = testTime.toLong() + 5000))
        taskDao.insert(createTaskEntity(2L, "A Task"))

        // WHEN
        val taskList = localTaskRepository.getRootTaskModels(
            showCompleted = false, showArchived = false, sortDirection = SortDirection.ASC
        ).first()

        // THEN
        assertThat(taskList).hasSize(2)
        assertThat(taskList.map { it.title }).containsExactly("A Task", "B Task").inOrder()
    }

    /**
     * GIVEN a parent task with an associated sub-task exists.
     * WHEN the PARENT task is deleted.
     * THEN the child task should be deleted automatically due to the self-referencing CASCADE constraint.
     */
    @Test
    fun deleteParentTask_shouldCascadeDeleteChildTasks() = runTest {
        // GIVEN
        val parentId = localTaskRepository.insert(createTaskModel(0L, "Parent Task"))
        val childId =
            localTaskRepository.insert(createTaskModel(0L, "Child Task", parentTaskId = parentId))

        assertThat(localTaskRepository.getTaskModelById(parentId).first()).isNotNull()
        assertThat(localTaskRepository.getTaskModelById(childId).first()).isNotNull()

        // WHEN
        localTaskRepository.delete(parentId)

        // THEN
        assertThat(localTaskRepository.getTaskModelById(parentId).first()).isNull()
        assertThat(localTaskRepository.getTaskModelById(childId).first()).isNull()
    }

}