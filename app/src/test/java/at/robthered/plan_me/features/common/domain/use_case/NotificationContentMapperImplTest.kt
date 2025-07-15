package at.robthered.plan_me.features.common.domain.use_case

import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit


@DisplayName("NotificationContentMapperImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DurationTest {

    @Test
    fun durationShouldReturnCorrectMinutes() {
        val duration = 2.hours + 15.minutes
        val expected = (2 * 60) + 15
        val result = duration.toInt(unit = DurationUnit.MINUTES)
        println("expected: $expected")
        println("result: $result")
        assertEquals(expected, result)
    }

}
@DisplayName("NotificationContentMapperImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationContentMapperImplTest {

    private lateinit var notificationContentMapper: NotificationContentMapperImpl
    private val viennaTimeZone = TimeZone.of("Europe/Vienna")

    @BeforeEach
    fun setUp() {
        notificationContentMapper = NotificationContentMapperImpl(timeZone = viennaTimeZone)
    }

    private fun createTask(
        id: Long,
        title: String,
        description: String?,
        taskSchedule: TaskScheduleEventModel? = null,
    ) = TaskModel(
        taskId = id, title = title, description = description, isCompleted = false,
        isArchived = false, updatedAt = Instant.DISTANT_PAST, createdAt = Instant.DISTANT_PAST,
        taskSchedule = taskSchedule

    )

    private fun createEvent(date: LocalDate, time: Int?, duration: Int?, enabled: Boolean) =
        TaskScheduleEventModel(
            startDateInEpochDays = date.toEpochDays(),
            timeOfDayInMinutes = time,
            durationInMinutes = duration,
            isNotificationEnabled = enabled,
            createdAt = Instant.DISTANT_PAST
        )

    @Test
    @DisplayName("Should return null if notifications are disabled")
    fun shouldReturnNullWhenNotificationsDisabled() {
        // Given
        val task = createTask(1L, "Test", null)
        val event = createEvent(LocalDate(2025, 6, 11), 10 * 60, 30, false)
        // When
        val result = notificationContentMapper(task)
        // Then
        assertNull(result?.scheduledDate)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Given only date is provided (full-day)")
    inner class DefaultNotificationTests {

        @ParameterizedTest(name = "For date {0}, title should be {1}")
        @CsvSource(
            value = [
                "2025-06-12, Task am Donnerstag",
                "2025-12-24, Heiligabend Task"
            ]
        )
        fun `Should create Default notification with trigger at 9 AM`(
            dateString: String,
            title: String,
        ) {
            // Given
            val date = LocalDate.parse(dateString)
            val event = createEvent(date, null, null, true)
            val task = createTask(1L, title, "Beschreibung", taskSchedule = event)

            // When
            val result = notificationContentMapper(task)

            // Then
            assertInstanceOf(NotificationContent.Default::class.java, result)
            val notification = result as NotificationContent.Default
            val expectedTrigger = LocalDateTime(date, LocalTime(9, 0)).toInstant(viennaTimeZone)

            assertAll(
                "Default Notification Properties",
                { assertEquals(title, notification.title) },
                { assertEquals(date, notification.scheduledDate) },
                { assertEquals(expectedTrigger, notification.triggerInstant) }
            )
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Given time is provided (without duration)")
    inner class WithTimeNotificationTests {

        @ParameterizedTest(name = "For time {0}:{1}, title should be {2}")
        @CsvSource(value = ["10, 30, Morgens", "16, 45, Nachmittags"])
        fun `Should create WithTime notification`(hour: Int, minute: Int, title: String) {
            // Given
            val date = LocalDate(2025, 6, 11)
            val event = createEvent(date, hour * 60 + minute, null, true)
            val task = createTask(2L, title, null, taskSchedule = event)

            // When
            val result = notificationContentMapper(task)

            // Then
            assertInstanceOf(NotificationContent.WithTime::class.java, result)
            val notification = result as NotificationContent.WithTime
            val expectedTime = LocalTime(hour, minute)

            assertAll(
                "WithTime Notification Properties",
                { assertEquals(title, notification.title) },
                { assertEquals(expectedTime, notification.scheduleStartTime) },
                { assertEquals(date, notification.scheduledDate) }
            )
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Given time and duration are provided")
    inner class WithDurationNotificationTests {

        // MethodSource ist hier besser, da wir LocalTime-Objekte übergeben
        fun provideStandardDurationCases(): Stream<Arguments> = Stream.of(
            Arguments.of(10 * 60, 30, LocalTime(10, 0), LocalTime(10, 30)),
            Arguments.of(11 * 60 + 45, 90, LocalTime(11, 45), LocalTime(13, 15))
        )

        fun provideComplexDurationCases(): Stream<Arguments> = Stream.of(
            Arguments.of(
                LocalDate(2025, 6, 11),
                23 * 60 + 30,
                60,
                LocalTime(23, 30),
                LocalTime(0, 30)
            ),
            // Sommerzeit-Umstellung (Uhr wird von 02:00 auf 03:00 vorgestellt) am 30. März 2025 in Wien
            Arguments.of(
                LocalDate(2025, 3, 30),
                1 * 60 + 30,
                120,
                LocalTime(1, 30),
                LocalTime(4, 30)
            )
        )

        @ParameterizedTest
        @MethodSource("provideStandardDurationCases")
        @DisplayName("Should create WithDuration notification for standard cases")
        fun testWithDurationStandard(
            timeInMinutes: Int,
            duration: Int,
            expectedStart: LocalTime,
            expectedEnd: LocalTime,
        ) {
            // Given
            val date = LocalDate(2025, 6, 11)
            val event = createEvent(date, timeInMinutes, duration, true)
            val task = createTask(3L, "Meeting", "Wichtig", event)

            // When
            val result = notificationContentMapper(task)

            // Then
            assertInstanceOf(NotificationContent.WithDuration::class.java, result)
            val notification = result as NotificationContent.WithDuration
            assertAll(
                "WithDuration Standard Properties",
                { assertEquals(expectedStart, notification.scheduleStartTime) },
                { assertEquals(expectedEnd, notification.scheduleEndTime) }
            )
        }

        @ParameterizedTest(name = "Complex Case: Date={0}, StartTimeInMinutes={1}, Duration={2}")
        @MethodSource("provideComplexDurationCases")
        @DisplayName("Should create WithDuration notification correctly for complex cases (midnight, DST)")
        fun testWithDurationComplex(
            date: LocalDate,
            timeInMinutes: Int,
            duration: Int,
            expectedStart: LocalTime,
            expectedEnd: LocalTime,
        ) {
            // Given
            val event = createEvent(date, timeInMinutes, duration, true)
            val task = createTask(4L, "Complex Event", null, event)

            // When
            val result = notificationContentMapper(task)

            // Then
            assertInstanceOf(NotificationContent.WithDuration::class.java, result)
            val notification = result as NotificationContent.WithDuration
            assertAll(
                "WithDuration Complex Properties",
                {
                    assertEquals(
                        expectedStart,
                        notification.scheduleStartTime,
                        "Start time should be correct"
                    )
                },
                {
                    assertEquals(
                        expectedEnd,
                        notification.scheduleEndTime,
                        "End time should be correct and DST-aware"
                    )
                }
            )
        }
    }

}