package com.iezview.app

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val green by cssclass()
        val red by cssclass()
        val codeArea by cssclass()
        val splitPaneDivider by cssclass()
        val splitPane by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
        green {
            fill = c("#80b380")
        }
        red {
            fill = c("#e64d4d")
        }
        codeArea {
            backgroundColor += c("#333333")
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

    }
}