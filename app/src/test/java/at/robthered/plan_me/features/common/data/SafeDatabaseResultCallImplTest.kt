package at.robthered.plan_me.features.common.data

import android.database.sqlite.SQLiteConstraintException
import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class FakeAppLogger : AppLogger {
    var loggedMessage: String? = null
    var loggedTag: String? = null
    override fun e(tag: String, msg: String, throwable: Throwable?) {
        loggedTag = tag
        loggedMessage = msg
    }

    override fun v(tag: String, msg: String, throwable: Throwable?) {}
    override fun d(tag: String, msg: String, throwable: Throwable?) {}
    override fun i(tag: String, msg: String, throwable: Throwable?) {}
    override fun w(tag: String, msg: String, throwable: Throwable?) {}
    override fun wtf(tag: String, msg: String, throwable: Throwable?) {}
}

class FakeRoomDatabaseErrorMapper : RoomDatabaseErrorMapper {
    override fun invoke(e: Exception): RoomDatabaseError {
        return if (e is SQLiteConstraintException) RoomDatabaseError.CONSTRAINT_VIOLATION
        else RoomDatabaseError.UNKNOWN
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("SafeDatabaseResultCallImpl Tests")
class SafeDatabaseResultCallImplTest {

    private lateinit var fakeLogger: FakeAppLogger
    private lateinit var fakeErrorMapper: FakeRoomDatabaseErrorMapper
    private lateinit var safeDatabaseCall: SafeDatabaseResultCallImpl

    @BeforeEach
    fun setUp() {
        fakeLogger = FakeAppLogger()
        fakeErrorMapper = FakeRoomDatabaseErrorMapper()
        safeDatabaseCall = SafeDatabaseResultCallImpl(fakeLogger, fakeErrorMapper)
    }

    @Test
    fun `invoke - when block returns Success - returns the same Success result and does not log`() =
        runTest {
            // GIVEN
            val successResult = AppResult.Success("Test Data")
            val block = suspend { successResult }

            // WHEN
            val result = safeDatabaseCall(block = block)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            assertThat((result as AppResult.Success).data).isEqualTo("Test Data")

            // AND
            assertThat(fakeLogger.loggedMessage).isNull()
        }

    @Test
    fun `invoke - when block returns Error - returns the same Error result and does not log`() =
        runTest {
            // GIVEN
            val errorResult = AppResult.Error(RoomDatabaseError.NO_TASK_FOUND)
            val block = suspend { errorResult }

            // WHEN
            val result = safeDatabaseCall(block = block)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.NO_TASK_FOUND)

            // AND
            assertThat(fakeLogger.loggedMessage).isNull()
        }

    @Test
    fun `invoke - when block throws Exception - catches, logs and returns a new Mapped Error`() =
        runTest {
            // GIVEN
            val exceptionToThrow = SQLiteConstraintException()
            val failingBlock = suspend { throw exceptionToThrow }
            val callerTag = "MyTestUseCase"

            // WHEN
            val result = safeDatabaseCall<Any>(callerTag = callerTag, block = failingBlock)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Error::class.java)
            assertThat((result as AppResult.Error).error).isEqualTo(RoomDatabaseError.CONSTRAINT_VIOLATION)

            // AND
            assertThat(fakeLogger.loggedTag).isEqualTo("SafeDatabaseResultCall - callerTag: $callerTag")
            assertThat(fakeLogger.loggedMessage).contains("A database operation failed for caller: $callerTag")
        }


}