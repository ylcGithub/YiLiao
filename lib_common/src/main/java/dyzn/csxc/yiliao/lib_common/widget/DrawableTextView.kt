package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import dyzn.csxc.yiliao.lib_common.R

/**
 * @author YLC-D
 * 说明:可以设置drawable 宽高的textView
 * 1.PorterDuff.Mode.CLEAR 所绘制不会提交到画布上。
 *
 *
 * 2.PorterDuff.Mode.SRC 显示上层绘制图片
 *
 *
 * 3.PorterDuff.Mode.DST 显示下层绘制图片
 *
 *
 * 4.PorterDuff.Mode.SRC_OVER 正常绘制显示，上下层绘制叠盖。
 *
 *
 * 5.PorterDuff.Mode.DST_OVER 上下层都显示。下层居上显示。
 *
 *
 * 6.PorterDuff.Mode.SRC_IN 取两层绘制交集。显示上层。
 *
 *
 * 7.PorterDuff.Mode.DST_IN 取两层绘制交集。显示下层。
 *
 *
 * 8.PorterDuff.Mode.SRC_OUT 取上层绘制非交集部分。
 *
 *
 * 9.PorterDuff.Mode.DST_OUT 取下层绘制非交集部分。
 *
 *
 * 10.PorterDuff.Mode.SRC_ATOP 取下层非交集部分与上层交集部分
 *
 *
 * 11.PorterDuff.Mode.DST_ATOP 取上层非交集部分与下层交集部分
 *
 *
 * 12.PorterDuff.Mode.XOR 异或：去除两图层交集部分
 *
 *
 * 13.PorterDuff.Mode.DARKEN 取两图层全部区域，交集部分颜色加深
 *
 *
 * 14.PorterDuff.Mode.LIGHTEN 取两图层全部，点亮交集部分颜色
 *
 *
 * 15.PorterDuff.Mode.MULTIPLY 取两图层交集部分叠加后颜色
 *
 *
 * 16.PorterDuff.Mode.SCREEN  取两图层全部区域，交集部分变为透明色
 */
class DrawableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppTextView(context, attrs, defStyleAttr) {
    private var drawableWidth = 0
    private var drawableHeight = 0
    private var drawablePressFilter: PorterDuffColorFilter? = null
    private var haveDrawable = false
    private var drawableLeft: Drawable? = null
    private var drawableTop: Drawable? = null
    private var drawableRight: Drawable? = null
    private var drawableBottom: Drawable? = null
    private fun setDrawable() {
        drawableLeft?.setBounds(0, 0, drawableWidth, drawableHeight)
        drawableRight?.setBounds(0, 0, drawableWidth, drawableHeight)
        drawableTop?.setBounds(0, 0, drawableWidth, drawableHeight)
        drawableBottom?.setBounds(0, 0, drawableWidth, drawableHeight)
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom)
    }

    fun setLeftDrawable(drawable: Drawable?) {
        drawableLeft = drawable
        setDrawable()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (isPressed && haveDrawable && drawablePressFilter != null) {
            drawableFilter(drawablePressFilter)
        } else if (haveDrawable && drawablePressFilter != null) {
            drawableFilter(null)
        }
    }

    private fun drawableFilter(drawablePressFilter: PorterDuffColorFilter?) {
        val drawables = compoundDrawables
        for (d in drawables) {
            if (d == null) {
                continue
            }
            if (drawablePressFilter != null) {
                d.colorFilter = drawablePressFilter
            } else {
                d.clearColorFilter()
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView)
        val count = typedArray.indexCount
        for (i in 0 until count) {
            when (val attr = typedArray.getIndex(i)) {
                R.styleable.DrawableTextView_drawableRight -> {
                    drawableRight = typedArray.getDrawable(attr)
                    haveDrawable = true
                }
                R.styleable.DrawableTextView_drawableLeft -> {
                    drawableLeft = typedArray.getDrawable(attr)
                    haveDrawable = true
                }
                R.styleable.DrawableTextView_drawableTop -> {
                    drawableTop = typedArray.getDrawable(attr)
                    haveDrawable = true
                }
                R.styleable.DrawableTextView_drawableBottom -> {
                    drawableBottom = typedArray.getDrawable(attr)
                    haveDrawable = true
                }
                R.styleable.DrawableTextView_drawableWidth -> drawableWidth =
                    typedArray.getDimensionPixelSize(attr, 0)
                R.styleable.DrawableTextView_drawableHeight -> drawableHeight =
                    typedArray.getDimensionPixelSize(attr, 0)
                R.styleable.DrawableTextView_drawablePressedColor -> {
                    val drawablePressedColor = typedArray.getColor(attr, 0)
                    //DST_IN 显示
                    drawablePressFilter = PorterDuffColorFilter(drawablePressedColor, PorterDuff.Mode.DST_IN)
                }
            }
        }
        typedArray.recycle()
        setDrawable()
    }
}