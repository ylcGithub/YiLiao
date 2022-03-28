package dyzn.csxc.yiliao.bluetooth.core

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.hawk.Hawk
import dyzn.csxc.yiliao.bluetooth.util.BluetoothUtils
import dyzn.csxc.yiliao.lib_common.config.HawkKey
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LogUtil.log

/**
 *@author YLC-D
 *@create on 2022/3/25 09
 *说明: 蓝牙的广播监听
 */
object BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if(BluetoothDevice.ACTION_FOUND == action){
            // 获取查找到的蓝牙设备
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            //需要搜索到的蓝牙
            val searchBlueAddress = Hawk.get(HawkKey.ROBOT_BLUETOOTH_ADDRESS, "")
            log("蓝牙名称：${device?.name},蓝牙地址：${device?.address}")
            //找到预期的蓝牙
            if(searchBlueAddress.lowercase() == device?.address?.lowercase()){
                BluetoothUtils.cancelDiscovery()
                // 判断蓝牙设备的连接状态
                when(device.bondState){
                    // 未配对
                    BluetoothDevice.BOND_NONE->BluetoothUtils.createBond(device)
                    //已配对
                    BluetoothDevice.BOND_BONDED-> LiveEventBus.get(LiveBusKey.BLUETOOTH_BOND_SUCCESS,BluetoothDevice::class.java).post(device)
                }
            }
        }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
            // 获取到状态改变的蓝牙设备
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            val currBlueAddress= Hawk.get(HawkKey.ROBOT_BLUETOOTH_ADDRESS, "")
           if(currBlueAddress.lowercase() == device?.address?.lowercase()){
               when (device.bondState) {
                   BluetoothDevice.BOND_BONDING -> log("BluetoothConnectFragment: 蓝牙正在配对......")
                   BluetoothDevice.BOND_BONDED -> LiveEventBus.get(LiveBusKey.BLUETOOTH_BOND_SUCCESS,BluetoothDevice::class.java).post(device)
                   BluetoothDevice.BOND_NONE -> "取消了蓝牙配对".toast()
                   else -> {}
               }
           }
        }
    }
}