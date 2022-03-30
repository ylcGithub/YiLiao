package dyzn.csxc.yiliao.bluetooth.util

import android.content.Context
import android.net.wifi.WifiManager
import org.apache.commons.lang3.StringUtils

/**
 * @author YLC-D
 * @create on 2019/5/15 10
 *
 * 说明: wifi 的工具类
 */
object WifiUtils {
    fun getCurrWifiName(mContext: Context?): String {
        val wfManager: WifiManager? =
            mContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        return checkName(wfManager?.connectionInfo?.ssid)
    }

    private fun checkName(wifiName: String?): String {
        if (StringUtils.isEmpty(wifiName)) return ""
        val flag = "\""
        return if (wifiName!!.startsWith(flag) and wifiName.endsWith(flag) and (wifiName.length > 2)) {
            wifiName.substring(1, wifiName.length - 1)
        } else {
            wifiName
        }
    }
}