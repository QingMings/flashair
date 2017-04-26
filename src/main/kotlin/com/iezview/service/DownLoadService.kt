package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.model.Camera
import com.iezview.util.API
import javafx.concurrent.Service
import javafx.concurrent.Task
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/23.
 * 下载文件服务
 */
class DownLoadService(camera: Camera, solutionController: SolutionController): Service<String>() {

    var c=camera
    val api= Rest()
    var sc=solutionController

    override fun createTask(): Task<String> {
            return  object: DownLoadTask<String>(c,api,sc){}
    }
}

/**
 * 下载文件任务
 */
open class DownLoadTask<String>(camera: Camera, api: Rest, solutionController: SolutionController): Task<kotlin.String>(){
    val queue=camera.queue
    var api=api// 网络访问 每个相机一个实例，互不干扰
    val  c=camera//方案控制器
    val sc=solutionController //当前相机
    override fun call(): kotlin.String {
        api.engine.requestInterceptor={(it as HttpURLRequest).connection.readTimeout=10000}
        api.baseURI="${API.Base}${c.ipProperty().value}"
        while (true){
        val filepath=queue.take()
        sc.downloadJPG(filepath,api,c)
        }
        return ""
    }


}