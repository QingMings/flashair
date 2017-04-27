package com.iezview.flashair

import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/27.
 */
class MenuTest : View("My View") {
    val  menuController:MenuController by inject()
    override val root =hbox{
        menubar{
            menu("Settings"){
//                isUseSystemMenuBar=true
                menu("选择方案"){
                    var emptyMenuItem = radiomenuitem("(空)")
                    emptyMenuItem.visibleProperty().set(false)
                    emptyMenuItem.isDisable = true
                    showingProperty().addListener { observable, oldValue, newValue ->
                        if(newValue){
                            println(newValue)
                            items.removeIf { it != emptyMenuItem }

                            menuController.menuList {
                                radiomenuitem(it)
                            }
                        }
                    }

//                        radiomenuitem("ccc")


                }

            }
        }
    }
}

class  MenuController:Controller(){
    var  menus= listOf<String>("menu1","menu2","menu3","menu4")
    fun menuList(buildMenu:(menuName:String)->Unit){
        menus.forEach { buildMenu(it) }
    }
}