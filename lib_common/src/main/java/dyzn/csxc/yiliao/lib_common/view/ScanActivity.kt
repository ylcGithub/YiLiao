package dyzn.csxc.yiliao.lib_common.view

import android.os.Bundle
import android.os.Vibrator
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.alibaba.android.arouter.facade.annotation.Route
import com.jaeger.library.StatusBarUtil
import com.jeremyliao.liveeventbus.LiveEventBus
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.base.BaseActivity
import dyzn.csxc.yiliao.lib_common.config.LiveBusKey
import dyzn.csxc.yiliao.lib_common.config.RoutePath
import dyzn.csxc.yiliao.lib_common.databinding.ActivityScanBinding
import dyzn.csxc.yiliao.lib_common.expand.toast

/**
 *@author YLC-D
 *说明:
 */
@Route(path = RoutePath.SCAN_ACTIVITY)
class ScanActivity:BaseActivity(), QRCodeView.Delegate {
    protected lateinit var mBinding: ActivityScanBinding
    override fun getLayoutId(): Int = R.layout.activity_scan

    override fun initStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mBinding.scanTitle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needSetLayout = false
        super.onCreate(savedInstanceState)
        mBinding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
    
    override fun initData() {
        mBinding.zbarView.setDelegate(this)
        mBinding.scanTitle.setOnClickListener {
            closeActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        // 显示扫描框，并开始识别
        mBinding.zbarView.startSpotAndShowRect()
    }

    override fun onStop() {
        super.onStop()
        mBinding.zbarView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.zbarView.onDestroy()
    }

    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(200L)
        }
    }
    override fun onScanQRCodeSuccess(result: String?) {
        //传递出扫码结果
        vibrate()
        LiveEventBus.get(LiveBusKey.SCAN_RESULT, String::class.java).post(result)
        closeActivity()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText: String = mBinding.zbarView.scanBoxView.tipText
        val ambientBrightnessTip = "\n环境过暗，已打开闪光灯"
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mBinding.zbarView.scanBoxView.tipText = tipText + ambientBrightnessTip
                mBinding.zbarView.openFlashlight() // 打开闪光灯
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                mBinding.zbarView.scanBoxView.tipText = tipText
                mBinding.zbarView.closeFlashlight() //关闭闪光灯
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        "打开相机出错".toast()
    }
}