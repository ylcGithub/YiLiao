package dyzn.csxc.yiliao.bluetooth.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanRecord

/**
 * 扫描到的蓝牙设备信息
 */
data class BlueDevice(
    val device: BluetoothDevice,
    val rssi: Int,
    val scanRecordBytes: ByteArray?,
    val isConnectable: Boolean = true,
    val scanRecord: ScanRecord? = null
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlueDevice

        if (device != other.device) return false
        if (rssi != other.rssi) return false
        if (!scanRecordBytes.contentEquals(other.scanRecordBytes)) return false
        if (isConnectable != other.isConnectable) return false
        if (scanRecord != other.scanRecord) return false

        return true
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + rssi
        result = 31 * result + scanRecordBytes.contentHashCode()
        result = 31 * result + isConnectable.hashCode()
        result = 31 * result + scanRecord.hashCode()
        return result
    }

}