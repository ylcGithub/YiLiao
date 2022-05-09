package dyzn.csxc.yiliao.bluetooth.view

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.bean.ADStructure
import dyzn.csxc.yiliao.bluetooth.databinding.BlueShowRawDataPopBinding
import dyzn.csxc.yiliao.lib_common.base.BaseCustomPopWindow
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import java.util.*

class ShowRawDataPop(
    context: Context,
    private val rawDataStr: String?,
    private val mADStructureArray: ArrayList<ADStructure>
) :
    BaseCustomPopWindow<BlueShowRawDataPopBinding>(context) {
    override fun getRootView(): View =
        LayoutInflater.from(context).inflate(R.layout.blue_show_raw_data_pop, null)

    override fun getBind(): BlueShowRawDataPopBinding = BlueShowRawDataPopBinding.bind(contentView)

    override fun init() {
        popupGravity = Gravity.CENTER
        val rd = "0x" + rawDataStr?.uppercase(Locale.getDefault())
        mBind.tvRawData.text = rd
        //设置数据单元
        val len = SpannableStringBuilder("LEN")
        val type = SpannableStringBuilder("TYPE")
        val value = SpannableStringBuilder("VALUE")
        val fcs = ForegroundColorSpan(ResUtil.getColor(R.color.color_main_blue))
        len.setSpan(fcs, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        type.setSpan(fcs, 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        value.setSpan(fcs, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        for (adStructure in mADStructureArray) {
            len.append("\n")
            len.append(adStructure.length.toString())
            type.append("\n")
            type.append(adStructure.type)
            value.append("\n")
            value.append(adStructure.data)
        }
        mBind.tvLen.text = len
        mBind.tvType.text = type
        mBind.tvValue.text = value
        mBind.btnSure.setOnClickListener {
            dismiss()
        }
    }
}