package at.robthered.plan_me.features.data_source.di

import androidx.room.Room
import at.robthered.plan_me.features.data_source.data.local.database.AppDatabase
import at.robthered.plan_me.features.data_source.data.local.executor.SafeDatabaseExecutorImpl
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val productionLocalDataSourceModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        )
            .build()
    }

    single<SafeDatabaseExecutor> {
        SafeDatabaseExecutorImpl(
            roomDatabaseErrorMapper = get<RoomDatabaseErrorMapper>()
        )
    }

    includes(localDataSourceModule)
}