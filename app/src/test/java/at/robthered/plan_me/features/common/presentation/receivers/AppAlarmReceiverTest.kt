package at.robthered.plan_me.features.common.presentation.receivers

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import at.robthered.plan_me.features.common.data.notification.AppAlarmSchedulerImpl
import at.robthered.plan_me.features.common.domain.use_case.ShowNotificationUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

class AppAlarmReceiverTestTestApp : Application()

@ExtendWith(RobolectricExtension::class)
@DisplayName("AppAlarmReceiver Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Config(application = AppAlarmReceiverTestTestApp::class)
class AppAlarmReceiverTest : KoinTest {

    @MockK
    private lateinit var showNotificationUseCase: ShowNotificationUseCase

    private lateinit var context: Context
    private lateinit var alarmReceiver: AppAlarmReceiver
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext<Application>()
        alarmReceiver = spyk(AppAlarmReceiver(dispatcher = testDispatcher))

        startKoin {
            modules(
                module {
                    single { showNotificationUseCase }
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Nested
    @DisplayName("GIVEN a valid Intent")
    inner class ValidIntent {
        /**
         * GIVEN a valid Intent with a task ID is received.
         * WHEN onReceive is called.
         * THEN it should launch a coroutine and call the showNotificationUseCase with the correct task ID.
         */
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        @DisplayName("WHEN onReceive is called THEN should call showNotificationUseCase")
        fun `calls use case with correct task id`() = runTest(testDispatcher) {
            // GIVEN
            val taskId = 123L
            val intent = Intent(context, AppAlarmReceiver::class.java).apply {
                putExtra(AppAlarmSchedulerImpl.PENDING_INTENT_TASK_ID, taskId)
            }

            val mockPendingResult = mockk<BroadcastReceiver.PendingResult>(relaxed = true)
            every { alarmReceiver.goAsync() } returns mockPendingResult
            coEvery { showNotificationUseCase.invoke(any()) } returns Unit

            // WHEN
            alarmReceiver.onReceive(context, intent)

            advanceUntilIdle()

            // THEN
            coVerify(exactly = 1) { showNotificationUseCase.invoke(taskId) }
            verify(exactly = 1) { mockPendingResult.finish() }
        }
    }

    @Nested
    @DisplayName("GIVEN an invalid Intent")
    inner class InvalidIntent {
        /**
         * GIVEN an Intent is received but it's missing the task ID extra.
         * WHEN onReceive is called.
         * THEN it should do nothing and not call the use case.
         */
        @Test
        @DisplayName("WHEN taskId is missing THEN should do nothing")
        fun `does nothing when task id is missing`() = runTest {
            // GIVEN
            val intent = Intent(context, AppAlarmReceiver::class.java)

            // WHEN
            alarmReceiver.onReceive(context, intent)

            // THEN
            coVerify(exactly = 0) { showNotificationUseCase.invoke(any()) }
        }
    }
}