package com.iezview.view

import com.iezview.controller.*
import com.iezview.model.Camera
import com.iezview.model.CameraModel
import com.iezview.model.SolutionModel
import com.iezview.util.Config
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 新建方案   Wizard
 */
class NewSolutionWizard : Wizard("新建方案", "添加一个新的工作方案") {
    val solutionController:SolutionController by param()
    val solution: SolutionModel by inject()
    override val canGoNext = currentPageComplete
    override val canFinish = allPagesComplete
    init {
        enterProgresses=true//响应回车事件
        stepsTextProperty.set("步骤")
        Config.chineseWizard(this)
        graphic = resources.imageview("graphics/icon_16x16@2x.png")
        add(SolutionNameView::class,mapOf(SolutionNameView::solutionController to solutionController))
        add(SolutionCameraListView::class,mapOf(SolutionNameView::solutionController to solutionController))
    }

}

/**
 * 新建方案  page1
 */
class SolutionNameView : View("新建方案") {
    val solutionController:SolutionController by param()
    val solutionModel: SolutionModel by inject()
    override val complete = solutionModel.valid(solutionModel.name)
    override val root = hbox { form {
        fieldset(title) {
            field("方案名称") {
                textfield(solutionModel.name) {
                    prefColumnCount = 10
                    required()
                }.validator { str ->
                    if(solutionController.checkSolutionNameExists(str?:""))  error("已经存在相同名字的方案") else
                    if(str=="solutions"||str=="selectedSolution"||str=="selectedCamera") error("系统保留,不可使用该名称") else null
                }
            }
        }
    } }
}

/**
 * 新建方案 page2
 */
class SolutionCameraListView : View("相机列表") {
    val solutionModel: SolutionModel by inject()
    override val complete = booleanBinding(solutionModel.cameraList.value) { isNotEmpty() }
    override val root =
            stackpane {
                vbox {
                    label("相机列表") {
                        style { fontSize = 16.px }
                    }
                    listview(solutionModel.cameraList) {
                        solutionModel.addValidator(this, itemsProperty()) {
                            if (items.isEmpty()) error("至少配置一个相机") else null
                        }
                        cellCache { CameraItemEditFragment(it).root }
                        subscribe<putCameras> {
                            event ->
                            solutionModel.item.cameraList.setAll(event.cameras)
                        }
                        selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
                            if (newValue != null) fire(selectedCamera(newValue))
                        }
                        style {
                            prefHeight = 200.px
                        }
                    }
                    hbox {
                        button("+") {
                            shortcut("shortcut+N")
                            setOnAction {
                                find<newCamera>(mutableMapOf(newCamera::commitParam to {camera:Camera -> fire(saveCamera(camera))},newCamera::cameraList to solutionModel.cameraList.value)).openModal(stageStyle = StageStyle.UTILITY)
                            }
                        }
                        button("-") {
                            shortcut("DELETE")
                            shortcut("BackSpace")
                            setOnAction {
                                fire(removeCamera())
                            }
                        }
                    }
                }
            }
}

/**
 * 新建方案 page2 添加相机
 */
class newCamera : Fragment("添加相机") {
    val commitParam : (Camera) -> Unit by param()
    val cameraList :ObservableList<Camera>? by param()
    //    val cameraModel:CameraModel by inject()
    val cameraModel = CameraModel()
    override val root =hbox{ form {
        fieldset(title) {
            field("相机名称") {
                textfield(cameraModel.name) {
                    prefColumnCount = 10
                    required()
                }.validator {
                    if(checkCameraNameExists(it)) error("当前方案中已经存在相同名称的相机") else null
                }
            }
            field("相机IP") {
                val regex = makePartialIPRegex()
                val ipAddressFilter = { c: TextFormatter.Change ->
                    val text = c.controlNewText
                    if (text.matches(regex.toRegex())) {c} else {null}
                }
                textfield(cameraModel.ip) {
                    prefColumnCount = 10
                    required()
                    textFormatter = TextFormatter<Any>(ipAddressFilter)
                }.validator {
                    val regex = makeIPRegex()
                    if (it.isNullOrEmpty().not() && (it?.matches(regex.toRegex()))?.not() ?: false) error("IP地址不对") else
                    if(checkCameraIpExists(it)) error("已经添加过该IP地址") else null
                }
            }
            hbox {
                button("保存") {
                    shortcut("Enter")
                    disableProperty().bind(cameraModel.dirty.not())
                    enableWhen { cameraModel.valid }
                    setOnAction {
                        save(commitParam)
                    }
                }
                style {
                    alignment = Pos.CENTER
                    padding = box(10.px, 0.px, 0.px, 0.px)
                }
            }

        }
        style {
            padding = box(10.px, 10.px, 0.px, 10.px)
        }
    }
    }
    /**
     * IP输入格式验证
     */
    private fun makePartialIPRegex(): String {
        val partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))"
        val subsequentPartialBlock = "(\\.$partialBlock)"
        val ipAddress = "$partialBlock?$subsequentPartialBlock{0,3}"
        return "^" + ipAddress
    }

    /**
     * IP输入合法性验证
     */
    private fun makeIPRegex(): String {
        val partialBlock = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"
        return partialBlock
    }

    /**
     * 检查camera名字是否存在
     */
    private fun checkCameraNameExists(cameraName:String?):Boolean{
        cameraList?.forEach { camera->
            if(camera.name==cameraName){return true}
        }
        return false
    }

    /**
     * 检查相机Ip是否存在
     */
    private  fun  checkCameraIpExists(cameraIp:String?):Boolean{
        cameraList?.forEach { camera->
            if(camera.ip==cameraIp) return true
        }
        return false
    }
    /**
     * 保存
     */
    private fun save(commitCamera:(Camera)->Unit) {
        cameraModel.commit(cameraModel.name, cameraModel.ip)
        commitCamera(cameraModel.item)
//        var camera = cameraModel.item
//        fire(saveCamera(camera))
        close()
    }
}

