package dyzn.csxc.yiliao.lib_common.config

import dyzn.csxc.yiliao.lib_common.BuildConfig

/**
 *@author YLC-D
 *说明:
 */
object UrlConfig {
    val HTTP_SERVER: String get() = if (!BuildConfig.DEBUG) "http://192.168.5.218:8080/" else "http://47.107.95.201:8080/"

    const val SERVER_IP: String = "47.107.95.201"

    /**
     * thrift 专用
     */
    const val SERVER_PORT = 8899
}