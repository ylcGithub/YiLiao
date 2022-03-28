package dyzn.csxc.yiliao.lib_common.common

import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import dyzn.csxc.yiliao.lib_common.widget.AppTitle
import dyzn.csxc.yiliao.lib_common.widget.AppProgressView

/**
 *@author YLC-D
 *说明:
 */
// AppTitle 适配dataBinding的方法
@BindingAdapter("titleBackListener")
fun setAppTitleBackListener(appTitle: AppTitle, listener: View.OnClickListener) {
    appTitle.setClickBackListener(listener)
}

@BindingAdapter("titleOneIconListener")
fun setAppTitleRightOneIconListener(appTitle: AppTitle, listener: View.OnClickListener) {
    appTitle.setRightOneImageBtnClickListener(listener)
}

@BindingAdapter("titleTwoIconListener")
fun setAppTitleRightTwoIconListener(appTitle: AppTitle, listener: View.OnClickListener) {
    appTitle.setRightTwoImageBtnClickListener(listener)
}

@BindingAdapter("titleOneTextListener")
fun setAppTitleRightOneTextListener(appTitle: AppTitle, listener: View.OnClickListener) {
    appTitle.setRightOneTextBtnClickListener(listener)
}

@BindingAdapter("titleTwoTextListener")
fun setAppTitleRightTwoTextListener(appTitle: AppTitle, listener: View.OnClickListener) {
    appTitle.setRightTwoTextBtnClickListener(listener)
}

@BindingAdapter("setTitle")
fun setAppTitleTitle(appTitle: AppTitle, tile: String?) {
    appTitle.setTitle(tile)
}
//AppEditText
@BindingAdapter("afterTextChanged")
fun editAfterTextChange(et: EditText, value: MutableLiveData<String>?) {
//    et.addTextChangedListenerDsl {
//        afterTextChanged {
//            value?.value = it?.toString()
//        }
//    }
//    et.addTextChangedListenerClosure(
//        afterTextChanged = {
//            value?.value= it?.toString()
//        }
//    )
    et.addTextChangedListenerClosure {
        value?.value = it?.toString()
    }
}

//进度控件的适配方法
@BindingAdapter("schedule")
fun setProgressViewSchedule(view:AppProgressView, schedule:Int){
    view.setSchedule(schedule)
}
@BindingAdapter("progressHintText")
fun setProgressViewHintText(view:AppProgressView, hintText:String?){
    view.setShowText(hintText)
}