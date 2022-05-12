package dyzn.csxc.yiliao.lib_common.widget

import android.graphics.Rect
import android.view.View

class CustomRect(var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0) {

    fun isEmpty(): Boolean {
        return left == 0 && top == 0 && 0 == right && bottom == 0
    }

    fun getLineRectS(iv: View): List<Rect>? {
        val l = arrayListOf<Rect>()
        if (left > 0) {
            l.add(Rect(iv.left - left, iv.top, iv.left, iv.bottom))
        }
        if (top > 0) {
            l.add(Rect(iv.left - left, iv.top - top, iv.right + right, iv.top))
        }
        if (right > 0) {
            l.add(Rect(iv.right, iv.top, iv.right + right, iv.bottom))
        }
        if (bottom > 0) {
            l.add(Rect(iv.left - left, iv.bottom, iv.right + right, iv.bottom + bottom))
        }
        return if(l.isEmpty()) null else l
    }
}