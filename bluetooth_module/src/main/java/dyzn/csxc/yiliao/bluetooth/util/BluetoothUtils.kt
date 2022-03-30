package dyzn.csxc.yiliao.bluetooth.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.hawk.Hawk
import dyzn.csxc.yiliao.lib_common.config.HawkKey
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import java.nio.charset.StandardCharsets
import java.util.*

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

    /** 获取本机已经绑定的蓝牙列表  */
    private val bondedDevices: List<BluetoothDevice>
        get() {
            return if (bluetoothAdapter != null) {
                ArrayList(bluetoothAdapter.bondedDevices)
            } else {
                ArrayList()
            }
        }

    /** 获取本机已绑定的蓝牙  */
   private fun getMyDevice(): BluetoothDevice {
        val myAddress = Hawk.get<String>(HawkKey.BLUETOOTH_ADDRESS)
        val list = bondedDevices
        for (d in list) {
            if (d.address.lowercase() == myAddress.lowercase()) {
               return d
            }
        }
        throw RuntimeException("没有获取到蓝牙设备")
    }
    private var mSocket: BluetoothSocket? = null

    /**
     * 连接蓝牙
     */
    fun sendMsg(name: String?, pwd: String?) {
        Thread {
            val sendMessage = ("<name>${name}</name>" +
                    "<password>${pwd}</password>").trimIndent() //需要发送给配对蓝牙的数据
            try {
                val device = getMyDevice()
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                mSocket = device.createRfcommSocketToServiceRecord(uuid)
                if (mSocket == null) throw RuntimeException("向蓝牙发送数据时，获取蓝牙socket失败")
                mSocket!!.connect()
                if (mSocket!!.isConnected) { //连接成功
                    val os = mSocket!!.outputStream
                    os.write(sendMessage.toByteArray())
                    os.flush()
                    os.close()
                    getBlueResult()
                } else {
                    //创建连接失败
                    mSocket!!.close()
                    "蓝牙连接创建失败".toast()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                LogUtil.log(e.toString())
            }
        }.start()
    }

    private fun getBlueResult() {
        var haveMsg = true
        val result = StringBuilder()
        try {
            if (mSocket == null) throw RuntimeException("读取返回数据时，获取蓝牙socket失败")
            val ins = mSocket!!.inputStream
            while (haveMsg) {
                val buffer = ByteArray(1024)
                val count: Int = ins.read(buffer)
                if (count > 0) {
                    result.append(String(buffer, 0, count, StandardCharsets.UTF_8))
                }
                if (ins.available() == 0) {
                    haveMsg = false
                    LiveEventBus.get(LiveBusKey.BLUETOOTH_RESULT, String::class.java)
                        .post(result.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.log(e.toString())
        } finally {
            mSocket?.close()
            mSocket = null
        }
    }
}