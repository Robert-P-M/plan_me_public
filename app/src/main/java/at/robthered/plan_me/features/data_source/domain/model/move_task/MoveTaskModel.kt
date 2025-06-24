package at.robthered.plan_me.features.data_source.domain.model.move_task

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MoveTaskModel : Parcelable {
    data class Root(val moveTaskModelRootEnum: MoveTaskModelRootEnum) : MoveTaskModel()
    data class Section(val title: String, val sectionId: Long) : MoveTaskModel()
    data class Task(val title: String, val taskId: Long, val depth: Int = 0) : MoveTaskModel()
}