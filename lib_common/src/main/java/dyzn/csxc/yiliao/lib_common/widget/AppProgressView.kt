package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import kotlin.math.min

/**
 *@author YLC-D
 *说明: 进度控件
 */
class AppProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val normalColor: Int
    private val coverColor: Int
    private val mLinePaint: Paint
    private val mTextPaint: Paint
    private val ring: RectF
    private var hintText: String?
    private var schedule: Int = 0
    private val ringWidth: Float
    private val textColor: Int
    private val textSize: Float
    private val scheduleTextColor: Int
    private val scheduleTextSize: Float

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
        ringWidth = typedArray.getDimension(R.styleable.ProgressView_ring_width, 0f)
        normalColor = typedArray.getColor(
            R.styleable.ProgressView_normal_color,
            ResUtil.getColor(R.color.color_bbb,context)
        )
        coverColor = typedArray.getColor(
            R.styleable.ProgressView_cover_color,
            ResUtil.getColor(R.color.color_333,context)
        )
        textColor = typedArray.getColor(
            R.styleable.ProgressView_hint_text_color,
            ResUtil.getColor( R.color.color_333,context)
        )
        textSize = typedArray.getDimension(
            R.styleable.ProgressView_hint_text_size,
            ResUtil.getDimen(R.dimen.font_size_16,context)
        )

        scheduleTextColor = typedArray.getColor(
            R.styleable.ProgressView_schedule_text_color,
            ResUtil.getColor(R.color.color_999,context)
        )
        scheduleTextSize = typedArray.getDimension(
            R.styleable.ProgressView_schedule_text_size,
            ResUtil.getDimen( R.dimen.font_size_16,context)
        )
        hintText = typedArray.getString(R.styleable.ProgressView_hint_text)
        typedArray.recycle()
        mLinePaint = Paint()
        mTextPaint = Paint()
        mLinePaint.isDither = true
        mLinePaint.isAntiAlias = true
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = ringWidth

        mTextPaint.isDither = true
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
        ring = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minSide = min(measuredWidth, measuredHeight)
        ring.left = ringWidth / 2
        ring.right = minSide - ringWidth / 2
        ring.top = ringWidth / 2
        ring.bottom = minSide - ringWidth / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制默认的底层色
        mLinePaint.color = normalColor
        canvas?.drawArc(ring, 0f, 360f, false, mLinePaint)
        //绘制进度
        mLinePaint.color = coverColor
        canvas?.drawArc(ring, 0f, schedule * 3.6f, false, mLinePaint)
        //绘制进度值
        drawSchedule(canvas, "$schedule%")
        hintText?.let { drawText(canvas, hintText!!) }
    }

    private fun drawSchedule(canvas: Canvas?, text: String) {
        val textWidth = mTextPaint.measureText(text)

        val textX = (ring.left+ring.right) / 2 - textWidth / 2
        val fontMetrics: Paint.FontMetrics = mTextPaint.fontMetrics
        val dy: Float = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
        val textY = (ring.top+ring.bottom) / 2 + dy
        mTextPaint.color = scheduleTextColor
        mTextPaint.textSize = scheduleTextSize
        canvas?.drawText(text, textX, textY, mTextPaint)
    }

    private fun drawText(canvas: Canvas?, text: String) {
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize
        val textWidth = mTextPaint.measureText(text)
        val textX = width / 2 - textWidth / 2
        val fontMetrics: Paint.FontMetrics = mTextPaint.fontMetrics
        val dy: Float = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
        val textY = height - dy
        canvas?.drawText(text, textX, textY, mTextPaint)
    }

    fun setSchedule(schedule: Int) {
        this.schedule = schedule
        invalidate()
    }

    fun setShowText(hintText: String?) {
        this.hintText = hintText
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        LogUtil.log("progress:${System.currentTimeMillis()}")
    }
}