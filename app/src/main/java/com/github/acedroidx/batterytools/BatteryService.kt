package com.github.acedroidx.batterytools

import android.app.Service
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.widget.Toast
import android.os.IBinder
import com.github.acedroidx.batterytools.fragment.MainFragment


private const val ACTION_REG = "com.github.acedroidx.batterytools.action.REG"
private const val ACTION_UNREG = "com.github.acedroidx.batterytools.action.UNREG"
private const val ACTION_CHANGE = "com.github.acedroidx.batterytools.action.CHANGE"

private const val EXTRA_LEVEL = "com.github.acedroidx.batterytools.extra.LEVEL"
private const val EXTRA_TOTAL = "com.github.acedroidx.batterytools.extra.TOTAL"

class BatteryService : Service() {

    private var state: Int = 2
    private var maxBattery: Int = 505
    private var minBattery: Int = 505
    val batteryReceiver = BatteryReceiver()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_REG -> {
                register()
            }
            ACTION_UNREG -> {
                unregister()
            }
            ACTION_CHANGE -> {
                val param1: Int = intent.getIntExtra(EXTRA_LEVEL, 404)
                val param2: Int = intent.getIntExtra(EXTRA_TOTAL, 404)
                handleActionChange(param1, param2)
            }
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun handleActionChange(param1: Int, param2: Int) {
        if (param1 > 100 || param2 > 100) {
            Toast.makeText(applicationContext, "数据错误1", Toast.LENGTH_LONG).show()
        }
        getBatterySetting()
        if (state != 1) {
            return
        }
        if (param1 >= maxBattery) {
            changeBattery("disable")
        } else if (param1 <= minBattery) {
            changeBattery("resume")
        }
    }

    private fun register(){
        val settings = applicationContext.getSharedPreferences("BatterySetting", 0)
        val editor = settings?.edit()
        editor?.putInt("state", 1)?.apply()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(batteryReceiver, intentFilter)
    }
    private fun unregister(){
        val settings = applicationContext.getSharedPreferences("BatterySetting", 0)
        val editor = settings?.edit()
        editor?.putInt("state", 0)?.apply()
        applicationContext.unregisterReceiver(batteryReceiver)
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

    private fun changeBattery(type: String) {
        val bp = SuDo(applicationContext).execReturn("getprop BatteryTools.bp")
        when (type) {
            "disable" -> {
                Log.d("disable")
                if (bp != "1") SuDo(applicationContext).execCmdSync(MainFragment.DisableChanger)
            }
            "resume" -> {
                Log.d("resume")
                if (bp != "0") SuDo(applicationContext).execCmdSync(MainFragment.ResumeChanger)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, BatteryService::class.java).apply {
                action = ACTION_REG
            }
            context.startService(intent)
        }

        @JvmStatic
        fun stopService(context: Context) {
            val intent = Intent(context, BatteryService::class.java).apply {
                action = ACTION_UNREG
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
