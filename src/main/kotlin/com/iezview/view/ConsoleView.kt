package com.iezview.view

import com.iezview.controller.LogController
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 日志输出视图
 */
class ConsoleView : View("运行日志") {
    val logController:LogController by inject()
    override val root =
        titledpane("日志"){
                add(logController.regConsoleView())
                prefWidthProperty().bind(primaryStage.widthProperty())
    }
}
