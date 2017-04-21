package com.iezview.view

import tornadofx.*

/**
 * 主视图
 */
class MainView : View("Hello Flash Air") {
    val toolBar :TopView by inject()
    val cameraList : CameraListView by inject()
    val console :ConsoleView by inject()
    override val root =borderpane {
        top=toolBar.root
        center=cameraList.root
        bottom =console.root
    }
}