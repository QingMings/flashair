package com.iezview.controller

import com.iezview.util.ArrowFactory
import com.iezview.util.Config
import com.iezview.util.LoggerFormat
import com.iezview.util.PathUtil
import com.iezview.view.consoleViewStyle
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import tornadofx.*
import java.nio.file.Paths
import java.time.LocalDate
import java.util.function.IntFunction
import java.util.logging.FileHandler
import java.util.logging.Level

/**
 * Created by shishifanbuxie on 2017/4/16.
 * 日志 Controller
 */

class  LogController:Controller(){
    companion object{
        val GREEN="green"
        val RED="red"
        val ORANGE="orange"
    }
    val consoleView: CodeArea=CodeArea()//日志输出视图
    //配置 log输出位置
    val  logPath= Paths.get(Config.log)!!
    val  logfilePath= logPath.resolve(LocalDate.now().toString()+".log")!!
    init {
        importStylesheet(consoleViewStyle::class)
        writeLogToFile("log组件初始化")
        PathUtil.resolvePath(logPath)
        PathUtil.resolvefile(logfilePath)
        val fileHandler =FileHandler(logfilePath.toString(),true)
        fileHandler.formatter=LoggerFormat()
        log.addHandler(fileHandler)
        subscribe<writeLogEvent>{event-> writeOutLog(event.loglevel,event.message)}
        subscribe<cleanLogEvent>{cleanlog()}
        subscribe<writeReplaceEvent> { event-> writeProgress(event.logLevel,event.message) }
    }
    /**
     * RichText 控件,用来显示日志输出
     */
    fun  regConsoleView():CodeArea{
        var numberF= LineNumberFactory.get(consoleView)//行号
        var arrowF= ArrowFactory(consoleView.currentParagraphProperty())//箭头指示
        var graphicF = IntFunction<Node> { line ->
            val hbox = HBox( arrowF.apply(line))
            hbox.alignment = Pos.CENTER_LEFT
            return@IntFunction hbox
        }
        consoleView.setParagraphGraphicFactory(graphicF)
        consoleView.isEditable=false//禁止编辑

        return consoleView
    }
    /**
     * 写日志
     */
    fun writeOutLog(logLevel:Level, message:String):Unit{
        if(logLevel==Level.INFO){
            log(message, GREEN)
            log.info(message)
        }else if(logLevel== Level.WARNING){
           log(message, ORANGE)
            log.warning(message)
        }else{
            log(message, RED)
            log.warning(message)
        }
    }
    fun writeProgress(logLevel: Level,message: String){
        if(logLevel==Level.INFO){
            replace(message, GREEN)
            log.info(message)
        }else if(logLevel== Level.WARNING){
            replace(message, ORANGE)
            log.warning(message)
        }else{
            replace(message, RED)
            log.warning(message)
        }
    }
    /**
     * 写日志到文件
     */
    fun writeLogToFile(message: String){
        log.info(message)
    }
    /**
     * 打印日志
     */
    private  fun  log(message: String,color:String){
         if(consoleView.paragraphs.size>10){
             consoleView.clear()
         }
        var range = consoleView.selection
        var start =range.start
        consoleView.appendText(message+"\n")
        range = consoleView.selection
        consoleView.setStyleClass(start,range.end,color)
        consoleView.moveTo(consoleView.paragraphs.size-1,0)
        consoleView.requestFollowCaret()

        consoleView.isAutoScrollOnDragDesired=true
    }
    private  fun replace(message: String ,color: String){
        if(consoleView.paragraphs.size>10){
            consoleView.clear()
        }
        consoleView.moveTo(consoleView.paragraphs.size-1,0)
        var range = consoleView.selection
        var start =range.start

//        consoleView.appendText(message+"\n")
//        consoleView.replaceText(range,message+"\n")
        consoleView.replaceText(start,range.end,"sdfasdf")
        range = consoleView.selection
        consoleView.setStyleClass(start,range.end,color)

        consoleView.requestFollowCaret()

        consoleView.isAutoScrollOnDragDesired=true
    }
    /**
     * 清理日志
     */
    fun  cleanlog():Unit=consoleView.clear()
}

class writeLogEvent(val loglevel: Level,val message: String) :FXEvent(EventBus.RunOn.ApplicationThread)//写日志事件
class cleanLogEvent :FXEvent(EventBus.RunOn.ApplicationThread)//清除日志事件
class writeReplaceEvent(val logLevel: Level,val message: String):FXEvent(EventBus.RunOn.ApplicationThread)//进度君