package dyzn.csxc.yiliao.bluetooth.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import com.jeremyliao.liveeventbus.LiveEventBus
import dyzn.csxc.yiliao.bluetooth.bean.BlueDevice
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.util.LogUtil

/**
 * @author YLC-D
 * @create on 2019/5/15 10
 *
 * 说明: 蓝牙的工具类
 */
object BluetoothUtils {
    //蓝牙扫描的时间 10s
    private const val SCAN_PERIOD = 10000L

    //本地蓝牙设备的适配类,所有的蓝牙操作都要通过该类完成
    private var bluetoothAdapter: BluetoothAdapter? = null

    //扫描回调
    private var leScanner: BluetoothLeScanner? = null

    //蓝牙列表
    private var scanResultSet: ArrayList<BlueDevice>? = null

    //结果回调
    private lateinit var listener: (ArrayList<BlueDevice>) -> Unit

    //设置监听
    fun setOnListener(bluetoothDevice: (ArrayList<BlueDevice>) -> Unit) {
        listener = bluetoothDevice
    }

    /**
     * 开始搜索蓝牙设备
     * Android官方提供的蓝牙扫描方式有三种，分别是：
     * 1、BluetoothAdapter.startDiscovery()//可以扫描经典蓝牙和ble蓝牙两种
     * 2、BluetoothAdapter.startLeScan()//扫描低功耗蓝牙，在api21已经弃用，不过还是可以使用
     * 3、BluetoothLeScanner.startScan()//新的ble扫描方法
     */
    fun startScanBle(context: Context?): Boolean {
        if (null == scanResultSet) {
            scanResultSet = ArrayList()
        }
        scanResultSet?.clear()
        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) ?: return false
        bluetoothAdapter = (bluetoothManager as BluetoothManager).adapter
        if (bluetoothAdapter == null) {
            "该手机没有蓝牙模块".toast()
            return false
        }
        leScanner = bluetoothAdapter!!.bluetoothLeScanner
        if (bluetoothAdapter!!.isEnabled) {
            val builder = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            if(Build.VERSION.SDK_INT >= 23){
                //定义回调类型
                builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
                builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                /**
                 *  //设置扫描模式。可选择模式主要三种( 从上到下越来越耗电，扫描间隔越来越短，即扫描速度会越来越快。
                //1、SCAN_MODE_LOW_POWER：低功耗模式(默认扫描模式，如果扫描应用程序不在前台，则强制使用此模式。)
                //2、SCAN_MODE_BALANCED： 平衡模式
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                //设置回调类型。可选择模式主要三种：
                //1、CALLBACK_TYPE_ALL_MATCHES：1 寻找符合过滤条件的蓝牙广播，如果没有设置过滤条件，则返回全部广播包
                //2、CALLBACK_TYPE_FIRST_MATCH：2 与筛选条件匹配的第一个广播包触发结果回调
                //3、CALLBACK_TYPE_MATCH_LOST：4 有过滤条件时过滤，返回符合过滤条件的蓝牙广播。无过滤条件时，返回全部蓝牙广播
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                //设置蓝牙扫描滤波器硬件匹配的匹配模式
                //MATCH_MODE_STICKY：2 粘性模式，在通过硬件报告之前，需要更高的信号强度和目击阈值
                //MATCH_MODE_AGGRESSIVE：1 激进模式，即使信号强度微弱且持续时间内瞄准/匹配的次数很少，hw也会更快地确定匹配。
                //.setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）。
                //该参数默认为 0，如果不修改它的值，则默认只会在onScanResult(int,ScanResult)中返回扫描到的蓝牙设备，不会触发onBatchScanResults(List)方法。(onScanResult(int,ScanResult) 和 onBatchScanResults(List) 是互斥的。 )
                //设置为0以立即通知结果,不开启批处理扫描模式。即ScanCallback蓝牙回调中，不会触发onBatchScanResults(List)方法，但会触发onScanResult(int,ScanResult)方法，返回扫描到的蓝牙设备。
                //当设置的时间大于0L时，则会开启批处理扫描模式。即触发onBatchScanResults(List)方法，返回扫描到的蓝牙设备列表。但不会触发onScanResult(int,ScanResult)方法。
                //.setReportDelay(0)
                 */
            }

            //芯片组支持批处理芯片上的扫描
            if (bluetoothAdapter!!.isOffloadedScanBatchingSupported) {
                //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
                //设置为0以立即通知结果
                builder.setReportDelay(0L)
            }

            //第一个参数(ScanFilter)：筛选条件,可以通过设置过滤器的mDeviceName、mDeviceAddress、mServiceUuid等作为过滤条件进行过滤。
            //第二个参数(ScanSettings)：设置,可以设置扫描的mScanMode 、mCallbackType 、mScanResultType 等。
            //List<ScanFilter> scanFilters = new ArrayList<>();
            //scanFilters.add(new ScanFilter.Builder().setDeviceName("deviceName").build());
            leScanner?.startScan(null, builder.build(), leScanCallback)
        } else {
            context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return false
        }
        startTimer()
        return true
    }

    private var timer:CountDownTimer?= null
    private fun startTimer() {
        timer = object : CountDownTimer(SCAN_PERIOD, 1000L) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                stopScanBle()
            }
        }
        timer?.start()
    }

    /**
     * 回调函数中尽量不要做耗时操作
     */
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            //信号强度【在dBm中返回接收到的信号强度。有效范围为[-127,126]。】
            val rssi = result.rssi
            //ScanRecord record =result.getScanRecord();//搜索记录相关
            val device = result.device
            //设备名称
            val deviceName = device.name
//            //设备MAC地址
//            val deviceAddress = device.address
            //BOND_NONE：数值 10 表示远程设备未绑定，没有共享链接密钥，因此通信（如果允许的话）将是未经身份验证和未加密的。【默认10未绑定】
            //BOND_BONDING：数值 11 表示正在与远程设备进行绑定;
            //BOND_BONDED：数值 12 表示远程设备已绑定，远程设备本地存储共享连接的密钥，因此可以对通信进行身份验证和加密
//            val bondState = device.bondState
            //设备的蓝牙设备类型
            //DEVICE_TYPE_CLASSIC 传统蓝牙 常量值：1,
            //DEVICE_TYPE_LE  低功耗蓝牙 常量值：2【一般为2，表示为BLE设备】
            //DEVICE_TYPE_DUAL 双模蓝牙 常量值：3.
            //DEVICE_TYPE_UNKNOWN：未知 常量值：0）
            val type = device.type
            //设备名称为空的设备不显示
            if (deviceName == null) return
            val d = BlueDevice(
                device,
                rssi,
                result.scanRecord?.bytes,
                result.isConnectable,
                result.scanRecord
            )
            //过滤掉无设备名【注意：无设备名不代表无mac地址】和被绑定的
            if (!containsDevice(d)) {
                scanResultSet!!.add(d)
                listener.invoke(scanResultSet!!)
            }
        }


        override fun onBatchScanResults(results: List<ScanResult>) {
            LogUtil.log("批量扫描结果：${results}")
        }

        override fun onScanFailed(errorCode: Int) {
            //手机在startScan扫描的过程中还没来得及stopScan ,就被系统强制杀掉了, 导致mClientIf未被正常释放,
            //实例和相关蓝牙对象已被残留到系统蓝牙服务中,  打开app后又重新初始化ScanCallback多次被注册,
            //导致每次的扫描mClientIf的值都在递增, 于是mClientIf的值在增加到一定程度时
            //(最大mClientIf数量视国产系统而定不做深究), onScanFailed 返回了 errorCode =2  。
            LogUtil.log("扫描BLE蓝牙是吧，错误码: $errorCode")
        }
    }

    private fun containsDevice(bd: BlueDevice): Boolean {
        var c = false
        scanResultSet?.forEach { d ->
            if (d.device.address == bd.device.address) c = true
        }
        return c
    }

    /**
     * 关闭扫描
     */
    fun stopScanBle() {
        leScanner?.let {
            if (bluetoothAdapter?.isEnabled == true) {
                //获取缓冲好的扫描数据
                it.flushPendingScanResults(leScanCallback)
                //关闭扫描
                it.stopScan(leScanCallback)
                timer?.cancel()
                LiveEventBus.get<Boolean>(LiveBusKey.SCAN_BLUE_STOP).post(true)
            }
        }
    }
}