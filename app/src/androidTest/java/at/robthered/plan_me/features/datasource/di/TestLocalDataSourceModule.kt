package at.robthered.plan_me.features.datasource.di

import androidx.room.Room
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.di.localDataSourceModule
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.datasource.data.local.executor.TestSafeDatabaseExecutor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val androidTestDataSourceModule = module {
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    single<SafeDatabaseExecutor> {
        TestSafeDatabaseExecutor()
    }

    includes(localDataSourceModule)

}