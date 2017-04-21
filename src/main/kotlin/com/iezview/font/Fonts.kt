package com.iezview.font

import javafx.scene.text.Font

/**
 * Created by shishifanbuxie on 2017/4/11.
 */
class Fonts {
      companion object{
        private  var  LATO_LIGHT_NAME:String = Font.loadFont(Fonts::class.java.getResourceAsStream("/com/iezview/font/Lato-Lig.otf"),10.0).name
        private  var  LATO_REGULAR_NAME:String = Font.loadFont(Fonts::class.java.getResourceAsStream("/com/iezview/font/Lato-Reg.otf"),10.0).name
        private  var  LATO_BOLD_NAME:String = Font.loadFont(Fonts::class.java.getResourceAsStream("/com/iezview/font/Lato-Bol.otf"),10.0).name
        fun latoLight(size:Double): Font=Font(LATO_LIGHT_NAME,size)
        fun latoRegular(size:Double): Font=Font(LATO_LIGHT_NAME,size)
        fun latoBold(size:Double): Font=Font(LATO_LIGHT_NAME,size)
   }


}