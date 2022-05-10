package dyzn.csxc.yiliao.bluetooth.view.scan

import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.core.BlueDeviceListAdapter
import dyzn.csxc.yiliao.bluetooth.databinding.BlueFragmentBluetoothScanBinding
import dyzn.csxc.yiliao.bluetooth.util.BluetoothUtils
import dyzn.csxc.yiliao.bluetooth.view.BluetoothViewModel
import dyzn.csxc.yiliao.lib_aop.double_click.DoubleClickCheck
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionDefied
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionNeed
import dyzn.csxc.yiliao.lib_common.base.BaseFragment
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.config.RoutePath
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LayoutManagerUtil
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import dyzn.csxc.yiliao.lib_common.widget.CustomItemDecoration
/**
 *@author YLC-D
 *@create on 2022/3/14 09
 *说明:
 */
class BluetoothScanFragment :
    BaseFragment<BluetoothScanViewModel, BlueFragmentBluetoothScanBinding>() {
    override fun getViewModel(): BluetoothScanViewModel =
        getFragmentViewModelProvider(this).get(BluetoothScanViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.blue_fragment_bluetooth_scan
    private lateinit var bvm: BluetoothViewModel
    override fun initData() {
        bvm = getActivityViewModelProvider(requireActivity()).get(BluetoothViewModel::class.java)
        mBinding.click = ClickProxy()
        mBinding.vm = mViewModel
        val ada = BlueDeviceListAdapter()
        mBinding.rcBleList.adapter = ada
        mBinding.rcBleList.layoutManager = LayoutManagerUtil.getVertical(mContext)
        mBinding.rcBleList.addItemDecoration(
            CustomItemDecoration(
                CustomItemDecoration.Type.VER,
                ResUtil.getColor(R.color.color_main_blue)
            ).also {
                it.space = 4
                it.mostTop = 4
            }
        )
        ada.setConnectClickListener {
            BluetoothUtils.stopScanBle()
            val bundle = Bundle()
            bundle.putString("deviceName",it.device.name)
            bundle.putString("deviceAddress",it.device.address)
            toNextActivity(RoutePath.CONNECT_BlUE_TOOTH_ACTIVITY,bundle)
        }
        BluetoothUtils.setOnListener {
            ada.updateList(it, true)
            dismissLoading()
        }
        LiveEventBus.get<Boolean>(LiveBusKey.SCAN_BLUE_STOP).observe(this) {
            if(it) dismissLoading()
        }
    }

    inner class ClickProxy {

        fun back() {
            bvm.pageBack
        }
        @PermissionNeed(permissions = [
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION], 102)
        @DoubleClickCheck
        fun searchBlue() {
            if (BluetoothUtils.startScanBle(mContext)) showLoading("蓝牙扫描中....")
        }
        @PermissionDefied(requestCode = 102)
        fun denyPer() {
            "拒绝定位无法搜索到附近的蓝牙设备".toast()
        }
    }
}