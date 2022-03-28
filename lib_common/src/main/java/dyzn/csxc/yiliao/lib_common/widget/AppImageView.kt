package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.ResUtil.getColor
import kotlin.math.max
import kotlin.math.min

/**
 * @author YLC-D
 * 说明: 自定义带有点击滤镜的 ImageView
 * 通过剪裁整个canvas来实现圆角避免对bitmap操作，避免了大量图片显示时候的内存负担
 */
class AppImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val pressedColor: Int
    private val excludeDirection: Int

    //宽高比值 比值为1 表示 高度等于宽度 默认没有改变控件高度，设置后才改变
    private var aspectRatio: Float
    private val isCircle: Boolean

    /**
     * 圆角半径 如果 isCircle 是真 就是 圆形半径
     */
    private var filletRadius: Int

    /**
     * 圆角控件的边界路径
     */
    private var roundRectF: RectF? = null

    /**
     * 被排除不显示圆角的路径
     */
    private var excludeRectF: RectF? = null

    /**
     * 画笔
     */
    private var mBitmapPaint: Paint? = null

    /**
     * 矩阵
     */
    private var mMatrix: Matrix? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppImageView)
        pressedColor = typedArray.getColor(
            R.styleable.AppImageView_image_pressed_color,
            getColor(R.color.image_press_color,context)
        )
        excludeDirection = typedArray.getInt(R.styleable.AppImageView_exclude_direction, 0)
        aspectRatio = typedArray.getFloat(R.styleable.AppImageView_aspect_ratio, -1f)
        isCircle = typedArray.getBoolean(R.styleable.AppImageView_is_circle, false)
        //getDimensionPixelSize 获取到四舍五入的 int 值
        //getDimension 获取一个float 的实际值
        //getDimensionPixelOffset 直接取 getDimension 值的整数部分
        filletRadius = typedArray.getDimensionPixelSize(R.styleable.AppImageView_fillet_radius, 0)
        typedArray.recycle()
        init()
    }

    private fun init() {
        if (aspectRatio > 0 || isCircle || filletRadius > 0) {
            scaleType = ScaleType.CENTER_CROP
        }
        if (isCircle || filletRadius > 0) {
            //是圆形图片 或者 是圆角图片
            mBitmapPaint = Paint()
            mBitmapPaint!!.isAntiAlias = true
            mMatrix = Matrix()
            if (filletRadius > 0) roundRectF = RectF()
            if (excludeDirection > 0) excludeRectF = RectF()
        }
    }

    fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        init()
        invalidate()
    }

    fun setFilletRadius(filletRadius: Int) {
        this.filletRadius = filletRadius
        init()
        invalidate()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateFilter()
    }

    private fun updateFilter() {
        if (isPressed) {
            //设置滤镜
            setColorFilter(pressedColor, PorterDuff.Mode.DST_IN)
        } else {
            // 清除滤镜
            clearColorFilter()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //根据比例设置图片控件的高度
        if (aspectRatio > 0 || isCircle) {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val width = MeasureSpec.getSize(widthMeasureSpec)
            // 根据宽度值和 宽高比例值 确定高度
            val hMeasureSpec: Int = if (isCircle) {
                //如果是圆形宽高相同
                MeasureSpec.makeMeasureSpec(width, widthMode)
            } else {
                MeasureSpec.makeMeasureSpec((width / aspectRatio).toInt(), widthMode)
            }
            super.onMeasure(widthMeasureSpec, hMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        if (isCircle) {
            filletRadius = min(width, height) / 2
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 圆角图片的范围
        if (roundRectF != null) {
            roundRectF!![0f, 0f, w.toFloat()] = h.toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        if (!isCircle && filletRadius == 0) {
            super.onDraw(canvas)
            return
        }
        setUpShader(drawable)
        if (isCircle) {
            //是圆形图片
            canvas.drawCircle(
                filletRadius.toFloat(),
                filletRadius.toFloat(),
                filletRadius.toFloat(),
                mBitmapPaint!!
            )
        } else if (filletRadius > 0) {
            //圆角图片
            //先绘制四个圆角
            canvas.drawRoundRect(
                roundRectF!!,
                filletRadius.toFloat(),
                filletRadius.toFloat(),
                mBitmapPaint!!
            )
            //填充被排除的圆角
            if (excludeDirection > 0 && excludeRectF != null) {
                sureDirectionWillBeExclude(canvas)
            }
        }
    }

    /**
     * 确定有哪些角要被排除
     *
     * @param canvas 画布
     * LEFT_TOP = 0x0001;
     * RIGHT_TOP = 0x0010;
     * LEFT_BOTTOM = 0x0100;
     * RIGHT_BOTTOM = 0x1000;
     */
    private fun sureDirectionWillBeExclude(canvas: Canvas) {
        val direction = numToHex16(excludeDirection)
        val array = direction.toCharArray()
        val rightCount = 4
        if (rightCount == array.size) {
            val rb = array[0].toString().toInt() == 1
            val lb = array[1].toString().toInt() == 1
            val rt = array[2].toString().toInt() == 1
            val lt = array[3].toString().toInt() == 1
            excludeSomeDirection(rb, lb, rt, lt, canvas)
        }
    }

    /**
     * int 转成 4位的16进制数据
     *
     * @param b int 值
     * @return 16进制的字符串
     */
    private fun numToHex16(b: Int): String {
        //将 1 转成字符 "0001"
        //将 16 转成字符 "0010"
        return String.format("%04x", b)
    }

    /**
     * 排除某个角不绘制
     *
     * @param leftTop     是否有左上
     * @param leftBottom  是否有左下
     * @param rightTop    是否有右上
     * @param rightBottom 是否有右下
     * @param canvas      画布
     */
    private fun excludeSomeDirection(
        rightBottom: Boolean,
        leftBottom: Boolean,
        rightTop: Boolean,
        leftTop: Boolean,
        canvas: Canvas
    ) {
        //左上角不为圆角
        if (leftTop) {
            excludeRectF!![0f, 0f, filletRadius.toFloat()] = filletRadius.toFloat()
            canvas.drawRect(excludeRectF!!, mBitmapPaint!!)
        }
        if (leftBottom) {
            excludeRectF!![0f, height - filletRadius.toFloat(), filletRadius.toFloat()] =
                height.toFloat()
            canvas.drawRect(excludeRectF!!, mBitmapPaint!!)
        }
        if (rightTop) {
            excludeRectF!![width - filletRadius.toFloat(), 0f, width.toFloat()] =
                filletRadius.toFloat()
            canvas.drawRect(excludeRectF!!, mBitmapPaint!!)
        }
        if (rightBottom) {
            excludeRectF!![width - filletRadius.toFloat(), height - filletRadius.toFloat(), width.toFloat()] =
                height.toFloat()
            canvas.drawRect(excludeRectF!!, mBitmapPaint!!)
        }
    }

    /**
     * 初始化BitmapShader
     */
    private fun setUpShader(drawable: Drawable) {
        val bmp = drawableToBitmap(drawable)
        val mBitmapShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        //缩放宽高
        val scaleWidth: Float
        val scaleHeight: Float
        val widthRatio = width * 1.0f / bmp.width
        val heightRatio = height * 1.0f / bmp.height
        if (isCircle) {
            // 拿到bitmap宽或高的小值
            val bSize = max(bmp.width, bmp.height)
            scaleWidth = width * 1.0f / bSize
            scaleHeight = height * 1.0f / bSize
        } else if (filletRadius > 0) {
            //图片宽度大于控件宽度，或者图片高度大于图片高度
            if (bmp.width != width || bmp.height != height) {
                scaleWidth = max(widthRatio, heightRatio)
                scaleHeight = scaleWidth
            } else {
                scaleWidth = 1.0f
                scaleHeight = 1.0f
            }
        } else {
            scaleWidth = 1.0f
            scaleHeight = 1.0f
        }
        mMatrix!!.setScale(scaleWidth, scaleHeight)
        mMatrix!!.postTranslate(-(bmp.width * scaleWidth - width) / 2, 0f)
        //默认是centerCrop
        mMatrix!!.postTranslate(0f, -(bmp.height * scaleHeight - height) / 2)
        mBitmapShader.setLocalMatrix(mMatrix)
        mBitmapPaint!!.shader = mBitmapShader
    }

    /**
     * drawable转bitmap
     *
     * @param drawable 图片资源
     * @return Bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

}