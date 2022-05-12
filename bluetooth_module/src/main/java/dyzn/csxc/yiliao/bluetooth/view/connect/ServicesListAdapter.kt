package dyzn.csxc.yiliao.bluetooth.view.connect


import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.bean.GattServiceBean
import dyzn.csxc.yiliao.bluetooth.databinding.BlueServicesListItemBinding
import dyzn.csxc.yiliao.lib_common.base.BaseOneLayoutAdapter
import dyzn.csxc.yiliao.lib_common.expand.setSpannableString
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.widget.AppTextView

class ServicesListAdapter : BaseOneLayoutAdapter<GattServiceBean, BlueServicesListItemBinding>(
    R.layout.blue_services_list_item) {
    override fun itemIsSame(oldItem: GattServiceBean,
        newItem: GattServiceBean): Boolean = oldItem == newItem

    @SuppressLint("SetTextI18n")
    override fun onBindOneTypeItem(binding: BlueServicesListItemBinding, item: GattServiceBean,
        holder: RecyclerView.ViewHolder) {
        binding.tvName.text = item.serviceName
        binding.tvUuid.text = item.serviceUuid
        binding.tvType.text = item.serviceType
        binding.ivSelected.isSelected = item.selected
        binding.root.setOnClickListener {
            val currIndex = holder.adapterPosition
            val selected = dataList[currIndex].expand
            if (selected) { //当前是选中状态就关闭
                setSelected(currIndex, false)
            } else { //展开当前item
                //首先关闭可能展开的其他item
                dataList.forEachIndexed { index, bean ->
                    if (bean.expand) {
                        setSelected(index, false)
                    }
                }
                selectClickListener?.invoke(currIndex, -1, -1)
                setSelected(currIndex, true)
            }
        }
        if (item.expand) setExpandView(binding, item)
        else {
            binding.tvExpand.text = ""
            binding.tvExpand.visibility = View.GONE
        }
    }

    private var selectClickListener: ((s_index: Int, c_index: Int, d_index: Int) -> Unit)? = null
    fun setSelectClick(lis: ((s_index: Int, c_index: Int, d_index: Int) -> Unit)?) {
        selectClickListener = lis
    }

    private fun setSelected(ind: Int, selected: Boolean) {
        dataList[ind].expand = selected
        dataList[ind].selected = selected
        notifyItemChanged(ind)
    }


    private fun setExpandView(binding: BlueServicesListItemBinding, item: GattServiceBean) {
        val characteristics: List<BluetoothGattCharacteristic> = item.characteristics ?: return
        binding.tvExpand.visibility = View.VISIBLE
        binding.tvExpand.text = ""
        characteristics.forEachIndexed { c_ind, bgc -> //特征值显示
            val cs = "特征${c_ind + 1}\nUUID:${bgc.uuid}\n特征特性:${getProperties(bgc.properties)}\n"
            addSpanText(binding.tvExpand, cs, false, c_ind)
            val descriptors = bgc.descriptors ?: return@forEachIndexed
            descriptors.forEachIndexed { d_ind, bgd ->
                val ds = "\t\t描述${d_ind + 1}\n\t\tUUID:${bgd.uuid}\n"
                addSpanText(binding.tvExpand, ds, true, d_ind)
            }
        }
    }

    private fun addSpanText(view: AppTextView, text: String, isDes: Boolean, index: Int) {
        view.setSpannableString(true) {
            addText(text) {
                setColor(if (isDes) R.color.color_main_red else R.color.color_main_blue)
                onClick(true) {
                    "选中:$text".toast()
                    selectClickListener?.invoke(-1, if (isDes) -1 else index,
                        if (isDes) index else -1)
                }
            }
        }
    }

    /**
     * 获取特征的具体属性
     */
    private fun getProperties(properties: Int): String {
        val buffer = StringBuffer()
        if (properties and PROPERTY_BROADCAST != 0) buffer.append("BROADCAST,")
        if (properties and PROPERTY_READ != 0) buffer.append("READ,")
        if (properties and PROPERTY_WRITE_NO_RESPONSE != 0) buffer.append("WRITE NO RESPONSE,")
        if (properties and PROPERTY_WRITE != 0) buffer.append("WRITE,")
        if (properties and PROPERTY_NOTIFY != 0) buffer.append("NOTIFY,")
        if (properties and PROPERTY_INDICATE != 0) buffer.append("INDICATE,")
        if (properties and PROPERTY_SIGNED_WRITE != 0) buffer.append("SIGNED WRITE,")
        if (properties and PROPERTY_EXTENDED_PROPS != 0) buffer.append("EXTENDED PROPS,")
        val str = buffer.toString()
        return if (str.isNotEmpty()) str.substring(0, str.length - 1) //减最后的逗号
        else ""
    }


}