package dyzn.csxc.yiliao.lib_aop.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dyzn.csxc.yiliao.lib_aop.permission.util.PUtil

/**
 *@author YLC-D
 *@create on 2022/3/21 09
 *说明:
 */
class PermissionActivity : Activity() {
    private var mRequestCode: Int = 0

    companion object {
        private const val PERMISSION: String = "permission"
        private const val REQUEST_CODE: String = "request_code"
        private var mIPermission: IPermission? = null
        fun startPermissionRequest(
            context: Context?,
            permissions: Array<String>,
            requestCode: Int,
            iPermission: IPermission) {
            if (context == null) return
            mIPermission = iPermission
            val intent = Intent(context, PermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val bundle = Bundle()
            bundle.putStringArray(PERMISSION, permissions)
            bundle.putInt(REQUEST_CODE, requestCode)
            intent.putExtras(bundle)
            context.startActivity(intent)
            if (context is Activity) context.overridePendingTransition(0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            val permissions = it.getStringArray(PERMISSION)!!
            mRequestCode = it.getInt(REQUEST_CODE)
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, mRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if (mRequestCode != requestCode) return
        when {
            PUtil.verifyPermission(grantResults) -> {
                mIPermission?.onPermissionGranted()
            }
            PUtil.shouldShoeRequestPermission(this, permissions) -> {
                //需要权限申请提示
                mIPermission?.onPermissionCancel(requestCode)
            }
            else -> {
                //权限申请被拒绝 只有去设置页面打开
                mIPermission?.onPermissionDefied(requestCode)
            }
        }
        finish()
        overridePendingTransition(0,0)
    }

}