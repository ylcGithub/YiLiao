package dyzn.csxc.yiliao.bluetooth.util

/**
 *@author YLC-D
 *@create on 2022/3/24 13
 *说明:蓝颜模块中的条件判断工具类
 */
object BlueMatchUtil {

    fun kanConnectBlue(address: String?): Boolean {
        return !address.isNullOrEmpty()
    }

    fun kanSendMsg(blueConnected: Boolean, wifiName: String?, wifiPass: String?): Boolean {
        return blueConnected && !wifiName.isNullOrEmpty() && !wifiPass.isNullOrEmpty()
    }

}