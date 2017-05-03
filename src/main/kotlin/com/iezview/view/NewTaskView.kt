package com.iezview.view

import com.iezview.controller.SolutionController
import com.iezview.model.TaskModel
import com.iezview.util.Config
import com.iezview.util.PathUtil
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by shishifanbuxie on 2017/4/24.
 *
 * 新建任务 Wizard
 */
class NewTaskWizard:Wizard("新建任务","新建拍摄任务"){
    val taskModel:TaskModel by inject()
    val solutionController:SolutionController by param()
    init {
        enterProgresses=true//响应回车事件
        Config.chineseWizard(this)
        showSteps=false
        graphic = resources.imageview("graphics/icon_16x16@2x.png")
        add(NewTaskView::class, mapOf(NewTaskView::solutionController to solutionController))
    }
//    override val canGoNext = currentPageComplete
    override val canFinish = currentPageComplete
}

/**
 * 新建任务 View
 */
class NewTaskView : View("新建任务") {
    val  taskModel:TaskModel by inject()
    val solutionController:SolutionController by param()
    override val complete=taskModel.valid(taskModel.taskName,taskModel.savePath)
    override val root =hbox {
        form {
            fieldset(title) {
                field("任务名称") {
                    textfield(taskModel.taskName) {
                        text="${solutionController.selectedSolution()}${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"))}"
                        prefColumnCount = 10
                        required()
                    }
                }

                field("存储路径") {
                    textfield(taskModel.savePath) {
                        prefColumnCount = 15
                        text="${Config.Img}/${solutionController.selectedSolution()}"
                        required()
                        isEditable = false
                    }
                    button("选择文件夹") {
                        setOnAction {
                            var file = chooseDirectory("选择任务存储路径", File(PathUtil.resolvePath(Paths.get(Config.Img)).toUri()), currentWindow)
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
