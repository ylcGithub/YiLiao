package dyzn.csxc.yiliao.bluetooth.bean

import android.bluetooth.BluetoothGattService

data class GattServiceBean(val service:BluetoothGattService,var selected:Boolean = false,var expand:Boolean = false)
