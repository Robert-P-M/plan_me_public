package at.robthered.plan_me.features.data_source.domain.local.executor

interface SafeDatabaseExecutor {
    suspend operator fun <T> invoke(execute: suspend () -> T): T
}