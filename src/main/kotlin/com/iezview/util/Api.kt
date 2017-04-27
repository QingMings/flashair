package com.iezview.util

/**
 * Created by shishifanbuxie on 2017/4/25.
 */
/**
 * FlashAir api
 */
 open class API{
    companion object{
        val Base="http://"
        val LastFile="/api/lastFile.lua" //getlastFile
        val FileList="/command.cgi?op=100&DIR="//  getfileList from special dir
        val LastWrite="/command.cgi?op=121&TIME=" // get start times when new file writing
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
    }
}
