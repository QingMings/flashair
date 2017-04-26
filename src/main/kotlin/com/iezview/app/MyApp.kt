package com.iezview.app

import com.iezview.view.MainView
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths


/**
 * app 程序入口
 */
class MyApp : App(Image("icon/icon_512x512.png"), MainView::class, Styles::class) {
    override val configBasePath: Path= Paths.get(".conf")

    init {

        reloadStylesheetsOnFocus()

    }
    override fun start(stage: Stage) {
        super.start(stage)
        stage.setOnCloseRequest { event ->
          alert(Alert.AlertType.CONFIRMATION, "退出程序？","确定要退出么？请检查下载任务是否完成！", ButtonType.OK, ButtonType.CANCEL){
                        if(!(ButtonType.OK.equals(it))){event.consume()}
            }
        }
//        stage.addEventFilter(KeyEvent.KEY_PRESSED,({event->
//            println(event.code)
//        }))
    }

}

