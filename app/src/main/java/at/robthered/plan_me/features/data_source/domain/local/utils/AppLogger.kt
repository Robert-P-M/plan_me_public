package at.robthered.plan_me.features.data_source.domain.local.utils

interface AppLogger {
    fun v(tag: String, msg: String, throwable: Throwable? = null)
    fun d(tag: String, msg: String, throwable: Throwable? = null)
    fun i(tag: String, msg: String, throwable: Throwable? = null)
    fun w(tag: String, msg: String, throwable: Throwable? = null)
    fun e(tag: String, msg: String, throwable: Throwable? = null)
    fun wtf(tag: String, msg: String, throwable: Throwable? = null)

    fun v(tag: String, msg: String) = v(tag, msg, null)
    fun d(tag: String, msg: String) = d(tag, msg, null)
    fun i(tag: String, msg: String) = i(tag, msg, null)
    fun w(tag: String, msg: String) = w(tag, msg, null)
    fun e(tag: String, msg: String) = e(tag, msg, null)
    fun wtf(tag: String, msg: String) = wtf(tag, msg, null)
}