package at.robthered.plan_me.features.common.utils.ext

import kotlinx.datetime.Instant

fun Instant.toLong(): Long = this.toEpochMilliseconds()