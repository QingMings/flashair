package com.iezview.flashair

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by shishifanbuxie on 2017/4/20.
 */
object Test1 {

    @JvmStatic fun main(args: Array<String>) {
        val fattime = 1251218286
        val year = (0xFE000000.toInt() and fattime shr 25) + 1980
        val mouth = 0x1e00000 and fattime shr 21
        val day = 0x001F0000 and fattime shr 16
        val H = 0xf800 and fattime shr 11
        val m = 0x07E0 and fattime shr 5
        val s = (0x001F and fattime) * 2
        var sb = StringBuilder()
            sb.append(year)
                    .append("-")
                    .append(if (mouth.toString().length>1) mouth else "0"+mouth.toString())
                    .append("-")
                    .append(if(day.toString().length>1)  day else "0"+day.toString())
                    .append(" ")
                    .append(if (H.toString().length>1) H else "0"+H.toString() )
                    .append(":")
                    .append(if (m.toString().length>1) m else "0"+H.toString())
                    .append(":")
                    .append(if (s.toString().length>1) m else "0"+s.toString())
         var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

          var date = LocalDateTime.parse(sb.toString(),format)
            var localDateTime= LocalDateTime.of(year,mouth,day,H,m,s)
            var  javadate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
//    print(date::class)
//        println(localDateTime::class)
//        print(javadate::class)


        var ref_year=((year-1980)shl 25)
        var ref_mouth=(mouth shl 21)
        val ref_day = (day shl 16)
        val ref_H = H shl 11
        val ref_m =m shl 5
        val ref_s= (s / 2)
        println(ref_year)

        println(ref_mouth)
        println(ref_day)
        println(ref_H)
        println(ref_m)
        println(ref_s)

        println(ref_year+ref_mouth+ref_day+ref_H+ref_m+ref_s)
        println(System.currentTimeMillis())

       var instant= Instant.ofEpochMilli(1492676258)
        var localtime= LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
       localtime.year
        println(localtime.year)
    }
}
