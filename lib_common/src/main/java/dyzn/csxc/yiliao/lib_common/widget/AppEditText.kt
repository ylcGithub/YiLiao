package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.FontCustom

/**
 *@author YLC-D
 *说明:自定义的editText
 */
class AppEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatEditText(context, attrs, defStyleAttr) {
    @ColorInt
    var underLineColor: Int = -1
        set(value) {
            field = value
            initPaint()
        }

    var underLineHeight: Int = 2
        set(value) {
            field = value
            invalidate()
        }

    init {
        //不是预览页面情况下，设置字体样式
        if (!isInEditMode) {
            typeface = FontCustom.setFont(context)
            //避免自定义editText无法获取焦点
            isFocusableInTouchMode = true
        }
        gravity = Gravity.CENTER_VERTICAL
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AppEditText)
        underLineColor = ta.getColor(R.styleable.AppEditText_under_line_color, -1)
        underLineHeight = ta.getDimensionPixelSize(R.styleable.AppEditText_under_line_height, 2)
        ta.recycle()
    }

    fun notifyTypeFace() {
        typeface = FontCustom.setFont(context)
    }

    private var paint: Paint? = null
    private fun initPaint() {
        if (underLineColor != -1) {
            //设置画笔的属性
            paint = Paint()
            paint?.style = Paint.Style.STROKE
            //设置画笔颜色为红色
            paint?.color = underLineColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /**
         * canvas画直线，和原来的下划线的重合
         */

        paint?.let {
            canvas?.drawLine(0f,
                (this.height - underLineHeight).toFloat(),
                this.width.toFloat(),
                (this.height - underLineHeight).toFloat(),
                it)
        }
    }
}