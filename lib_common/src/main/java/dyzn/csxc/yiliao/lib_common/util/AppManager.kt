package dyzn.csxc.yiliao.lib_common.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 *@author YLC-D
 *说明:APP的页面管理工具
 */
object AppManager {
    /**
     * Activity栈  ， 先进后出
     */
    private val activityStack: Stack<WeakReference<Activity>> = Stack()

    /**
     * 添加activity
     */
    fun addActivity(activity: Activity) {
        activityStack.add(WeakReference(activity))
    }

    /**
     * 关闭页面
     */
    fun finishActivity(activity: Activity) {
            activity.finish()
            val iterator = activityStack.iterator()
            while (iterator.hasNext()) {
                val a = iterator.next().get()?.javaClass
                if (activity.javaClass == a) {
                    iterator.remove()
                    break
                }
            }

    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivityWithName(cls: Class<*>) {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            activity.get()?.let {
                if(it.javaClass == cls) finishActivity(it)
            }
        }
    }

    /**
     * 退出应用
     */
    fun quitApp(context: Context) {
        val iterator = activityStack.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next().get()
            activity?.finish()
        }
        val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityMgr.killBackgroundProcesses(context.packageName)
        exitProcess(0)
    }
}