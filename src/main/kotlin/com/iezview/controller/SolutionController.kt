package com.iezview.controller

import com.iezview.model.Camera
import com.iezview.model.Solution
import com.iezview.model.SolutionName
import com.iezview.model.Solutions
import com.iezview.util.MyScheduledService
import com.iezview.util.PathUtil
import com.iezview.view.NewSolutionWizard
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Level
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/4/12.
 */
class SolutionController:Controller() {
    companion object{
        val SELECTED_SOLUTION="selectedSolution" //选中的 方案
        val LOADED_SOLUTION="loadedSolution"  // 标识已经加载的方案  运行时属性，不储存在配置文件中
        val SELECTED_CAMERA="selectedCamera"// 选中的相机
        val FileName = "fileName"
        val FilePath = "filePath"
    }
    //新建方案是的 camera列表
    val cameras = FXCollections.observableArrayList<Camera>()
    //选中的方案的相机列表 初始化的是这个列表
    var selectedSolutionCameras=FXCollections.observableArrayList<Camera>()
    var selectedCamera:Camera? =null
    val selectedSolution =SimpleBooleanProperty(false)
    init {
        println("方案组件初始化")
        //加载方案
        subscribe<loadSolution> {event->
            val solution=loadSolu(event.solutionName)
            selectedSolutionCameras=solution?.cameraList
            fire(solutionList(selectedSolutionCameras))
            setSelected()
        }
        subscribe<InitCameras> { event ->
            defaultCameraList().forEach { camera ->fire(InitCamera(camera))}
        }
        subscribe<InitCamera> { event ->
            val svc = object : MyScheduledService(event.camera,this@SolutionController){}
            svc.period = Duration.seconds(1.0)
            svc.start()
            svc.setOnSucceeded { successEvent->
                println(successEvent.source.value)
            }
            svc.setOnFailed { fileEvent->
                println(fileEvent.source.value?:"空")

            }
            event.camera.lastwriteProperty().addListener { observable, oldValue, newValue ->
                if(!(oldValue?:"0").equals("0")) {
                    var lastileResp = svc.api.get("/api/lastFile.lua")
                    if (lastileResp.ok()) {
                        cameraOnline(lastileResp.one(), event.camera)
                        fire(DownloadJPG(lastileResp.one(),svc.api))
                    }
                }
                print(oldValue+"--------")
                println(newValue)
                println(event.camera)
            }
        }
        subscribe<DownloadJPG> {event->
            downloadJPG(event.lastfile,event.api)
        }
        subscribe<updateCamera> {event->
            fire(solutionList(selectedSolutionCameras))
        }
        //新建方案使用
        subscribe<saveCamera> {event->
            cameras.add(event.camera)
            fire(putCameras(cameras))
        }
        //用于删除 选中的 相机  新建方案时候使用
        subscribe<selectedCamera> {event->
            with(config){
                set(SELECTED_CAMERA to event.selectedCamera)
            }
        }
        subscribe<removeCamera> {event->
            var  selectedCamera=config.jsonModel<Camera>(SELECTED_CAMERA)
            if (selectedCamera != null) {
                cameras.forEach { camera->
                    if (camera.id==selectedCamera?.id){
                        selectedCamera=camera
                    }
                }
                cameras.remove(selectedCamera)
                fire(putCameras(cameras))
            }

        }

    }

    /**
     * 写错误日志
     */
    fun  writeErrorlog(message:String){
        fire(writeLogEvent(Level.WARNING,message))
    }

    /**
     * 相机在线
     */
    fun  cameraOnline(lastfile:JsonObject,camera: Camera){
        var fileName=lastfile.getString(FileName)
        var filePath=lastfile.getString(FilePath)
        camera.currimg=fileName
        camera.currpath=filePath
    }
    fun  downloadJPG(lastfile: JsonObject,api: Rest){
        var downFileResp = api.get(lastfile.getString(CameraController.FilePath))
        try {
            if (downFileResp.ok()) {
                var fileStream = downFileResp.content()
                fileStream.use {
                    Files.copy(it, PathUtil.resolvePath(Paths.get("img").resolve(lastfile.getString(CameraController.FileName))), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }
    /**
     * 相机离线
     */
    fun  cameraOffline(camera: Camera){
        camera.online=-1
        camera.currimg=""
        camera.currpath=""
    }

    /**
     * 储存方案
     * 并维护 方案列表 ，没有做去重判断 这是个bug
     *
     */
    fun saveSolution(solution:Solution): Unit {
            with(config){
                set(solution.name to solution)
                save()
                if(config.jsonModel<Solutions>(Solutions.ROOT)==null){
                    set(Solutions.ROOT to Solutions(listOf(SolutionName(solution.name))))
                    save()
                }else{
                   var solutions= config.jsonModel<Solutions>(Solutions.ROOT)
                    solutions?.solutionNames?.add(SolutionName(solution.name))
                    set(Solutions.ROOT to solutions)
                    save()
                }
            }
    }

    /**
     * 方案列表，
     * 接受一个 带参数的lambda 表达式
     */
    fun  solutionsListMenu(buildMenu:(name:SolutionName)->Unit):Unit{
           var solutionNames=     config.jsonModel<Solutions>(Solutions.ROOT)?.solutionNames
            solutionNames?.forEach { buildMenu(it) }
    }

    /**
     * 保存选中的方案名字
     */
    fun  saveSelectedSolutionName(seledtedSolution:String):Unit{
        with(config){
            set(SELECTED_SOLUTION , seledtedSolution)
            setSelected()
            save()
        }
    }


    /**
     * 新建方案
     */
    fun  newSolution(){
        find(NewSolutionWizard::class){
            onComplete {
                saveSolution(solution.item)
                cameras.clear()
            }
            openModal(stageStyle = StageStyle.UTILITY)

        }
    }
    /**
     *  检查方案是否选中
     */
    fun  isSelected(solutionName:String):Boolean{
        return config.string(SELECTED_SOLUTION)==solutionName
    }
    fun setSelected(){
        selectedSolution.set(true)
    }

    /**
     * 返回选中的方案名字
     */
    fun selectedSolution():String=config.string(SELECTED_SOLUTION)?:""
    /**
     * 已加载的方案，
     * 因为菜单是每次重新生成，加一个标识，比对 菜单勾选的方案，和列表加载的方案是不是同一个
     */
    fun isloaded(solutionName: String):Boolean{
        return  config.string(LOADED_SOLUTION)==solutionName
    }

    /**
     * 返回方案个数
     * 如果为空  返回0
     */
    fun solutionsSize():Int=config.jsonModel<Solutions>(Solutions.ROOT)?.solutionNames?.size?:0



    /**
     * 加载指定的方案
     */
    fun loadSolu(solutionName: String): Solution? =config.jsonModel<Solution>(solutionName)

    /**
     * 加载默认的方案
     * 如果没有 不做操作
     */
    fun loaddefault():Unit{
        if(!selectedSolution().isNullOrEmpty()){
            fire(loadSolution(selectedSolution()))
            fire(writeLogEvent(Level.INFO,"加载默认方案：" +selectedSolution()))
        }
    }

    /**
     * 确保 选中了  方案之后调用
     * 否则出异常
     */
    fun defaultCameraList():ObservableList<Camera> = selectedSolutionCameras

    fun addlastWriteChangeListener(cameras: ObservableList<Camera>){

    }


    fun testLoadSolu(solutionName: String):Unit{
        var s =config.jsonModel<Solution>(solutionName)
        println(s)
    }
    fun testsolutions():Unit= println(config.jsonModel<Solutions>("solutions"))

}

class  loadSolution(val solutionName: String):FXEvent(EventBus.RunOn.BackgroundThread)//从config加载solution事件
class  solutionList(val cameras: ObservableList<Camera>):FXEvent(EventBus.RunOn.ApplicationThread)//将solution加载到 listView
class  saveCamera(val  camera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//保存Camera
class  putCameras(val cameras: ObservableList<Camera>):FXEvent(EventBus.RunOn.ApplicationThread)//将Camer添加到list中
class  selectedCamera(val selectedCamera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//选中的 list item
class  removeCamera():FXEvent(EventBus.RunOn.ApplicationThread)// 从list items中移除 item
class  updateCamera():FXEvent(EventBus.RunOn.BackgroundThread)
class  InitCameras() : FXEvent(EventBus.RunOn.BackgroundThread)//初始化相机列表
class  InitCamera(val camera: Camera) : FXEvent(EventBus.RunOn.ApplicationThread)//初始化相机
class  DownloadJPG(val lastfile: JsonObject,val api:Rest):FXEvent(EventBus.RunOn.BackgroundThread)