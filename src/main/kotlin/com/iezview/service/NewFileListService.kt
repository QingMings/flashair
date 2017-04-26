package com.iezview.service

import com.iezview.controller.SolutionController
import com.iezview.controller.enQueue
import com.iezview.model.Camera
import com.iezview.util.API
import com.iezview.util.JK
import javafx.concurrent.Service
import javafx.concurrent.Task
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.json.Json
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/4/24.
 * 为了解决 丢片问题
 */
class NewFileListService(filedir: String, basiURI: String, camera: Camera, solutionController: SolutionController) : Service<kotlin.String>() {
    val api = Rest()
    val uri = basiURI
    val sc = solutionController
    val c = camera
    val dir = filedir
    override fun createTask(): Task<kotlin.String> {
        api.baseURI = uri
        api.engine.requestInterceptor = { (it as HttpURLRequest).connection.readTimeout = 3000 }
        return object : NewFileListTask(dir, api, c, sc) {}
    }

}

/**
 * 新任务检查
 * checkNewFile
 */
open class NewFileListTask(dir: String, api: Rest, camera: Camera, solutionController: SolutionController) : Task<String>() {
    val api = api
    val sc = solutionController
    val c = camera
    val dir = dir
    override fun call(): String {
        var lastfileResp = api.get("${API.FileList}$dir")
        if (lastfileResp.ok()) {
            var map = HashMap<String, String>()
            var lastfileStream = lastfileResp.content()
            BufferedReader(InputStreamReader(lastfileStream)).useLines { lines ->
                lines.forEach {
                    var temp = it.split(",")
                    if (temp.size == 6) {
                        map.put(temp[1], temp[0])
                    }
                }
            }
            if (c.filemap == null) {
                c.filemap = map
                var file = createFile(c.filemap.keys.last(), c.filemap.values.last())
//                println("first: ${file.getString(JK.FileName)}")
                sc.fire(enQueue(file, c))
            } else {
                map.forEach { key, value ->
                    if (c.filemap.containsKey(key).not()) {
//                        println(key)
                        var file = createFile(key, value)
                        sc.fire(enQueue(file, c))
                        c.filemap.put(key, value)
                    }
                }
            }
        }
        return ""
    }

    /**
     * buildJsonObject
     */
    private fun createFile(key: String, value: String): JsonObject = Json.createObjectBuilder().add(JK.FileName, key).add(JK.FilePath, "$value/$key").build()
}