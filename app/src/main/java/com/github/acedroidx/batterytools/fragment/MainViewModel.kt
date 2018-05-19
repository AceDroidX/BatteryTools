package com.github.acedroidx.batterytools.fragment

import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.widget.Toast

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    companion object {
        fun newFragment(context: Context, string: String): Fragment {
            when (string) {
                MainFragment.newInstance().javaClass.name -> return MainFragment.newInstance()
                AboutFragment.newInstance().javaClass.name -> return AboutFragment.newInstance()
                else -> {
                    Toast.makeText(context, "fragment获取失败", Toast.LENGTH_LONG).show()
                    return MainFragment.newInstance()
                }
            }
        }
    }
}
