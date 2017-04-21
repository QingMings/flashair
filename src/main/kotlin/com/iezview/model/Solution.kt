package com.iezview.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/4/12.
 * 方案
 */

class Solution() :JsonModel{
     var name by property<String>()
    fun nameProperty() =getProperty(Solution::name)
    var cameraList by property<ObservableList<Camera>>(FXCollections.observableArrayList())
    fun cameraListProperty() =getProperty(Solution::cameraList)
    override fun updateModel(json: JsonObject) {
            with(json){
                name= string("name")
                cameraList.setAll(getJsonArray("cameraList").toModel())
            }
    }
    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("name",name)
            add("cameraList",cameraList.toJSON())
        }
    }
    override fun toString(): String {
        return "Solution(name=$name,cameraList=$cameraList)"
    }
}

class SolutionModel : ItemViewModel<Solution>(Solution()) {
    val name = bind ( Solution::nameProperty,true )
    val cameraList = bind(Solution::cameraListProperty,true)
}


/**
 * 方案列表
 * 储存方案列表
 * 并且用于动态构建菜单
 */

class Solutions(sn: List<SolutionName>?=FXCollections.observableArrayList<SolutionName>(emptyList<SolutionName>())) :JsonModel{
    companion object{
        val ROOT="solutions"
        val SOLUTION_NAMES="solutionNames"
    }
    var solutionNames = FXCollections.observableArrayList<SolutionName>(sn)
    fun solutionNamesProperty()=getProperty(Solutions::solutionNames)
    override fun updateModel(json: JsonObject) {
        with(json){
            solutionNames.setAll(getJsonArray(SOLUTION_NAMES).toModel<SolutionName>())
        }
    }
    override fun toJSON(json: JsonBuilder) {
        with(json){
            add(SOLUTION_NAMES,solutionNames.toJSON())
        }
    }
    override fun toString(): String {
        return "Solutions(solutionNames:$solutionNames)"
    }
}

class SolutionsModel : ItemViewModel<Solutions>() {
    val solutionNames = bind { item?.solutionNamesProperty() }
}


/**
 * 方案名称
 */
 class  SolutionName( solutionName: String?="") :JsonModel{
     var name by property(solutionName)
    fun nameProperty() =getProperty(SolutionName::name)
    override fun updateModel(json: JsonObject) {
        with(json){
            name=string("name")
        }
    }
    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("name",name)
        }
    }
    override fun toString(): String {
        return "SolutionName(name=$name)"
    }
}

class SolutionNameModel : ItemViewModel<SolutionName>() {
    val name = bind { item?.nameProperty() }
}




