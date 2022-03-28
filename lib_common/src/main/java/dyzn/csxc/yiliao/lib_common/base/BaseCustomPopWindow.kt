package dyzn.csxc.yiliao.lib_common.base

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.viewbinding.ViewBinding
import razerdp.basepopup.BasePopupWindow

/**
 *@author YLC-D
 *说明: 在BasePopup框架基础上，自定义的弹窗基类
 */
abstract class BaseCustomPopWindow<VB : ViewBinding>(context: Context,width:Int = 0,height:Int = 0) : BasePopupWindow(context,width,height) {

    protected var mBind: VB? = null

    init {
        initBinding()
    }

    protected abstract fun getRootView(): View

    protected abstract fun getBind(): VB

    abstract fun init()
    override fun showPopupWindow() {
        init()
        super.showPopupWindow()
    }

    private fun initBinding() {
        contentView = getRootView()
        mBind = getBind()
    }

    /**
     * 创建动画垂直移动动画
     *
     * @param yFrom 开始位置
     * @param yTo   结束位置
     * @return 返回
     */
    protected fun createVerticalAnimation(yFrom: Float, yTo: Float): Animation {
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            yFrom,
            Animation.RELATIVE_TO_SELF,
            yTo
        )
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        return animation
    }

    /**
     * 创建一个横向移动的动画
     * @param xFrom 动画起始位置
     * @param xTo 动画结束位置
     * @return 创建好的动画
     */
    protected fun createHorizontalAnimation(xFrom: Float, xTo: Float): Animation {
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            xFrom,
            Animation.RELATIVE_TO_SELF,
            xTo,
            Animation.RELATIVE_TO_SELF,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        return animation
    }

    /**
     * 创建放大缩小动画
     *
     * @param fromScale 开始大小
     * @param toScale   结束大小
     * @return 返回创建好的动画
     */
    protected fun createScaleAnimation(fromScale: Float, toScale: Float): Animation {
        val animation = ScaleAnimation(
            fromScale,
            toScale,
            fromScale,
            toScale,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        return animation
    }
}