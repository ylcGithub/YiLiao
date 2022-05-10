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
import dyzn.csxc.yiliao.lib_common.util.LayoutManagerUtil
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import dyzn.csxc.yiliao.lib_common.widget.CustomItemDecoration
import okio.ByteString.Companion.decodeHex
import java.util.regex.Pattern

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
        mBinding.rcvServices.addItemDecoration(CustomItemDecoration(
            CustomItemDecoration.Type.VER, ResUtil.getColor(R.color.color_main_blue)
        ).also {
            it.space = 4
        })
        ada.setSelectClick { s_index, c_index, d_index ->
            //关闭展开并取消选中
            if (s_index == -2) ada.setSelected(c_index)
            else if (s_index != -1) {//展开并选中
                mViewModel.c_s_index = s_index
                mViewModel.c_c_index = -1
                mViewModel.c_d_index = -1
                ada.setSelected(s_index)
                mBinding.rcvServices.scrollToPosition(s_index)
            }
            if (c_index != -1) {
                mViewModel.c_c_index = c_index
                mViewModel.c_d_index = -1
            }
            if (d_index != -1) mViewModel.c_d_index = d_index


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
            if (mViewModel.c_s_index < 0) {
                "请选中要使用的服务".toast()
                return
            }
            if (mViewModel.c_c_index < 0) {
                "请选中要使用的特征".toast()
                return
            }
            val msg: String? = mViewModel.msg.value
            when {
                msg == null -> "请输入发送内容".toast()
                Pattern.matches(
                    "^[A-Fa-f0-9]+\$", msg
                ) -> mViewModel.sendDataWithCharacteristic(msg.decodeHex().toByteArray())
                else -> "需要16进制字符串".toast()
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
            mViewModel.msg.value = mViewModel.msg.value + "\n蓝牙返回：\n${data.substring(ind + 4)}"
        }

        override fun writeCharacteristic(data: String) {
            LogUtil.log(data)
            "特征写入结果====$data".toast()
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