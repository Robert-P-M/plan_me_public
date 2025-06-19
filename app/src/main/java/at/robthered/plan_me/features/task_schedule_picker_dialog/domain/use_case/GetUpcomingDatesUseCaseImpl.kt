package at.robthered.plan_me.features.task_schedule_picker_dialog.domain.use_case

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetUpcomingDatesUseCaseImpl(
    private val dateTimeHelper: DateTimeHelper,
) : GetUpcomingDatesUseCase {
    override fun invoke(localDate: LocalDate): Flow<List<UpcomingDate>> {
        return dateTimeHelper.getUpcomingDates(localDate)
    }
}