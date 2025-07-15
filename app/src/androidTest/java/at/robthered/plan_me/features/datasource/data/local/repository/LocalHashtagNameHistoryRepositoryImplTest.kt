package at.robthered.plan_me.features.datasource.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.data.repository.LocalHashtagNameHistoryRepositoryImpl
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
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
 * Instrumentation tests for [LocalHashtagNameHistoryRepositoryImpl].
 * This suite uses a Koin test module to provide a real in-memory Room database
 * and validates the repository's CRUD operations and foreign key constraints.
 */
@RunWith(AndroidJUnit4::class)
class LocalHashtagNameHistoryRepositoryImplTest : KoinTest {

    // The KoinTestRule automatically starts and stops Koin with the specified test modules.
    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule, repositoryModule)
    )

    // Inject the real DAOs and the SUT (System Under Test) directly from the Koin container.
    private val database: AppDatabase by inject()
    private val hashtagDao: HashtagDao by inject()
    private val localHashtagNameHistoryRepository: LocalHashtagNameHistoryRepository by inject()

    private val testTime = Instant.parse("2025-01-01T12:00:00Z")
    private val parentHashtag = HashtagEntity(
        hashtagId = 1L,
        name = "Parent Hashtag",
        createdAt = testTime.toEpochMilliseconds(),
        updatedAt = testTime.toEpochMilliseconds()
    )

    /**
     * Inserts a parent hashtag before each test to satisfy the foreign key constraint.
     */
    @Before
    fun setUp() {

        runBlocking {
            hashtagDao.insert(parentHashtag)
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
     * GIVEN a new history model for an existing hashtag.
     * WHEN it is inserted and then fetched with get().
     * THEN the returned model should match the inserted data.
     */
    @Test
    fun getById_afterInsert_returnsCorrectlyMappedModel() = runTest {
        // GIVEN
        val historyToInsert =
            HashtagNameHistoryModel(hashtagId = 1L, text = "New Name", createdAt = testTime)

        // WHEN
        val newHistoryId = localHashtagNameHistoryRepository.insert(historyToInsert)
        val fetchedHistory = localHashtagNameHistoryRepository.get(newHistoryId).first()

        // THEN
        assertThat(fetchedHistory).isNotNull()
        assertThat(fetchedHistory?.hashtagNameHistoryId).isEqualTo(newHistoryId)
        assertThat(fetchedHistory?.text).isEqualTo("New Name")
        assertThat(fetchedHistory?.hashtagId).isEqualTo(1L)
    }

    /**
     * GIVEN multiple history entries for one hashtag exist.
     * WHEN getForHashtag() is called with the parent hashtag's ID.
     * THEN a list containing all associated history entries should be returned.
     */
    @Test
    fun getForHashtag_whenMultipleEntriesExist_returnsCompleteList() = runTest {
        // GIVEN
        localHashtagNameHistoryRepository.insert(
            HashtagNameHistoryModel(
                hashtagId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        localHashtagNameHistoryRepository.insert(
            HashtagNameHistoryModel(
                hashtagId = 1L,
                text = "History 2",
                createdAt = testTime
            )
        )

        // WHEN
        val fetchedHistory = localHashtagNameHistoryRepository.getForHashtag(1L).first()

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
        val historyIdToDelete = localHashtagNameHistoryRepository.insert(
            HashtagNameHistoryModel(
                hashtagId = 1L,
                text = "History to delete",
                createdAt = testTime
            )
        )
        val historyIdToKeep = localHashtagNameHistoryRepository.insert(
            HashtagNameHistoryModel(
                hashtagId = 1L,
                text = "History to keep",
                createdAt = testTime
            )
        )

        // WHEN
        val rowsAffected = localHashtagNameHistoryRepository.delete(historyIdToDelete)
        val remainingHistory = localHashtagNameHistoryRepository.getForHashtag(1L).first()

        // THEN
        assertThat(rowsAffected).isEqualTo(1)
        assertThat(remainingHistory).hasSize(1)
        assertThat(remainingHistory.first().hashtagNameHistoryId).isEqualTo(historyIdToKeep)
    }

    /**
     * GIVEN a parent hashtag and its history entries exist.
     * WHEN the PARENT hashtag is deleted.
     * THEN all associated history entries should be deleted automatically due to the CASCADE constraint.
     */
    @Test
    fun deleteParentHashtag_whenCascadeIsActive_deletesChildHistory() = runTest {
        // GIVEN
        localHashtagNameHistoryRepository.insert(
            HashtagNameHistoryModel(
                hashtagId = 1L,
                text = "History 1",
                createdAt = testTime
            )
        )
        assertThat(localHashtagNameHistoryRepository.getForHashtag(1L).first()).hasSize(1)

        // WHEN
        hashtagDao.delete(hashtagId = 1L)

        // THEN
        val historyAfterDelete = localHashtagNameHistoryRepository.getForHashtag(1L).first()
        assertThat(historyAfterDelete).isEmpty()
    }
}