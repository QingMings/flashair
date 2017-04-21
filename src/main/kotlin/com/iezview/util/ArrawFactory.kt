package com.iezview.util

import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import org.reactfx.value.Val
import java.util.function.IntFunction

/**
 * Created by shishifanbuxie on 2017/4/14.
 * richText help
 * 绿色小三角
 */
class ArrowFactory internal constructor(private val shownLine: ObservableValue<Int>) : IntFunction<Node> {
    override fun apply(lineNumber: Int): Node {
        val triangle = Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0)
        triangle.fill = Color.GREEN
        val visible = Val.map(shownLine) { sl -> sl === lineNumber }
        triangle.visibleProperty().bind( Val.flatMap<Scene, Boolean>(triangle.sceneProperty())
        { scene -> if (scene != null) visible else Val.constant(false) })
        return triangle
    }
}