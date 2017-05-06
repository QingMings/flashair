package com.iezview.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/5/4.
 * 丢片相机列表
 */
class CheckCameras :JsonModel {
    var cameras by  property<ObservableList<Camera>>(FXCollections.observableArrayList())
    fun camerasProperty() =getProperty(CheckCameras::cameras)

    override fun updateModel(json: JsonObject) {
          with(json){
              cameras.setAll(getJsonArray("cameras").toModel())
          }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("cameras",cameras.toJSON())
        }
    }

    override fun toString(): String {
        return "CheckCameras(cameras=$cameras)"
    }



}

class CheckCamerasModel : ItemViewModel<CheckCameras>(CheckCameras()) {
    val cameras = bind { item?.camerasProperty() }
}


