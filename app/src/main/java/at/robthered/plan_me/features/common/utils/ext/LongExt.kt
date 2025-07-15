package at.robthered.plan_me.features.common.utils.ext

import kotlinx.datetime.Instant

fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}