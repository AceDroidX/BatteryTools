package com.github.acedroidx.batterytools

import android.app.*
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.widget.Toast
import android.os.IBinder
import com.github.acedroidx.batterytools.fragment.MainFragment
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat


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

    override fun onDestroy() {
        super.onDestroy()
        isrunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Log.d("intent:" + intent.toString())
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }
        val backIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, backIntent, 0)
        val notification = NotificationCompat.Builder(this,channelId)
                .setContentTitle("后台运行通知")
                .setContentText("应用正在后台运行中")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build()
        startForeground(1, notification)
        if (intent == null) {
            Log.d("intent:null")
            register()
        } else {
            when (intent.action) {
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
                else -> {
                    Log.d("intent:else")
                    register()
                }
            }
        }
        return Service.START_STICKY
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

    private fun register() {
        val settings = applicationContext.getSharedPreferences("BatterySetting", 0)
        val editor = settings?.edit()
        editor?.putInt("state", 1)?.apply()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(batteryReceiver, intentFilter)
        isrunning = true
    }

    private fun unregister() {
        try {
            val settings = applicationContext.getSharedPreferences("BatterySetting", 0)
            val editor = settings?.edit()
            editor?.putInt("state", 0)?.apply()
            applicationContext.unregisterReceiver(batteryReceiver)
            stopSelf()
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e(e.localizedMessage)
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
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

    private fun changeBattery(type: String) {
        val bp = SuDo(applicationContext).execReturn("getprop BatteryTools.bp")
        when (type) {
            "disable" -> {
                if (bp != "1") SuDo(applicationContext).execCmdSync(MainFragment.DisableChanger)
            }
            "resume" -> {
                if (bp != "0") SuDo(applicationContext).execCmdSync(MainFragment.ResumeChanger)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "background"
        val channelName = "后台服务通知"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    companion object {
        var isrunning = false

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
