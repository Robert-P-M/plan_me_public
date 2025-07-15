package at.robthered.plan_me.features.common.di

import android.content.Context
import at.robthered.plan_me.features.common.data.SafeDatabaseResultCallImpl
import at.robthered.plan_me.features.common.data.notification.AndroidAppNotifier
import at.robthered.plan_me.features.common.data.notification.AppAlarmSchedulerImpl
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.domain.notification.AppNotifier
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapper
import at.robthered.plan_me.features.common.domain.use_case.NotificationContentMapperImpl
import at.robthered.plan_me.features.common.domain.use_case.RescheduleAlarmsUseCase
import at.robthered.plan_me.features.common.domain.use_case.RescheduleAlarmsUseCaseImpl
import at.robthered.plan_me.features.common.domain.use_case.ShowNotificationUseCase
import at.robthered.plan_me.features.common.domain.use_case.ShowNotificationUseCaseImpl
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcherImpl
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperatorImpl
import at.robthered.plan_me.features.data_source.data.local.utils.AndroidAppLogger
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBusImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.koin.dsl.module

val productionCommonModule = module {


    single<AppUiEventDispatcher> {
        AppUiEventDispatcherImpl()
    }

    factory<AppLogger> {
        AndroidAppLogger()
    }

    single<SafeDatabaseResultCall> {
        SafeDatabaseResultCallImpl(
            appLogger = get<AppLogger>(),
            roomDatabaseErrorMapper = get<RoomDatabaseErrorMapper>()
        )
    }

    single<UseCaseOperator> {
        UseCaseOperatorImpl(
            appUiEventDispatcher = get<AppUiEventDispatcher>(),
            appLogger = get<AppLogger>(),
        )
    }


    single<Clock> {
        Clock.System
    }

    single<PriorityPickerEventBus> {
        PriorityPickerEventBusImpl()
    }

    single<TimeZone> {
        TimeZone.currentSystemDefault()
    }
    single<NotificationContentMapper> {
        NotificationContentMapperImpl(
            timeZone = get<TimeZone>()
        )
    }

    single<AppAlarmScheduler> {
        AppAlarmSchedulerImpl(
            context = get<Context>(),
            appLogger = get<AppLogger>()
        )
    }

    single<ShowNotificationUseCase> {
        ShowNotificationUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            appNotifier = get<AppNotifier>()
        )
    }

    single<RescheduleAlarmsUseCase> {
        RescheduleAlarmsUseCaseImpl(
            localTaskRepository = get<LocalTaskRepository>(),
            notificationContentMapper = get<NotificationContentMapper>(),
            appAlarmScheduler = get<AppAlarmScheduler>()
        )
    }

    single<AppNotifier> {
        AndroidAppNotifier(
            context = get<Context>(),
            notificationContentMapper = get<NotificationContentMapper>()
        )
    }

    single<AppAlarmScheduler> {
        AppAlarmSchedulerImpl(
            context = get<Context>(),
            appLogger = get<AppLogger>()
        )
    }

}