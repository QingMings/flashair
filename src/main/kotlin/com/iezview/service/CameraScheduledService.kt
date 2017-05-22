package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.controller.writeLogEvent
import com.iezview.model.Camera
import com.iezview.util.API
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import tornadofx.*
import java.util.logging.Level
import kotlin.concurrent.thread

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
//        println("创建计划任务"+c.ip)
        return  object : MyTask<String>(api,c,solu){}
    }
}

/**
 *  任务
 */
open class  MyTask<String>(// 网络访问 每个相机一个实例，互不干扰
         api: Rest, camera: Camera, solutionController: SolutionController): Task<kotlin.String>(){
    val api =api
    val  c=camera//方案控制器
    val sc=solutionController //当前相机
    override fun call(): kotlin.String {
        if(sc.serviceStart.value.not()){cancel()}
        if(isCancelled){
            sc.cameraInit(c)
           return ""
         }
        if(c.downloadStartProperty().value){
            return ""
        }
        thread(true,true,null,c.ip) {
//            println(Thread.currentThread().name)

            try {
                Rest.useApacheHttpClient()
            api.engine.requestInterceptor = {
                (it as HttpURLRequest).connection.readTimeout = 1500
                (it as HttpURLRequest).connection.connectTimeout=1500
            }
            api.baseURI = "${API.Base}${c.ipProperty().value}"
            val resp = api.get("${API.LastWrite}${System.currentTimeMillis()}")
            if (resp.ok()) {
                if (resp.text()!!.length < 30) {
                    //如果最后写入事件和上次值相同，则没有拍摄新照片 ，就不发送 更新UI事件了
                                sc.cameraOnLine(c)
                            c.lastwrite =resp.text()
                } else {
                        sc.writeErrorlog("${c.ip}  连接异常")
                        sc.cameraOffline(c)
                }

            } else {
                    sc.writeErrorlog("${c.ip}  连接异常")
                    sc.cameraOffline(c)

            }
            if (c.lastwrite != null) {
                    sc.fire(writeLogEvent(Level.WARNING, "心跳@${c.ip}：${c.lastwrite ?: ""}"))

            }
        }catch (e:Exception){
                println(c.ip +e.message)
//                    sc.writeErrorlog("${c.ip}  连接异常")
                    sc.cameraOffline(c)
            }
        }

        return c.lastwrite?:""
    }
}

