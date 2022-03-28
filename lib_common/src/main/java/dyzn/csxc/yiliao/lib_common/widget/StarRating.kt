package dyzn.csxc.yiliao.lib_common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dyzn.csxc.yiliao.lib_common.R
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 *@author YLC-D
 *说明: 自定义的星级平分控件
 */
class StarRating @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    /**
     * 星星间距
     */
    private var starDistance = 0

    /**
     * 星星个数
     */
    private var starCount = 5

    /**
     * 星星高度大小，星星一般正方形，宽度等于高度
     */
    private var starSize = 0

    /**
     * 显示的评分星星个数
     */
    private var starMark = 0.0f

    /**
     * 亮星星
     */
    private var starFillBitmap: Bitmap? = null

    /**
     * 暗星星
     */
    private var starEmptyDrawable: Drawable? = null

    /**
     * 监听星星变化接口
     */
    private var onStarChangeListener: OnStarChangeListener? = null

    /**
     * 绘制星星画笔
     */
    private var paint: Paint? = null

    /**
     * 是否是整数评分
     */
    private var integerMark = false

    /**
     * 是否是分数显示器 是的话就不能点击
     */
    private var isIndicator = true

    init {
        isClickable = true
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StarRating)
        starDistance = mTypedArray.getDimension(R.styleable.StarRating_starDistance, 0f).toInt()
        starSize = mTypedArray.getDimension(R.styleable.StarRating_starSize, 20f).toInt()
        starCount = mTypedArray.getInteger(R.styleable.StarRating_starCount, 5)
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.StarRating_starEmpty)
        starFillBitmap = drawableToBitmap(mTypedArray.getDrawable(R.styleable.StarRating_starFill))
        isIndicator = mTypedArray.getBoolean(R.styleable.StarRating_isIndicator, true)
        mTypedArray.recycle()
        paint = Paint()
        paint?.isAntiAlias = true
        if (starFillBitmap != null) {
            paint?.shader = BitmapShader(
                starFillBitmap!!,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )
        }
    }

    /**
     * 设置是否需要整数评分
     *
     * @param integerMark 星星是否为整数还是小数
     */
    fun setIntegerMark(integerMark: Boolean) {
        this.integerMark = integerMark
    }

    /**
     * 设置显示的星星的分数
     *
     * @param mark 分数
     */
    fun setStarMark(mark: Float) {
        starMark = if (integerMark) {
            ceil(mark.toDouble()).toFloat()
        } else {
            (mark * 10).roundToInt() * 1.0f / 10
        }
        onStarChangeListener?.onStarChange(starMark)
        invalidate()
    }
    /**
     * 定义星星点击的监听接口 点击星星点亮或者空心
     */
    interface OnStarChangeListener {
        /**
         * 星星个数改变回调
         *
         * @param mark 当前显示的星星个数
         */
        fun onStarChange(mark: Float)
    }

    /**
     * 设置监听
     *
     * @param onStarChangeListener 点击星星图标后触发
     */
    fun setOnStarChangeListener(onStarChangeListener: OnStarChangeListener?) {
        this.onStarChangeListener = onStarChangeListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(starSize * starCount + starDistance * (starCount - 1), starSize)
    }

    /**
     * 绘制星星
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (starFillBitmap == null || starEmptyDrawable == null) {
            return
        }
        for (i in 0 until starCount) {
            starEmptyDrawable!!.setBounds(
                (starDistance + starSize) * i,
                0,
                (starDistance + starSize) * i + starSize,
                starSize
            )
            starEmptyDrawable!!.draw(canvas)
        }
        if (starMark > 1) {
            canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint!!)
            if (starMark - starMark.toInt() == 0f) {
                var i = 1
                while (i < starMark) {
                    canvas.translate((starDistance + starSize).toFloat(), 0f)
                    canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint!!)
                    i++
                }
            } else {
                var i = 1
                while (i < starMark - 1) {
                    canvas.translate((starDistance + starSize).toFloat(), 0f)
                    canvas.drawRect(0f, 0f, starSize.toFloat(), starSize.toFloat(), paint!!)
                    i++
                }
                canvas.translate((starDistance + starSize).toFloat(), 0f)
                canvas.drawRect(
                    0f,
                    0f,
                    starSize * (((starMark - starMark) * 10).roundToInt() * 1.0f / 10),
                    starSize.toFloat(),
                    paint!!
                )
            }
        } else {
            canvas.drawRect(0f, 0f, starSize * starMark, starSize.toFloat(), paint!!)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isIndicator) {
            return true
        }
        var x = event.x.toInt()
        if (x < 0) {
            x = 0
        }
        if (x > measuredWidth) {
            x = measuredWidth
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> setStarMark(x * 1.0f / (measuredWidth * 1.0f / starCount))
            MotionEvent.ACTION_UP -> {
            }
            else -> {
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }

    /**
     * drawable转bitmap
     *
     * @param drawable 星星的资源文件
     * @return bitmap
     */
    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, starSize, starSize)
        drawable.draw(canvas)
        return bitmap
    }
}