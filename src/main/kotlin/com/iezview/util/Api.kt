package com.iezview.util

import tornadofx.*
import javax.json.Json
import javax.json.JsonObject

/**
 * Created by shishifanbuxie on 2017/4/25.
 */
/**
 * FlashAir api
 */
 open class API{
    companion object{
        val Base="http://"
        val LastFile="/IEZView/api/lastFile.lua" //getlastFile
        val FileList="/command.cgi?op=100&DIR="//  getfileList from special dir
        val LastWrite="/command.cgi?op=121&TIME=" //     get start times when new file writing
    }
}

/**
 *  @API:lastFile.lua
 *  @return a jsonObject
 *  @desc：there is keys for return value
 */
open class  JK{
    companion object{
        val DirPath="dirPath"
        val FileName="fileName"
        val FilePath="filePath"
        /**
         * buildJsonObject
         */
         fun createFile(key: String, value: String): JsonObject = Json.createObjectBuilder().add(JK.FileName, key).add(JK.FilePath, "$value/$key").build()

    }
}

/**
 * 程序运行时配置路径
 */
open class Config{
    companion object{
        val Conf="${System.getProperty("user.home")}/.flashAir"
        val Img="${System.getProperty("user.home")}/img"
        val log= Conf+"/log"
        val AppicationName="Hello Flash Air"
        // 配置Wizard 中文
        fun chineseWizard(wizard: Wizard){
            wizard.stepsTextProperty.set("步骤")
            wizard.backButtonTextProperty.set("上一步")
            wizard.nextButtonTextProperty.set("下一步")
            wizard.finishButtonTextProperty.set("完成")
            wizard.cancelButtonTextProperty.set("取消")
        }
        fun  isMac():Boolean=System.getProperty("os.name").contains("mac",true)
    }

}

