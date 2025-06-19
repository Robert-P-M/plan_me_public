package at.robthered.plan_me.features.data_source.domain.local.executor

interface TransactionProvider {
    suspend fun <R> runAsTransaction(block: suspend () -> R): R
}