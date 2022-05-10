package dyzn.csxc.yiliao.bluetooth.view

import com.alibaba.android.arouter.facade.annotation.Route
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.lib_common.base.BaseActivity
import dyzn.csxc.yiliao.lib_common.config.RoutePath
import dyzn.csxc.yiliao.lib_common.util.AppManager

@Route(path = RoutePath.BlUE_TOOTH_ACTIVITY)
class BluetoothActivity : BaseActivity(){
    private lateinit var model: BluetoothViewModel
    override fun getLayoutId(): Int = R.layout.blue_activity_bluetooth
    override fun initData() {
        model = getActivityViewModelProvider(this).get(BluetoothViewModel::class.java)
        model.quitApp.observe(this) {
            if (it) AppManager.quitApp(this)
        }
    }
}