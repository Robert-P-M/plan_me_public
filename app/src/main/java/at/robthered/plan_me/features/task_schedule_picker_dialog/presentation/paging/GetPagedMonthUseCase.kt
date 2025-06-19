package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging

import androidx.paging.PagingData
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GetPagedMonthUseCase {
    operator fun invoke(initialLocalDate: LocalDate): Flow<PagingData<CalendarMonthModel>>
}