package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.FontCustom
import dyzn.csxc.yiliao.lib_common.util.ResUtil

/**
 *@author YLC-D
 *说明:自定义button
 */
class AppButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatButton(context, attrs, defStyle) {


    @ColorInt
    var bgColor: Int = ResUtil.getColor(R.color.color_main_blue,context)
        set(value) {
            field = value
            setButtonBg()
        }

    @ColorInt
    var bgPressedColor: Int = ResUtil.getColor(R.color.color_main_blue_light,context)
        set(value) {
            field = value
            setButtonBg()
        }

    //enabled 为false时显示的颜色
    @ColorInt
    var disabledColor: Int = ResUtil.getColor(R.color.color_ccc,context)
        set(value) {
            field = value
            setButtonBg()
        }

    @ColorInt
    var strokeColor: Int = ResUtil.getColor(R.color.transparent,context)
        set(value) {
            field = value
            setButtonBg()
        }
    var strokeWidth: Int = 0
        set(value) {
            field = value
            setButtonBg()
        }
    var radius: Int = 0
        set(value) {
            field = value
            setButtonBg()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AppButton)
        bgColor = ta.getColor(R.styleable.AppButton_btn_bg_color, bgColor)
        bgPressedColor = ta.getColor(R.styleable.AppButton_btn_pressed_color, bgPressedColor)
        disabledColor = ta.getColor(R.styleable.AppButton_btn_disabled_color, disabledColor)
        strokeColor = ta.getColor(R.styleable.AppButton_btn_stroke_color, strokeColor)
        strokeWidth = ta.getDimensionPixelSize(R.styleable.AppButton_btn_stroke_color, 0)
        radius = ta.getDimensionPixelSize(R.styleable.AppButton_btn_radius, 0)
        isEnabled
        ta.recycle()
        gravity = Gravity.CENTER
        setTextColor(ResUtil.getColor(R.color.white,context))
        //不是预览页面情况下，设置字体样式
        if (!isInEditMode) {
            typeface = FontCustom.setFont(context)
        }
        setButtonBg()
    }

    private fun setButtonBg() {
        background = if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable(getColorSelector(), createStateListDrawable(true), null)
        } else {
           createStateListDrawable(false)
        }
    }

    private fun getColorSelector(): ColorStateList {
        val colors = intArrayOf(bgPressedColor,disabledColor, bgColor)
        val states = arrayOfNulls<IntArray>(3)
        states[0] = intArrayOf(android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_window_focused)
        states[2] = intArrayOf()
        return ColorStateList(states, colors)
    }

    private fun createStateListDrawable(isRipple: Boolean):StateListDrawable{
        val bg  = StateListDrawable()
        val normal = GradientDrawable().apply {
            cornerRadius = radius.toFloat()
            setStroke(strokeWidth, strokeColor)
            setColor(bgColor)
        }
        val disabled = GradientDrawable().apply {
            cornerRadius = radius.toFloat()
            setStroke(strokeWidth, strokeColor)
            setColor(disabledColor)
        }
        if(!isRipple){
            val pressed = GradientDrawable().apply {
                cornerRadius = radius.toFloat()
                setStroke(strokeWidth, strokeColor)
                setColor(bgPressedColor)
            }
            bg.addState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), pressed)
        }
        bg.addState(intArrayOf(android.R.attr.state_enabled), normal)
        bg.addState(intArrayOf(android.R.attr.state_window_focused), disabled)
        bg.addState(intArrayOf(), normal)
        return bg
    }
}