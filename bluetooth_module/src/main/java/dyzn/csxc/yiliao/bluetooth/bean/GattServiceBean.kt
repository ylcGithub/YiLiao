package dyzn.csxc.yiliao.bluetooth.bean

import android.bluetooth.BluetoothGattCharacteristic

data class GattServiceBean(val serviceName: String, val serviceUuid: String,
    val serviceType: String, val characteristics:List<BluetoothGattCharacteristic>?,
    var selected: Boolean = false, var expand: Boolean = false)
