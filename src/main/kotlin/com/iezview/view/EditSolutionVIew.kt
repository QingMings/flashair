package com.iezview.view

import com.iezview.controller.SolutionController
import com.iezview.model.Camera
import javafx.geometry.Pos
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/25.
 * 编辑方案
 */
class EditSolutionView: Fragment() {
    val solutionController:SolutionController by inject()
    val solution=solutionController.loadSolu(solutionController.selectedSolution())
    val  cameraList = solutionController.loadSolu(solutionController.selectedSolution())!!.cameraList!!
    companion object{
        val WillRemoveCamera="willRemoveCamera"
    }
    init {
        title="编辑方案 ：${solutionController.selectedSolution()}"
        //添加相机
        subscribe<addCamera> {event->
            cameraList.add(event.camera)
        }
        //用于删除 选中的 相机  编辑方案时候使用
        subscribe<willRemoveCamera> {event->
            with(config){
                set(WillRemoveCamera to event.camera)
            }
        }
        //编辑建方案->删除已添加的相机
        subscribe<removeCameraFromList> {
            var  selectedCamera=config.jsonModel<Camera>(WillRemoveCamera)
            if (selectedCamera != null) {
                cameraList.forEach { camera->
                    if (camera.id==selectedCamera?.id){
                        selectedCamera=camera
                    }
                }
                cameraList.remove(selectedCamera)
            }
        }

    }
    override val root =borderpane{
         center=vbox{listview(cameraList) {
             cellCache { CameraItemEditFragment(it).root }
             selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
                 if (newValue != null) fire(willRemoveCamera(newValue))
             }
         }
             hbox {
            button("+") {
                shortcut("shortcut+N")
                setOnAction {
                    find<newCamera>(mutableMapOf(newCamera::commitParam to {camera: Camera -> fire(addCamera(camera))},newCamera::cameraList to cameraList)).openModal(stageStyle = StageStyle.UTILITY)
                }
            }
            button("-") {
                shortcut("DELETE")
                shortcut("BackSpace")
                setOnAction {
                    fire(removeCameraFromList())
                }
            }
        }}

         bottom=  hbox {
              buttonbar {
                  button("保存"){
                      enableWhen {  booleanBinding(cameraList){isNotEmpty()}  }
                      setOnAction {
                          solution!!.cameraList=cameraList
                      }
                  }
                  button("取消"){
                      setOnAction { close() }
                  }
              }
             style{
                 padding=box(5.px,5.px,5.px,5.px)
                 alignment= Pos.CENTER_RIGHT
             }
          }
    }
}

class  addCamera(val camera: Camera):FXEvent(EventBus.RunOn.ApplicationThread)//添加相机
class  willRemoveCamera(val camera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//标识选中的camera 用于删除
class  removeCameraFromList:FXEvent(EventBus.RunOn.ApplicationThread)