package dyzn.csxc.yiliao.bluetooth.view.connect

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import dyzn.csxc.yiliao.bluetooth.util.BluetoothUtils
import dyzn.csxc.yiliao.lib_common.base.BaseViewModel
import dyzn.csxc.yiliao.lib_common.expand.toast

/**
 *@author YLC-D
 *@create on 2022/3/14 09
 *说明:
 */
class BluetoothConnectViewModel : BaseViewModel() {
    val bluetoothAddress = MutableLiveData<String>()
    val wifiName = MutableLiveData<String>()
    val wifiPassword = MutableLiveData<String>()
    //绑定蓝牙成功
    val blueBondSuc = MutableLiveData(false)
    private var timer: CountDownTimer? = null

    /**
     * 一分钟还没搜索到蓝牙就取消搜索
     */
    fun startTimer() {
        timer = object : CountDownTimer(60 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                loadingText.value = "蓝牙搜索中...(${millisUntilFinished/1000}s)"
            }

            override fun onFinish() {
                BluetoothUtils.cancelDiscovery()
                setLoadingState(false)
                "没有搜索到蓝牙，请确保蓝牙已打开".toast()
            }
        }
        timer?.start()
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }
}