package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalSectionTitleHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalSectionTitleHistoryRepository
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
 * Instrumentation tests for [LocalSectionTitleHistoryRepositoryImpl].
 *
 * Verifies all CRUD operations and the foreign key cascade behavior using a
 * real in-memory Room database.
 */
@RunWith(AndroidJUnit4::class)
class LocalSectionTitleHistoryRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )


    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.

    private val database: AppDatabase by inject()
    private val sectionDao: SectionDao by inject()
    private val localSectionTitleHistoryRepository: LocalSectionTitleHistoryRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")
    private val parentSection = SectionEntity(
        sectionId = 1L,
        title = "Parent Section",
        createdAt = testTime.toEpochMilliseconds(),
        updatedAt = testTime.toEpochMilliseconds()
    )

    /**
     * Creates a fresh in-memory database and inserts a parent section before each test
     * to satisfy the foreign key constraint of the SectionTitleHistoryEntity.
     */
    @Before
    fun setUp() {

        runBlocking {
            sectionDao.insert(parentSection)
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
     * GIVEN a new history model for an existing section.
     * WHEN it is inserted and then fetched with get().
     * THEN the returned model should match the inserted data.
     */
    @Test
    fun getById_afterInsert_returnsCorrectlyMappedModel() = runTest {
        // GIVEN
        val historyToInsert = SectionTitleHistoryModel(
            sectionId = 1L,
            text = "New Section Title",
            createdAt = testTime
        )

        // WHEN
        val newHistoryId = localSectionTitleHistoryRepository.insert(historyToInsert)
        val fetchedHistory = localSectionTitleHistoryRepository.get(newHistoryId).first()

        // THEN
        assertThat(fetchedHistory).isNotNull()
        assertThat(fetchedHistory?.sectionTitleHistoryId).isEqualTo(newHistoryId)
        assertThat(fetchedHistory?.text).isEqualTo("New Section Title")
        assertThat(fetchedHistory?.sectionId).isEqualTo(1L)
    }

    /**
     * GIVEN multiple history entries for one section exist.
     * WHEN getForSection() is called with the parent section's ID.
     * THEN a list containing all associated history entries should be returned.
     */
    @Test
    fun getForSection_whenMultipleEntriesExist_returnsCompleteList() = runTest {
        // GIVEN
        localSectionTitleHistoryRepository.insert(
            SectionTitleHistoryModel(
                sectionId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        localSectionTitleHistoryRepository.insert(
            SectionTitleHistoryModel(
                sectionId = 1L,
                text = "History 2",
                createdAt = testTime
            )
        )

        // WHEN
        val fetchedHistory = localSectionTitleHistoryRepository.getForSection(1L).first()

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
        val historyIdToDelete = localSectionTitleHistoryRepository.insert(
            SectionTitleHistoryModel(
                sectionId = 1L,
                text = "History to delete",
                createdAt = testTime
            )
        )
        val historyIdToKeep = localSectionTitleHistoryRepository.insert(
            SectionTitleHistoryModel(
                sectionId = 1L,
                text = "History to keep",
                createdAt = testTime
            )
        )

        // WHEN
        localSectionTitleHistoryRepository.delete(historyIdToDelete)
        val remainingHistory = localSectionTitleHistoryRepository.getForSection(1L).first()

        // THEN
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().sectionTitleHistoryId).isEqualTo(historyIdToKeep)
    }

    /**
     * GIVEN a parent section and its history entries exist.
     * WHEN the PARENT section is deleted.
     * THEN all associated history entries should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteParentSection_whenCascadeIsActive_deletesChildHistory() = runTest {
        // GIVEN
        localSectionTitleHistoryRepository.insert(
            SectionTitleHistoryModel(
                sectionId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        assertThat(localSectionTitleHistoryRepository.getForSection(1L).first()).hasSize(1)

        // WHEN
        sectionDao.delete(sectionId = 1L)

        // THEN
        val historyAfterDelete = localSectionTitleHistoryRepository.getForSection(1L).first()
        assertThat(historyAfterDelete).isEmpty()
    }
}