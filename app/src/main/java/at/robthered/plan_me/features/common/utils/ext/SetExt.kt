package at.robthered.plan_me.features.common.utils.ext

fun Set<Long>.toggle(id: Long): Set<Long> = if (contains(id)) minus(id) else plus(id)