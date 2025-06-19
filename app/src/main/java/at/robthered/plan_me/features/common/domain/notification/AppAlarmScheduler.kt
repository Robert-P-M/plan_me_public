package at.robthered.plan_me.features.common.domain.notification

/**
 * Definiert eine Schnittstelle zum Planen und Stornieren von App-Benachrichtigungen.
 * Arbeitet mit dem sauberen NotificationContent-Modell aus der Domänenschicht.
 */
interface AppAlarmScheduler {
    /**
     * Plant einen Alarm basierend auf den im NotificationContent-Objekt enthaltenen Informationen.
     * @param notificationContent Das Datenmodell, das alle Infos für den Alarm enthält.
     */
    fun schedule(notificationId: Int, taskId: Long, triggerAtMillis: Long)

    /**
     * Storniert einen bereits geplanten Alarm.
     * @param notificationId Die eindeutige ID der Benachrichtigung, die storniert werden soll.
     */
    fun cancel(notificationId: Int, taskId: Long)

    /**
     * Prüft, ob die App die Berechtigung hat, exakte Alarme zu planen.
     * @return true, wenn die Berechtigung erteilt wurde, ansonsten false.
     */
    fun canScheduleExactAlarms(): Boolean
}