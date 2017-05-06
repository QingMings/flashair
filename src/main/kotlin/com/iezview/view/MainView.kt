package com.iezview.view

import com.iezview.util.Config
import tornadofx.*

/**
 * 主视图
 */
class MainView : View(Config.AppicationName) {
    val toolBar :TopView by inject()
    val cameraList : CameraListView by inject()
    val console :ConsoleView by inject()
    init {
    }
    override val root =borderpane {
        top=toolBar.root
        center=cameraList.root
        bottom =console.root
    }
}