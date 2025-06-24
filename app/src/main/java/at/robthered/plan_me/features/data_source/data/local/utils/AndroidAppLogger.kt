package at.robthered.plan_me.features.data_source.data.local.utils

import android.util.Log
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger

class AndroidAppLogger : AppLogger {

    override fun v(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.v(tag, msg, throwable)
        } else {
            Log.v(tag, msg)
        }
    }

    override fun d(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.d(tag, msg, throwable)
        } else {
            Log.d(tag, msg)
        }
    }

    override fun i(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.i(tag, msg, throwable)
        } else {
            Log.i(tag, msg)
        }
    }

    override fun w(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.w(tag, msg, throwable)
        } else {
            Log.w(tag, msg)
        }
    }

    override fun e(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag, msg, throwable)
        } else {
            Log.e(tag, msg)
        }
    }

    override fun wtf(tag: String, msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.wtf(tag, msg, throwable)
        } else {
            Log.wtf(tag, msg, throwable)
        }
    }

}