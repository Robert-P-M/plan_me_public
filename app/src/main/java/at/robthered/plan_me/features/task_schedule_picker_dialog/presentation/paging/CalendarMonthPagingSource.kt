package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class CalendarMonthPagingSource(
    private val dateTimeHelper: DateTimeHelper,
    private val initialLocalDate: LocalDate,
) : PagingSource<Int, CalendarMonthModel>() {
    override fun getRefreshKey(state: PagingState<Int, CalendarMonthModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CalendarMonthModel> {
        return try {

            val position = params.key ?: 0
            val currentDate = initialLocalDate.plus(period = DatePeriod(months = position * 3))
            val items = (0..2).map { monthOffset ->
                val date = currentDate.plus(period = DatePeriod(months = monthOffset))
                dateTimeHelper.getCalendarMonth(date)
            }
            return LoadResult.Page(
                data = items,
                prevKey = if (position > 0) position - 1 else null,
                nextKey = position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}