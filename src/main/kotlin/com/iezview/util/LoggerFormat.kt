package com.iezview.util

import java.time.Instant
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord

/**
 * Created by shishifanbuxie on 2017/4/16.
 * 日志格式化
 */
class LoggerFormat: Formatter() {
    override fun format(record: LogRecord?): String {
            var sb=StringBuilder()
                sb.append(Instant.now()).append(" ")
                if(record?.level== Level.INFO) sb.append("INFO ") else sb.append("ERROR ")
                sb.append(record?.message)
        return sb.toString()
    }
}