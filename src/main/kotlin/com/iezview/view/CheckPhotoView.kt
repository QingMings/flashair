package com.iezview.view

import com.iezview.model.Camera
import com.iezview.model.CheckCamerasModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/5/4.
 * 检查相机照片是否丢失
 */
class CheckPhotoView : Fragment("未获取到图片的相机") {
    init {

    }
    val  checkCameras:CheckCamerasModel by  param()
    override val root =vbox{
        tableview(checkCameras.cameras){
            column("相机IP", Camera::ip)
            column("当前下载目录", Camera::currpath)
            column("当前下载图片", Camera::currimg)

        }
        hbox {
            alignment= Pos.CENTER
            button("重新获取全部") {
                enableWhen { SimpleBooleanProperty(checkCameras.cameras.value.size>0) }

            }
        }
    }


}
