package dyzn.csxc.yiliao.lib_aop.permission.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionCancel
import dyzn.csxc.yiliao.lib_aop.permission.ant.PermissionDefied
import java.lang.reflect.InvocationTargetException

/**
 *@author YLC-D
 *@create on 2022/3/21 15
 *说明:权限申请的方法
 */
object PUtil {
    private val MIN_SDK_PERMISSIONS = SimpleArrayMap<String, Int>(8).apply {
        put("com.android.voicemail.permission.ADD_VOICEMAIL", 14)
        put("android.permission.BODY_SENSORS", 20)
        put("android.permission.READ_CALL_LOG", 16)
        put("android.permission.READ_EXTERNAL_STORAGE", 16)
        put("android.permission.USE_SIP", 9)
        put("android.permission.WRITE_CALL_LOG", 16)
        put("android.permission.SYSTEM_ALERT_WINDOW", 23)
        put("android.permission.WRITE_SETTINGS", 23)
    }

    /**
     * 判断多个权限是否通过权限是否全部通过
     */
    fun hasSelfPermissions(context: Context, permissions: Array<String>): Boolean {
        for (item in permissions) {
            if (permissionExists(item)
                and !hasSelfPermission(context, item)
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 检查单个权限是否通过
     */
    private fun hasSelfPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 判断权限是否在当前的Android版本中存在
     */
    private fun permissionExists(permission: String): Boolean {
        val minVersion = MIN_SDK_PERMISSIONS.get(permission)
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion
    }


    /**
     * 验证权限是否全部通过
     */
    fun verifyPermission(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) return false
        for (item in grantResults) {
            if (item != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    /**
     * 检查所给权限list 是否需要给提示
     * 如果某个权限需要权限申请提示则返回true
     */
    fun shouldShoeRequestPermission(activity: Activity, permissions: Array<out String>): Boolean {
        for (item in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, item)) {
                return true
            }
        }
        return false
    }

    /**
     * 通过反射调用指定方法
     */
    fun invokeAnt(obj: Any, antClass: Class<out Annotation>, requestCode: Int) {
        //获取切面上下文的类型
        val clazz: Class<*> = obj.javaClass
        //获取类型中的方法
        val methods = clazz.declaredMethods
        if (methods.isEmpty()) return
        for (method in methods) {
            //如果方法上没有对应注解 则退出本次循环 继续下一个方法的检测
            if (!method.isAnnotationPresent(antClass)) continue

            val code = when (val antClazz = method.getAnnotation(antClass)) {
                is PermissionDefied -> antClazz.requestCode
                is PermissionCancel -> antClazz.requestCode
                else -> -1
            }
            //判断对应的请求码是否一致 不一致 就结束本次循环 执行下一次
            if (code != requestCode) continue

            val parameterTypes = method.parameterTypes
            if (parameterTypes.isNotEmpty()) {
                throw RuntimeException("${antClass.name}注解的方法不能存在参数")
            }
            method.isAccessible = true
            try {
                method.invoke(obj)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }
}