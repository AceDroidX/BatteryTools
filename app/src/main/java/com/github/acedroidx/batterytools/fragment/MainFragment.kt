package com.github.acedroidx.batterytools.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.main_fragment.*
import android.widget.SeekBar
import com.github.acedroidx.batterytools.*


class MainFragment : Fragment() {

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
        var max:Int = settings!!.getInt("maxBattery", 404)
        var min:Int = settings.getInt("minBattery", 404)
        Log.d(max)
        Log.d(min)
        if (max == 404) {
            Log.d("max:404")
            max = 80
            editor?.putInt("maxBattery", 80)?.apply()
        }
        if (min == 404) {
            Log.d("min:404")
            min = 30
            editor?.putInt("minBattery", 30)?.apply()
        }
        seekBar_maxAutoCharge.progress = max
        seekBar_minAutoCharge.progress = min
        text_max.text = settings.getInt("maxBattery", 80).toString()
        text_min.text = settings.getInt("minBattery", 30).toString()
        switch_autoCharge.isChecked = BatteryService.isrunning
        //ClickListener
        button_ResumeChanger.setOnClickListener { SuDo(context).execCmdSync(ResumeChanger) }
        button_DisableChanger.setOnClickListener { SuDo(context).execCmdSync(DisableChanger) }
        switch_autoCharge.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                BatteryService.startService(context!!)
            } else {
                BatteryService.stopService(context!!)
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
