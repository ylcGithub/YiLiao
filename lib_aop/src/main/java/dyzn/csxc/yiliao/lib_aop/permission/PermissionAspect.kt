package dyzn.csxc.yiliao.lib_aop.permission

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionCancel
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionDefied
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionNeed
import dyzn.csxc.yiliao.lib_aop.permission.util.PUtil
import dyzn.csxc.yiliao.lib_common.base.BaseApplication
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 *@author YLC-D
 *@create on 2022/3/21 10
 *说明:
 */
@Aspect
class PermissionAspect {
    @Pointcut("execution(@dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionNeed * *(..)) && @annotation(permissionNeed)")
    fun requestPermission(permissionNeed: PermissionNeed) {
    }

    @Around("requestPermission(permissionNeed)")
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint, permissionNeed: PermissionNeed) {
        //版本低于23时不用申请权限，SDK_INT是build.gradle中的编译版本
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val obj = joinPoint.`this`
        val iPermission = object : IPermission {
            override fun onPermissionGranted() {
                try {
                    joinPoint.proceed()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPermissionDefied(requestCode: Int) =
                PUtil.invokeAnt(obj, PermissionDefied::class.java, requestCode)

            override fun onPermissionCancel(requestCode: Int) =
                PUtil.invokeAnt(obj, PermissionCancel::class.java, requestCode)
        }

        val context = when (obj) {
            is Fragment -> obj.context
            is Activity -> obj.baseContext
            else -> BaseApplication.getAppContext()
        }
        context?.let {
            //如果权限已经是通过的状态
            when {
                PUtil.hasSelfPermissions(context, permissionNeed.permissions) -> {
                    iPermission.onPermissionGranted()
                }
                else -> {
                    PermissionActivity.startPermissionRequest(
                        context,
                        permissionNeed.permissions,
                        permissionNeed.requestCode,
                        iPermission
                    )
                }
            }
        }
    }
}