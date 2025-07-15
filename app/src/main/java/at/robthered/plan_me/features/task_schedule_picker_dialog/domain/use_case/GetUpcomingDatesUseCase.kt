package at.robthered.plan_me.features.task_schedule_picker_dialog.domain.use_case

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GetUpcomingDatesUseCase {
    operator fun invoke(localDate: LocalDate): Flow<List<UpcomingDate>>
}