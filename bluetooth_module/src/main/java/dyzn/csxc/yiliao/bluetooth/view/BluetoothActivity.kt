package dyzn.csxc.yiliao.bluetooth.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.core.BluetoothReceiver
import dyzn.csxc.yiliao.lib_common.base.BaseActivity
import dyzn.csxc.yiliao.lib_common.config.RoutePath

@Route(path = RoutePath.BlUETOOTH_ACTIVITY)
class BluetoothActivity : BaseActivity(){
    private lateinit var model: BluetoothViewModel
    override fun getLayoutId(): Int = R.layout.blue_activity_bluetooth
    override fun initData() {
        model = getActivityViewModelProvider(this).get(BluetoothViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regisRe()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(BluetoothReceiver)
    }

    private fun regisRe(){
        val intent = IntentFilter()
        //搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_FOUND)
        //状态改变
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        //行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        //动作状态发生了变化
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(BluetoothReceiver,intent)
    }
}