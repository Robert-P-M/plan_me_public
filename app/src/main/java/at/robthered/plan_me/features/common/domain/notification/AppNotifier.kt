package at.robthered.plan_me.features.common.domain.notification

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel

interface AppNotifier {
    /**
     * Erstellt und zeigt eine Benachrichtigung für den übergebenen Task an.
     * @param task Das vollständige Task-Modell, dessen Details angezeigt werden sollen.
     */
    suspend fun show(task: TaskModel)
}