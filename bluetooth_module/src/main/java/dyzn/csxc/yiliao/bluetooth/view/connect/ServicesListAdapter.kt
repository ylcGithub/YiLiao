package dyzn.csxc.yiliao.bluetooth.view.connect


import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_SECONDARY
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.bean.GattServiceBean
import dyzn.csxc.yiliao.bluetooth.databinding.BlueServicesListItemBinding
import dyzn.csxc.yiliao.lib_common.base.BaseOneLayoutAdapter
import dyzn.csxc.yiliao.lib_common.expand.buildSpannableString
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import java.util.*

class ServicesListAdapter :
    BaseOneLayoutAdapter<GattServiceBean, BlueServicesListItemBinding>(R.layout.blue_services_list_item) {
    override fun itemIsSame(oldItem: GattServiceBean, newItem: GattServiceBean): Boolean =
        oldItem == newItem

    @SuppressLint("SetTextI18n")
    override fun onBindOneTypeItem(
        binding: BlueServicesListItemBinding, item: GattServiceBean, holder: RecyclerView.ViewHolder
    ) {
        binding.tvName.text = "名称：${getServiceName(item.service.uuid)}"
        binding.tvUuid.text = "UUID:${item.service.uuid}"
        when (item.service.type) {
            SERVICE_TYPE_PRIMARY -> binding.tvType.text = "类型：主要服务"
            SERVICE_TYPE_SECONDARY -> binding.tvType.text = "类型：二级服务(包括在主要服务中)"
        }
        binding.ivSelected.isSelected = item.selected
        binding.root.setOnClickListener {
            dataList.forEachIndexed { index, gsb ->
                if (gsb == item) {
                    gsb.expand = !gsb.expand
                    selectClickListener?.invoke(if (gsb.expand) index else -2, index, -1)
                } else gsb.expand = false
            }
            notifyItemChanged(holder.adapterPosition)
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

    fun setSelected(ind: Int) {
        dataList.forEachIndexed { index, bean ->
            if (ind == index) bean.selected = !bean.selected
            else bean.selected = false
        }
        notifyItemChanged(ind)
    }


    private fun setExpandView(binding: BlueServicesListItemBinding, item: GattServiceBean) {
        val characteristics: List<BluetoothGattCharacteristic>? = item.service.characteristics
        characteristics?.let { c_list ->
            binding.tvExpand.visibility = View.VISIBLE
            binding.tvExpand.text = ""
            c_list.forEachIndexed { c_ind, bgc ->
                //特征值显示
                val cs = "特征${c_ind + 1}\nUUID:${bgc.uuid}\n特征特性:${getProperties(bgc.properties)}\n"
                binding.tvExpand.buildSpannableString {
                    addText(cs) {
                        setColor(ResUtil.getColor(R.color.color_main_blue))
                        onClick(false) {
                            "选中特征${c_ind + 1}".toast()
                            selectClickListener?.invoke(-1, c_ind, -1)
                        }
                    }
                }
                bgc.descriptors?.let { d_list ->
                    d_list.forEachIndexed { d_ind, bgd ->
                        bgd.describeContents()
                        val ds = "\t\t描述${d_ind + 1}\n\t\tUUID:${bgd.uuid}\n"
                        binding.tvExpand.buildSpannableString {
                            addText(ds) {
                                setColor(ResUtil.getColor(R.color.color_main_red))
                                onClick(false) {
                                    "选中描述${d_ind + 1}".toast()
                                    selectClickListener?.invoke(-1, -1, d_ind)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取特征的具体属性
     */
    private fun getProperties(properties: Int): String {
        val buffer = StringBuffer()
        for (i in 1..8) {
            when (i) {
                1 -> if (properties and BluetoothGattCharacteristic.PROPERTY_BROADCAST != 0) buffer.append(
                    "BROADCAST,"
                )
                2 -> if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) buffer.append(
                    "READ,"
                )
                3 -> if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) buffer.append(
                    "WRITE NO RESPONSE,"
                )
                4 -> if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) buffer.append(
                    "WRITE,"
                )
                5 -> if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) buffer.append(
                    "NOTIFY,"
                )
                6 -> if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) buffer.append(
                    "INDICATE,"
                )
                7 -> if (properties and BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE != 0) buffer.append(
                    "SIGNED WRITE,"
                )
                8 -> if (properties and BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS != 0) buffer.append(
                    "EXTENDED PROPS,"
                )
            }
        }
        val str = buffer.toString()
        return if (str.isNotEmpty()) str.substring(0, str.length - 1) //减最后的逗号
        else ""
    }

    private fun getServiceName(uuid: UUID): String {
        return when (uuid.toString()) {
            "00001801-0000-1000-8000-00805f9b34fb" -> {
                "通用属性"
            }
            "00001800-0000-1000-8000-00805f9b34fb" -> {
                "通用访问"
            }
            else -> "服务"
        }
    }
}