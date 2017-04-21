package com.iezview.app

import com.iezview.view.MainView
import javafx.scene.image.Image
import tornadofx.*

/**
 * app 程序入口
 */
class MyApp: App(Image("icon/icon_512x512.png"), MainView::class, Styles::class){
    init {
        reloadStylesheetsOnFocus()
    }
}