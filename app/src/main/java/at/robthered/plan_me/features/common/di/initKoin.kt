package at.robthered.plan_me.features.common.di

import at.robthered.plan_me.features.add_section_dialog.di.addSectionModule
import at.robthered.plan_me.features.add_task_dialog.di.addTaskDialogModule
import at.robthered.plan_me.features.data_source.di.localDataSourceModule
import at.robthered.plan_me.features.data_source.di.productionLocalDataSourceModule
import at.robthered.plan_me.features.data_source.di.repositoryModule
import at.robthered.plan_me.features.data_source.di.useCaseModule
import at.robthered.plan_me.features.hashtag_picker_dialog.di.hashtagPickerDialogModule
import at.robthered.plan_me.features.hashtag_tasks_dialog.di.hashtagTasksDialogModule
import at.robthered.plan_me.features.inbox_screen.di.inboxScreenModule
import at.robthered.plan_me.features.move_task_dialog.di.moveTaskDialogModule
import at.robthered.plan_me.features.priority_picker_dialog.di.priorityPickerDialogModule
import at.robthered.plan_me.features.task_details_dialog.di.taskDetailsDialogModule
import at.robthered.plan_me.features.task_hashtags_dialog.di.taskHashtagsModule
import at.robthered.plan_me.features.task_schedule_picker_dialog.di.taskSchedulePickerDialogModule
import at.robthered.plan_me.features.task_statistics_dialog.di.taskStatisticsDialogModule
import at.robthered.plan_me.features.update_hashtag_name_dialog.di.updateHashtagNameDialogModule
import at.robthered.plan_me.features.update_section_title_dialog.di.updateSectionTitleDialogModule
import at.robthered.plan_me.features.update_task_dialog.di.updateTaskDialogModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        koinApplication(config)
        androidLogger(
            level = Level.ERROR
        )
        config?.invoke(this)
        modules(
            productionLocalDataSourceModule,
            repositoryModule,
            useCaseModule,
            productionCommonModule,
            inboxScreenModule,
            addTaskDialogModule,
            addSectionModule,
            priorityPickerDialogModule,
            updateSectionTitleDialogModule,
            updateTaskDialogModule,
            taskDetailsDialogModule,
            taskStatisticsDialogModule,
            hashtagPickerDialogModule,
            updateHashtagNameDialogModule,
            taskHashtagsModule,
            hashtagTasksDialogModule,
            moveTaskDialogModule,
            taskSchedulePickerDialogModule
        )
    }
}