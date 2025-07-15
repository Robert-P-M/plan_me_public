package at.robthered.plan_me.features.datasource.data.local.dao


import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.CustomKoinTestRule
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.dao.HashtagNameHistoryDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagNameHistoryEntity
import at.robthered.plan_me.features.datasource.di.androidTestDataSourceModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * Instrumentation tests for [HashtagNameHistoryDao].
 *
 * This suite uses a real in-memory Room database to verify the correctness of all
 * SQL queries and foreign key constraints related to the hashtag name history.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HashtagNameHistoryDaoTest : KoinTest {

    @get:Rule
    val customKoinTestRule = CustomKoinTestRule(
        modules = listOf(androidTestDataSourceModule)
    )

    private val db: AppDatabase by inject()
    private val historyDao: HashtagNameHistoryDao by inject()
    private val hashtagDao: HashtagDao by inject()

    /**
     * Clears all tables after each test to ensure perfect isolation.
     * The database connection is closed automatically by the test runner.
     */
    @After
    fun closeDb() {
        db.clearAllTables()
    }

    /**
     * GIVEN a parent hashtag exists.
     * WHEN a new history entry is inserted and then retrieved by its ID.
     * THEN the retrieved entity should contain the correct data.
     */
    @Test
    fun insert_andGet_insertsAndRetrievesHistoryEntry() = runTest {
        // GIVEN
        val parentHashtagId =
            hashtagDao.insert(HashtagEntity(name = "#initial", createdAt = 200L, updatedAt = 200L))
        val historyEntry = HashtagNameHistoryEntity(
            hashtagId = parentHashtagId,
            text = "#updated",
            createdAt = 100L
        )

        // WHEN
        val newHistoryId = historyDao.insert(historyEntry)
        val retrieved = historyDao.get(newHistoryId).first()

        // THEN
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.text).isEqualTo("#updated")
        assertThat(retrieved?.hashtagId).isEqualTo(parentHashtagId)
    }

    /**
     * GIVEN multiple history entries for different hashtags exist.
     * WHEN getForHashtag() is called for a specific hashtag.
     * THEN it should return only the history for that hashtag, sorted by creation date.
     */
    @Test
    fun getForHashtag_returnsAllHistoryEntriesForAGivenHashtag_orderedByDateAsc() = runTest {
        // GIVEN
        val parentHashtagId =
            hashtagDao.insert(HashtagEntity(name = "#initial", createdAt = 200L, updatedAt = 200L))
        // Insert out of order to test the sorting
        historyDao.insert(
            HashtagNameHistoryEntity(
                hashtagId = parentHashtagId,
                text = "#second",
                createdAt = 200L
            )
        )
        historyDao.insert(
            HashtagNameHistoryEntity(
                hashtagId = parentHashtagId,
                text = "#first",
                createdAt = 100L
            )
        )

        // Create unrelated data that should not be fetched
        val otherHashtagId =
            hashtagDao.insert(HashtagEntity(name = "#other", createdAt = 200L, updatedAt = 200L))
        historyDao.insert(
            HashtagNameHistoryEntity(
                hashtagId = otherHashtagId,
                text = "#other_history",
                createdAt = 300L
            )
        )

        // WHEN
        val historyForParent = historyDao.getForHashtag(parentHashtagId).first()

        // THEN
        assertThat(historyForParent).hasSize(2)
        // Verify that the list is correctly sorted by createdAt ASC
        assertThat(historyForParent.map { it.text }).containsExactly("#first", "#second").inOrder()
    }

    /**
     * GIVEN a parent hashtag and its history entry exist.
     * WHEN the parent hashtag is deleted.
     * THEN the history entry should also be deleted due to the CASCADE constraint.
     */
    @Test
    fun delete_whenParentIsDeleted_cascadesAndDeletesHistory() = runTest {
        // GIVEN
        val parentHashtagId = hashtagDao.insert(
            HashtagEntity(
                name = "#to_be_deleted",
                createdAt = 200L,
                updatedAt = 200L
            )
        )
        historyDao.insert(
            HashtagNameHistoryEntity(
                hashtagId = parentHashtagId,
                text = "#history",
                createdAt = 200L
            )
        )

        // WHEN
        // Delete the PARENT hashtag
        hashtagDao.delete(parentHashtagId)
        val history = historyDao.getForHashtag(parentHashtagId).first()

        // THEN
        // Verify the history is now empty
        assertThat(history).isEmpty()
    }

}