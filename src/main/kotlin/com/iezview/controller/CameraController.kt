package com.iezview.controller

import com.iezview.model.Camera
import javafx.collections.ObservableList
import tornadofx.*
import java.net.ConnectException
import java.net.Inet4Address
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Created by shishifanbuxie on 2017/4/11.
 */


class CameraController : Controller() {
    val solutionController: SolutionController by inject()
    val api: Rest = Rest()

    companion object {
        val FileName = "fileName"
        val FilePath = "filePath"
    }

    fun online(IP: String) {
        api.baseURI = "http://" + IP
        println("执行了")
        try {
            println(api.get("/command.cgi?op=121&TIME=" + System.currentTimeMillis()).text())
//            api.get("/command.cgi?op=121&TIME="+System.currentTimeMillis()).ok()
        } catch (ce: ConnectException) {
            println(ce.message)
        } finally {

        }

//        var url = URL("http://"+IP)
//        println("执行了")
//        try {
//
//            val inputStream=url.openStream()
//            println("连接正常")
//            inputStream.close()
//        }catch (e:IOException){
//                println("无法连接到" + url.toString())
//        }
//        try {
//        var  address= Inet4Address.getByName(IP)
//            if( address.isReachable(5000)){
//                print("连通")
//            }else{
//                print("不通")
//            }
//        }catch (e:Exception){
//            println(e.message)
//        }

    }

    init {
//        println(" CamraController")
//        subscribe<Keepconn> { event ->
//            //            触发事件
//            online(event.IP)
//        }



    }

    fun testConn(camera: Camera) {
//            var api =Rest()
//            try {
//                if(api.get("http://"+camera.ip+"/command.cgi?op=121&TIME="+System.currentTimeMillis()).ok()){
//                        camera.online=1
//                }
//            }catch (ex:ConnectException){
//                camera.online=-1
//                fire(writeLogEvent(Level.WARNING,"相机 ${camera.ip} 连接异常： ${ex.message?:""}"))
//
//            }finally {
//                println("testConn method 执行了")
//                fire(updateCamera())
//            }
//
//            println("testConn method 执行了")
////            camera.name="444"
//            camera.online=1

        task { }
        try {
            var address = Inet4Address.getByName(camera.ip)
            if (address.isReachable(5000)) {
                camera.online = 1
            } else {
                camera.online = -1
            }
        } catch (e: Exception) {
            camera.online = -1
        } finally {
            fire(updateCamera())
        }


    }

    fun init() {
        print("初始化")
    }

    /**
     * 获取选中的solution 的 cameraList
     */
    fun getDefaultCameraList(): ObservableList<Camera> = solutionController.defaultCameraList()

    /**
     * 获得相机最新的照片
     * return json
     */
    fun getlastFile(ip: String) {
        var api = Rest()
        api.baseURI = "http://$ip"
        var resp = api.get("/api/lastFile.lua")
        if (resp.ok()) {
            downloadJPG(api, resp)
        }


    }

    /**
     * 下载图片
     */
    fun downloadJPG(api: Rest, resp: Rest.Response) {
        var downFileResp = api.get(resp.one().getString(FilePath).toString())
        try {
            if (downFileResp.ok()) {
                var fileStream = downFileResp.content()
                fileStream.use {
                    Files.copy(it, resolvePath(Paths.get("img").resolve(resp.one().getString(FileName))), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }
//    fun download(link: String): Boolean {
//        try {
//            URL(link).openStream().use {
//                val fileName = link.substringAfterLast("/")
//
//                Files.copy(it, Paths.get("./$fileName"), StandardCopyOption.REPLACE_EXISTING)
//                return true
//            }
//        } catch (e: Exception) {
//            println(e)
//            return false
//        }
//    }
    /**
     * 创建目录
     */
    private fun resolvePath(savepath: Path): Path {
        if (!Files.exists(savepath)) {
            Files.createDirectories(savepath)
        }
        return savepath
    }

    fun  testservice(){
//        var  svc = ScheduledService<String>
    }
}


