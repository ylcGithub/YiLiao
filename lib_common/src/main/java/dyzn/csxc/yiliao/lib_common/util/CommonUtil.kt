package dyzn.csxc.yiliao.lib_common.util

import dyzn.csxc.yiliao.lib_common.config.MatchConfig
import dyzn.csxc.yiliao.lib_common.config.UrlConfig
import okhttp3.internal.and
import org.apache.commons.lang3.StringUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 *@author YLC-D
 *说明: 通用工具类
 */
object CommonUtil {
    fun joinImageUrl(url: String?): String? {
        val b = url?.startsWith(MatchConfig.HTTP_MARK) ?: true
        return if (b) {
            url
        } else {
            UrlConfig.HTTP_SERVER + "app/image/" + url
        }
    }
    fun md5(string: String): String? {
        if (StringUtils.isEmpty(string)) {
            return ""
        }
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString(b and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}