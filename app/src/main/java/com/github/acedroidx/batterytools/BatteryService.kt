package com.github.acedroidx.batterytools

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.widget.Toast
import android.content.SharedPreferences
import com.github.acedroidx.batterytools.fragment.MainFragment


private const val ACTION_START = "com.github.acedroidx.batterytools.action.START"
private const val ACTION_CHANGE = "com.github.acedroidx.batterytools.action.CHANGE"

private const val EXTRA_LEVEL = "com.github.acedroidx.batterytools.extra.LEVEL"
private const val EXTRA_TOTAL = "com.github.acedroidx.batterytools.extra.TOTAL"

class BatteryService : IntentService("BatteryService") {
    private var state: Int = 2
    private var maxBattery: Int = 505
    private var minBattery: Int = 505

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_START -> {
                handleActionStart()
            }
            ACTION_CHANGE -> {
                val param1: Int = intent.getIntExtra(EXTRA_LEVEL, 404)
                val param2: Int = intent.getIntExtra(EXTRA_TOTAL, 404)
                handleActionChange(param1, param2)
            }
        }
    }

    private fun handleActionStart() {

    }

    private fun handleActionChange(param1: Int, param2: Int) {
        if (param1 > 100 || param2 > 100) {
            Toast.makeText(applicationContext, "数据错误1", Toast.LENGTH_LONG).show()
        }
        getBatterySetting()
        if(state!=1){
            return
        }
        if (param1 >= maxBattery) {
            SuDo(applicationContext).execCmdSync(MainFragment.DisableChanger)
        } else if (param1 <= minBattery) {
            SuDo(applicationContext).execCmdSync(MainFragment.ResumeChanger)
        }
    }

    private fun getBatterySetting() {
        // Restore preferences
        val settings = getSharedPreferences("BatterySetting", 0)
        state = settings.getInt("state", 3)
        maxBattery = settings.getInt("maxBattery", 404)
        minBattery = settings.getInt("minBattery", 404)
        if (maxBattery > 100 || minBattery > 100 || state > 1) {
            Toast.makeText(applicationContext, "数据错误2", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, BatteryService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionChange(context: Context, param1: Int, param2: Int) {
            val intent = Intent(context, BatteryService::class.java).apply {
                action = ACTION_CHANGE
                putExtra(EXTRA_LEVEL, param1)
                putExtra(EXTRA_TOTAL, param2)
            }
            context.startService(intent)
        }
    }
}
