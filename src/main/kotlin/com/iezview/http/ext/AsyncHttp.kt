package com.iezview.http.ext

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import tornadofx.*
import java.net.URI

/**
 * Created by shishifanbuxie on 2017/5/10.
 * 异步http 请求
 */

class  HttpAsyncClientEngine(val rest: Rest): Rest.Engine(){
    lateinit var client: CloseableHttpAsyncClient
//    lateinit var context: HttpAsyncc
    init {
        reset()
    }
    override fun setBasicAuth(username: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reset() {
        client =HttpAsyncClientBuilder.create().build()

    }

    override fun request(seq: Long, method: Rest.Request.Method, uri: URI, entity: Any?): Rest.Request {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
