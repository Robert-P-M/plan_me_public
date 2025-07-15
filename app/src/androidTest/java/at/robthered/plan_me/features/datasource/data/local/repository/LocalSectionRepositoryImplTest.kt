package at.robthered.plan_me.features.datasource.data.local.repository


import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.repository.LocalSectionRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionRepository
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
 * Instrumentation tests for [LocalSectionRepositoryImpl].
 *
 * This suite uses a real in-memory Room database to verify all CRUD operations
 * and the foreign key cascade behavior on its child tasks.
 */
@RunWith(AndroidJUnit4::class)
class LocalSectionRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val taskDao: TaskDao by inject()
    private val localSectionRepository: LocalSectionRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")

    /**
     * Clears all database tables after each test to ensure perfect test isolation.
     */
    @After
    fun tearDown() {
        database.clearAllTables()
    }

    /**
     * GIVEN a new section model.
     * WHEN it is inserted and then fetched by its new ID.
     * THEN the fetched section should match the inserted data.
     */
    @Test
    fun insertAndGetById_shouldReturnCorrectData() = runTest {
        // GIVEN
        val sectionToInsert =
            SectionModel(title = "New Section", createdAt = testTime, updatedAt = testTime)

        // WHEN
        val newId = localSectionRepository.insert(sectionToInsert)
        val fetchedSection = localSectionRepository.get(newId).first()

        // THEN
        assertThat(fetchedSection).isNotNull()
        assertThat(fetchedSection?.sectionId).isEqualTo(newId)
        assertThat(fetchedSection?.title).isEqualTo("New Section")
    }

    /**
     * GIVEN an existing section.
     * WHEN upsert is called with a modified version.
     * THEN the section should be updated in the database.
     */
    @Test
    fun upsert_shouldUpdateExistingSection() = runTest {
        // GIVEN
        val initialId = localSectionRepository.insert(createSectionModel(0L, "Initial Title"))

        // WHEN
        val updatedSection = SectionModel(
            sectionId = initialId,
            title = "Updated Title",
            createdAt = testTime,
            updatedAt = testTime
        )
        localSectionRepository.upsert(updatedSection)
        val fetchedSection = localSectionRepository.get(initialId).first()

        // THEN
        assertThat(fetchedSection?.title).isEqualTo("Updated Title")
    }

    /**
     * GIVEN multiple sections exist.
     * WHEN delete(sectionId) is called.
     * THEN only the specified section should be removed.
     */
    @Test
    fun deleteById_shouldRemoveOnlySpecifiedSection() = runTest {
        // GIVEN
        val idToDelete = localSectionRepository.insert(createSectionModel(0L, "To Delete"))
        val idToKeep = localSectionRepository.insert(createSectionModel(0L, "To Keep"))

        // WHEN
        val rowsAffected = localSectionRepository.delete(idToDelete)
        val remainingSections = localSectionRepository.get().first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(remainingSections).hasSize(1)
        assertThat(remainingSections.first().sectionId).isEqualTo(idToKeep)
    }

    /**
     * GIVEN a parent section with associated tasks exists.
     * WHEN the parent section is deleted.
     * THEN all child tasks should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteSection_whenCascadeIsActive_deletesChildTasks() = runTest {
        // GIVEN
        val sectionId = localSectionRepository.insert(createSectionModel(0L, "Parent Section"))

        val childTaskId = 1L
        taskDao.insert(createTaskEntity(childTaskId, "Child Task", sectionId = sectionId))

        assertThat(taskDao.getTaskEntityById(childTaskId).first()).isNotNull()

        // WHEN
        localSectionRepository.delete(sectionId)

        // THEN
        val taskAfterDelete = taskDao.getTaskEntityById(childTaskId).first()
        assertThat(taskAfterDelete).isNull()
    }

}