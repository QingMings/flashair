package com.iezview.controller

import com.iezview.model.*
import com.iezview.service.CameraScheduledService
import com.iezview.service.DownLoadService
import com.iezview.service.LastFileService
import com.iezview.util.PathUtil
import com.iezview.view.NewSolutionWizard
import com.iezview.view.NewTaskWizard
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Level
import javax.json.JsonObject
import kotlin.concurrent.thread

/**
 * Created by shishifanbuxie on 2017/4/12.
 */
class SolutionController:Controller() {
    companion object{
        val SELECTED_SOLUTION="selectedSolution" //选中的 方案
        val LOADED_SOLUTION="loadedSolution"  // 标识已经加载的方案  运行时属性，不储存在配置文件中
        val SELECTED_CAMERA="selectedCamera"// 选中的相机
        val FileName = "fileName"  // json key
        val FilePath = "filePath" //json key
    }
    var  Applicaiton_Modal =SimpleBooleanProperty(false)// 标识是否打开了dialog
    var  currentTask:Task= Task()//当前任务
    var  serviceStart=SimpleBooleanProperty(false)//标志是否开始任务
    //新建方案的 camera列表
    val cameras = FXCollections.observableArrayList<Camera>()
    //选中的方案的相机列表 初始化的是这个列表
    var selectedSolutionCameras=FXCollections.observableArrayList<Camera>()
    val selectedSolution =SimpleBooleanProperty(false) //选中的方案
    val logc:LogController by  inject()
//    override val configPath:Path=PathUtil.resolvePath(Paths.get(".conf"))
    init {
        logc.writeLogToFile("方案控制器初始化成功")
        //加载方案
        subscribe<loadSolution> {event->
            val solution=loadSolu(event.solutionName)
            selectedSolutionCameras=solution?.cameraList
            fire(solutionList(selectedSolutionCameras))
            setSelected()
            fire(writeLogEvent(Level.INFO,"当前方案: ${solution!!.name}"))
        }
        //初始化所有相机
        subscribe<InitCameras> { event ->
            serviceStart.set(true)
            defaultCameraList().forEach { camera ->fire(InitCamera(camera))}
        }
        //初始化相机
        subscribe<InitCamera> { event ->
//            log.info("初始化相机 ${event.camera.name}}")
            fire(writeLogEvent(Level.INFO,"初始化相机 ${event.camera.name}"))

            val svc = object : CameraScheduledService(event.camera,this@SolutionController){}
            svc.period = Duration.seconds(1.0)
            svc.start()
            svc.setOnSucceeded { successEvent->
//                println(successEvent.source.value)
//                fire(writeLogEvent(Level.WARNING,"心跳@${event.camera.ip}：${successEvent.source.value}"))
            }
            svc.setOnFailed { fileEvent->
                fire(writeLogEvent(Level.WARNING,"${event.camera.name}@${event.camera.ip} ${fileEvent.source.value?:"连接超时"}"))
                cameraOffline(event.camera)
                if(serviceStart.value.not()){
                    svc.cancel()
                }
            }
            svc.setOnCancelled {
//                println(event.camera.name+"计划任务成功取消")
                fire(writeLogEvent(Level.INFO,"${event.camera.name}@${event.camera.ip} 计划任务成功取消"))
            }
            event.camera.lastwriteProperty().addListener { observable, oldValue, newValue ->
                if(!(oldValue?:"0").equals("0")) {
                    //获取最新写入的文件和文件所在目录
                    thread(true,true,null,null){
                        LastFileService(svc.api.baseURI!!,event.camera,this@SolutionController).start()
                    }
                }
            }
            //下载照片的线程
           thread(true,true,null,event.camera.ip){
               DownLoadService(event.camera,this@SolutionController).start()
            }
        }
        // 入队
        subscribe<enQueue> {event->
                enqueue(event.downloadAddress,event.camera)
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
        //新建方案->删除已添加的相机
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
        if(lastfile.size>0) {
            var fileName = lastfile.getString(FileName)
            var filePath = lastfile.getString(FilePath)
            camera.currimg = fileName
            camera.currpath = filePath
        }
    }

    /**
     * 入队
     */
    fun enqueue(downloadAddress: JsonObject,camera: Camera){
        camera.queue.put(downloadAddress)
        println("入队成功"+camera.queue.size)

        camera.photosizeProperty().bindBidirectional(SimpleStringProperty(camera.queueProperty().value.size.toString()))
    }
    fun  downloadJPG(lastfile: JsonObject,api: Rest,camera: Camera) {
        fire(writeLogEvent(Level.INFO,"${api.baseURI}/${lastfile.getString(FilePath)}"))
            camera.downloadStartProperty().value=true//开始下载
        var downFileResp = api.get(lastfile.getString(FilePath))
        var savepath="${currentTask.savePathProperty().value}/${currentTask.taskNameProperty().value}"
        var rename="${camera.ipProperty().value}_${lastfile.getString(FileName)}"
        try {
            if (downFileResp.ok()) {
                var fileStream = downFileResp.content()
                fileStream.use {
                    var path=  PathUtil.resolvePath(Paths.get(savepath)).resolve(rename)
                    Files.copy(it,PathUtil.resolvefile(path) , StandardCopyOption.REPLACE_EXISTING)
                    camera.queue.count()
                    camera.photosizeProperty().bindBidirectional(SimpleStringProperty(camera.queueProperty().value.size.toString()))
                    fire(writeLogEvent(Level.INFO,"${camera.name}@${camera.ip} : ${lastfile.getString(FileName)} 重命名为: "+rename))
                    fire(writeLogEvent(Level.INFO,"下载队列size: ${camera.queueProperty().value.size}"))

                    if(camera.queueProperty().value.isEmpty()){
//                        当下载队列为空的时候,再检查一下是否所有的照片都同步完了,防止因为失联导致丢片
                        thread(true,true,null,null){
//                            println("检查是否丢片")
                            logc.writeLogToFile("${camera.name}@${camera.ip} 检查是否丢片")
                            LastFileService(api.baseURI!!,camera,this@SolutionController).start()
                        }
                    }
                    camera.downloadStartProperty().value=false
                }
            }
        } catch (e: Exception) {
            logc.writeLogToFile(e.message?:"")
        }
    }
    /**
     * 相机离线
     */
    fun  cameraOffline(camera: Camera){
        camera.online=-1
//        camera.currimg=""
//        camera.currpath=""
    }
    fun cameraInit(camera: Camera){
        camera.online=0
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
                    set(Solutions.ROOT to Solutions(FXCollections.observableArrayList(SolutionName(solution.name))))
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
    fun  checkTask(){
         var  taskPath="${currentTask.savePathProperty().value}/${currentTask.taskNameProperty().value}"
        if (taskPath != null) {
            if(File(taskPath).isDirectory){
                File(taskPath).walk(FileWalkDirection.TOP_DOWN).filter { file->file.name.endsWith(".JPG",true) }.forEach { file:File-> println(file.name) }
            }

        }
    }

    /**
     * 新建方案
     */
    fun  newSolution(){
        find<NewSolutionWizard>(mapOf(NewSolutionWizard::solutionController to this)){
            onComplete {
                saveSolution(solution.item)
                cameras.clear()
            }
            openModal(stageStyle = StageStyle.UTILITY)
            Applicaiton_Modal.bind(isDockedProperty)
        }
    }

    /**
     * 新建任务
     */
    fun  newTask(){
        find<NewTaskWizard>(mapOf(NewTaskWizard::solutionController to this)) {
            onComplete {
                taskModel.commit(taskModel.taskName,taskModel.savePath)
                currentTask.savePath=taskModel.item.savePath
                currentTask.taskName=taskModel.item.taskName
//                print(taskModel.item)
//                print(currentTask)
            }
            openModal(stageStyle = StageStyle.UTILITY)
            Applicaiton_Modal.bind(isDockedProperty)
        }
    }
    /**
     *  检查方案是否选中
     */
    fun  isSelected(solutionName:String):Boolean{
        return config.string(SELECTED_SOLUTION)==solutionName
    }

    /**
     *
     */
    fun setSelected(){
        selectedSolution.set(true)
    }

    /**
     * 返回选中的方案名字
     * 不存在返回空
     */
    fun selectedSolution():String=config.string(SELECTED_SOLUTION)?:""
    /**
     * 已加载的方案，
     * 因为菜单是每次重新生成，加一个标识，比对 菜单勾选的方案，和列表加载的方案是不是同一个
     */
    fun isloaded(solutionName: String):Boolean{
        return  config.string(LOADED_SOLUTION)==solutionName
    }

    fun deleteSolution(solutionName: String){
        with(config) {
            if(solutionName.isNullOrEmpty().not()){
            if (solutionName == selectedSolution()) {
                config.remove(SELECTED_SOLUTION)
                selectedSolution.set(false)
            }
            config.remove(solutionName)
            var solutions = config.jsonModel<Solutions>(Solutions.ROOT)
            solutions?.solutionNames?.removeIf { it.name == solutionName }
            if(solutions?.solutionNames?.size==0){
                remove(Solutions.ROOT)
            }else {
                set(Solutions.ROOT to solutions)
            }
            save()
                fire(cleanlistview())
        }
        }
    }

    /**
     * 返回方案个数
     * 如果为空  返回0
     */
    fun solutionsSize():Int{
        var solutions= config.jsonModel<Solutions>(Solutions.ROOT)
        if (solutions != null) {
          return  solutions.solutionNames.size
        }else{
            return 0
        }
        return 0
    }

    /**
     * 检查是否有同名的
     */
    fun checkSolutionNameExists(solutionName: String):Boolean{
        var solutions= config.jsonModel<Solutions>(Solutions.ROOT)
            solutions?.solutionNames?.forEach { solutionname-> if(solutionname.name==solutionName){ return true}}
            return false
    }


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

    /*
     * 确保 选中了  方案之后调用
     * 否则出异常
     */
    fun defaultCameraList():ObservableList<Camera> = selectedSolutionCameras






}

class  loadSolution(val solutionName: String):FXEvent(EventBus.RunOn.BackgroundThread)//从config加载solution事件
class  cleanlistview():FXEvent(EventBus.RunOn.ApplicationThread)//删除当前方案，清除listview 的 items
class  solutionList(val cameras: ObservableList<Camera>):FXEvent(EventBus.RunOn.ApplicationThread)//将solution加载到 listView
class  saveCamera(val  camera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//保存Camera
class  putCameras(val cameras: ObservableList<Camera>):FXEvent(EventBus.RunOn.ApplicationThread)//将Camer添加到list中
class  selectedCamera(val selectedCamera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//选中的 list item
class  removeCamera():FXEvent(EventBus.RunOn.ApplicationThread)// 从list items中移除 item
class  InitCameras() : FXEvent(EventBus.RunOn.ApplicationThread)//初始化相机列表
class  InitCamera(val camera: Camera) : FXEvent(EventBus.RunOn.ApplicationThread)//初始化相机
class  enQueue(val downloadAddress:JsonObject,val camera: Camera):FXEvent(EventBus.RunOn.BackgroundThread)//入队