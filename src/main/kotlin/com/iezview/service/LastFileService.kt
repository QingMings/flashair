package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.model.Camera
import com.iezview.util.API
import com.iezview.util.JK
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
        api.engine.requestInterceptor={(it as HttpURLRequest).connection.readTimeout=5000}
        return object : LastFileTask(api,c,sc){}
    }

}

/**
 * 获取最新拍摄的一张照片 任务
 */
open class  LastFileTask(val api: Rest, camera: Camera, solutionController: SolutionController): Task<String>(){
    val sc=solutionController
    val c=camera
    override fun call(): String {
        var lastileResp = api.get(API.LastFile)
        while(!lastileResp.ok()){
            Thread.sleep(500)
            lastileResp =api.get(API.LastFile)
        }
            println("获取最后照片")
        if (lastileResp.ok()&&lastileResp.one().size>0) {
                sc.cameraOnline(lastileResp.one(), c)
            NewFileListService(lastileResp.one().getString(JK.DirPath),api.baseURI!!,c,sc).start()
        }
        return ""
    }

}
