package at.robthered.plan_me.features.data_source.data.local.executor

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider

class TransactionProviderImpl(
    private val database: RoomDatabase,
) : TransactionProvider {
    override suspend fun <R> runAsTransaction(block: suspend () -> R): R {
        return database.withTransaction { block() }
    }
}