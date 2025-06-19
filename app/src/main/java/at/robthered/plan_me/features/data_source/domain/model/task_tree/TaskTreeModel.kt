package at.robthered.plan_me.features.data_source.domain.model.task_tree

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class TaskTreeModel : Parcelable {
    data class Root(val taskTreeModelRootEnum: TaskTreeModelRootEnum) : TaskTreeModel()
    data class Task(val title: String, val taskId: Long) : TaskTreeModel()
    data class Section(val title: String, val sectionId: Long) : TaskTreeModel()
}