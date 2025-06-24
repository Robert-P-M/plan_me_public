package at.robthered.plan_me.features.data_source.data.local.exception

import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("RoomDatabaseErrorMapperImpl Tests")
class RoomDatabaseErrorMapperImplTest {

    private lateinit var fakeLogger: FakeAppLogger
    private lateinit var errorMapper: RoomDatabaseErrorMapperImpl

    @BeforeEach
    fun setUp() {
        fakeLogger = FakeAppLogger()
        errorMapper = RoomDatabaseErrorMapperImpl(fakeLogger)
    }

    fun provideExceptionsAndExpectedErrors(): Stream<Arguments> = Stream.of(
        Arguments.of(SQLiteConstraintException(), RoomDatabaseError.CONSTRAINT_VIOLATION),
        Arguments.of(SQLiteDiskIOException(), RoomDatabaseError.DISK_IO_ERROR),
        Arguments.of(SQLiteDatabaseCorruptException(), RoomDatabaseError.CORRUPT_DATABASE),
        Arguments.of(SQLiteCantOpenDatabaseException(), RoomDatabaseError.CANT_OPEN),
        Arguments.of(IllegalStateException(), RoomDatabaseError.ILLEGAL_STATE),
        Arguments.of(RuntimeException("Some other error"), RoomDatabaseError.UNKNOWN)
    )

    @ParameterizedTest(name = "Given {0}, should map to {1}")
    @MethodSource("provideExceptionsAndExpectedErrors")
    fun `invoke should map different SQLite exceptions to correct RoomDatabaseError enums`(
        inputException: Exception,
        expectedError: RoomDatabaseError,
    ) {
        // WHEN
        val actualError = errorMapper(inputException)

        // THEN
        assertThat(actualError).isEqualTo(expectedError)

        // THEN
        assertThat(fakeLogger.loggedTag).isEqualTo("RoomDatabaseErrorMapperImpl")
        assertThat(fakeLogger.loggedMessage).contains("Database operation failed")
    }
}