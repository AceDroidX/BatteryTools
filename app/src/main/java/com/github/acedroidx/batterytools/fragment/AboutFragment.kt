package com.github.acedroidx.batterytools.fragment

import android.support.v4.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.acedroidx.batterytools.R
import android.content.pm.PackageManager


/**
 * Created by a1274 on 2018/2/4.
 */

class AboutFragment : Fragment() {

    // 获取packagemanager的实例
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val versionName: String
        get() {
            if (activity != null) {
                val packageManager = activity!!.packageManager
                val packInfo = packageManager?.getPackageInfo(activity!!.packageName, 0)
                Log.d("wxxDebugAbout", packInfo!!.versionName)
                return packInfo.versionName
            } else {
                return "null"
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val versionText = view!!.findViewById<TextView>(R.id.textView_version)
        try {
            versionText.text = "V$versionName"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val copyrightText = view!!.findViewById<TextView>(R.id.textView_copyright)
        if (copyrightText.text != "By AceDroidX") {
            copyrightText.text = "By AceDroidX"
            Log.d("wxxDebugAbout", "原程序已被修改！！！！By wxx")
        }

        val websiteText = view!!.findViewById<TextView>(R.id.textView_website)
        val appInfo = this.activity!!.packageManager
                .getApplicationInfo(this.activity!!.packageName,
                        PackageManager.GET_META_DATA)
        val url = appInfo.metaData.getString("Website")
        websiteText.text = url
        websiteText.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            websiteText.text = url
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}
