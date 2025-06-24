package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertInstanceOf
import java.io.IOException

/**
 * Sammelt Elemente aus einem PagingData-Flow in eine Liste für Testzwecke.
 *
 * @param testScope Der TestScope (von runTest), um Coroutinen zu starten und advanceUntilIdle() zu nutzen.
 * @return Eine Liste der gesammelten Elemente.
 */


@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetPagedMonthUseCaseImplTest {

    private lateinit var mockDateTimeHelper: DateTimeHelper
    private lateinit var initialLocalDate: LocalDate
    private lateinit var pagingSource: CalendarMonthPagingSource

    @BeforeEach
    fun setUp() {
        mockDateTimeHelper = mockk()
        initialLocalDate = LocalDate(2025, Month.JULY, 15)
        pagingSource = CalendarMonthPagingSource(mockDateTimeHelper, initialLocalDate)

        coEvery { mockDateTimeHelper.getCalendarMonth(any()) } answers {
            val date = it.invocation.args[0] as LocalDate
            CalendarMonthModel(
                monthNumber = date.monthNumber,
                year = date.year,
                weeks = emptyList(),
                midOfMonth = LocalDate(
                    year = date.year,
                    monthNumber = date.monthNumber,
                    dayOfMonth = 15
                )
            )
        }
    }

    @AfterAll
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load - Refresh - returns first page of data correctly`() = runTest {
        val expectedMonths = listOf(
            CalendarMonthModel(
                monthNumber = 7,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(year = 2025, monthNumber = 7, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 8,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(year = 2025, monthNumber = 8, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 9,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(year = 2025, monthNumber = 9, dayOfMonth = 15)
            )
        )

        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    7,
                    15
                )
            )
        } returns expectedMonths[0]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    8,
                    15
                )
            )
        } returns expectedMonths[1]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    9,
                    15
                )
            )
        } returns expectedMonths[2]

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        val expectedLoadResult = PagingSource.LoadResult.Page(
            data = expectedMonths,
            prevKey = null,
            nextKey = 1
        )
        assertEquals(expectedLoadResult, loadResult)

        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 7, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 8, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 9, 15)) }
    }

    @Test
    fun `load - Append - returns next page of data correctly`() = runTest {
        val expectedMonths = listOf(
            CalendarMonthModel(
                monthNumber = 10,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 10, year = 2025, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 11,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 11, year = 2025, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 12,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 12, year = 2025, dayOfMonth = 15)
            )
        )
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    10,
                    15
                )
            )
        } returns expectedMonths[0]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    11,
                    15
                )
            )
        } returns expectedMonths[1]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    12,
                    15
                )
            )
        } returns expectedMonths[2]

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 1,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        val expectedLoadResult = PagingSource.LoadResult.Page(
            data = expectedMonths,
            prevKey = 0,
            nextKey = 2
        )
        assertEquals(expectedLoadResult, loadResult)

        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 10, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 11, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 12, 15)) }
    }

    @Test
    fun `load - Prepend - returns previous page of data correctly`() = runTest {
        val expectedMonths = listOf(
            CalendarMonthModel(
                monthNumber = 4,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 4, year = 2025, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 5,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 5, year = 2025, dayOfMonth = 15)
            ),
            CalendarMonthModel(
                monthNumber = 6,
                year = 2025,
                weeks = emptyList(),
                midOfMonth = LocalDate(monthNumber = 6, year = 2025, dayOfMonth = 15)
            )
        )
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    4,
                    15
                )
            )
        } returns expectedMonths[0]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    5,
                    15
                )
            )
        } returns expectedMonths[1]
        coEvery {
            mockDateTimeHelper.getCalendarMonth(
                LocalDate(
                    2025,
                    6,
                    15
                )
            )
        } returns expectedMonths[2]

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Prepend(
                key = -1,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        val expectedLoadResult = PagingSource.LoadResult.Page(
            data = expectedMonths,
            prevKey = null,
            nextKey = 0
        )
        assertEquals(expectedLoadResult, loadResult)

        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 4, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 5, 15)) }
        verify(exactly = 1) { mockDateTimeHelper.getCalendarMonth(LocalDate(2025, 6, 15)) }
    }

    @Test
    fun `load - handles error from DateTimeHelper - returns LoadResult_Error`() = runTest {
        val testException = IOException("Failed to fetch month data from helper")
        coEvery { mockDateTimeHelper.getCalendarMonth(any()) } throws testException

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertInstanceOf<PagingSource.LoadResult.Error<Int, CalendarMonthModel>>(loadResult)

        val errorResult = loadResult as PagingSource.LoadResult.Error<Int, CalendarMonthModel>

        assertEquals(
            testException,
            errorResult.throwable
        )

        verify(atLeast = 1) { mockDateTimeHelper.getCalendarMonth(any()) }
    }

    @Test
    fun `getRefreshKey - returns current key for anchor position`() {
        val anchorKey = 5
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(
                        CalendarMonthModel(
                            monthNumber = 1,
                            year = 2000,
                            weeks = emptyList(),
                            midOfMonth = LocalDate(year = 2000, monthNumber = 1, dayOfMonth = 15)
                        )
                    ),
                    prevKey = anchorKey - 1,
                    nextKey = anchorKey + 1
                )
            ),
            anchorPosition = 1,
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(anchorKey, refreshKey)
    }

    @Test
    fun `getRefreshKey - returns null if no anchor position`() {
        val state = PagingState<Int, CalendarMonthModel>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(null, refreshKey)
    }

}