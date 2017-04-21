package com.iezview.view

import com.iezview.controller.putCameras
import com.iezview.controller.removeCamera
import com.iezview.controller.saveCamera
import com.iezview.controller.selectedCamera
import com.iezview.model.CameraModel
import com.iezview.model.SolutionModel
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 新建方案   Wizard
 */
class NewSolutionWizard : Wizard("新建方案", "添加一个新的工作方案") {
    init {
        stepsTextProperty.set("步骤")
        backButtonTextProperty.set("上一步")
        nextButtonTextProperty.set("下一步")
        finishButtonTextProperty.set("完成")
        cancelButtonTextProperty.set("取消")
        graphic = resources.imageview("graphics/icon_16x16@2x.png")
        add(SolutionNameView::class)
        add(SolutionCameraListView::class)
    }

    val solution: SolutionModel by inject()
    override val canGoNext = currentPageComplete
    override val canFinish = allPagesComplete
}

/**
 * 新建方案  page1
 */
class SolutionNameView : View("新建方案") {
    val solutionModel: SolutionModel by inject()
    override val complete = solutionModel.valid(solutionModel.name)
    override val root = form {
        fieldset(title) {
            field("方案名称") {
                textfield(solutionModel.name) {
                    prefColumnCount = 5
                    required()
                }
            }
        }
    }
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
                            println(event.cameras)
                            println(solutionModel.item)
                            println(solutionModel.valid(solutionModel.cameraList))
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
                            setOnAction {
                                find(newCamera::class).openModal(stageStyle = StageStyle.UTILITY)
                            }
                        }
                        button("-") {
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
    //    val cameraModel:CameraModel by inject()
    val cameraModel = CameraModel()
    override val root = form {
        fieldset(title) {
            field("相机名称") {
                textfield(cameraModel.name) {
                    prefColumnCount = 10
                    required()
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
                    if (it.isNullOrEmpty().not() && (it?.matches(regex.toRegex()))?.not() ?: false) error("IP地址不对") else null
                }
            }
            hbox {
                button("保存") {
                    disableProperty().bind(cameraModel.dirty.not())
                    enableWhen { cameraModel.valid }
                    setOnAction {
                        save()
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
     * 保存
     */
    private fun save() {
        cameraModel.commit(cameraModel.name, cameraModel.ip)
        var camera = cameraModel.item
        fire(saveCamera(camera))
        close()
    }
}

