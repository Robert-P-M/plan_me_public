package at.robthered.plan_me

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestPlanMeApplication::class.java.name, context)
    }
}