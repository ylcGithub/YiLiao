package dyzn.csxc.yiliao.lib_common.util

import android.util.Log
import dyzn.csxc.yiliao.lib_common.BuildConfig;

/**
 * @author Administrator
 * 说明:打印的封装工具 暂时统一使用警告错误级别的打印
 */
object LogUtil {
    var DEBUG: Boolean = true
    fun log(errorMsg: String) {
        log("医疗项目===", errorMsg)
    }

    fun log(tag: String, errorMsg: String) {
        if (!BuildConfig.DEBUG && DEBUG) {
            CacheFileUtil.saveLogToLocal(tag,errorMsg)
        } else if (BuildConfig.DEBUG) {
            Log.e(tag, errorMsg)
        }
    }
}