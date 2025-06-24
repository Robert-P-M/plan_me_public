package at.robthered.plan_me.features.datasource.data.local.executor

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.robthered.plan_me.features.data_source.data.local.dao.SectionDao
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.executor.TransactionProviderImpl
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionProviderImplTest {

    private lateinit var database: AppDatabase
    private lateinit var sectionDao: SectionDao
    private lateinit var transactionProvider: TransactionProvider

    private lateinit var clock: Clock
    private lateinit var now: Instant

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        sectionDao = database.sectionDao()
        transactionProvider = TransactionProviderImpl(database)
        clock = Clock.System
        now = clock.now()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun runAsTransaction_executesBlock_onSuccess() = runTest {
        // GIVEN
        val sectionToInsert = SectionModel(title = "Test Section", createdAt = now, updatedAt = now)

        // WHEN
        transactionProvider.runAsTransaction {
            sectionDao.insert(sectionToInsert.toEntity())
        }

        // THEN
        val result = sectionDao.get(1).first()
        assertThat(result).isNotNull()
        assertThat(result?.title).isEqualTo("Test Section")
    }

    @Test
    fun runAsTransaction_rollsBackTransaction_onFailure() = runTest {
        // GIVEN
        val sectionToInsert = SectionModel(title = "Test Section", createdAt = now, updatedAt = now)
        val exception = RuntimeException("Ein Fehler ist aufgetreten!")

        // WHEN
        try {
            transactionProvider.runAsTransaction {
                sectionDao.insert(sectionToInsert.toEntity())
                throw exception
            }
        } catch (e: RuntimeException) {
            assertThat(e).isEqualTo(exception)
        }

        // THEN
        val result = sectionDao.get(1).first()
        assertThat(result).isNull()
    }
}