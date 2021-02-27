package com.example.firebasechatapp.util

import java.text.SimpleDateFormat
import java.text.DateFormat
import java.util.*

object TimeUtil {
    fun timeStampToDate(timeStamp:String): String{
        return  try {
            val sdf: DateFormat= SimpleDateFormat("HH:mm")
            val netDate = Date(timeStamp.toLong())
            sdf.format(netDate)
        }catch (ex: Exception){
        "xx"
        }
    }
}