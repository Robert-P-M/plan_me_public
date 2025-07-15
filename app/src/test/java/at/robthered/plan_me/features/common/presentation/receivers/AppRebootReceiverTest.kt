package at.robthered.plan_me.features.common.presentation.receivers

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import at.robthered.plan_me.features.common.domain.use_case.RescheduleAlarmsUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
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

class AppRebootReceiverTestApp : Application()

@ExtendWith(RobolectricExtension::class)
@DisplayName("AppRebootReceiver Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Config(application = AppRebootReceiverTestApp::class)
class AppRebootReceiverTest : KoinTest {

    @MockK
    private lateinit var rescheduleAlarmsUseCase: RescheduleAlarmsUseCase

    private lateinit var context: Context
    private lateinit var rebootReceiver: AppRebootReceiver
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext<Application>()

        startKoin {
            modules(
                module {
                    single { rescheduleAlarmsUseCase }
                    single<CoroutineDispatcher> { testDispatcher }
                }
            )
        }
        rebootReceiver = spyk(AppRebootReceiver(dispatcher = testDispatcher))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        clearAllMocks()
    }

    @Nested
    @DisplayName("GIVEN a BOOT_COMPLETED intent")
    inner class BootIntent {
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        @DisplayName("WHEN onReceive is called with BOOT_COMPLETED THEN should call rescheduleAlarmsUseCase")
        fun `calls use case on boot completed`() = runTest(testDispatcher) {
            // GIVEN
            val intent = Intent(Intent.ACTION_BOOT_COMPLETED)
            val mockPendingResult = mockk<BroadcastReceiver.PendingResult>(relaxed = true)
            coEvery { rescheduleAlarmsUseCase.invoke() } just Runs
            every { rebootReceiver.goAsync() } returns mockPendingResult

            // WHEN
            rebootReceiver.onReceive(context, intent)
            advanceUntilIdle()

            // THEN
            coVerify(exactly = 1) { rescheduleAlarmsUseCase.invoke() }
            verify(exactly = 1) { mockPendingResult.finish() }
        }
    }

    @Nested
    @DisplayName("GIVEN a non-boot intent")
    inner class NonBootIntent {
        @Test
        @DisplayName("WHEN onReceive is called with non-boot action THEN should do nothing")
        fun `does nothing on non boot intent`() = runTest {
            // GIVEN
            val intent = Intent("some.other.ACTION")

            // WHEN
            rebootReceiver.onReceive(context, intent)

            // THEN
            coVerify(exactly = 0) { rescheduleAlarmsUseCase.invoke() } // ✅ sollte nun korrekt durchgehen
        }
    }
}