package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetPagedMonthUseCaseImpl(
    private val dateTimeHelper: DateTimeHelper,
) : GetPagedMonthUseCase {
    override operator fun invoke(initialLocalDate: LocalDate): Flow<PagingData<CalendarMonthModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CalendarMonthPagingSource(
                    dateTimeHelper = dateTimeHelper,
                    initialLocalDate = initialLocalDate
                )
            }
        ).flow
    }
}