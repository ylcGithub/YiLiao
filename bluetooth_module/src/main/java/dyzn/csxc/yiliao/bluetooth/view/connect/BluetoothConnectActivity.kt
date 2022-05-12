package dyzn.csxc.yiliao.bluetooth.view.connect

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.databinding.BlueActivityConnectBluetoothBinding
import dyzn.csxc.yiliao.lib_common.base.BaseMvvmActivity
import dyzn.csxc.yiliao.lib_common.config.RoutePath
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.expand.ylDecodeHexStringToString
import dyzn.csxc.yiliao.lib_common.util.LayoutManagerUtil
import dyzn.csxc.yiliao.lib_common.util.LogUtil

@Route(path = RoutePath.CONNECT_BlUE_TOOTH_ACTIVITY)
class BluetoothConnectActivity :
    BaseMvvmActivity<BluetoothConnectViewModel, BlueActivityConnectBluetoothBinding>() {
    override fun getViewModel(): BluetoothConnectViewModel =
        getActivityViewModelProvider(this).get(BluetoothConnectViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.blue_activity_connect_bluetooth

    @JvmField
    @Autowired(name = "deviceAddress")
    var deviceAddress: String? = null

    @JvmField
    @Autowired(name = "deviceName")
    var deviceName: String? = null

    override fun initData() {
        mBinding.vm = mViewModel
        mBinding.click = ClickProxy()
        val title = "连接蓝牙<${deviceName}>"
        mBinding.appTitle.setTitle(title)
        if (deviceAddress == null || deviceAddress?.isEmpty() == true) {
            "蓝牙地址获取失败，无法连接！".toast()
        } else {
            showLoading("蓝牙连接中...")
            mViewModel.setBleConnectionListener(mListener)
            mViewModel.connect(getCurrentDevice())
        }
        val ada = ServicesListAdapter()
        mBinding.rcvServices.layoutManager = LayoutManagerUtil.getVertical(this)
        mBinding.rcvServices.adapter = ada
        ada.setSelectClick { s_index, c_index, d_index ->
            when {
                s_index != -1 -> {//展开并选中
                    mViewModel.sIndex = s_index
                    mViewModel.cIndex = -1
                    mViewModel.dIndex = -1
                    mBinding.rcvServices.smoothScrollToPosition(s_index)
                }
                c_index != -1 -> {
                    mViewModel.cIndex = c_index
                    mViewModel.dIndex = -1
                }
                d_index != -1 -> {
                    mViewModel.dIndex = d_index
                }
            }
        }
        mViewModel.list.observe(this) {
            ada.updateList(it)
        }
    }

    private fun getCurrentDevice(): BluetoothDevice {
        val mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = mBluetoothManager.adapter
        //获取原始蓝牙设备对象进行连接
        return mBluetoothAdapter.getRemoteDevice(deviceAddress)
    }

    inner class ClickProxy {
        fun back() {
            finishActivity()
        }

        fun sendMsg() {
            if (mViewModel.sIndex < 0) {
                "请选中要使用的服务".toast()
                return
            }
            if (mViewModel.cIndex < 0) {
                "请选中要使用的特征".toast()
                return
            }
            when (val msg: String? = mViewModel.msg.value) {
                null -> "请输入发送内容".toast()
                "" -> "请输入发送内容".toast()
                else -> mViewModel.sendDataWithCharacteristic(msg.toByteArray())
            }
        }
    }

    private var mListener = object : BluetoothConnectViewModel.BleConnectionListener {
        override fun discoveredServices() {
            //发现服务
            LogUtil.log("发现服务")
        }

        override fun onConnectionSuccess() {
            "蓝牙连接成功".toast()
            dismissLoading()
        }

        override fun onConnectionFail() {
            "蓝牙连接失败".toast()
            dismissLoading()
            finishActivity()
        }

        override fun disConnection() {
            "蓝牙连接断开".toast()
            dismissLoading()
            finishActivity()
        }

        override fun readCharacteristic(data: String) {
            LogUtil.log(data)
            val ind = data.indexOf("特征值=")
            mViewModel.msg.value = mViewModel.msg.value + "\nBle返回:${
                data.substring(ind + 4).ylDecodeHexStringToString()
            }"
        }

        override fun writeCharacteristic(data: String) {
            LogUtil.log(data)
            val indexOf = data.indexOf("写入值=")
            if (indexOf > 0) {
                val s = data.substring(indexOf + 4).ylDecodeHexStringToString()
                LogUtil.log("(解析后的)写入值=:$s")
            }
            mViewModel.readCharacteristicReturn()
        }

        override fun characteristicChange(data: String) {
            LogUtil.log(data)
        }

        override fun readDescriptor(data: String) {
            LogUtil.log(data)
        }

        override fun writeDescriptor(data: String) {
            LogUtil.log(data)
        }
    }
}