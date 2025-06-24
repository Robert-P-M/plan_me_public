package at.robthered.plan_me

import android.app.Application
import at.robthered.plan_me.features.common.di.initKoin
import org.koin.android.ext.koin.androidContext

class PlanMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@PlanMeApplication)
        }
    }
}