package dyzn.csxc.yiliao.lib_common.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * @author YLC-D
 * 说明:
 */
object OpenGlUtils {
    /**
     * 检查机器是否支持 OpenGL ES 2.0
     *
     * @param context 上下文
     * @return Boolean
     */
    fun checkSupported(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000
        @SuppressLint("ObsoleteSdkInt") val isEmulator =
            (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                    && (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")))
        return supportsEs2 || isEmulator
    }
}