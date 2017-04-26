package com.iezview.view

import com.iezview.model.TaskModel
import tornadofx.*
import java.io.File
import java.nio.file.Paths

/**
 * Created by shishifanbuxie on 2017/4/24.
 *
 * 新建任务 Wizard
 */
class NewTaskWizard:Wizard("新建任务","新建拍摄任务"){
    val taskModel:TaskModel by inject()
    init {
        showSteps=false
        stepsTextProperty.set("步骤")
        backButtonTextProperty.set("上一步")
        nextButtonTextProperty.set("下一步")
        finishButtonTextProperty.set("完成")
        cancelButtonTextProperty.set("取消")
        graphic = resources.imageview("graphics/icon_16x16@2x.png")
        add(NewTaskView::class)
    }
//    override val canGoNext = currentPageComplete
    override val canFinish = currentPageComplete

}

/**
 * 新建任务 View
 */
class NewTaskView : View("新建任务") {
    val  taskModel:TaskModel by inject()
    override val complete=taskModel.valid(taskModel.taskName,taskModel.savePath)
    override val root =hbox {
        form {
            fieldset(title) {
                field("任务名称") {
                    textfield(taskModel.taskName) {
                        prefColumnCount = 5
                        required()
                    }
                }

                field("存储路径") {
                    textfield(taskModel.savePath) {
                        prefColumnCount = 10
                        required()
                        isEditable = false
                    }
                    button("选择文件夹") {
                        setOnAction {
                            var file = chooseDirectory("选择任务存储路径", File(Paths.get("img").toUri()), currentWindow)
                            if (file != null) {
                                taskModel.savePath.value = file.absolutePath
                            }
                        }
                    }

                }
            }
        }
    }
}
