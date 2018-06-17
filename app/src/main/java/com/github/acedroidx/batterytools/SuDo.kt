package com.github.acedroidx.batterytools

import android.content.Context
import android.widget.Toast
import java.io.*

/**
 * Created by AceDroidX on 2018/5/20 2:12.
 * 本文件部分引用https://github.com/helloklf/vtools/blob/master/app/src/main/java/com/omarea/shell/SuDo.java
 */

class SuDo(private val context: Context?) {

    private fun noRoot() {
        if (context != null)
            Toast.makeText(context, "没有ROOT权限无法运行", Toast.LENGTH_SHORT).show()
    }

    //执行命令
    fun execCmdSync(cmd: String): Boolean {
        try {
            val p = Runtime.getRuntime().exec("su")
            val out = DataOutputStream(p.outputStream)
            out.write(cmd.toByteArray(charset("UTF-8")))
            out.writeBytes("\n")
            out.writeBytes("exit\n")
            out.writeBytes("exit\n")
            out.flush()
            p.waitFor()
            Log.d("" + p.exitValue())
            return p.exitValue() == 0
        } catch (e: IOException) {
            noRoot()
        } catch (ignored: Exception) {

        }

        return false
    }

    fun execCmd(cmd: String) {
        try {
            val p = Runtime.getRuntime().exec("su")
            val out = DataOutputStream(p.outputStream)
            out.write(cmd.toByteArray(charset("UTF-8")))
            out.writeBytes("\n")
            out.writeBytes("exit\n")
            out.writeBytes("exit\n")
            out.flush()
        } catch (e: IOException) {
            noRoot()
        } catch (ignored: Exception) {

        }

    }

    fun execReturn(cmd: String): String {
        val mRuntime = Runtime.getRuntime()
        val mProcess = mRuntime.exec(cmd)
        val mReader = BufferedReader(mProcess.inputStream.bufferedReader())
        mProcess.waitFor()
        val result = mReader.readLine()
        mReader.close()
        Log.d("execRt:"+result)
        return result
    }
}
