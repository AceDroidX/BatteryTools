package com.github.acedroidx.batterytools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            //得到系统当前电量
            val level = intent.getIntExtra("level", 0)
            //取得系统总电量
            val total = intent.getIntExtra("scale", 100)
            Log.d("now Level:"+level.toString())
            BatteryService.startActionChange(context,level,total)
        }
    }
}
