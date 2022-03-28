package dyzn.csxc.yiliao.lib_common.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import dyzn.csxc.yiliao.lib_common.R

/**
 *@author YLC-D
 *说明: 加载动画的dialog
 */
class LoadingDialog private constructor(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)
        this.setContentView(R.layout.base_loading_dialog_layout)
        //设置dialog属性
        super.setCancelable(false)
        super.setCanceledOnTouchOutside(false)
        //获得window窗口的属性
        val params = window!!.attributes
        //设置窗口宽度为充满全屏
        //如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        //就是这个属性导致window后所有的东西都成暗淡
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //设置对话框的透明程度背景(非布局的透明度)
        params.dimAmount = 0.5f
        //将设置好的属性set回去
        window!!.attributes = params
        //设置dialog沉浸式效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window!!.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
            window!!.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
        } else {
            window!!.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    fun show(text: String?) {
        show()
        val tvMessage: AppTextView? = this.findViewById(R.id.tv_message)
        tvMessage?.text = text ?: ""
    }

    companion object {
        fun create(context: Context): LoadingDialog {
            return LoadingDialog(context)
        }
    }
}