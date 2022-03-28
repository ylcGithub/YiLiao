package dyzn.csxc.yiliao.bluetooth.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.jeremyliao.liveeventbus.LiveEventBus
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import java.util.*
import kotlin.math.log

/**
 * @author YLC-D
 * @create on 2019/5/15 10
 *
 * 说明: 蓝牙的工具类
 */
object BluetoothUtils {
    /** 获取蓝牙的adapter  */
    /** 如果blueadapter 为null 则说明手机没有蓝牙  */
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    /** 判断蓝牙功能是否打开  */
    val isEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled ?: false


    /** 取消蓝牙搜索  */
    fun cancelDiscovery() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
    }

    /** 开始蓝牙搜索  */
    fun startDiscovery() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            bluetoothAdapter.startDiscovery()
        } else "该手机没有蓝牙模块".toast()
    }

    fun isBond(device: BluetoothDevice?): Boolean {
        if (device == null) return false
        var bond = false
        val list = bondedDevices
        for (d in list) {
            if (d.address == device.address) bond = true
        }
        return bond
    }

    /** 蓝牙配对  */
    fun createBond(btDevice: BluetoothDevice) {
        try {
            btDevice.createBond()
            val createBondMethod = btDevice.javaClass.getMethod("createBond")
            createBondMethod.invoke(btDevice)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.log(e.toString())
        }
    }

    /** 反射来调用BluetoothDevice.removeBond取消设备的配对  */
    fun removeDevice(device: BluetoothDevice?) {
        device?.let {
            try {
                val removeBond = "removeBond"
                val m = device.javaClass.getMethod(removeBond, null as Class<*>?)
                m.invoke(device, null as Array<Any?>?)
            } catch (e: Exception) {
                LogUtil.log(e.toString())
            }
        }
    }

    /**
     * 连接蓝牙
     */
    fun sendMsg(device: BluetoothDevice,name:String?,pwd:String?) {
        Thread {
            val sendMessage = ("<name>${name}</name>" +
                    "<password>${pwd}</password>").trimIndent() //需要发送给配对蓝牙的数据
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                val socket = device.createRfcommSocketToServiceRecord(uuid)
                socket.connect()
                if (socket.isConnected) { //连接成功
                    val os = socket.outputStream
                    os.write(sendMessage.toByteArray())
                    os.flush()
                    LiveEventBus.get(LiveBusKey.BLUETOOTH_MSG_SEND_STATE,Boolean::class.java).post(true)
                } else {
                    //创建连接失败
                    socket.close()
                    LiveEventBus.get(LiveBusKey.BLUETOOTH_MSG_SEND_STATE,Boolean::class.java).post(false)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                LogUtil.log(e.toString())
                LiveEventBus.get(LiveBusKey.BLUETOOTH_MSG_SEND_STATE,Boolean::class.java).post(false)
            }
        }.start()
    }

    /** 获取本机已经绑定的蓝牙列表  */
    private val bondedDevices: List<BluetoothDevice>
        get() {
            return if (bluetoothAdapter != null) {
                ArrayList(bluetoothAdapter.bondedDevices)
            } else {
                ArrayList()
            }
        }

}