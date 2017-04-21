package com.iezview.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Created by shishifanbuxie on 2017/4/20.
 * 日期帮助类
 *  convert  exFat timestamp to osUTC  or java.util.Date
 */

class DateUtil {
    /**
     * @input exFat timestamp
     * @out java.util.Date
     *@onException return new Date()
     */
    fun exFatTimeStamp2date(exFatTimeStamp: Int): Date {
        try {
            val year = (0xFE000000.toInt() and exFatTimeStamp shr 25) + 1980
            val mouth = 0x1e00000 and exFatTimeStamp shr 21
            val day = 0x001F0000 and exFatTimeStamp shr 16
            val H = 0xf800 and exFatTimeStamp shr 11
            val m = 0x07E0 and exFatTimeStamp shr 5
            val s = (0x001F and exFatTimeStamp) * 2
            var localDateTime = LocalDateTime.of(year, mouth, day, H, m, s)
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())

        } catch (e: Exception) {
            return Date()
        }

    }

    /**
     *  将系统utc 时间戳  转换成  exFat 时间戳
     *  @input  utc time   .e.g  1492676781615
     *  @out   exfat time  .e.g  1251218286
     *  @OnException  return   now() exfat timestamp
     */
    fun utcTimeStamp2exFatTimeStampFromEpochMilli(utcTimeStampMilli: Long): Int {
        try {

            return  convert(utcTimeStampMilli)
        } catch(e:Exception){
               return convert(System.currentTimeMillis())
        }
    }

    private  fun  convert(utcTimeStampMilli: Long):Int{
        var instant = Instant.ofEpochMilli(utcTimeStampMilli)
        var localtime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        var ref_year = ((localtime.year - 1980) shl 25)
        var ref_mouth = (localtime.month.value shl 21)
        val ref_day = (localtime.dayOfMonth shl 16)
        val ref_H = localtime.hour shl 11
        val ref_m = localtime.minute shl 5
        val ref_s = (localtime.second / 2)
        return ref_year + ref_mouth + ref_day + ref_H + ref_m + ref_s
    }
}