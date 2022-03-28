package dyzn.csxc.yiliao.lib_common.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.FontCustom

/**
 * @author YLC-D
 * 说明:
 */
open class AppTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var normalTextColor: ColorStateList
    private var pressedColor: ColorStateList? = null
    private var selectedColor: ColorStateList? = null

    private val radiusArray = FloatArray(8)
    private val bgDrawable: GradientDrawable = GradientDrawable()

    var radius: Int = 0
        set(value) {
            field = value
            setBgSelector()
        }
    private var leftTopRadius: Int = 0
    private var leftBottomRadius: Int = 0
    private var rightTopRadius: Int = 0
    private var rightBottomRadius: Int = 0

    var strokeWidth: Int = 0
        set(value) {
            field = value
            setBgSelector()
        }

    @ColorInt
    var strokeColor: Int = 0
        set(value) {
            field = value
            setBgSelector()
        }

    @ColorInt
    var bgColor: Int = 0
        set(value) {
            field = value
            setBgSelector()
        }
    private var isRippleEnable = false


    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (selectedColor != null && isSelected) {
            setTextColor(selectedColor)
        } else {
            if (isPressed && pressedColor != null) {
                setTextColor(pressedColor)
            } else {
                setTextColor(normalTextColor)
            }
        }
    }

    fun setPressedColor(@ColorInt pColor: Int) {
        pressedColor = ColorStateList.valueOf(pColor)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        normalTextColor = ColorStateList.valueOf(color)
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AppTextView)
        val pressColor = ta.getColor(R.styleable.AppTextView_text_pressed_color, 0)
        val selectColor = ta.getColor(R.styleable.AppTextView_text_selected_color, 0)
        bgColor = ta.getColor(R.styleable.AppTextView_bg_color, 0)
        radius = ta.getDimensionPixelSize(R.styleable.AppTextView_bg_radius, 0)
        strokeWidth = ta.getDimensionPixelSize(R.styleable.AppTextView_stroke_width, 0)
        strokeColor = ta.getColor(R.styleable.AppTextView_stroke_color, 0)
        leftTopRadius = ta.getDimensionPixelSize(R.styleable.AppTextView_bg_left_top_radius, 0)
        leftBottomRadius =
            ta.getDimensionPixelSize(R.styleable.AppTextView_bg_left_bottom_radius, 0)
        rightTopRadius = ta.getDimensionPixelSize(R.styleable.AppTextView_bg_right_top_radius, 0)
        rightBottomRadius =
            ta.getDimensionPixelSize(R.styleable.AppTextView_bg_right_bottom_radius, 0)
        isRippleEnable = ta.getBoolean(R.styleable.AppTextView_is_ripple_enable, false)
        ta.recycle()

        normalTextColor = textColors
        if (pressColor != 0) {
            pressedColor = ColorStateList.valueOf(pressColor)
        }
        if (selectColor != 0) {
            selectedColor = ColorStateList.valueOf(selectColor)
            pressedColor = selectedColor
        }

        //不是预览页面情况下，设置字体样式
        if (!isInEditMode) {
            typeface = FontCustom.setFont(context)
        }

        setBgSelector()
    }

    private fun setBgSelector() {
        if (bgColor != 0) {
            val bg = StateListDrawable()
            initBgDrawable()
            background = if (Build.VERSION.SDK_INT >= 21 && isRippleEnable && isEnabled) {
                RippleDrawable(
                    getColorSelector(
                        bgColor,
                        bgColor,
                        bgColor
                    ), bgDrawable, null
                )
            } else {
                if (isEnabled) bg.addState(intArrayOf(-16842919, -16842913), bgDrawable)
                bg
            }
        }
    }

    private fun initBgDrawable() {
        bgDrawable.setColor(bgColor)
        if (this.leftTopRadius <= 0 && this.rightTopRadius <= 0 && this.leftBottomRadius <= 0 && this.rightBottomRadius <= 0) {
            bgDrawable.cornerRadius = radius.toFloat()
        } else {
            radiusArray[0] = leftTopRadius.toFloat()
            radiusArray[1] = leftTopRadius.toFloat()
            radiusArray[2] = rightTopRadius.toFloat()
            radiusArray[3] = rightTopRadius.toFloat()
            radiusArray[4] = rightBottomRadius.toFloat()
            radiusArray[5] = rightBottomRadius.toFloat()
            radiusArray[6] = leftBottomRadius.toFloat()
            radiusArray[7] = leftBottomRadius.toFloat()
            bgDrawable.cornerRadii = radiusArray
        }
        bgDrawable.setStroke(strokeWidth, strokeColor)
    }

    @TargetApi(11)
    private fun getColorSelector(
        normalColor: Int,
        pressedColor: Int,
        enabledColor: Int
    ): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(16842908),
                intArrayOf(16843518),
                intArrayOf(16842919),
                intArrayOf(-16842910),
                IntArray(0)
            ), intArrayOf(pressedColor, pressedColor, pressedColor, enabledColor, normalColor)
        )
    }

    fun notifyTypeFace() {
        typeface = FontCustom.setFont(context)
    }
}