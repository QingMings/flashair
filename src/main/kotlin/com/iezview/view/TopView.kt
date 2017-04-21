package com.iezview.view

import com.iezview.controller.*
import javafx.scene.control.ToggleGroup
import tornadofx.*
import java.util.logging.Level

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 工具栏视图
 */
class TopView : View("My View") {
    val solutionController: SolutionController by inject()
    val cameraContoller: CameraController by inject()
    init {
        solutionController.loaddefault()
    }
    override val root = vbox {
        hbox {
            menubar {
                menu("配置") {
                    menuitem("新建方案") {
                        solutionController.newSolution()
                    }
                    menu("选择方案") {
                        //添加默认菜单项，因为如果没有默认的，则不会触发 showingProperty事件
                        var emptyMenuItem = radiomenuitem("(空)")
                        emptyMenuItem.isDisable = true
                        var solutionsToggle = ToggleGroup()
                        showingProperty().addListener { observable, oldValue, newValue ->
                            if (newValue!!) {
                                //方案列表大于0,隐藏 '(空)' 菜单
                                if (solutionController.solutionsSize() > 0) {
                                    emptyMenuItem.isVisible = false
                                }
                                //加载方案到菜单上,每次重新生成方案，移除除了 emptyMenuItem
                                items.removeIf { menuitem -> menuitem != emptyMenuItem }
                                solutionController.solutionsListMenu({
                                    solutionName ->
                                    radiomenuitem(solutionName.name ?: "(空)", solutionsToggle) {
                                        //读取默认选中你的方案
                                        if (solutionController.isSelected(text)) {
                                            isSelected = true
                                            solutionController.setSelected()
                                            println("设置默认")
                                        }
                                        setOnAction {
                                            //保存选中的方案
                                            solutionController.saveSelectedSolutionName(text)
                                            if (!solutionController.isloaded(text)) {
                                                fire(loadSolution(text))//加载方案
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                    menuitem("Save") {
                    }
                    menuitem("Quit")
                }
                menu("Edit") {
                    menuitem("Copy")
                    menuitem("Paste")
                }
                menu("Help") {
                    menuitem("清理日志") {
                        fire(cleanLogEvent())
                    }
                }
                prefWidthProperty().bind(primaryStage.widthProperty())
            }
        }
        hbox {
            toolbar {
                button("初始化方案") {
                    enableWhen { solutionController.selectedSolution  }
                    setOnAction {
                        fire(InitCameras())
                    }
                }
                button("新建任务") {
                    setOnAction {
                        fire(writeLogEvent(Level.WARNING,"测试错误日志"))
                    }
                }
                button("改变状态"){
                    setOnAction {
                        fire(writeLogEvent(Level.INFO,"测试错误日志"))
                    }
                }
                prefWidthProperty().bind(primaryStage.widthProperty())
            }
        }
    }
}
