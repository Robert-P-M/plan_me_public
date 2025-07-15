package at.robthered.plan_me.features.task_schedule_picker_dialog.data

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.inOneMonth
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarDay
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.nextMonday
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.nextSaturday
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.toLocalDateOrNull
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.upcomingMondayInOneWeek
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test suite for the [DateTimeHelperImpl] .
 * @see DateTimeHelperImpl
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tests for DateTimeHelperImpl")
class DateTimeHelperImplTest {

    lateinit var dateTimeHelper: DateTimeHelperImpl


    @BeforeAll
    fun startUp() {
        dateTimeHelper = DateTimeHelperImpl()
    }

    @Nested
    @DisplayName("Tests for getBoundingDaysOfMonth")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBoundingDaysOfMonthTests {
        /**
         * Tests the [DateTimeHelperImpl.getBoundingDaysForMonth] extension.
         * Ensures that for various dates, including leap years, the correct
         * bounding days are returned.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedFirstDay The expected first day of the boundary in "YYYY-MM-DD" format.
         * @param expectedLastDay The expected last day of the boundary in "YYYY-MM-DD" format.
         */
        @ParameterizedTest(name = "The boundary for {0} is START: {1} and END {2}.")
        @CsvSource(
            value = [
                "2025-01-15, 2024-12-30, 2025-02-02",
                "2025-02-28, 2025-01-27, 2025-03-02",
                "2025-04-17, 2025-03-31, 2025-05-04",
                "2025-07-05, 2025-06-30, 2025-08-03",
            ]
        )
        fun `getBoundingDaysForMonth should return the correct boundaries`(
            dateString: String,
            expectedFirstDay: String,
            expectedLastDay: String,
        ) {
            val localDate = LocalDate.parse(dateString)
            val boundingDaysOfMonth =
                dateTimeHelper.getBoundingDaysForMonth(
                    monthNumber = localDate.monthNumber,
                    year = localDate.year
                )
            val boundaryFirstDate = LocalDate.parse(expectedFirstDay)
            val boundaryLastDate = LocalDate.parse(expectedLastDay)

            assertEquals(
                expected = DayOfWeek.MONDAY,
                actual = boundingDaysOfMonth.first().dayOfWeek
            )
            assertEquals(boundaryFirstDate, boundingDaysOfMonth.first())
            assertEquals(
                expected = DayOfWeek.SUNDAY,
                actual = boundingDaysOfMonth.last().dayOfWeek
            )
            assertEquals(boundaryLastDate, boundingDaysOfMonth.last())

        }
    }

    @Nested
    @DisplayName("Tests for getWeeksForMonthTest")
    inner class GetWeeksForMonthTests {
        /**
         * Tests the [DateTimeHelperImpl.getWeeksForMonth] method.
         * Ensures that for various months and years, the method returns a list of weeks,
         * where each week contains 7 days, and the overall bounding box (from the first Monday
         * to the last Sunday) is correct.
         *
         * @param monthNumber The month number to test (1-12).
         * @param year The year to test.
         * @param expectedNumberOfWeeks The expected number of weeks in the calendar view for this month (e.g., 5 or 6).
         * @param expectedFirstDayInViewString The expected first day (Monday) of the entire calendar view.
         * @param expectedLastDayInViewString The expected last day (Sunday) of the entire calendar view.
         */
        @ParameterizedTest(name = "The boundary for {0} is START: {1} and END {2}.")
        @CsvSource(
            value = [
                "1, 2025, 5, 2024-12-30, 2025-02-02",
                "2, 2025, 5, 2025-01-27, 2025-03-02",
                "2, 2024, 5, 2024-01-29, 2024-03-03",
                "4, 2025, 5, 2025-03-31, 2025-05-04",
                "6, 2025, 6, 2025-05-26, 2025-07-06",
                "7, 2025, 5, 2025-06-30, 2025-08-03",
                "12, 2025, 5, 2025-12-01, 2026-01-04",
                "12, 2024, 6, 2024-11-25, 2025-01-05"
            ]
        )
        fun `getWeeksForMonth returns list of weeks with correct structure and boundaries`(
            monthNumber: Int,
            year: Int,
            expectedNumberOfWeeks: Int,
            expectedFirstDayInViewString: String,
            expectedLastDayInViewString: String,
        ) {
            val weeksForMonth =
                dateTimeHelper.getWeeksForMonth(monthNumber = monthNumber, year = year)

            val expectedFirstDayInView = LocalDate.parse(expectedFirstDayInViewString)
            val expectedLastDayInView = LocalDate.parse(expectedLastDayInViewString)

            assertEquals(
                expected = expectedNumberOfWeeks,
                actual = weeksForMonth.size,
                message = "Expected $expectedNumberOfWeeks weeks for $monthNumber/$year, but got ${weeksForMonth.size}."
            )

            weeksForMonth.forEachIndexed { weekIndex, week ->
                assertEquals(
                    expected = 7,
                    actual = week.size,
                    message = "Week $weekIndex for $monthNumber/$year should contain 7 days."
                )
                assertEquals(
                    expected = DayOfWeek.MONDAY,
                    actual = week.first().dayOfWeek,
                    message = "Week $weekIndex for $monthNumber/$year should start on Monday."
                )
                assertEquals(
                    expected = DayOfWeek.SUNDAY,
                    actual = week.last().dayOfWeek,
                    message = "Week $weekIndex for $monthNumber/$year should end on Sunday."
                )
            }

            assertEquals(
                expected = expectedFirstDayInView,
                actual = weeksForMonth.first().first(),
                message = "The first day of the calendar view for $monthNumber/$year is incorrect."
            )
            assertEquals(
                expectedLastDayInView, weeksForMonth.last().last(),
                "The last day of the calendar view for $monthNumber/$year is incorrect."
            )
        }
    }

    @Nested
    @DisplayName("Test suite for getCalendarMonth")
    inner class GetCalendarMonthTests {
        /**
         * Tests the [DateTimeHelperImpl.getCalendarMonth] method.
         * Ensures that for various months and years, the method returns a [CalendarMonthModel]
         * with the correct month and year, the expected number of weeks, and that each week
         * starts with a [CalendarDay.WeekHeader] followed by 7 [CalendarDay.Day] items.
         *
         * @param monthNumber The month number to test (1-12).
         * @param year The year to test.
         * @param expectedNumberOfWeeks The expected total number of weeks in the calendar view for this month (e.g., 5 or 6).
         * @param expectedFirstDayInViewString The string representation of the expected first day (Monday) of the entire calendar view.
         * @param expectedLastDayInViewString The string representation of the expected last day (Sunday) of the entire calendar view.
         */
        @ParameterizedTest(name = "For {0}/{1}, CalendarMonth should have {2} weeks from {3} to {4}.")
        @CsvSource(
            value = [
                "1, 2025, 5, 2024-12-30, 2025-02-02",
                "2, 2025, 5, 2025-01-27, 2025-03-02",
                "2, 2024, 5, 2024-01-29, 2024-03-03",
                "4, 2025, 5, 2025-03-31, 2025-05-04",
                "6, 2025, 6, 2025-05-26, 2025-07-06",
                "7, 2025, 5, 2025-06-30, 2025-08-03",
                "12, 2025, 5, 2025-12-01, 2026-01-04",
                "12, 2024, 6, 2024-11-25, 2025-01-05"
            ]
        )
        fun `getCalendarMonth returns correct CalendarMonthModel structure`(
            monthNumber: Int,
            year: Int,
            expectedNumberOfWeeks: Int,
            expectedFirstDayInViewString: String,
            expectedLastDayInViewString: String,
        ) {
            val localDateForMonth = LocalDate(year, monthNumber, 15)
            val expectedFirstDayInView = LocalDate.parse(expectedFirstDayInViewString)
            val expectedLastDayInView = LocalDate.parse(expectedLastDayInViewString)

            val calendarMonth = dateTimeHelper.getCalendarMonth(localDate = localDateForMonth)

            assertInstanceOf<CalendarMonthModel>(calendarMonth)

            assertEquals(monthNumber, calendarMonth.monthNumber, "Month number mismatch")
            assertEquals(year, calendarMonth.year, "Year mismatch")

            assertEquals(
                expected = expectedNumberOfWeeks,
                actual = calendarMonth.weeks.size,
                message = "Expected $expectedNumberOfWeeks weeks for $monthNumber/$year, but got ${calendarMonth.weeks.size}."
            )

            calendarMonth.weeks.forEachIndexed { weekIndex, week ->
                assertEquals(
                    expected = 8,
                    actual = week.size,
                    message = "Week $weekIndex should have 8 items (1 header + 7 days)."
                )

                val weekHeader = week.first()
                assertInstanceOf<CalendarDay.WeekHeader>(
                    actualValue = weekHeader,
                    message = "First item of Week $weekIndex should be a WeekHeader."
                )

                assertEquals(
                    expected = weekHeader.localDate,
                    actual = week[1].localDate,
                    message = "WeekHeader date mismatch for week $weekIndex."
                )
                assertEquals(
                    DayOfWeek.MONDAY,
                    actual = weekHeader.localDate.dayOfWeek,
                    message = "WeekHeader should represent a Monday."
                )


                val dayItems = week.subList(1, week.size)
                assertEquals(
                    expected = 7,
                    actual = dayItems.size,
                    message = "Week $weekIndex should have 7 day items."
                )
                dayItems.forEachIndexed { dayItemIndex, dayItem ->
                    assertInstanceOf<CalendarDay.Day>(
                        actualValue = dayItem,
                        message = "Item $dayItemIndex in Week $weekIndex should be a Day."
                    )
                    if (dayItemIndex > 0) {
                        assertEquals(
                            expected = dayItems[dayItemIndex - 1].localDate.plus(DatePeriod(days = 1)),
                            actual = dayItem.localDate,
                            message = "Days in Week $weekIndex are not chronological."
                        )
                    }
                }
            }

            assertEquals(
                expected = expectedFirstDayInView,
                actual = calendarMonth.weeks.first()[1].localDate,
                message = "First day of calendar view mismatch."
            )
            assertEquals(
                expectedLastDayInView,
                actual = calendarMonth.weeks.last().last().localDate,
                message = "Last day of calendar view mismatch."
            )
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getNextMondayDate")
    inner class GetNextMondayDateTest {
        /**
         * Tests the [DateTimeHelperImpl.getUpcomingMonday] method.
         * Ensures that:
         * - If the input [LocalDate] is a Sunday, the method returns `null`.
         * - For all other days (Monday to Saturday), it returns an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.UpcomingMonday] object
         * representing the Monday of the *next calendar week* (i.e., the Monday following the current week's Sunday).
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedMondayString The expected date of the upcoming Monday in "YYYY-MM-DD" format, or "null".
         */
        @ParameterizedTest(name = "When date is {0}, the upcoming Monday should be {1}")
        @CsvSource(
            value = [
                "2025-06-02, 2025-06-09",
                "2025-06-03, 2025-06-09",
                "2025-06-04, 2025-06-09",
                "2025-06-05, 2025-06-09",
                "2025-06-06, 2025-06-09",
                "2025-06-07, 2025-06-09",
                "2025-06-08, 2025-06-09",

                "2025-06-09, 2025-06-16",
                "2025-06-14, 2025-06-16",
                "2025-06-15, 2025-06-16",

                "2024-12-30, 2025-01-06",
                "2024-12-31, 2025-01-06",
                "2025-01-04, 2025-01-06",
                "2025-01-05, 2025-01-06"
            ]

        )
        fun `getUpcomingMonday returns correct date or null`(
            dateString: String,
            expectedMondayString: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMonday = expectedMondayString.toLocalDateOrNull()
            val actualMonday = dateTimeHelper.getUpcomingMonday(date)

            assertEquals(
                expected = expectedMonday,
                actual = actualMonday.localDate,
                message = "For date $dateString, the upcoming Monday should be $expectedMonday."
            )
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getUpcomingMondayInOneWeek")
    inner class GetUpcomingMondayInOneWeekTest {
        /**
         * Tests the [DateTimeHelperImpl.getUpcomingMondayInOneWeek] method.
         * Ensures that:
         * - If the input [LocalDate] is a **Sunday**, the method returns an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.UpcomingMondayInOneWeek] object
         * representing the **Monday of the week two weeks after the current week's Monday**.
         * (This is derived from `localDate.upcomingMondayInOneWeek` which uses `nextNextMonday`).
         * - For all **other days** (Monday to Saturday), the method returns `null`.
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedMondayInPlusOneWeek The expected date for "Monday in one week" in "YYYY-MM-DD" format, or "null".
         */
        @ParameterizedTest(name = "When date is {0}, the upcoming Monday should be {1}")
        @CsvSource(
            value = [
                "2025-06-04, null",
                "2025-06-05, null",
                "2025-06-06, null",
                "2025-06-07, null",
                "2025-06-08, 2025-06-16",
                "2025-06-09, null",
                "2025-06-10, null",
                "2025-06-11, null",
                "2025-06-12, null",
                "2025-06-13, null",
                "2025-06-14, null",
                "2025-06-15, 2025-06-23",
                "2025-06-16, null",
                "2025-06-17, null",
                "2025-06-18, null"
            ]

        )
        fun `getUpcomingMondayInOneWeek returns correct date with monday plus one week or null`(
            dateString: String,
            expectedMondayInPlusOneWeek: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMondayInPlusOneWeek = expectedMondayInPlusOneWeek.toLocalDateOrNull()
            val actualMonday = dateTimeHelper.getUpcomingMondayInOneWeek(date)

            assertEquals(
                expected = expectedMondayInPlusOneWeek,
                actual = actualMonday?.localDate,
                message = "For date $dateString, the upcoming Monday in plus one week should be $expectedMondayInPlusOneWeek"
            )
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getTomorrowDate")
    inner class GetTomorrowDate {
        /**
         * Tests the [DateTimeHelperImpl.getTomorrowDate] method.
         * Ensures that:
         * - If the input [LocalDate] is a Sunday, the method returns `null`.
         * - For all other days (Monday to Saturday), it returns an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.Tomorrow] object
         * representing the date following the [LocalDate].
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedTomorrowDate The expected date for "Tomorrow" in "YYYY-MM-DD" format, or "null".
         */
        @ParameterizedTest(name = "When date is {0}, the tomorrow date should be {1}")
        @CsvSource(
            value = [
                "2025-06-04, 2025-06-05",
                "2025-06-05, 2025-06-06",
                "2025-06-06, 2025-06-07",
                "2025-06-07, 2025-06-08",
                "2025-06-08, null",
                "2025-06-09, 2025-06-10",
                "2025-06-10, 2025-06-11",
                "2025-06-11, 2025-06-12",
                "2025-06-12, 2025-06-13",
                "2025-06-13, 2025-06-14",
                "2025-06-14, 2025-06-15",
                "2025-06-15, null",
                "2025-06-16, 2025-06-17",
                "2025-06-17, 2025-06-18",
                "2025-06-18, 2025-06-19"
            ]

        )
        fun `getUpcomingMondayInOneWeek returns correct tomorrow date`(
            dateString: String,
            expectedTomorrowDate: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedTomorrowDate = expectedTomorrowDate.toLocalDateOrNull()
            val actualTomorrow = dateTimeHelper.getTomorrowDate(date)

            if (expectedTomorrowDate != null) {

                assertEquals(
                    expected = expectedTomorrowDate,
                    actual = actualTomorrow?.localDate,
                    message = "For date $dateString, the tomorrow date should be $expectedTomorrowDate"
                )
            } else {
                assertNull(
                    actualTomorrow,
                    "For date $dateString, this monday plus one should be null"
                )
            }
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getThisWeekendDate")
    inner class GetThisWeekendDate {
        /**
         * Tests the [DateTimeHelperImpl.getThisWeekendDate] method.
         * Ensures that:
         * - If the input [LocalDate] is Monday, Tuesday, Wednesday, or Thursday, it returns
         * an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.UpcomingWeekend] object representing the Saturday of the current week.
         * - If the input [LocalDate] is Friday, Saturday, or Sunday, it returns `null`
         * as "this weekend" is considered too close or already passed based on the function's logic.
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedSaturdayString The expected date for "this weekend" in "YYYY-MM-DD" format, or "null".
         */
        @ParameterizedTest(name = "From {0}, 'this weekend' should be {1}")
        @CsvSource(
            value = [
                "2025-06-02, 2025-06-07",
                "2025-06-03, 2025-06-07",
                "2025-06-04, 2025-06-07",
                "2025-06-05, 2025-06-07",
                "2025-06-06, null",
                "2025-06-07, null",
                "2025-06-08, null",
                "2024-12-30, 2025-01-04",
                "2025-01-04, null",
                "2025-01-05, null"
            ]
        )
        fun `getThisWeekendDate returns correct Saturday or null if weekend passed`(
            dateString: String,
            expectedSaturdayString: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedSaturday = expectedSaturdayString.toLocalDateOrNull()
            val actualWeekendItem = dateTimeHelper.getThisWeekendDate(date)

            if (expectedSaturday != null) {
                assertEquals(
                    expected = expectedSaturday,
                    actual = actualWeekendItem?.localDate,
                    message = "For date $dateString, 'this weekend' should be $expectedSaturday."
                )
            } else {
                assertNull(
                    actualWeekendItem,
                    "For date $dateString, 'this weekend' should be null."
                )
            }
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getNextWeekendDate")
    inner class GetNextWeekendDate {
        /**
         * Tests the [DateTimeHelperImpl.getNextWeekendDate] method.
         * Ensures that for any given [LocalDate], the method consistently returns
         * an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.NextWeekend] object representing the Saturday of the
         * calendar week immediately following the current week.
         * This function is expected to always return a non-null value.
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedNextSaturday The expected date for "next weekend" in "YYYY-MM-DD" format.
         */
        @ParameterizedTest(name = "From {0}, 'next weekend' should be {1}")
        @CsvSource(
            value = [
                "2024-12-30, 2025-01-11",
                "2025-01-04, 2025-01-11",
                "2025-01-05, 2025-01-11",
                "2025-06-02, 2025-06-14",
                "2025-06-03, 2025-06-14",
                "2025-06-04, 2025-06-14",
                "2025-06-05, 2025-06-14",
                "2025-06-06, 2025-06-14",
                "2025-06-07, 2025-06-14",
                "2025-06-08, 2025-06-14",
            ]
        )
        fun `getNextWeekendDate returns next Saturday`(
            dateString: String,
            expectedNextSaturday: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedSaturday = expectedNextSaturday.toLocalDateOrNull()
            val actualNextWeekendItem = dateTimeHelper.getNextWeekendDate(date)

            if (expectedSaturday != null) {
                assertEquals(
                    expected = expectedSaturday,
                    actual = actualNextWeekendItem.localDate,
                    message = "For date $dateString, 'this weekend' should be $expectedSaturday."
                )
            } else {
                assertNull(
                    actualNextWeekendItem,
                    "For date $dateString, 'this weekend' should be null."
                )
            }
        }
    }

    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getNextWeekDate")
    inner class GetInOneMonthDate {
        /**
         * Tests the [DateTimeHelperImpl.getInOneMonthDate] method.
         * Ensures that for any given [LocalDate], the method consistently returns
         * an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.InOneMonth] object representing the date exactly one month later.
         * This function is expected to always return a non-null value.
         *
         * @param dateString The input date in "YYYY-MM-DD" format.
         * @param expectedInOneMonthDate The expected date one month later in "YYYY-MM-DD" format.
         */
        @ParameterizedTest(name = "From {0}, 'in one month date' should be {1}")
        @CsvSource(
            value = [
                "2024-12-30, 2025-01-30",
                "2025-01-04, 2025-02-04",
                "2025-01-05, 2025-02-05",
                "2025-06-02, 2025-07-02",
                "2025-06-03, 2025-07-03",
                "2025-06-04, 2025-07-04",
                "2025-06-05, 2025-07-05",
                "2025-06-06, 2025-07-06",
                "2025-06-07, 2025-07-07",
                "2025-06-08, 2025-07-08",
                "2025-06-15, 2025-07-15",
            ]
        )
        fun `getNextWeekDate returns next Monday`(
            dateString: String,
            expectedInOneMonthDate: String?,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedNextWeek = expectedInOneMonthDate.toLocalDateOrNull()
            val actualNextDateItem = dateTimeHelper.getInOneMonthDate(date)

            assertEquals(
                expected = expectedNextWeek,
                actual = actualNextDateItem.localDate,
                message = "For date $dateString, 'next weekDate' should be $expectedNextWeek."
            )
            assertInstanceOf<UpcomingDate.InOneMonth>(actualNextDateItem)

        }
    }


    @Nested
    @DisplayName("Test suite for DateTimeHelperImpl.getUpcomingDates")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUpcomingDatesTests {
        /**
         * Tests the [DateTimeHelperImpl.getUpcomingDates] method.
         * This is an integration test that verifies the correct compilation and combination
         * of all internal upcoming date calculation helpers into a single, sorted list
         * based on various input dates (different days of the week).
         *
         * The test cases are derived from the specific filtering logic within each
         * internal helper function (e.g., `getTomorrowDate` returning null on Sunday,
         * `getThisWeekendDate` returning null on Friday/Saturday/Sunday, etc.).
         *
         * @param currentDate The [LocalDate] for the current day being tested.
         * @param expectedUpcomingDates The [List] of [UpcomingDate] objects expected to be returned,
         * already sorted by their [LocalDate].
         */
        @ParameterizedTest(name = "When date is {0} ({1}), Upcoming Dates should be correct")
        @MethodSource("provideUpcomingDateTestCases")
        fun `getUpcomingDates returns correct list based on current day`(
            currentDate: LocalDate,
            expectedUpcomingDates: List<UpcomingDate>,
        ) = runTest {
            val actualUpcomingDates =
                dateTimeHelper.getUpcomingDates(localDate = currentDate).first()

            assertEquals(
                expectedUpcomingDates.size, actualUpcomingDates.size,
                "List size mismatch for ${currentDate.dayOfWeek} (${currentDate})."
            )
            assertEquals(
                expectedUpcomingDates, actualUpcomingDates,
                "Upcoming dates mismatch for ${currentDate.dayOfWeek} (${currentDate})."
            )
        }

        fun provideUpcomingDateTestCases(): Stream<Arguments> {
            val monday = LocalDate.parse("2025-06-02")
            val tuesday = LocalDate.parse("2025-06-03")
            val wednesday = LocalDate.parse("2025-06-04")
            val thursday = LocalDate.parse("2025-06-05")
            val friday = LocalDate.parse("2025-06-06")
            val saturday = LocalDate.parse("2025-06-07")
            val sunday = LocalDate.parse("2025-06-08")


            return Stream.of(
                Arguments.of(
                    monday,
                    listOf(
                        UpcomingDate.Today(monday),
                        UpcomingDate.Tomorrow(monday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingWeekend(saturday),
                        UpcomingDate.UpcomingMonday(monday.nextMonday),
                        UpcomingDate.NextWeekend(monday.nextSaturday),
                        UpcomingDate.InOneMonth(monday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    tuesday,
                    listOf(
                        UpcomingDate.Today(tuesday),
                        UpcomingDate.Tomorrow(tuesday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingWeekend(saturday),
                        UpcomingDate.UpcomingMonday(tuesday.nextMonday),
                        UpcomingDate.NextWeekend(tuesday.nextSaturday),
                        UpcomingDate.InOneMonth(tuesday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    wednesday,
                    listOf(
                        UpcomingDate.Today(wednesday),
                        UpcomingDate.Tomorrow(wednesday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingWeekend(saturday),
                        UpcomingDate.UpcomingMonday(wednesday.nextMonday),
                        UpcomingDate.NextWeekend(wednesday.nextSaturday),
                        UpcomingDate.InOneMonth(wednesday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    thursday,
                    listOf(
                        UpcomingDate.Today(thursday),
                        UpcomingDate.Tomorrow(thursday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingWeekend(saturday),
                        UpcomingDate.UpcomingMonday(thursday.nextMonday),
                        UpcomingDate.NextWeekend(thursday.nextSaturday),
                        UpcomingDate.InOneMonth(thursday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    friday,
                    listOf(
                        UpcomingDate.Today(friday),
                        UpcomingDate.Tomorrow(friday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingMonday(friday.nextMonday),
                        UpcomingDate.NextWeekend(friday.nextSaturday),
                        UpcomingDate.InOneMonth(friday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    saturday,
                    listOf(
                        UpcomingDate.Today(saturday),
                        UpcomingDate.Tomorrow(saturday.plus(DatePeriod(days = 1))),
                        UpcomingDate.UpcomingMonday(saturday.nextMonday),
                        UpcomingDate.NextWeekend(saturday.nextSaturday),
                        UpcomingDate.InOneMonth(saturday.inOneMonth)
                    ).sortedBy { it.localDate }
                ),

                Arguments.of(
                    sunday,
                    listOf(
                        UpcomingDate.Today(sunday),
                        UpcomingDate.UpcomingMondayInOneWeek(sunday.upcomingMondayInOneWeek),
                        UpcomingDate.NextWeekend(sunday.nextSaturday),
                        UpcomingDate.UpcomingMonday(sunday.nextMonday),
                        UpcomingDate.InOneMonth(sunday.inOneMonth)
                    ).sortedBy { it.localDate }
                )
            )
        }
    }


}