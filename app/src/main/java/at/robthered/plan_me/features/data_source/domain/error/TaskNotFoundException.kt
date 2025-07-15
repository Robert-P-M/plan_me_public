package at.robthered.plan_me.features.data_source.domain.error

class TaskNotFoundException(val taskId: Long) :
    Exception("Task with id $taskId not found in database.")