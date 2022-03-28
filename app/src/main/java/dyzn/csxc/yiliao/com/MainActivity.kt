package dyzn.csxc.yiliao.com

import android.widget.TextView
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionCancel
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionDefied
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionNeed
import dyzn.csxc.yiliao.lib_aop.permission.setting.PerSetUtil
import dyzn.csxc.yiliao.lib_common.base.BaseActivity
import dyzn.csxc.yiliao.lib_common.expand.toast

class MainActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initData() {
        findViewById<TextView>(R.id.tv_text).setOnClickListener {
            test()
        }
    }

    @PermissionNeed([android.Manifest.permission.CAMERA,android.Manifest.permission.READ_CALENDAR],101)
    fun test(){
        "哎 我获取到权限了哟".toast()
    }

    @PermissionCancel(requestCode = 101)
    fun perCancel(){
       "权限被取消".toast()
    }

    @PermissionDefied(requestCode = 101)
    fun perDefied(){
        "权限被拒绝11".toast()
        PerSetUtil.goToSetting(this,BuildConfig.APPLICATION_ID)
    }
}