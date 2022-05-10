package dyzn.csxc.yiliao.bluetooth.view.connect

import android.bluetooth.*
import android.os.Build
import androidx.lifecycle.MutableLiveData
import dyzn.csxc.yiliao.bluetooth.bean.GattServiceBean
import dyzn.csxc.yiliao.lib_common.base.BaseApplication
import dyzn.csxc.yiliao.lib_common.base.BaseViewModel
import dyzn.csxc.yiliao.lib_common.expand.decodeHexString
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import java.util.*
import kotlin.concurrent.schedule


class BluetoothConnectViewModel : BaseViewModel() {
    //需要发送的消息
    val msg = MutableLiveData<String>()

    /*********************************************BLE蓝牙连接功能开始***************************************************/
    //保存服务列表
    private val mGattServiceList = ArrayList<BluetoothGattService>()
    //当前选中服务的列表下表
    var currentIndex:Int = -1
    val list = MutableLiveData<ArrayList<GattServiceBean>>()

    //标记是否连接
    private var isConnected = false

    //标记重置次数
    private var retryCount = 0

    //蓝牙Gatt回调
    private lateinit var mGattCallback: YLBluetoothGattCallback

    //蓝牙Gatt
    private lateinit var mBluetoothGatt: BluetoothGatt
    private lateinit var mDevice: BluetoothDevice

    //连接蓝牙
    fun connect(device: BluetoothDevice) {
        mDevice = device
        if (!isConnected) {
            mGattCallback = YLBluetoothGattCallback()
            //重置连接重试次数
            retryCount = 0
            //子线程进行连接设备
            runOnThread {
                mBluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDevice.connectGatt(
                        BaseApplication.getAppContext(),
                        false,
                        mGattCallback,
                        BluetoothDevice.TRANSPORT_LE
                    )
                } else {
                    mDevice.connectGatt(BaseApplication.getAppContext(), false, mGattCallback)
                }
            }
        }
    }

    //尝试重新连接蓝牙
    private fun tryReConnection() {
        retryCount++
        //之前尝试连接不成功，先关闭之前的连接
        closeConnection()
        //延迟500ms再重新尝试连接
        Timer().schedule(500) {
            mBluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDevice.connectGatt(
                    BaseApplication.getAppContext(),
                    false,
                    mGattCallback,
                    BluetoothDevice.TRANSPORT_LE
                )
            } else {
                mDevice.connectGatt(BaseApplication.getAppContext(), false, mGattCallback)
            }
        }
    }

    //Gatt回调
    private inner class YLBluetoothGattCallback : BluetoothGattCallback() {
        //发现服务的回调
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            //任务抛给子线程处理
            runOnThread { servicesDiscovered(status) }
        }


        //连接状态改变的回调
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            runOnThread { connectionStateChange(status, newState) }
        }

        //特征读取回调
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            runOnThread { characteristicRead(characteristic, status) }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            runOnThread { characteristicWrite(characteristic, status) }
        }

        //特征改变回调（主要由外设回调）
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            runOnThread { characteristicChanged(characteristic) }
        }

        //描述写入回调
        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            runOnThread { descriptorWrite(descriptor, status) }
        }

        //描述读取回调
        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
            runOnThread { descriptorRead(descriptor, status) }
        }

    }

    //发现服务时的处理流程，添加进服务列表
    private fun servicesDiscovered(status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                //清空之前的Service列表
                mGattServiceList.clear()
                //重新设置Service列表
                mGattServiceList.addAll(mBluetoothGatt.services)
                val lis = ArrayList<GattServiceBean>()
                mGattServiceList.forEach {
                    val b = GattServiceBean(getServiceName(it.uuid),it.uuid,it.type,false)
                    lis.add(b)
                }
                runOnMain {
                    list.value = lis
                    mListener?.discoveredServices()
                }
            }
            else -> "发现服务失败".toast()
        }
    }

    private fun getServiceName(uuid: UUID):String{
        return when(uuid.toString()){
            "00001801-0000-1000-8000-00805f9b34fb" -> {
                "通用属性"
            }
            "00001800-0000-1000-8000-00805f9b34fb" -> {
                "通用访问"
            }
            else -> "服务"
        }
    }

    //连接状态改变时的处理
    private fun connectionStateChange(status: Int, newState: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                //连接状态
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    isConnected = true
                    //连接成功回调
                    runOnMain { mListener?.onConnectionSuccess() }
                    LogUtil.log("任务开始时间：" + System.currentTimeMillis())
                    //发现服务
                    Timer().schedule(500L) {
                        mBluetoothGatt.discoverServices()
                        LogUtil.log("任务执行时间：" + System.currentTimeMillis())
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isConnected = false
                    //断开连接回调
                    runOnMain { mListener?.disConnection() }
                    //断开连接状态
                    mBluetoothGatt.close()
                }
            }
            else -> {
                //遇到特殊情况尝试重连，增加连接成功率
                if (retryCount < 1 && !isConnected) {
                    //尝试重连
                    tryReConnection()
                } else {
                    runOnMain {
                        if (isConnected) mListener?.disConnection()//异常连接断开
                        else mListener?.onConnectionFail() //连接失败回调
                    }
                }
                //断开连接
                closeConnection()
            }
        }
    }

    //特征读取
    private fun characteristicRead(characteristic: BluetoothGattCharacteristic?, status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                characteristic?.let {
                    val value = it.value.decodeHexString()
                    val s = "特征读取 CharacteristicUUID = ${it.uuid} ,特征值 = $value"
                    runOnMain { mListener?.readCharacteristic(s) }
                }
            }
            //无可读权限
            BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                runOnMain { mListener?.readCharacteristic("特征读取时，无读取权限") }
            }
            else -> {
                runOnMain { mListener?.readCharacteristic("特征读取失败 status = $status") }
            }
        }
    }

    //特征值得写入
    private fun characteristicWrite(characteristic: BluetoothGattCharacteristic?, status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                characteristic?.let {
                    val s =
                        "特征写入 CharacteristicUUID = ${it.uuid} ,写入值 = ${it.value.decodeToString()}"
                    runOnMain { mListener?.writeCharacteristic(s) }
                }
            }
            else -> {
                //特征写入失败
                runOnMain { mListener?.writeCharacteristic("特征写入失败 status = $status") }
            }
        }
    }

    //特征值的改变
    private fun characteristicChanged(characteristic: BluetoothGattCharacteristic?) {
        characteristic?.let {
            val s = "特征改变 CharacteristicUUID = ${it.uuid},改变值 = ${it.value.decodeHexString()}"
            runOnMain { mListener?.characteristicChange(s) }
        }
    }

    //描述写入
    private fun descriptorWrite(descriptor: BluetoothGattDescriptor?, status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                descriptor?.let {
                    val s = "描述写入 DescriptorUUID = ${it.uuid} ,写入值 = ${it.value.decodeHexString()}"
                    runOnMain { mListener?.writeDescriptor(s) }
                }
            }
            else -> {
                //描述写入失败
                runOnMain { mListener?.writeDescriptor("描述写入失败 status = $status") }
            }
        }
    }

    //描述读取
    private fun descriptorRead(descriptor: BluetoothGattDescriptor?, status: Int) {
        when (status) {
            //操作成功
            BluetoothGatt.GATT_SUCCESS -> {
                descriptor?.let {
                    val value = it.value.decodeHexString()
                    val s = "描述读取 DescriptorUUID = ${it.uuid} ,描述值 = $value"
                    runOnMain { mListener?.readDescriptor(s) }
                }
            }
            //无可读权限
            BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                runOnMain { mListener?.readDescriptor("无读取权限") }
            }
            else -> {
                runOnMain { mListener?.readDescriptor("读取描述失败 status = $status") }
            }
        }
    }

    /**
     * 监听回调
     */
    interface BleConnectionListener {
        //发现服务
        fun discoveredServices()

        //连接成功
        fun onConnectionSuccess()

        //连接失败
        fun onConnectionFail()

        //断开连接
        fun disConnection()

        //读取特征值
        fun readCharacteristic(data: String)

        //写入特征值
        fun writeCharacteristic(data: String)

        //特征值改变
        fun characteristicChange(data: String)

        //读取描述
        fun readDescriptor(data: String)

        //写入描述
        fun writeDescriptor(data: String)

    }

    private var mListener: BleConnectionListener? = null
    fun setBleConnectionListener(listener: BleConnectionListener) {
        mListener = listener
    }

    /**
     * 断开连接，会触发onConnectionStateChange回调，在onConnectionStateChange回调中调用mBluetoothGatt.close()
     */
    fun disConnection() {
        if (isConnected) {
            isConnected = false
            mBluetoothGatt.disconnect()
        }
    }

    /**
     * 彻底关闭连接，不带onConnectionStateChange回调的
     */
    private fun closeConnection() {
        if (isConnected) {
            isConnected = false
            mBluetoothGatt.disconnect()
            //调用close()后，连接时传入callback会被置空，无法得到断开连接时onConnectionStateChange（）回调
            mBluetoothGatt.close()
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeConnection()
    }


    /**
     * 发送数据
     *
     * @param data 数据
     * @return true：发送成功 false：发送失败
     */
    fun sendData(data: ByteArray?): Boolean {
        // 获取蓝牙设备的服务
        val serviceUuid = mGattServiceList[currentIndex].uuid
        val gattService: BluetoothGattService? = mBluetoothGatt.getService(serviceUuid)
        val  characteristicWriteUuid = mGattServiceList[currentIndex].characteristics[0].uuid
        // 获取蓝牙设备的特征
        val gattCharacteristic =
            gattService?.getCharacteristic(characteristicWriteUuid) ?: return false
        // 发送数据
        gattCharacteristic.value = data
        return mBluetoothGatt.writeCharacteristic(gattCharacteristic)
    }
    /**********************************************BLE蓝牙连接功能结束**************************************************/
}