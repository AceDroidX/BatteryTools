package com.github.acedroidx.batterytools.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.acedroidx.batterytools.R
import com.github.acedroidx.batterytools.SuDo
import kotlinx.android.synthetic.main.main_fragment.*
import android.R.id.edit
import android.content.SharedPreferences
import android.widget.SeekBar
import android.content.Intent
import android.content.IntentFilter
import com.github.acedroidx.batterytools.BatteryReceiver
import com.github.acedroidx.batterytools.Log


class MainFragment : Fragment() {
    val batteryReceiver = BatteryReceiver()

    companion object {
        //引用https://github.com/helloklf/vtools/blob/master/app/src/main/java/com/omarea/shared/Consts.kt
        val DisableChanger = "if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 0 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 1 > /sys/class/power_supply/battery/input_suspend; fi;setprop BatteryTools.bp 1;\n"
        val ResumeChanger = "if [ -f '/sys/class/power_supply/battery/battery_charging_enabled' ]; then echo 1 > /sys/class/power_supply/battery/battery_charging_enabled; else echo 0 > /sys/class/power_supply/battery/input_suspend; fi;setprop BatteryTools.bp 0;\n"
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        val settings = context?.getSharedPreferences("BatterySetting", 0)
        val editor = settings?.edit()
        //初始化控件
        seekBar_maxAutoCharge.progress = settings!!.getInt("maxBattery", 100)
        seekBar_minAutoCharge.progress = settings.getInt("minBattery", 100)
        text_max.text = settings.getInt("maxBattery", 100).toString()
        text_min.text = settings.getInt("minBattery", 100).toString()
        val state = settings.getInt("state", 0)
        switch_autoCharge.isChecked = state == 1
        //ClickListener
        button_ResumeChanger.setOnClickListener { SuDo(context).execCmdSync(ResumeChanger) }
        button_DisableChanger.setOnClickListener { SuDo(context).execCmdSync(DisableChanger) }
        switch_autoCharge.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor?.putInt("state", 1)?.apply()
                val intentFilter = IntentFilter()
                intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                context?.registerReceiver(batteryReceiver, intentFilter)
            } else {
                editor?.putInt("state", 0)?.apply()
                context?.unregisterReceiver(batteryReceiver)
            }
        }
        seekBar_maxAutoCharge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Something
                text_max.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Just an empty method
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Another empty method
                Log.d("max:" + seekBar.progress.toString())
                editor?.putInt("maxBattery", seekBar.progress)?.apply()
            }
        })
        seekBar_minAutoCharge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Something
                text_min.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Just an empty method
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Another empty method
                Log.d("min:" + seekBar.progress.toString())
                editor?.putInt("minBattery", seekBar.progress)?.apply()
            }
        })
    }

}
