package com.iezview.view

import com.iezview.font.Fonts
import com.iezview.model.Camera
import com.iezview.model.CameraState
import com.iezview.util.Gradient
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/11.
 *
 * 相机列表Item
 */
class CameraItemFragment(it: Camera) :  Fragment("CameraItem") {
    override val root =hbox {
        hbox {
            circle {
                centerX = 100.0
                centerY = 100.0
                radius = 5.0
                //渐变
                    it.onlineProperty().onChange { online->
                        if (online==CameraState.onLine) {
                            fillProperty().bindBidirectional(Gradient.ON_LINE.toProperty())
                        } else if(online==CameraState.errorConn) {
                            fillProperty().bindBidirectional(Gradient.ERROR_CONN.toProperty())
                        }else{
                            fillProperty().bindBidirectional(Gradient.OFF_LINE.toProperty())
                        }
                    }
                fillProperty().bindBidirectional(Gradient.OFF_LINE.toProperty())
                effect=DropShadow(3.0,Color.color(0.4,0.4,0.4))
                style{
                    borderColor+=box(Color.GREEN)
                }
            }
            style{
                padding= box(6.px,10.px,0.px,10.px)
                alignment= Pos.TOP_CENTER
            }
        }
        vbox {
            hbox {
                hbox {
                    text {
                        textProperty().bindBidirectional(it.nameProperty())
                        font=Fonts.latoRegular(20.0)
                    }
                    style {
                        padding= box(0.px,10.px,0.px,0.px)
                    }
                }
                hbox {
                    text {

                        textProperty().bindBidirectional(it.ipProperty())
                    }
                    style {
                        alignment=Pos.BOTTOM_CENTER
                    }
                }
            }
            hbox {
                label {
                    text="当前目录："
                }
                text{
                    textProperty().bindBidirectional(it.currpathProperty())
                }
                style{
                    fontSize=10.px
                }
            }
            hbox {
                label {
                    text="当前图片:"
                }
                text{
                    textProperty().bindBidirectional(it.currimgProperty())
                    font=Fonts.latoBold(10.0)
                }
                style{
                    fontSize=10.px
                }
            }
            hbox {
                label {
                    text="拍照数量:"
                }
                text{
                    textProperty().bindBidirectional(it.photosizeProperty())
                    font=Fonts.latoBold(10.0)
                }
                style{
                    fontSize=10.px
                }
            }
        }
    }
}

/**
 *新建方案相机列表 item
 */
class CameraItemEditFragment(it:Camera) : Fragment("Edit Camera") {
    override val root = hbox {
        vbox {
            hbox {
                label(it.name)
                style{
                    fontSize=20.px
                }
            }
        }
        vbox {
            hbox {
                label(it.ip)

            }
            style {
                alignment=Pos.BOTTOM_RIGHT
                padding= box(0.px,10.px,0.px,10.px)
            }
        }
    }
}
