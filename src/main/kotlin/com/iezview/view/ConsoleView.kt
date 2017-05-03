package com.iezview.view

import com.iezview.controller.LogController
import com.iezview.controller.SolutionController
import com.iezview.util.PathUtil
import javafx.scene.paint.Color
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.nio.file.Paths

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 日志输出视图
 */
class ConsoleView : View("运行日志") {
    val logController: LogController by inject()
    val solutionController: SolutionController by inject()
    override val root = vbox {
            addClass(consoleViewStyle.console)
            titledpane("日志") {
                add(logController.regConsoleView())
                prefWidthProperty().bind(primaryStage.widthProperty())
             }

            hbox {
                addClass(consoleViewStyle.bar)
                 label("存储路径") {
            }
            hyperlink {

                textProperty().bindBidirectional(solutionController.currentTask.savePathProperty())
                visibleWhen { textProperty().isNotEmpty }
                action {
                    PathUtil.resolvePath(Paths.get(textProperty().value))
                    Desktop.getDesktop().open(File(textProperty().value))
                }
            }
            prefWidthProperty().bind(primaryStage.widthProperty())
        }
    }
}

class consoleViewStyle : Stylesheet() {
    companion object {
        val console by cssclass()
        val bar by cssclass()
        val green by cssclass()
        val red by cssclass()
        val orange by cssclass()
        val codeArea by cssclass()
    }

    init {
        green {
            fill = c("#80b380")
        }
        red {
            fill = c("#e64d4d")
        }
        orange {
            fill  = c("#FFA500")
        }
        codeArea {
            backgroundColor += c("#333333")
        }
        console{
            bar{
                label{
                    padding = box(2.px, 10.px, 2.px, 10.px)
                    fontSize = 10.px
                }
            }
            hyperlink{
                fontSize = 10.px
                borderColor += box(Color.TRANSPARENT)
            }
        }
    }

}