package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.midOfMonth
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class MockDateTimeHelper : DateTimeHelper {
    override fun getCalendarMonth(localDate: LocalDate): CalendarMonthModel {
        return CalendarMonthModel(
            monthNumber = localDate.monthNumber,
            year = localDate.year,
            weeks = emptyList(),
            midOfMonth = localDate.midOfMonth
        )
    }

    override fun getUpcomingDates(localDate: LocalDate): Flow<List<UpcomingDate>> {
        return flowOf(emptyList())
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarMonthPagingSourceTest {
    private lateinit var mockDateTimeHelper: MockDateTimeHelper
    private lateinit var initialLocalDate: LocalDate
    private lateinit var pagingSource: CalendarMonthPagingSource


    @BeforeEach
    fun setUp() {
        mockDateTimeHelper = MockDateTimeHelper()
        initialLocalDate = LocalDate(2025, Month.JULY, 15)

        pagingSource = CalendarMonthPagingSource(mockDateTimeHelper, initialLocalDate)
    }

    @Test
    fun `CalendarMonthPagingSource loads expected data`() = runTest {
        val testDate = LocalDate(2025, 6, 1)
        val pagingSource = CalendarMonthPagingSource(
            dateTimeHelper = mockDateTimeHelper,
            initialLocalDate = testDate
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val data = (result as PagingSource.LoadResult.Page).data
        assertEquals(3, data.size)
    }

    @Test
    fun `loads first page with 3 months starting from initial date`() = runTest {
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val data = (result as PagingSource.LoadResult.Page).data
        assertEquals(3, data.size)
        assertEquals(initialLocalDate.monthNumber, data[0].monthNumber)
        assertEquals(initialLocalDate.year, data[0].year)
    }

    @Test
    fun `loads second page with correct month offset`() = runTest {
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val data = (result as PagingSource.LoadResult.Page).data
        val expectedFirstMonth = initialLocalDate.plus(period = DatePeriod(months = 3))
        assertEquals(expectedFirstMonth.monthNumber, data[0].monthNumber)
        assertEquals(expectedFirstMonth.year, data[0].year)
    }

    @Test
    fun `returns correct prevKey and nextKey`() = runTest {
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `getRefreshKey returns correct key based on anchor position`() {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(
                        CalendarMonthModel(
                            monthNumber = 1,
                            year = 2025,
                            midOfMonth = LocalDate(year = 2025, monthNumber = 1, dayOfMonth = 15),
                            weeks = emptyList()
                        ),
                        CalendarMonthModel(
                            monthNumber = 2,
                            year = 2025,
                            midOfMonth = LocalDate(year = 2025, monthNumber = 2, dayOfMonth = 15),
                            weeks = emptyList()
                        ),
                        CalendarMonthModel(
                            monthNumber = 3,
                            year = 2025,
                            midOfMonth = LocalDate(year = 2025, monthNumber = 3, dayOfMonth = 15),
                            weeks = emptyList()
                        )
                    ),
                    prevKey = null,
                    nextKey = 1,
                    itemsBefore = 0,
                    itemsAfter = 3
                )
            ),
            anchorPosition = 1,
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )
        val refreshKey = pagingSource.getRefreshKey(state)
        assertEquals(0, refreshKey)
    }

}