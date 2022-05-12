package dyzn.csxc.yiliao.lib_common.expand

import androidx.recyclerview.widget.RecyclerView

/**
 *@author YLC-D
 *说明: recyclerView 相关的扩展方法
 */

/**
 * 使用该方法添加分隔线，避免页面回退时 分割线重新设置
 */
fun RecyclerView.setItemDecoration(decor: RecyclerView.ItemDecoration){
    if(this.itemDecorationCount > 0){
        for (i in 0 until this.itemDecorationCount){
            this.removeItemDecorationAt(i)
        }
    }
    this.addItemDecoration(decor)
}