package com.iezview.controller

import com.iezview.util.ArrowFactory
import com.iezview.util.LoggerFormat
import com.iezview.util.PathUtil
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
    }
    val consoleView: CodeArea=CodeArea()//日志输出视图
    //配置 log输出位置
    val  logPath= Paths.get("log")
    val  logfilePath=logPath.resolve(LocalDate.now().toString()+".log")
    init {
        println("log组件初始化")
        PathUtil.resolvePath(logPath)
        PathUtil.resolvePath(logfilePath)
        val fileHandler =FileHandler(logfilePath.toString(),true)
        fileHandler.formatter=LoggerFormat()
        log.addHandler(fileHandler)
        subscribe<writeLogEvent>{event->writeOutlog(event.loglevel,event.message)}
        subscribe<cleanLogEvent>{cleanlog()}
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
    fun writeOutlog(loglevel:Level, message:String):Unit{
        if(loglevel==Level.INFO){
            log(message, GREEN)
            log.info(message+"\n")
        }else if(loglevel== Level.WARNING){
           log(message, RED)
        }
    }
    /**
     * 打印日志
     */
    private  fun  log(message: String,color:String){
        var range = consoleView.selection
        var start =range.start;
        consoleView.appendText(message+"\n")
        range = consoleView.selection
        consoleView.setStyleClass(start,range.end,color)
        consoleView.moveTo(consoleView.paragraphs.size-1,0)
        consoleView.requestFollowCaret()
        consoleView.isAutoScrollOnDragDesired=true
    }
    /**
     * 清理日志
     */
    fun  cleanlog():Unit=consoleView.clear()
}

class writeLogEvent(val loglevel: Level,val message: String) :FXEvent(EventBus.RunOn.ApplicationThread)//写日志事件
class cleanLogEvent():FXEvent(EventBus.RunOn.ApplicationThread)//清除日志事件