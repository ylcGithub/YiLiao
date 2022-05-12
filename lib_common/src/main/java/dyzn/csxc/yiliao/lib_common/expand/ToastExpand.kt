package dyzn.csxc.yiliao.lib_common.expand

/**
 *@author YLC-D
 *说明: toast 的扩展
 */
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dyzn.csxc.yiliao.lib_common.base.BaseApplication

fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, content, duration).apply { show() }
}
fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), duration)
}
fun Any.toast(duration: Int = Toast.LENGTH_SHORT) {
    BaseApplication.getAppContext().toast(this.toString(), duration)
}
