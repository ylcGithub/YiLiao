package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import dyzn.csxc.yiliao.lib_common.util.ResUtil

/**
 *@author YLC-D
 *说明:
 */
class CountingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    private var btnSize: Int

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CountingView)
        btnSize = ta.getDimension(
            R.styleable.CountingView_countingBtnSize,
            ResUtil.getDimen(R.dimen.widget_size_30,context)
        ).toInt()
        val textSize = ta.getDimension(
            R.styleable.CountingView_countingTextSize,
            ResUtil.getDimen(R.dimen.font_size_16,context)
        )
        val textColor = ta.getColor(
            R.styleable.CountingView_countingTextColor,
            ResUtil.getColor(R.color.color_333,context)
        )
        val btnTextSize = ta.getDimension(R.styleable.CountingView_countingBtnTextSize, 16f)
        val btnTextColor = ta.getColor(
            R.styleable.CountingView_countingBtnTextColor,
            ResUtil.getColor(R.color.color_999,context)
        )
        val btnBoxColor = ta.getColor(
            R.styleable.CountingView_countingBtnBoxColor,
            ResUtil.getColor(R.color.color_999,context)
        )
        ta.recycle()

        val left = AppTextView(context)
        val right = AppTextView(context)
        val text = AppTextView(context)
        fun initText(textView: AppTextView) {
            textView.radius = ResUtil.getDimen(R.dimen.widget_size_10,context).toInt()
            textView.textSize = btnTextSize
            textView.setTextColor(btnTextColor)
            textView.strokeColor = btnBoxColor
            textView.strokeWidth = ResUtil.getDimen(R.dimen.widget_size_1,context).toInt()
            textView.gravity = Gravity.CENTER
        }
        text.setTextColor(textColor)
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        text.gravity = Gravity.CENTER
        text.text = "0"

        initText(left)
        left.text = "-"
        initText(right)
        right.text = "+"

        addView(left)
        addView(text)
        addView(right)
        left.width = btnSize
        left.height = btnSize
        right.width = btnSize
        right.height = btnSize
        text.height = btnSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0..2) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec,heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = getChildAt(0)
        val text = getChildAt(1)
        val right = getChildAt(2)

        left.layout(l, t, l + btnSize, t + btnSize)
        text.layout(l + btnSize, t, r - btnSize, t + btnSize)
        right.layout(r - btnSize, t, r, t + btnSize)

        LogUtil.log("左:$l,上:$t,右:$r,下:$b")
    }
}