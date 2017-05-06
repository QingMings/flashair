package com.iezview.model

import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/24.
 * 任务模型
 */

class Task():ViewModel(){
     var savePath by property<String>("")
    fun savePathProperty() =getProperty(Task::savePath)
    var taskName by property<String>("")
    fun taskNameProperty() = getProperty(Task::taskName)

    override fun toString(): String {
        return "Task(taskName=$taskName,savePath=$savePath)"
    }
}

/**
 * itemModel
 */
class TaskModel : ItemViewModel<Task>(Task()) {
    val savePath = bind(Task::savePathProperty,true)
    val taskName =  bind(Task::taskNameProperty,true)
}
