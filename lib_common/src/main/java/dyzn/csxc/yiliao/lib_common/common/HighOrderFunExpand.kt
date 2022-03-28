package dyzn.csxc.yiliao.lib_common.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.adapters.TextViewBindingAdapter

/**
 *@author YLC-D
 *@create on 2022/3/24 14
 *说明:高阶函数的扩展文件
 */

/**
 * TextWatcher 的高阶函数扩展方法
 */
inline fun EditText.addTextChangedListenerClosure(
    crossinline beforeTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { charSequence, start, count, after -> },
    crossinline onTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { charSequence, start, before, count -> },
    crossinline afterTextChanged: (Editable?) -> Unit = {},
) {
    val listener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s,start,count,after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s,start,before,count)
        }

        override fun afterTextChanged(e: Editable?) {
           afterTextChanged.invoke(e)
        }
    }
    this.addTextChangedListener(listener)
}