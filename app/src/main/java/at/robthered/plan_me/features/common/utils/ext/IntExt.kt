package at.robthered.plan_me.features.common.utils.ext

fun Int.toTimeDigits(): String {
    return this.toString().padStart(2, '0')
}