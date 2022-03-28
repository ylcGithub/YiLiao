package dyzn.csxc.yiliao.lib_common.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import dyzn.csxc.yiliao.lib_common.base.BaseApplication

/**
 * @author YLC-D
 * 说明:资源类相关的工具方法
 */
object ResUtil {
    @ColorInt
    fun getColor(@ColorRes colorId: Int,context:Context = BaseApplication.getAppContext()): Int {
        return ContextCompat.getColor(context, colorId)
    }

    fun getDimen(@DimenRes dimenId: Int,context:Context = BaseApplication.getAppContext()): Float {
        return context.resources.getDimension(dimenId)
    }

    fun getString(@StringRes stringId: Int,context:Context = BaseApplication.getAppContext()): String {
        return context.resources.getString(stringId)
    }

    fun getDrawable(@DrawableRes drawableId: Int,context:Context = BaseApplication.getAppContext()): Drawable? {
        return ContextCompat.getDrawable(context, drawableId)
    }
}