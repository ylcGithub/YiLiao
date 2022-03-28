package dyzn.csxc.yiliao.lib_common.common

import android.widget.TextView

/**
 *@author YLC-D
 *@create on 2022/3/24 13
 *说明: 使用DSL时对应的原方法的扩展
 */

fun TextView.addTextChangedListenerDsl(init:TextWatcherDslImpl.()->Unit){
    val listener = TextWatcherDslImpl()
    listener.init()
    this.addTextChangedListener(listener)
}
