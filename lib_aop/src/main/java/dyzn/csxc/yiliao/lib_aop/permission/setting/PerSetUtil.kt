package dyzn.csxc.yiliao.lib_aop.permission.setting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import dyzn.csxc.yiliao.lib_aop.BuildConfig

/**
 *@author YLC-D
 *@create on 2022/3/22 14
 *说明:打开手机的权限设置页面工具类
 */
object PerSetUtil {
    /**
     * 各大品牌的匹配字符串，用于判断在哪个品牌的手机上
     * 使用对应的方法打开权限设置页面
     */
    private const val HUA_WEI = "huawei"
    private const val MEI_ZU = "meizu"
    private const val XIAO_MI = "xiaomi"
    private const val SONY = "sony"
    private const val OPPO = "oppo"
    private const val LG = "lg"
    private const val VI_VO = "vivo"
    private const val LE_TV = "letv"
    private const val ZTE = "zte"
    private const val YU_LONG = "yulong"//酷派
    private const val SAMSUNG = "samsung"
    private const val LENOVO = "lenovo"

    fun goToSetting(context: Context, applicationId: String) {
        var isDefault = false
        val intent = when (Build.MANUFACTURER.lowercase()) {
            HUA_WEI -> getCommonIntent(applicationId).also {
                it.component = ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity")
            }
            MEI_ZU -> getMeiZuIntent(applicationId)
            XIAO_MI -> getXiaoMiIntent(applicationId)
            SONY -> getCommonIntent(applicationId).also {
                it.component = ComponentName("com.sonymobile.cta",
                    "com.sonymobile.cta.SomcCTAMainActivity")
            }
            OPPO -> getCommonIntent(applicationId).also {
                it.component = ComponentName("com.color.safecenter",
                    "com.color.safecenter.permission.PermissionManagerActivity")
            }
            LG -> getCommonIntent(applicationId).also {
                it.component = ComponentName("com.android.settings",
                    "com.android.settings.Settings\$AccessLockSummaryActivity")
            }
            VI_VO -> getViVoIntent(context)
            LE_TV -> getCommonIntent(applicationId).also {
                it.component = ComponentName("com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.PermissionAndApps")
            }
            SAMSUNG -> {
                isDefault = true
                getDefaultIntent(context)
            }
            else -> {
                isDefault = true
                getDefaultIntent(context)
            }
        }

        when (isDefault) {
            true -> context.startActivity(intent)
            false -> {
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.startActivity(getDefaultIntent(context))
                }
            }
        }
    }

    private fun getCommonIntent(applicationId: String): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", applicationId)
        return intent
    }

    private fun getMeiZuIntent(applicationId: String): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("packageName", applicationId)
        return intent
    }

    private fun getXiaoMiIntent(applicationId: String): Intent {
        val i = Intent("miui.intent.action.APP_PERM_EDITOR")
        val componentName = ComponentName("com.miui.securitycenter",
            "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        i.component = componentName
        i.putExtra("extra_pkgname", applicationId)
        return i
    }

    private fun getViVoIntent(context: Context): Intent {
        val appIntent = context.packageManager.getLaunchIntentForPackage("com.iqoo.secure")
        if (appIntent != null && Build.VERSION.SDK_INT < 23) {
            return appIntent
        }
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Settings.ACTION_SETTINGS
        return intent
    }

    private fun getDefaultIntent(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }
}