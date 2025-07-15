package at.robthered.plan_me.features.datasource.data.local.executor

import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor

/**
 * A test implementation of the [SafeDatabaseExecutor] interface.
 *
 * This class is used exclusively in instrumentation tests (`/androidTest`).
 * Its purpose is to bypass any safety logic (like try-catch blocks) that the
 * real implementation might have. It directly invokes the passed lambda block.
 *
 * This allows tests to receive the raw, unfiltered exceptions thrown by the
 * database (e.g., `SQLiteConstraintException`), which is crucial for verifying
 * database constraints and behavior.
 */
class TestSafeDatabaseExecutor : SafeDatabaseExecutor {
    override suspend fun <T> invoke(execute: suspend () -> T): T {
        return execute()
    }
}