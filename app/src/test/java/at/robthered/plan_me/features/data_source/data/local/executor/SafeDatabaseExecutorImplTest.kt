package at.robthered.plan_me.features.data_source.data.local.executor

import android.database.sqlite.SQLiteConstraintException
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.data.local.exception.DatabaseOperationFailedException
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class FakeRoomDatabaseErrorMapper : RoomDatabaseErrorMapper {
    override fun invoke(e: Exception): RoomDatabaseError {
        return if (e is SQLiteConstraintException) {
            RoomDatabaseError.CONSTRAINT_VIOLATION
        } else {
            RoomDatabaseError.UNKNOWN
        }
    }
}

@DisplayName("SafeDatabaseExecutorImpl Tests")
class SafeDatabaseExecutorImplTest {

    private lateinit var fakeErrorMapper: FakeRoomDatabaseErrorMapper
    private lateinit var safeExecutor: SafeDatabaseExecutorImpl

    @BeforeEach
    fun setUp() {
        fakeErrorMapper = FakeRoomDatabaseErrorMapper()
        safeExecutor = SafeDatabaseExecutorImpl(fakeErrorMapper)
    }

    @Test
    fun `invoke - when lambda executes successfully - returns result of lambda`() = runTest {
        // GIVEN
        val successfulOperation = suspend { "Success" }

        // WHEN
        val result = safeExecutor(successfulOperation)

        // THEN
        assertThat(result).isEqualTo("Success")
    }

    @Test
    fun `invoke - when lambda throws known exception - throws mapped DatabaseOperationFailedException`() {
        // GIVEN
        val expectedOriginalException = SQLiteConstraintException("Constraint failed")
        val failingOperation = suspend { throw expectedOriginalException }

        // WHEN & THEN
        val thrownException = assertThrows<DatabaseOperationFailedException> {
            runTest {
                safeExecutor(failingOperation)
            }
        }

        // THEN 2

        assertThat(thrownException.error).isEqualTo(RoomDatabaseError.CONSTRAINT_VIOLATION)

        assertThat(thrownException.cause).isEqualTo(expectedOriginalException)
    }

    @Test
    fun `invoke - when lambda throws unknown exception - throws mapped DatabaseOperationFailedException with UNKNOWN`() {
        // GIVEN
        val expectedOriginalException = RuntimeException("Something unexpected happened")
        val failingOperation = suspend { throw expectedOriginalException }

        // WHEN & THEN
        val thrownException = assertThrows<DatabaseOperationFailedException> {
            runTest {
                safeExecutor(failingOperation)
            }
        }

        // THEN 2
        assertThat(thrownException.error).isEqualTo(RoomDatabaseError.UNKNOWN)
        assertThat(thrownException.cause).isEqualTo(expectedOriginalException)
    }
}