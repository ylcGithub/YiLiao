package dyzn.csxc.yiliao.lib_common.base

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *@author YLC-D
 *说明:解决java.lang.IndexOutOfBoundsException: Inconsistency detected
 * RecyclerView的自带bug
 */
class BaseLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}