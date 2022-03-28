package dyzn.csxc.yiliao.lib_aop.double_click

import android.os.SystemClock
import kotlin.math.abs

/**
 *@author YLC-D
 *@create on 2022/3/18 14
 *说明: 双击是否可以执行的判读
 */
object DoubleClickUtil {
    private var mLastClickTime: Long = 0

    fun isFastDoubleClick(intervalMillis: Long): Boolean {
        val time = SystemClock.elapsedRealtime()
        val timeInterval = abs(time - mLastClickTime)
        mLastClickTime = time
        return timeInterval < intervalMillis
    }

}