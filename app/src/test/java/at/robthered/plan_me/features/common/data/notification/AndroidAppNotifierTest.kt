package at.robthered.plan_me.features.common.data.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNotificationManager
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

/**
 * Test suite for [AndroidAppNotifier].
 * This test class uses Robolectric with the JUnit 5 extension to simulate the Android framework on the JVM.
 */

class AndroidAppNotifierTestTestApp : Application()

@ExtendWith(RobolectricExtension::class)
@DisplayName("AndroidAppNotifier Tests (with Robolectric)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Config(application = AndroidAppNotifierTestTestApp::class)
class AndroidAppNotifierTest {

    @MockK
    private lateinit var notificationContentMapper: NotificationContentMapper

    private lateinit var context: Context
    private lateinit var notifier: AndroidAppNotifier
    private lateinit var shadowNotificationManager: ShadowNotificationManager

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        context = ApplicationProvider.getApplicationContext<Application>()
        notifier = AndroidAppNotifier(context, notificationContentMapper)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        shadowNotificationManager = Shadows.shadowOf(notificationManager)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }


    @Nested
    @DisplayName("Notification Channel")
    inner class NotificationChannelTests {
        @Test
        @DisplayName("createNotificationChannel() should register the channel correctly")
        fun createsNotificationChannel() {
            // WHEN
            notifier.createNotificationChannel()
            // THEN
            val channel =
                shadowNotificationManager.notificationChannels.firstOrNull { it.id == "task_reminder_channel" }
            assertNotNull(channel)
            assertEquals("Task Reminders", channel?.name)
            assertEquals(NotificationManager.IMPORTANCE_HIGH, channel?.importance)
        }
    }

    @Nested
    @DisplayName("Show Notification Logic")
    inner class ShowNotificationTests {
        @Test
        @DisplayName("show() should do nothing if mapper returns null")
        fun doesNothingIfContentIsNull() = runTest {
            // GIVEN
            val testTask = createTask(1L)
            every { notificationContentMapper.invoke(testTask) } returns null
            // WHEN
            notifier.show(testTask)
            // THEN
            assertEquals(0, shadowNotificationManager.size())
        }

        @Test
        @DisplayName("show() should post correctly styled notification for duration content")
        fun showsNotificationWithDurationStyle() = runTest {
            // GIVEN
            val testTask = createTask(123L)
            val notificationId = testTask.taskId.toInt()
            val notificationContent = createTestNotificationContent(notificationId, testTask.taskId)
            every { notificationContentMapper.invoke(testTask) } returns notificationContent

            // WHEN
            notifier.show(testTask)

            // THEN
            val postedNotification = shadowNotificationManager.getNotification(notificationId)
            assertNotNull(postedNotification, "Notification should have been posted")
            assertEquals(R.drawable.ic_launcher_foreground, postedNotification.smallIcon.resId)

            val extras = postedNotification.extras
            assertEquals(notificationContent.title, extras.getString(Notification.EXTRA_TITLE))
            // ...
        }
    }

    private fun createTask(taskId: Long): TaskModel {
        val now = Instant.parse("2025-01-01T12:00:00Z")
        return TaskModel(
            taskSchedule = TaskScheduleEventModel(
                taskId = taskId,
                startDateInEpochDays = 1,
                createdAt = now
            ),
            taskId = taskId,
            title = "Task Title $taskId",
            isCompleted = false,
            isArchived = false,
            updatedAt = now,
            createdAt = now
        )
    }

    private fun createTestNotificationContent(notificationId: Int, taskId: Long) =
        NotificationContent.WithDuration(
            notificationId = notificationId,
            taskId = taskId,
            title = "Task with Duration",
            description = "Description for notification",
            scheduleStartTime = LocalTime(10, 0),
            scheduleEndTime = LocalTime(11, 0),
            scheduledDate = LocalDate(2025, 10, 20),
            triggerInstant = Instant.parse("2025-10-20T10:00:00Z"),
            priorityEnum = PriorityEnum.HIGH
        )
}