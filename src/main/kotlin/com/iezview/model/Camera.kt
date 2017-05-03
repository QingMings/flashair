package com.iezview.model

import tornadofx.*
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 相机 model
 */
class Camera():JsonModel{
    var id  =UUID.randomUUID()
//    fun idProperty() = getProperty(Camera::id)
    var name by property<String>()
    fun nameProperty() =getProperty(Camera::name)
    var ip by property<String>()
    fun ipProperty() =getProperty(Camera::ip)
    var online by property<Int>()
    fun onlineProperty()=getProperty(Camera::online)
    var currimg by property<String>()
    fun currimgProperty()=getProperty(Camera::currimg)
     var currpath by property<String>()
    fun currpathProperty()=getProperty(Camera::currpath)
    var lastwrite by property<String>()
    fun  lastwriteProperty()=getProperty(Camera::lastwrite)
    var queue  by property(LinkedBlockingDeque<JsonObject>(150))//提供一个容量为150的阻塞队列
    fun queueProperty()=getProperty(Camera::queue)
    var photosize by property("0")
    fun photosizeProperty()=getProperty(Camera::photosize)
    var filemap by property<HashMap<String,String>>()
    var downloadStart by property(false)
    fun downloadStartProperty() =getProperty(Camera::downloadStart)

    override fun updateModel(json: JsonObject) {
        super.updateModel(json)
        with(json){
            id=uuid("id")
            name=string("name")
            ip=string("ip")
            online=int("online")
            currimg=string("currimg")
            currpath=string("currpath")
            lastwrite=string("lastwrite")
            photosize=string("photosize")

        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json){
            add("id",id)
            add("name",name)
            add("ip",ip)
//            add("online",online)
//            add("currimg",currimg)
//            add("currpath",currpath)
        }
    }

    override fun toString(): String {
        return "Camera(id=$id,name=$name,ip=$ip,online=$online,currimg=$currimg,currpath=$currpath,lastwrite=$lastwrite,photosize=$photosize)"
    }
}

class CameraModel() : ItemViewModel<Camera>(Camera()) {
//    val id = bind { item?.idProperty() }
    val name = bind(Camera::nameProperty,true)
    val ip = bind (Camera::ipProperty,true)
    val online = bind (Camera::onlineProperty,true)
    val currimg = bind (Camera::currimgProperty,true)
    val currpath = bind (Camera::currpathProperty,true)
}

/**
 *
 */
class CameraState() {
    companion object {
        val onLine = 1
        val offLine = 0
        val errorConn = -1
    }
}

