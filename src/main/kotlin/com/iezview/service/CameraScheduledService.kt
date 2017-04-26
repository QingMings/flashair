package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.model.Camera
import com.iezview.util.API
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/21.
 * 心跳维护
 *
 */
open class CameraScheduledService(camera: Camera, solutionController: SolutionController): ScheduledService<String>() {
    val api= Rest()// 网络访问 每个相机一个实例，互不干扰
    var solu=solutionController //方案控制器
    var c=camera //当前相机
    override fun createTask(): Task<String> {
        return  object : MyTask<String>(api,c,solu){}
    }
}

/**
 *  任务
 */
open class  MyTask<String>(// 网络访问 每个相机一个实例，互不干扰
        val api: Rest, camera: Camera, solutionController: SolutionController): Task<kotlin.String>(){
    val  c=camera//方案控制器
    val sc=solutionController //当前相机
    override fun call(): kotlin.String {
        if(sc.serviceStart.value.not()){cancel()}
        if(isCancelled){
            sc.cameraInit(c)
           return ""
         }
        api.engine.requestInterceptor={(it as HttpURLRequest).connection.readTimeout=1000}
        api.baseURI="${API.Base}${c.ipProperty().value}"
        val resp=api.get("${API.LastWrite}${System.currentTimeMillis()}")
                if(resp.ok()){
                    c.online=1
                    c.lastwrite=resp.text()
                }else{
                    sc.writeErrorlog("${c.ip}  连接异常")
                    sc.cameraOffline(c)
                }
        return c.lastwrite?:""
    }
}

