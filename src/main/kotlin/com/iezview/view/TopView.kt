package com.iezview.view

import com.iezview.controller.*
import javafx.beans.binding.Bindings
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ToggleGroup
import javafx.scene.input.KeyCombination
import javafx.stage.WindowEvent
import tornadofx.*
import java.util.logging.Level

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 工具栏视图
 */
class TopView : View("My View") {
    val solutionController: SolutionController by inject()
    init {
        solutionController.loaddefault()
    }
    override val root = vbox {
        hbox {
            menubar {
                menu("配置") {
                    menuitem("新建方案",KeyCombination.keyCombination("shortcut+N")) {
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
                                emptyMenuItem.isVisible = solutionController.solutionsSize() <= 0
                                //加载方案到菜单上,每次重新生成方案，移除除了 emptyMenuItem
                                items.removeIf { menuitem -> menuitem != emptyMenuItem }
                                solutionController.solutionsListMenu({
                                    solutionName ->
                                    radiomenuitem(solutionName.name ?: "(空)", solutionsToggle) {
                                        //读取默认选中你的方案
                                        if (solutionController.isSelected(text)) {
                                            isSelected = true
                                            solutionController.setSelected()
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
//                    menuitem("Save") {
//                    }

                    separator()
                    menuitem("退出", KeyCombination.keyCombination("shortcut+Q")){

                        primaryStage.fireEvent(WindowEvent(primaryStage,WindowEvent.WINDOW_CLOSE_REQUEST))
                    }
                }
                menu("编辑") {

                    menuitem("编辑当前方案", KeyCombination.keyCombination("shortcut+E")){

                        find(EditSolutionView::class).openModal()
                    }.enableWhen { Bindings.and(solutionController.selectedSolution,
                            solutionController.serviceStart.not()) }
                    menuitem("删除当前方案", KeyCombination.keyCombination("shortcut+D")){
                        alert(Alert.AlertType.CONFIRMATION, "删除方案？","确定要删除当前方案么？", ButtonType.OK, ButtonType.CANCEL){
                            if(ButtonType.OK == it){
                                fire(writeLogEvent(Level.WARNING,"删除方案: ${solutionController.selectedSolution()}"))
                                solutionController.deleteSolution(solutionController.selectedSolution())
                            }
                        }
                    }.enableWhen {
                        Bindings.and(solutionController.selectedSolution,
                                solutionController.serviceStart.not()) }

                }
                menu("Help") {
                    menuitem("清理日志", KeyCombination.keyCombination("shortcut+shift+C")) {
                        fire(cleanLogEvent())
                    }
                }
                prefWidthProperty().bind(primaryStage.widthProperty())
            }
        }
        hbox {
            toolbar {
                button("新建任务") {
                    enableWhen { Bindings.and(solutionController.selectedSolution,
                            solutionController.serviceStart.not())}
                    setOnAction {
                        solutionController.newTask()
                    }
                }
                button("开始任务") {
                    enableWhen {
                        Bindings.and(solutionController.selectedSolution ,
                           solutionController.currentTask.taskNameProperty().isNotNull) }
                    setOnAction {
                        fire(InitCameras())
                    }
                }

                button("结束任务"){
                    enableWhen {
                        solutionController.serviceStart
                    }
                    setOnAction {
                        solutionController.serviceStart.set(false)
                    }
                }
                    prefWidthProperty().bind(primaryStage.widthProperty())
                }
            }
        }
    }

