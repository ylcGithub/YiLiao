package dyzn.csxc.yiliao.lib_common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import dyzn.csxc.yiliao.lib_common.R

/**
 *@author YLC-D
 *说明:
 */
class WordWrapView @JvmOverloads constructor(context: Context,attrs:AttributeSet?=null,defStyle:Int = 0):ViewGroup(context,attrs,defStyle) {

    /**
     * 子view水平方向padding
     */
    private var paddingHor = 0

    /**
     * 子view垂直方向padding
     */
    private var paddingVertical = 0

    /**
     * 子view之间的水平间距
     */
    private var marginHor = 0

    /**
     * 行间距
     */
    private var marginVertical = 0

    /**
     * 最多字个数
     */
    private var num = 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WordWrapView)
        paddingHor = ta.getDimension(R.styleable.WordWrapView_wrapPaddingHor, 0f).toInt()
        paddingVertical = ta.getDimension(R.styleable.WordWrapView_wrapPaddingVertical, 0f).toInt()
        marginHor = ta.getDimension(R.styleable.WordWrapView_wrapMarginHor, 0f).toInt()
        marginVertical = ta.getDimension(R.styleable.WordWrapView_wrapMarginVertical, 0f).toInt()
        ta.recycle()
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        //实际宽度
        val actualWidth = r - l
        var x = 0
        var y: Int
        var rows = 1
        //判断累积高度
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            var width = view.measuredWidth
            val height = view.measuredHeight
            x += width + marginHor
            if (x > actualWidth - marginHor) {
                if (i != 0) {
                    x = width + marginHor
                    rows++
                }
            }
            //当一个子view长度超出父view长度时
            if (x > actualWidth - marginHor) {
                //判断单个高度
                if (view is TextView) {
                    if (num == 0) {
                        val wordNum = view.text.toString().length
                        num =
                            wordNum * (actualWidth - 2 * marginHor - 2 * paddingHor) / (width - 2 * paddingHor) - 1
                    }
                    var text = view.text.toString()
                    text = text.substring(0, num) + "..."
                    view.text = text
                }
                x = actualWidth - marginHor
                width = actualWidth - 2 * marginHor
            }
            y = rows * (height + marginVertical)
            view.layout(x - width, y - height, x, y)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //横坐标
        var x = 0
        //纵坐标
        var y = 0
        //总行数
        var rows = 1
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        //实际宽度
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.setPadding(paddingHor, paddingVertical, paddingHor, paddingVertical)
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            val width = child.measuredWidth
            val height = child.measuredHeight
            x += width + marginHor
            //换行
            if (x > specWidth - marginHor) {
                if (i != 0) {
                    x = width + marginHor
                    rows++
                }
            }
            y = rows * (height + marginVertical)
        }
        setMeasuredDimension(specWidth, y + marginVertical)
    }

}