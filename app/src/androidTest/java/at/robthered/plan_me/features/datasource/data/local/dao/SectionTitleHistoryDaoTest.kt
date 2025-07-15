package at.robthered.plan_me.features.datasource.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.dao.SectionTitleHistoryDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.data_source.data.local.entities.SectionTitleHistoryEntity
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
 * Instrumentation tests for [SectionTitleHistoryDao].
 * This suite uses a real in-memory Room database to verify all CRUD operations,
 * queries, and foreign key constraints for the SectionTitleHistory entity.
 */
@RunWith(AndroidJUnit4::class)
class SectionTitleHistoryDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val historyDao: SectionTitleHistoryDao by inject()
    private val sectionDao: SectionDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * GIVEN a valid parent section exists.
     * WHEN a new history entry is inserted and retrieved by its ID.
     * THEN the retrieved entity should match the inserted data.
     */
    @Test
    fun insertAndGetById_shouldInsertAndRetrieveCorrectHistory() = runTest {
        // GIVEN
        val parentId = sectionDao.insert(createSection(1, "Original Title"))
        val historyEntry = createHistory(sectionId = parentId, text = "New Title", createdAt = 100L)

        // WHEN
        val newHistoryId = historyDao.insert(historyEntry)
        val retrieved = historyDao.get(newHistoryId).first()

        // THEN
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.text).isEqualTo("New Title")
        assertThat(retrieved?.sectionId).isEqualTo(parentId)
    }

    /**
     * GIVEN multiple history entries for different sections exist.
     * WHEN getForSection is called for a specific section ID.
     * THEN it should return only the entries for that section, sorted by creation date.
     */
    @Test
    fun getForSection_shouldReturnFilteredAndSortedList() = runTest {
        // GIVEN
        val parentId = sectionDao.insert(createSection(1, "My Section"))
        // Insert out of order to test sorting
        historyDao.insert(createHistory(sectionId = parentId, text = "Title v2", createdAt = 200L))
        historyDao.insert(createHistory(sectionId = parentId, text = "Title v1", createdAt = 100L))

        // Insert unrelated data that should be ignored
        val otherParentId = sectionDao.insert(createSection(2, "Other Section"))
        historyDao.insert(
            createHistory(
                sectionId = otherParentId,
                text = "Other History",
                createdAt = 300L
            )
        )

        // WHEN
        val historyForSection = historyDao.getForSection(sectionId = parentId).first()

        // THEN
        assertThat(historyForSection).hasSize(2)
        assertThat(historyForSection.map { it.text }).containsExactly("Title v1", "Title v2")
            .inOrder()
    }

    /**
     * GIVEN a history entry is deleted by its specific ID.
     * WHEN the remaining entries are fetched.
     * THEN the deleted entry should be gone, and others should remain.
     */
    @Test
    fun delete_byHistoryId_shouldRemoveCorrectEntry() = runTest {
        // GIVEN
        val parentId = sectionDao.insert(createSection(1, "My Section"))
        val idToDelete = historyDao.insert(
            createHistory(
                sectionId = parentId,
                text = "History 1",
                createdAt = 100L
            )
        )
        val idToKeep = historyDao.insert(
            createHistory(
                sectionId = parentId,
                text = "History 2",
                createdAt = 200L
            )
        )

        // WHEN
        historyDao.delete(sectionTitleHistoryId = idToDelete)
        val remainingHistory = historyDao.getForSection(sectionId = parentId).first()

        // THEN
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().sectionTitleHistoryId).isEqualTo(idToKeep)
    }

    /**
     * GIVEN a parent section and its history entries exist.
     * WHEN the parent section is deleted.
     * THEN all its child history entries should be deleted automatically due to CASCADE constraint.
     */
    @Test
    fun delete_whenParentIsDeleted_cascadesAndDeletesHistory() = runTest {
        // GIVEN
        val parentId = sectionDao.insert(createSection(1, "Section to be deleted"))
        historyDao.insert(createHistory(sectionId = parentId, text = "History A", createdAt = 100L))
        assertThat(historyDao.getForSection(parentId).first()).isNotEmpty()

        // WHEN
        // Delete the PARENT entity
        sectionDao.delete(parentId)
        val history = historyDao.getForSection(sectionId = parentId).first()

        // THEN
        // Verify the children are gone
        assertThat(history).isEmpty()
    }

    // --- Helper Functions ---
    private fun createSection(id: Long, title: String): SectionEntity {
        return SectionEntity(sectionId = id, title = title, createdAt = id, updatedAt = id)
    }

    private fun createHistory(
        sectionId: Long,
        text: String,
        createdAt: Long,
    ): SectionTitleHistoryEntity {
        return SectionTitleHistoryEntity(sectionId = sectionId, text = text, createdAt = createdAt)
    }
}