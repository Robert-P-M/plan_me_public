package at.robthered.plan_me.features.task_schedule_picker_dialog.domain

import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

/**
 * Test suite for the [LocalDate] extension functions that perform pure date calculations.
 * @see at.robthered.plan_me.features.task_schedule_picker_dialog.domain.LocalDateCalculatorExtText
 */
@DisplayName("Tests for LocalDateCalculatorExt")
class LocalDateCalculatorExtText {

    @Nested
    @DisplayName("Tests for 'lastDayOfMonth'")
    inner class LastDayOfMonthTest {
        /**
         * Tests the [LocalDate.lastDayOfMonth] extension.
         * Ensures that for various dates, including leap years, the correct
         * last day of the month is always returned.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedLastDayString The expected last day of the month in "YYYY-MM-DD" format.
         */
        @ParameterizedTest(name = "The last day of month for {0} should be {1}")
        @CsvSource(
            value = [
                "2025-01-15, 2025-01-31",
                "2025-02-01, 2025-02-28",
                "2024-02-10, 2024-02-29",
                "2025-12-25, 2025-12-31"
            ]
        )
        fun `lastDayOfMonth should return the correct last day of the month`(
            dateString: String,
            expectedLastDayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedLastDay = LocalDate.parse(expectedLastDayString)
            assertEquals(expectedLastDay, date.lastDayOfMonth)
        }
    }


    @Nested
    @DisplayName("Tests for 'mondayOfWeek'")
    inner class MondayOfWeekTest {
        /**
         * Tests the [LocalDate.mondayOfWeek] extension.
         * Ensures that for any day of a week (including year-end transitions),
         * the correct Monday of that week is returned.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedMondayString The expected Monday of the same week.
         */
        @ParameterizedTest(name = "For date {0}, the Monday of the week should be {1}")
        @CsvSource(
            value = [
                "2025-06-02, 2025-06-02",
                "2025-06-04, 2025-06-02",
                "2025-06-08, 2025-06-02",
                "2025-01-05, 2024-12-30"
            ]
        )
        fun `mondayOfWeek should return the correct Monday for any day of the week`(
            dateString: String,
            expectedMondayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMonday = LocalDate.parse(expectedMondayString)
            assertEquals(expectedMonday, date.mondayOfWeek)
        }
    }

    @Nested
    @DisplayName("Tests for 'sundayOfWeek'")
    inner class SundayOfWeekTest {
        /**
         * Tests the [LocalDate.sundayOfWeek] extension.
         * Ensures that for any day of a week, the correct Sunday is returned.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedSundayString The expected Sunday of the same week.
         */
        @ParameterizedTest(name = "Date {0} should have sundayOfWeek {1}")
        @CsvSource(
            value = [
                "2025-06-02, 2025-06-08",
                "2025-06-08, 2025-06-08",
                "2024-12-30, 2025-01-05"
            ]
        )
        fun `sundayOfWeek should return the correct Sunday for any day of the week`(
            dateString: String,
            expectedSundayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedSunday = LocalDate.parse(expectedSundayString)
            assertEquals(
                expected = expectedSunday,
                actual = date.sundayOfWeek
            )
        }
    }


    @Nested
    @DisplayName("Tests for 'nextMonday'")
    inner class NextMondayTest {
        /**
         * Tests the [LocalDate.nextMonday] extension.
         * This should always return the Monday of the *following* week.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedMondayString The expected Monday of the next week.
         */
        @ParameterizedTest(name = "From {0}, the next Monday should be {1}")
        @CsvSource(
            "2025-06-09, 2025-06-16",
            "2025-06-15, 2025-06-16",
            "2024-12-30, 2025-01-06"
        )
        fun `nextMonday should return the Monday of the following week`(
            dateString: String,
            expectedMondayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMonday = LocalDate.parse(expectedMondayString)
            assertEquals(
                expected = expectedMonday,
                actual = date.nextMonday
            )
        }
    }

    @Nested
    @DisplayName("Tests for 'saturdayOfWeek'")
    inner class SaturdayOfWeekTest {
        /**
         * Tests the [LocalDate.saturdayOfWeek] extension.
         * Ensures for any given day, the correct Saturday of that week is returned.
         *
         * @param dateString The start date in "YYYY-MM-DD" format.
         * @param expectedSaturdayString The expected Saturday of the same week.
         */
        @ParameterizedTest(name = "From {0}, the Saturday of the week should be {1}")
        @CsvSource(
            "2025-06-09, 2025-06-14",
            "2025-06-14, 2025-06-14",
            "2025-06-15, 2025-06-14",
            "2024-12-30, 2025-01-04"
        )
        fun `saturdayOfWeek should return the correct Saturday for any day of the week`(
            dateString: String,
            expectedSaturdayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedSaturday = LocalDate.parse(expectedSaturdayString)
            assertEquals(
                expected = expectedSaturday,
                actual = date.saturdayOfWeek
            )
        }
    }


    @Nested
    @DisplayName("Tests for 'isoWeekYear'")
    inner class IsoWeekYearTest {
        /**
         * Tests the [LocalDate.isoWeekYear] extension.
         * Ensure for any given day, the correct isoWeekYear is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedYear The expected year.
         *
         */
        @ParameterizedTest(name = "Date {0} should be ISO week year {1}")
        @CsvSource(
            "2024-12-21, 2024",
            "2024-12-29, 2024",
            "2024-12-30, 2025",
            "2024-12-31, 2025",
            "2025-01-01, 2025",
            "2025-01-02, 2025",
            "2025-01-03, 2025",
            "2025-01-04, 2025",
            "2025-01-05, 2025",
            "2025-06-04, 2025",
            "2025-12-28, 2025",
            "2025-12-29, 2026",
            "2025-12-31, 2026",
            "2026-01-01, 2026",
            "2026-01-05, 2026",
        )
        fun `test ISO week year calculation`(
            dateString: String,
            expectedYear: Int,
        ) {
            val date = LocalDate.parse(dateString)
            assertEquals(expected = expectedYear, actual = date.isoWeekYear)
        }
    }

    @Nested
    @DisplayName("Tests for 'isoWeekNumber'")
    inner class IsoWeekNumberTest {
        /**
         * Tests the [LocalDate.isoWeekNumber] extension.
         * Ensure for any given day, the correct isoWeekYear is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedWeek The expected year.
         *
         */
        @ParameterizedTest(name = "Date {0} should be ISO week {1}")
        @CsvSource(
            "2024-12-21, 51",
            "2024-12-22, 51",
            "2024-12-23, 52",
            "2024-12-24, 52",
            "2024-12-25, 52",
            "2024-12-26, 52",
            "2024-12-27, 52",
            "2024-12-28, 52",
            "2024-12-29, 52",
            "2024-12-30, 1",
            "2024-12-31, 1",
            "2025-01-01, 1",
            "2025-01-02, 1",
            "2025-01-03, 1",
            "2025-01-04, 1",
            "2025-01-05, 1",
            "2025-01-06, 2",
            "2025-01-07, 2",
            "2025-12-27, 52",
            "2025-12-28, 52",
            "2025-12-29, 1",
            "2025-12-30, 1",
            "2025-12-31, 1",
            "2026-01-01, 1",
            "2026-01-02, 1",
            "2026-01-03, 1",
            "2026-01-04, 1",
            "2026-01-05, 2",
            "2026-01-06, 2",
        )
        fun `test ISO week number calculation`(
            dateString: String,
            expectedWeek: Int,
        ) {
            val date = LocalDate.parse(dateString)
            assertEquals(
                expected = expectedWeek,
                actual = date.isoWeekNumber
            )
        }
    }

    @Nested
    @DisplayName("Test for mondayOfWeek and sundayOfWeek boundary")
    inner class WeekBoundariesCombinedTest {
        /**
         * Tests the [LocalDate.mondayOfWeek] extension.
         * Tests the [LocalDate.sundayOfWeek] extension.
         * Ensure for any given day, the correct monday and sunday is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedMondayString The expected monday.
         * @param expectedSundayString The expected sunday.
         *
         */
        @ParameterizedTest(name = "Date {0} should have mondayOfWeek {1} and sundayOfWeek {2}")
        @CsvSource(
            "2025-06-02, 2025-06-02, 2025-06-08",
            "2025-06-03, 2025-06-02, 2025-06-08",
            "2025-06-04, 2025-06-02, 2025-06-08",
            "2025-06-05, 2025-06-02, 2025-06-08",
            "2025-06-06, 2025-06-02, 2025-06-08",
            "2025-06-07, 2025-06-02, 2025-06-08",
            "2025-06-08, 2025-06-02, 2025-06-08",
            "2024-12-30, 2024-12-30, 2025-01-05",
            "2025-01-01, 2024-12-30, 2025-01-05",
            "2025-01-05, 2024-12-30, 2025-01-05"
        )
        fun `test week boundary calculations`(
            dateString: String,
            expectedMondayString: String,
            expectedSundayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMonday = LocalDate.parse(expectedMondayString)
            val expectedSunday = LocalDate.parse(expectedSundayString)

            assertEquals(
                expected = expectedMonday,
                actual = date.mondayOfWeek
            )
            assertEquals(
                expected = expectedSunday,
                actual = date.sundayOfWeek
            )
        }
    }

    @Nested
    @DisplayName("Test isoWeekNumber and isoWeekYear")
    inner class IsoWeekNumberAndIsoWeekYearTest {
        /**
         * Tests the [LocalDate.isoWeekNumber] extension.
         * Tests the [LocalDate.isoWeekYear] extension.
         * Ensure for any given day, the correct ISO week and ISO year is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedWeek The expected ISO week.
         * @param expectedYear The expected ISO year.
         *
         */
        @ParameterizedTest(name = "Date {0} should be ISO week {1} (ISO year {2})")
        @CsvSource(
            "2024-12-21, 51, 2024",
            "2024-12-22, 51, 2024",
            "2024-12-23, 52, 2024",
            "2024-12-24, 52, 2024",
            "2024-12-25, 52, 2024",
            "2024-12-26, 52, 2024",
            "2024-12-27, 52, 2024",
            "2024-12-28, 52, 2024",
            "2024-12-29, 52, 2024",
            "2024-12-30, 1, 2025",
            "2024-12-31, 1, 2025",
            "2025-01-01, 1, 2025",
            "2025-01-02, 1, 2025",
            "2025-01-03, 1, 2025",
            "2025-01-04, 1, 2025",
            "2025-01-05, 1, 2025",
            "2025-01-06, 2, 2025",
            "2025-01-07, 2, 2025",
            "2025-12-27, 52, 2025",
            "2025-12-28, 52, 2025",
            "2025-12-29, 1, 2026",
            "2025-12-30, 1, 2026",
            "2025-12-31, 1, 2026",
            "2026-01-01, 1, 2026",
            "2026-01-02, 1, 2026",
            "2026-01-03, 1, 2026",
            "2026-01-04, 1, 2026",
            "2026-01-05, 2, 2026",
            "2026-01-06, 2, 2026",
        )
        fun `test ISO week numbers`(
            dateString: String,
            expectedWeek: Int,
            expectedYear: Int,
        ) {
            val date = LocalDate.parse(dateString)

            assertEquals(
                expected = expectedWeek,
                actual = date.isoWeekNumber
            )
            assertEquals(
                expected = expectedYear,
                actual = date.isoWeekYear
            )
        }
    }

    @Nested
    @DisplayName("Tests for midOfMonth")
    inner class MidOfMonthTest {
        /**
         * Tests the [LocalDate.midOfMonth] extension.
         * Ensure for any given day, the correct mid of month is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param midOfMonth The date in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "Date {0} should have dayOfMonth=15")
        @CsvSource(
            "2024-12-22, 15",
            "2024-12-23, 15",
            "2024-12-24, 15",
            "2024-12-25, 15",
            "2024-12-26, 15",
            "2024-12-27, 15",
            "2024-12-28, 15",
            "2024-12-29, 15",
            "2024-12-30, 15",
            "2024-12-31, 15",
            "2025-01-01, 15",
            "2025-01-02, 15",
            "2025-01-03, 15",
            "2025-01-04, 15",
            "2025-01-05, 15",
            "2025-01-06, 15",
            "2025-01-07, 15",
            "2025-12-27, 15",
            "2025-12-28, 15",
            "2025-12-29, 15",
            "2025-12-30, 15",
            "2025-12-31, 15",
            "2026-01-01, 15",
            "2026-01-02, 15",
            "2026-01-03, 15",
            "2026-01-04, 15",
            "2026-01-05, 15",
            "2026-01-06, 15",
        )
        fun `test mid of month`(
            dateString: String,
            midOfMonth: String,
        ) {
            val date = LocalDate.parse(dateString)
            assertEquals(
                expected = midOfMonth.toInt(),
                actual = date.midOfMonth.dayOfMonth
            )
        }
    }


    @Nested
    @DisplayName("Tests upcomingMondayInOneWeek")
    inner class UpcomingMondayInOneWeekText {
        /**
         * Tests the [LocalDate.upcomingMondayInOneWeek] extension.
         * Ensure for any given day, the correct monday is returned
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedMondayString The expected monday date in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "From {0}, the upcoming Monday in one week should be {1}")
        @CsvSource(
            value = [
                "2025-06-09, 2025-06-23",
                "2025-06-10, 2025-06-23",
                "2025-06-11, 2025-06-23",
                "2025-06-12, 2025-06-23",
                "2025-06-13, 2025-06-23",
                "2025-06-14, 2025-06-23",
                "2025-06-15, 2025-06-23",
                "2025-06-16, 2025-06-30",
                "2025-06-17, 2025-06-30",
                "2025-06-18, 2025-06-30",
                "2024-12-29, 2025-01-06",
                "2024-12-30, 2025-01-13",
                "2024-12-31, 2025-01-13",
                "2025-01-01, 2025-01-13",
                "2025-01-02, 2025-01-13",
                "2025-01-05, 2025-01-13"
            ]

        )
        fun `upcomingMondayInOneWeek returns the next Monday + 1 Week including current day if it's Monday`(
            dateString: String,
            expectedMondayString: String,
        ) {
            val date = LocalDate.parse(dateString)
            val expectedMonday = LocalDate.parse(expectedMondayString)

            val actualMonday = date.upcomingMondayInOneWeek

            assertEquals(
                expected = expectedMonday,
                actual = actualMonday,
                message = "From $dateString, the next Monday + 1 week should be $expectedMonday."
            )
        }
    }

    @Nested
    @DisplayName("Tests for tomorrow")
    inner class LocalDateTomorrowTest {
        /**
         * Tests the [LocalDate.tomorrow] extension.
         * Ensure for any given day, the correct output is the date of tomorrow.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedTomorrowString The expected tomorrow date in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "From {0}, tomorrow should be {1}")
        @CsvSource(
            value = [
                "2024-02-28, 2024-02-29",
                "2024-02-29, 2024-03-01",
                "2025-06-09, 2025-06-10",
                "2025-06-10, 2025-06-11",
                "2025-06-11, 2025-06-12",
                "2025-06-12, 2025-06-13",
                "2025-06-13, 2025-06-14",
                "2025-06-14, 2025-06-15",
                "2025-06-15, 2025-06-16",
                "2025-06-16, 2025-06-17",
                "2025-06-17, 2025-06-18",
                "2025-06-18, 2025-06-19",
                "2024-12-29, 2024-12-30",
                "2024-12-30, 2024-12-31",
                "2024-12-31, 2025-01-01",
                "2025-01-01, 2025-01-02",
                "2025-01-02, 2025-01-03",
                "2025-01-05, 2025-01-06",
            ]

        )
        fun `LocalDate_tomorrow returns the next day`(
            dateString: String,
            expectedTomorrowString: String,
        ) {
            val today = LocalDate.parse(dateString)
            val tomorrow = LocalDate.parse(expectedTomorrowString)

            val actualTomorrow = today.tomorrow

            assertEquals(
                expected = tomorrow,
                actual = actualTomorrow,
                message = "From $dateString, the next day should be $tomorrow."
            )
        }
    }

    @Nested
    @DisplayName("Tests for saturdayOfWeek")
    inner class LocalDateSaturdayOfWeekTest {
        /**
         * Tests the [LocalDate.saturdayOfWeek] extension.
         * Ensure for any given day, the correct output is the date of saturday.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedSaturdayString The expected saturday date in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "From {0}, the saturday should be {1}")
        @CsvSource(
            value = [
                "2024-02-28, 2024-03-02",
                "2024-02-29, 2024-03-02",
                "2025-06-09, 2025-06-14",
                "2025-06-10, 2025-06-14",
                "2025-06-11, 2025-06-14",
                "2025-06-12, 2025-06-14",
                "2025-06-13, 2025-06-14",
                "2025-06-14, 2025-06-14",
                "2025-06-15, 2025-06-14",
                "2025-06-16, 2025-06-21",
                "2025-06-17, 2025-06-21",
                "2025-06-18, 2025-06-21",
                "2024-12-29, 2024-12-28",
                "2024-12-30, 2025-01-04",
                "2024-12-31, 2025-01-04",
                "2025-01-01, 2025-01-04",
                "2025-01-02, 2025-01-04",
                "2025-01-05, 2025-01-04",
            ]

        )
        fun `LocalDate_tomorrow returns the next day`(
            dateString: String,
            expectedSaturdayString: String,
        ) {
            val today = LocalDate.parse(dateString)
            val saturday = LocalDate.parse(expectedSaturdayString)

            val actualTomorrow = today.saturdayOfWeek

            assertEquals(
                expected = saturday,
                actual = actualTomorrow,
                message = "From $dateString, the next saturday should be $saturday."
            )
        }
    }

    @Nested
    @DisplayName("Tests for nextSaturday")
    inner class LocalDateNextSaturdayTest {
        /**
         * Tests the [LocalDate.saturdayOfWeek] extension.
         * Ensure for any given day, the correct output is the date of saturday.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedNextSaturdayString The expected saturday date in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "From {0}, the saturday next week should be {1}")
        @CsvSource(
            value = [
                "2024-02-28, 2024-03-09",
                "2024-02-29, 2024-03-09",
                "2024-12-29, 2025-01-04",
                "2024-12-30, 2025-01-11",
                "2024-12-31, 2025-01-11",
                "2025-01-01, 2025-01-11",
                "2025-01-02, 2025-01-11",
                "2025-01-05, 2025-01-11",
                "2025-06-09, 2025-06-21",
                "2025-06-10, 2025-06-21",
                "2025-06-11, 2025-06-21",
                "2025-06-12, 2025-06-21",
                "2025-06-13, 2025-06-21",
                "2025-06-14, 2025-06-21",
                "2025-06-15, 2025-06-21",
                "2025-06-16, 2025-06-28",
                "2025-06-17, 2025-06-28",
                "2025-06-18, 2025-06-28",
            ]

        )
        fun `LocalDate_tomorrow returns the next day`(
            dateString: String,
            expectedNextSaturdayString: String,
        ) {
            val today = LocalDate.parse(dateString)
            val nextSaturday = LocalDate.parse(expectedNextSaturdayString)

            val actualNextSaturday = today.nextSaturday

            assertEquals(
                expected = nextSaturday,
                actual = actualNextSaturday,
                message = "From $dateString, the next sunday should be $nextSaturday."
            )
        }
    }


    @Nested
    @DisplayName("Tests for isSunday")
    inner class LocalDateIsSundayTest {
        /**
         * Tests the [LocalDate.isSunday] extension.
         * Ensure for any given day, the correct output is the date of saturday.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedIsSunday The expected boolean.
         *
         */
        @ParameterizedTest(name = "Date {0} should be Sunday: {1}")
        @CsvSource(
            value = [
                "2025-06-01, true",
                "2025-06-02, false",
                "2025-06-03, false",
                "2025-06-04, false",
                "2025-06-05, false",
                "2025-06-06, false",
                "2025-06-07, false",
                "2025-06-08, true",
                "2025-06-09, false",
                "2025-01-01, false",
                "2025-01-05, true",
                "2024-12-29, true",
                "2024-12-30, false"
            ]

        )
        fun `isSunday returns true only for Sundays`(
            dateString: String,
            expectedIsSunday: Boolean,
        ) {
            val date = LocalDate.parse(dateString)
            val actualIsSunday = date.isSunday

            assertEquals(
                expected = expectedIsSunday,
                actual = actualIsSunday,
                message = "Date $dateString  isSunday: $expectedIsSunday."
            )
        }
    }

    @Nested
    @DisplayName("Tests for inOneMonth")
    inner class LocalDateInOneMonthTest {
        /**
         * Tests the [LocalDate.isSunday] extension.
         * Ensure for any given day, the correct output is the date of saturday.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedDateInOneMonth The expected date in one month in "YYYY-MM-DD" format.
         *
         */
        @ParameterizedTest(name = "Date {0} should be Sunday: {1}")
        @CsvSource(
            value = [
                "2025-06-01, 2025-07-01",
                "2025-06-02, 2025-07-02",
                "2025-06-03, 2025-07-03",
                "2025-06-04, 2025-07-04",
                "2025-06-05, 2025-07-05",
                "2025-06-06, 2025-07-06",
                "2025-06-07, 2025-07-07",
                "2025-06-08, 2025-07-08",
                "2025-06-09, 2025-07-09",
                "2025-01-01, 2025-02-01",
                "2025-01-05, 2025-02-05",
                "2024-12-29, 2025-01-29",
                "2024-12-30, 2025-01-30"
            ]

        )
        fun `inOneMonth returns date in one month`(
            dateString: String,
            expectedDateInOneMonth: String,
        ) {
            val date = LocalDate.parse(dateString)
            val actualInOneMonth = date.inOneMonth
            val expectedDate = LocalDate.parse(expectedDateInOneMonth)

            assertEquals(
                expected = expectedDate,
                actual = actualInOneMonth,
                message = "Date $dateString should have its day in on month at $expectedDateInOneMonth."
            )
        }
    }

    @Nested
    @DisplayName("Tests for String?.toLocalDateOrNull")
    inner class StringToLocalDateOrNullTest() {
        /**
         * Tests the [String.toLocalDateOrNull] extension.
         * Ensure for any given day, the correct result is date in "YYYY-MM-DD" or null.
         *
         * @param dateString The date in "YYYY-MM-DD" format.
         * @param expectedLocalDateString The expected result - date in "YYYY-MM-DD" format or null.
         *
         */
        @ParameterizedTest(name = "Parsing {0} should return {1}")
        @CsvSource(
            value = [
                "2025-01-01, 2025-01-01",
                "1999-12-31, 1999-12-31",
                "2000-02-29, 2000-02-29",
                "2025-06-05, 2025-06-05",
                "null, null",
                "NULL, null",
                "NuLl, null",
                "2025/01/01, null",
                "01-01-2025, null",
                "2025-1-1, null",
                "invalid-date, null",
                "2025-13-01, null",
                "2025-01-32, null",
                "null, null",
                "'', null",
                "'  ', null"
            ],
            nullValues = ["null"],

            )
        fun `toLocalDateOrNull returns correct LocalDate or null`(
            dateString: String?,
            expectedLocalDateString: String?,
        ) {
            val expectedLocalDate = expectedLocalDateString.toLocalDateOrNull()

            val actualLocalDate = dateString.toLocalDateOrNull()

            assertEquals(
                expected = expectedLocalDate,
                actual = actualLocalDate,
                message = "Parsing '$dateString' should match expected result."
            )
        }
    }


}