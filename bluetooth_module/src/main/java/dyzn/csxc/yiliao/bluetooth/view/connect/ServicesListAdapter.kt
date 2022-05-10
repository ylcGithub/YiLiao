package dyzn.csxc.yiliao.bluetooth.view.connect


import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_SECONDARY
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.bean.GattServiceBean
import dyzn.csxc.yiliao.bluetooth.databinding.BlueServicesListItemBinding
import dyzn.csxc.yiliao.lib_common.base.BaseOneLayoutAdapter

class ServicesListAdapter :
    BaseOneLayoutAdapter<GattServiceBean, BlueServicesListItemBinding>(R.layout.blue_services_list_item) {
    override fun itemIsSame(oldItem: GattServiceBean, newItem: GattServiceBean): Boolean =
        oldItem == newItem

    @SuppressLint("SetTextI18n")
    override fun onBindOneTypeItem(
        binding: BlueServicesListItemBinding, item: GattServiceBean, holder: RecyclerView.ViewHolder
    ) {
        binding.tvName.text = "名称：${item.name}"
        binding.tvUuid.text = "UUID:${item.uuid}"
        when (item.type) {
            SERVICE_TYPE_PRIMARY -> binding.tvType.text = "类型：主要服务"
            SERVICE_TYPE_SECONDARY -> binding.tvType.text = "类型：二级服务(包括在主要服务中)"
        }
        binding.ivSelected.isSelected = item.selected
    }

    fun setSelected(i: Int, item: GattServiceBean) {
        dataList[i].selected = !item.selected
        notifyItemChanged(i)
    }

}