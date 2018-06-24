package com.github.acedroidx.batterytools

import android.util.Log
import android.widget.Toast
import kotlin.coroutines.experimental.coroutineContext

/**
 * Created by AceDroidX on 2018/6/18 0:38.
 */
class Log {
    companion object {
        fun d(str: String) {
            Log.d("wxxDebug", str)
        }
        fun e(str:String){
            Log.e("wxxDebug", str)
        }
    }
}