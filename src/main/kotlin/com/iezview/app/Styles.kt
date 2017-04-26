package com.iezview.app

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()


        val splitPaneDivider by cssclass()
        val splitPane by cssclass()
        val titledPane by cssclass()

    }

    init {
        root{
            fontFamily= "Hei"
        }
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }


        splitPane {
            splitPaneDivider {
                borderColor += box(Color.TRANSPARENT)
                prefWidth = 2.px
                backgroundInsets += box(0.px)
                backgroundColor += Color.TRANSPARENT
            }

            backgroundInsets += box(0.px)
            padding = box(0.px)
        }
        titledPane{
            text{
                fontSize=10.px
            }
        }
    }
}