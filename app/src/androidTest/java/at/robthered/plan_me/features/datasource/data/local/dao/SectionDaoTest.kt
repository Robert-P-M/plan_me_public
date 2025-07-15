package at.robthered.plan_me.features.datasource.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Instrumentation tests for [SectionDao].
 *
 * This suite uses a real in-memory Room database to verify that all CRUD
 * (Create, Read, Update, Delete) operations and SQL queries for the Section table
 * work as expected.
 */
@RunWith(AndroidJUnit4::class)
class SectionDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val dao: SectionDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * GIVEN a new section entity.
     * WHEN it is inserted and then retrieved by its auto-generated ID.
     * THEN the retrieved entity should match the inserted one.
     */
    @Test
    fun insertAndGetById_shouldInsertAndRetrieveCorrectSection() = runTest {
        // GIVEN
        val newSection = createSection(title = "Shopping", createdAt = 100L)

        // WHEN
        val newId = dao.insert(newSection)
        val retrievedSection = dao.get(sectionId = newId).first()

        // THEN
        assertThat(retrievedSection).isNotNull()
        assertThat(retrievedSection?.sectionId).isEqualTo(newId)
        assertThat(retrievedSection?.title).isEqualTo("Shopping")
    }

    /**
     * GIVEN multiple sections are inserted with different timestamps.
     * WHEN getAll() is called.
     * THEN it should return all sections, ordered by their creation time ascending.
     */
    @Test
    fun getAll_whenSectionsExist_shouldReturnSortedList() = runTest {
        // GIVEN
        dao.insert(createSection(id = 1, title = "Work", createdAt = 200L))
        dao.insert(createSection(id = 2, title = "Private", createdAt = 100L))
        dao.insert(createSection(id = 3, title = "Hobby", createdAt = 300L))

        // WHEN
        val allSections = dao.get().first()

        // THEN
        assertThat(allSections).hasSize(3)
        // Verifies the ORDER BY createdAt ASC in the DAO's query
        assertThat(allSections.map { it.sectionId }).containsExactly(2L, 1L, 3L).inOrder()
    }

    /**
     * GIVEN an existing section.
     * WHEN upsert() is called with a modified version of that section.
     * THEN the entry in the database should be updated.
     */
    @Test
    fun upsert_whenSectionExists_shouldUpdateSection() = runTest {
        // GIVEN
        val originalSection = createSection(id = 1, title = "Original", createdAt = 100L)
        dao.insert(originalSection)

        // WHEN
        val updatedSection = originalSection.copy(title = "New Title")
        dao.upsert(updatedSection)
        val retrievedSection = dao.get(sectionId = 1L).first()

        // THEN
        assertThat(retrievedSection).isNotNull()
        assertThat(retrievedSection?.title).isEqualTo("New Title")
    }

    /**
     * GIVEN multiple sections exist.
     * WHEN delete() is called with a list of specific IDs.
     * THEN only the specified sections should be removed.
     */
    @Test
    fun deleteByIds_shouldRemoveOnlySpecifiedSections() = runTest {
        // GIVEN
        val id1 = dao.insert(createSection(title = "A"))
        val id2 = dao.insert(createSection(title = "B"))
        val id3 = dao.insert(createSection(title = "C"))

        // WHEN
        dao.delete(sectionIds = listOf(id1, id3))
        val allSections = dao.get().first()

        // THEN
        assertThat(allSections).hasSize(1)
        assertThat(allSections.first().sectionId).isEqualTo(id2)
    }

    /**
     * GIVEN multiple sections exist.
     * WHEN deleteAll() is called.
     * THEN the sections table should be empty.
     */
    @Test
    fun deleteAll_shouldClearTable() = runTest {
        // GIVEN
        dao.insert(createSection(title = "A"))
        dao.insert(createSection(title = "B"))

        // WHEN
        dao.delete()
        val allSections = dao.get().first()

        // THEN
        assertThat(allSections).isEmpty()
    }

    // Helper function to create test entities cleanly
    private fun createSection(
        id: Long = 0,
        title: String,
        createdAt: Long = 0,
        updatedAt: Long = 0,
    ): SectionEntity {
        return SectionEntity(
            sectionId = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }


}