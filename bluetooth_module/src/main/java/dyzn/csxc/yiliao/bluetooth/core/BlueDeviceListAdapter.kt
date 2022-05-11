package dyzn.csxc.yiliao.bluetooth.core

import android.annotation.SuppressLint
import android.bluetooth.le.ScanRecord
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.bluetooth.R
import dyzn.csxc.yiliao.bluetooth.bean.ADStructure
import dyzn.csxc.yiliao.bluetooth.bean.BlueDevice
import dyzn.csxc.yiliao.bluetooth.databinding.BlueDeviceListItemBinding
import dyzn.csxc.yiliao.bluetooth.view.ShowRawDataPop
import dyzn.csxc.yiliao.lib_common.base.BaseApplication
import dyzn.csxc.yiliao.lib_common.base.BaseOneLayoutAdapter
import dyzn.csxc.yiliao.lib_common.expand.ylDecodeAlignHexString
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import dyzn.csxc.yiliao.lib_common.widget.AppTextView
import java.util.*

class BlueDeviceListAdapter :
    BaseOneLayoutAdapter<BlueDevice, BlueDeviceListItemBinding>(R.layout.blue_device_list_item) {
    override fun itemIsSame(oldItem: BlueDevice, newItem: BlueDevice): Boolean = oldItem == newItem

    @SuppressLint("SetTextI18n")
    override fun onBindOneTypeItem(
        binding: BlueDeviceListItemBinding,
        item: BlueDevice,
        holder: RecyclerView.ViewHolder
    ) {
        binding.tvName.text = item.device.name
        binding.tvAddress.text = item.device.address
        binding.tvBondState.text = (if (item.device.bondState == 12) "已绑定" else "未绑定")
        binding.tvRssi.text = "rssi:${item.rssi}"
        //存储广播数据单元数组
        val mADStructureArray = ArrayList<ADStructure>()
        val raw = item.scanRecordBytes?.let { parseBleADData(it,mADStructureArray) }
        binding.tvUuids.text = getUUID(item.scanRecord,mADStructureArray)
        setFirmData(binding.tvFirmId,binding.tvFirmData,item.scanRecord,mADStructureArray)
        setServiceData(binding.tvServiceData,item.scanRecord,mADStructureArray)
        binding.btnConnect.setOnClickListener { l?.invoke(item) }
        binding.btnRaw.setOnClickListener {
            ShowRawDataPop(BaseApplication.getAppContext(),raw,mADStructureArray).showPopupWindow()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setFirmData(tvFirmId: AppTextView, tvFirmData: AppTextView, scanRecord: ScanRecord?,mADStructureArray:ArrayList<ADStructure>) {
        //厂商数据
        scanRecord?.manufacturerSpecificData?.let{
            //添加厂商数据信息
            for (adStructure in mADStructureArray) {
                if (adStructure.type == "0xFF"){
                    //除去之前添加的0x
                    val data = adStructure.data.subSequence(2,adStructure.data.length)
                    //获取厂商id
                    val manufacturerId = data.substring(2,4) + data.substring(0,2)
                    //获取真正的厂商数据
                    val manufacturerData = data.substring(4,data.length)
                    tvFirmId.text = "厂商ID:${"0X$manufacturerId"}"
                    tvFirmData.text = "厂商数据:${"0X$manufacturerData"}"
                    break
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setServiceData(tvServiceData:AppTextView, scanRecord: ScanRecord?,mADStructureArray:ArrayList<ADStructure>){
        tvServiceData.text = ""
        tvServiceData.visibility = View.GONE
        scanRecord?.serviceData?.let{
            for (adStructure in mADStructureArray) {
                when(adStructure.type){
                    //16-bit 服务数据
                    "0x16" -> {
                        //除去之前添加的0x
                        val data = adStructure.data.subSequence(2,adStructure.data.length)
                        //获取16bit的uuid
                        val uuid = "0x" + data.substring(2,4) + data.substring(0,2)
                        //获取对应的数据
                        val serviceData = data.substring(4,data.length)
                        tvServiceData.visibility = View.VISIBLE
                        tvServiceData.text = "16-bit UUID: ${"0X$uuid"} \n数据: ${"0x$serviceData"}"
                    }
                    //android发不出32bit的服务数据
                    //32-bit 服务数据
                    "0x20" -> {
                        LogUtil.log("哎，我是32位的数据，6不6")
                    }
                    else->{
                        LogUtil.log("deviceName:${scanRecord.deviceName},服务数据的的类型：${adStructure.type}")
                    }
                }
            }
        }
    }

    private var l: ((BlueDevice) -> Unit)? = null
    fun setConnectClickListener(listener: (bd: BlueDevice) -> Unit) {
        l = listener
    }

    /**
     * 解析蓝牙广播报文，获取数据单元
     */
    private fun parseBleADData(byteArray: ByteArray,mADStructureArray:ArrayList<ADStructure>): String {
        //将字节数组转十六进制字符串
        var rawDataStr = byteArray.ylDecodeAlignHexString()
        //存储实际数据段
        var dataStr = ""
        while (true) {
            //取长度
            val lengthStr = rawDataStr.substring(0, 2)
            //如果长度为0，则退出
            if (lengthStr == "00") {
                break
            }
            //将长度转10进制
            val length = Integer.parseInt(lengthStr, 16)
            //length表示后面多少字节也属于该数据单元，所以整个数据单元的长度 = length + 1
            val data = rawDataStr.substring(0, (length + 1) * 2)
            //存储每个数据单元的值
            dataStr += data
            //裁剪原始数据，方便后面裁剪数据单元
            rawDataStr = rawDataStr.substring((length + 1) * 2, rawDataStr.length)
            //创建广播数据单元bean，并存储到数据中
            //第一个字节是长度，第二个字节是类型，再后面才是数据（一个字节用两个十六进制字符串表示）
            mADStructureArray.add(ADStructure(length, "0x" + data.substring(2, 4).uppercase(Locale.getDefault()),
                    "0x" + data.substring(4, data.length).uppercase(Locale.getDefault())))
        }
        //返回蓝牙广播数据报文
        return dataStr
    }

    private fun getUUID(scanRecord: ScanRecord?,mADStructureArray:ArrayList<ADStructure>): String {
        var uuids = ""
        scanRecord?.serviceUuids?.let {
            //添加UUID信息
            for (adStructure in mADStructureArray) {
                when (adStructure.type) {
                    //完整的16bit UUID 列表
                    "0x03" -> {
                        //除去之前添加的0x
                        val dataStr = adStructure.data.substring(2, adStructure.data.length)
                        for (i in 0 until dataStr.length / 4) {
                            val uuid = "0x" + dataStr.substring(2 + i * 4, 4 + i * 4) +
                                    dataStr.substring(0 + i * 4, 2 + i * 4)
                            uuids = if (uuids == "") "16位UUIDS:${uuid}" else "$uuids,$uuid"
                        }
                    }
                    //完整的32bit UUID 列表
                    "0x05" -> {
                        //除去之前添加的0x
                        val dataStr = adStructure.data.substring(2, adStructure.data.length)
                        for (i in 0 until dataStr.length / 8) {
                            val uuid = "0x" + dataStr.substring(
                                6 + i * 8,
                                8 + i * 8
                            ) + dataStr.substring(4 + i * 8, 6 + i * 8) +
                                    dataStr.substring(
                                        2 + i * 8,
                                        4 + i * 8
                                    ) + dataStr.substring(0 + i * 8, 2 + i * 8)
                            uuids = (if (uuids == "") "32位UUIDS:${uuid}" else "$uuids, $uuid")
                        }
                    }
                }
            }
        }
        if(uuids.isEmpty()) uuids = "没有查到UUID"
        return uuids
    }


}