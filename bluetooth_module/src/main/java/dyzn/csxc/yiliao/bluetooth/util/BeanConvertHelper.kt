package dyzn.csxc.yiliao.bluetooth.util

import android.bluetooth.BluetoothGattService
import dyzn.csxc.yiliao.bluetooth.bean.GattServiceBean
import java.util.*

/**
 * @author YLC-D
 * @create on 2019/5/15 10
 *
 * 说明: 将系统的BluetoothGattService 转换成我们需要的 数据体
 */
object BeanConvertHelper {
    //转换BluetoothGattService类成我们的数据体
    fun convertBgs(src: BluetoothGattService): GattServiceBean {
        return GattServiceBean(getServiceName(src.uuid), "UUID:${src.uuid}",
            getServiceType(src.type), src.characteristics)
    }

    private fun getServiceName(uuid: UUID): String {
        return when (uuid.toString()) {
            "00001801-0000-1000-8000-00805f9b34fb" -> {
                "名称:通用属性"
            }
            "00001800-0000-1000-8000-00805f9b34fb" -> {
                "名称:通用访问"
            }
            else -> "名称:服务"
        }
    }

    private fun getServiceType(type: Int): String {
        return when (type) {
            BluetoothGattService.SERVICE_TYPE_PRIMARY -> "类型:主要服务"
            BluetoothGattService.SERVICE_TYPE_SECONDARY -> "类型:二级服务(包括在主要服务中)"
            else -> ""
        }
    }

}