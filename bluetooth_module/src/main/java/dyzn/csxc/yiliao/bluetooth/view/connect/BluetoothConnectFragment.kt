package dyzn.csxc.yiliao.bluetooth.view.connect

import android.bluetooth.BluetoothDevice
import android.os.CountDownTimer
import android.view.View
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.hawk.Hawk
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.databinding.BlueFragmentBluetoothConnectBinding
import dyzn.csxc.yiliao.bluetooth.util.BluetoothUtils
import dyzn.csxc.yiliao.bluetooth.util.WifiUtils
import dyzn.csxc.yiliao.lib_aop.double_click.DoubleClickCheck
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionDefied
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionNeed
import dyzn.csxc.yiliao.lib_common.base.BaseFragment
import dyzn.csxc.yiliao.lib_common.config.HawkKey
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.config.RoutePath
import dyzn.csxc.yiliao.lib_common.expand.toast
import kotlin.concurrent.timer


/**
 *@author YLC-D
 *@create on 2022/3/14 09
 *说明:
 */
class BluetoothConnectFragment :
    BaseFragment<BluetoothConnectViewModel, BlueFragmentBluetoothConnectBinding>() {
    override fun getViewModel(): BluetoothConnectViewModel =
        getFragmentViewModelProvider(this).get(BluetoothConnectViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.blue_fragment_bluetooth_connect

    private var mDevice: BluetoothDevice? = null

    override fun initData() {
        mBinding.click = ClickProxy()
        mBinding.vm = mViewModel
        //蓝牙接收广播 在MyDeviceModuleActivity注册的
        if (mDevice != null) BluetoothUtils.cancelDiscovery()
        //监听 蓝牙绑定是否成功
        LiveEventBus.get(LiveBusKey.BLUETOOTH_BOND_SUCCESS, BluetoothDevice::class.java)
            .observe(this, {
                mDevice = it
                mViewModel.blueBondSuc.value = it != null
                if (it != null) {
                    mBinding.etWifiName.setText(WifiUtils.getCurrWifiName(mContext))
                    mViewModel.cancelTimer()
                }
            })
    }

    inner class ClickProxy {

        fun back() {
            pageBack()
        }

        @PermissionNeed([android.Manifest.permission.CAMERA], 101)
        fun toScan() {
            toNextActivity(RoutePath.SCAN_ACTIVITY)
        }

        @PermissionDefied(requestCode = 101)
        fun defiedCamera(){
            "拒绝了相机权限，无法打开扫码界面".toast()
        }

        @PermissionNeed(
            permissions = [android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION], 102)
        @DoubleClickCheck
        fun searchBlue(@Suppress("UNUSED_PARAMETER") v: View) {
            if (BluetoothUtils.isEnabled) {
                Hawk.put(HawkKey.ROBOT_BLUETOOTH_ADDRESS,mViewModel.bluetoothAddress.value)
                BluetoothUtils.startDiscovery()
                showLoading("搜索蓝牙中....")
                mViewModel.startTimer()
            } else "请打开蓝牙".toast()
        }

        @PermissionDefied(requestCode = 102)
        fun denyPer(){
            "拒绝定位无法搜索到附近的蓝牙设备".toast()
        }

        @DoubleClickCheck
        fun sendWifiInfo(@Suppress("UNUSED_PARAMETER") v: View) {
           BluetoothUtils.sendMsg(mDevice!!,mViewModel.wifiName.value,mViewModel.wifiPassword.value)
        }
    }
}