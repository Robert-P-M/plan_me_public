package at.robthered.plan_me.features.data_source.presentation.ext

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.presentation.UiText

fun RoomDatabaseError.toUiText(): UiText {
    return when (this) {
        RoomDatabaseError.DISK_IO_ERROR -> UiText
            .StringResource(
                id = R.string.room_database_error_disk_io_error
            )

        RoomDatabaseError.CONSTRAINT_VIOLATION -> UiText
            .StringResource(
                id = R.string.room_database_error_constraint_violation
            )

        RoomDatabaseError.CORRUPT_DATABASE -> UiText
            .StringResource(
                id = R.string.room_database_error_corrupt_database
            )

        RoomDatabaseError.CANT_OPEN -> UiText
            .StringResource(
                id = R.string.room_database_error_cant_open
            )

        RoomDatabaseError.ILLEGAL_STATE -> UiText
            .StringResource(
                id = R.string.room_database_error_illegal_state
            )

        RoomDatabaseError.UNKNOWN -> UiText
            .StringResource(
                id = R.string.room_database_error_unknown
            )

        RoomDatabaseError.NO_TASK_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_found
            )

        RoomDatabaseError.NO_SECTION_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_section_found
            )

        RoomDatabaseError.NO_TASK_ARCHIVED_HISTORY_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_archived_history_found
            )

        RoomDatabaseError.NO_TASK_COMPLETED_HISTORY_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_completed_history_found
            )

        RoomDatabaseError.NO_TASK_DESCRIPTION_HISTORY_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_description_history_found
            )

        RoomDatabaseError.NO_TASK_PRIORITY_HISTORY_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_priority_history_found
            )

        RoomDatabaseError.NO_TASK_TITLE_HISTORY_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_title_history_found
            )

        RoomDatabaseError.NO_HASHTAG_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_hashtag_found
            )

        RoomDatabaseError.NO_TASK_HASHTAG_CROSS_REF_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_hashtag_cross_ref_found
            )

        RoomDatabaseError.NO_TASK_SCHEDULE_EVENT_FOUND -> UiText
            .StringResource(
                id = R.string.room_database_error_no_task_schedule_event_found
            )
    }
}