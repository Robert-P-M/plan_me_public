package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time

fun Int.toTimeOfDay(): Time {
    val totalMinutes = this
    val hours = totalMinutes.div(60)
    val minutes = totalMinutes.mod(60)
    return Time(
        hours,
        minutes
    )
}

fun Int.toDurationEnd(): Time {
    val totalMinutesInDay = this.mod(24 * 60)
    val hours = totalMinutesInDay.div(60)
    val minutes = totalMinutesInDay.mod(60)
    return Time(hours, minutes)
}

fun Time.toInt(): Int {
    return this.hours.times(60) + this.minutes
}

fun Time.toText(): String {
    return this.hours.toString()
        .padStart(2, '0') + ":" + this.minutes.toString().padStart(2, '0')
}