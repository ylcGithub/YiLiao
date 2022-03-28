package dyzn.csxc.yiliao.lib_common.common

import android.text.Editable
import android.text.TextWatcher

/**
 *@author YLC-D
 *@create on 2022/3/24 12
 *说明:
 */
class TextWatcherDslImpl : TextWatcher {
    /**
     * 原接口对应的kotlin函数对象
     */
    private var beforeTextChanged: ((CharSequence?,Int,Int,Int) -> Unit)? = null
    private var onTextChanged: ((CharSequence?,Int,Int,Int) -> Unit)? = null
    private var afterTextChanged: ((Editable?) -> Unit)? = null

    /**
     * DSL中使用的函数，一般和原函数保持同名即可
     */
    fun beforeTextChanged(method:(CharSequence?,Int,Int,Int) -> Unit){
        beforeTextChanged = method
    }
    fun onTextChanged(method:(CharSequence?,Int,Int,Int) -> Unit){
        onTextChanged = method
    }
    fun afterTextChanged(method:(Editable?) -> Unit){
        afterTextChanged = method
    }

    /**
     * 实现原接口的函数
     */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       beforeTextChanged?.invoke(s,start,count,after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s,start,before,count)
    }

    override fun afterTextChanged(e: Editable?) {
       afterTextChanged?.invoke(e)
    }
}