package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.controller.enQueue
import com.iezview.model.Camera
import javafx.concurrent.Service
import javafx.concurrent.Task
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/23.
 * 获取最新拍摄的一张照片信息 服务
 */
class LastFileService(basiURI:String, camera: Camera, solutionController: SolutionController): Service<String>() {
    val api = Rest()
    val uri=basiURI
    val sc=solutionController
    val c=camera
    override fun createTask(): Task<String> {
        api.baseURI=uri
        api.engine.requestInterceptor={(it as HttpURLRequest).connection.readTimeout=2000}
        return object : LastFileTask(api,c,sc){}
    }
}

/**
 * 获取最新拍摄的一张照片 任务
 */
open class  LastFileTask(api: Rest, camera: Camera, solutionController: SolutionController): Task<String>(){
    val api=api
    val sc=solutionController
    val c=camera
    override fun call(): String {

        println(api.baseURI)
        var lastileResp = api.get("/api/lastFile.lua")
        println("访问最后文件事件触发")
        println("访问是否成功： "+lastileResp.ok())
        if (lastileResp.ok()&&lastileResp.one().size>0) {
            println("获得范文结果")
            sc.cameraOnline(lastileResp.one(), c)
            sc.fire(enQueue(lastileResp.one(),c))
            println("加入队列:")
        }
        return ""
    }

}
