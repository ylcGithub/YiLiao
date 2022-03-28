package dyzn.csxc.yiliao.lib_common.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import kotlin.math.log

/**
 *@author YLC-D
 *说明:
 */
class CustomItemDecoration(val type: Type, @ColorInt val color: Int = Color.TRANSPARENT) :
    RecyclerView.ItemDecoration() {
    /**
     * 画笔
     */
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    init {
        paint.color = color
    }

    /**
     * 分割线宽度
     */
    var space = 0

    private val offset: Int
        get() = space / 2

    /**
     * 最左边的线宽
     * 1.纵向的所有item
     * 2.横向的第一个item
     * 3.表格的每一行，第一个
     */
    var mostLeft: Int = 0

    /**
     * 最顶部的线宽
     * 1.纵向的第一个item
     * 2.横向的所有item
     * 3.表格的第一行 所有item
     */
    var mostTop: Int = 0

    /**
     * 最右边的线宽
     * 1.纵向的所有item
     * 2.横向的最后一个item
     * 3.表格的每一行 最后一个
     */
    var mostRight: Int = 0

    /**
     * 最底部的线宽
     * 1.纵向的最后一个item
     * 2.横向的所有item
     * 3.表格的最后一行，所有item
     */
    var mostBottom: Int = 0

    private val map: HashMap<Int, Rect> = HashMap(10)
    private var childCount: Int = 0

    private val leftRect: Rect = Rect()
    private val rightRect: Rect = Rect()
    private val topRect: Rect = Rect()
    private val bottomRect: Rect = Rect()

    enum class Type {
        HOR, VER, GRID
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (type == Type.GRID) {
            gridDecoration(outRect, view, parent)
        } else {
            linearDecoration(outRect, view, parent)
        }
        val childAdapterPosition = parent.getChildAdapterPosition(view)
        map[childAdapterPosition] = Rect(outRect)
    }

    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private var gridManager: GridLayoutManager? = null

    /**
     * 绘制表格布局的分割线
     */
    private fun gridDecoration(outRect: Rect, view: View, parent: RecyclerView) {
        if (adapter == null) {
            adapter = parent.adapter
        }
        if (gridManager == null) {
            gridManager = parent.layoutManager as GridLayoutManager
        }
        if (gridManager == null || adapter == null) return

        val pos = parent.getChildAdapterPosition(view)
        val spanCount = gridManager!!.spanCount
        val spanSize = gridManager!!.spanSizeLookup.getSpanSize(pos)
        val span = spanCount / spanSize//当前行有几个item
        val spanIndex = gridManager!!.spanSizeLookup.getSpanIndex(pos, spanCount)//当前的pos在当前行的位置下标
        childCount = adapter!!.itemCount//一个有几个item
        when {
            span >= childCount -> {//只有一行
                outRect.set(offset, mostTop, offset, mostBottom)
            }
            pos < span -> {//第一行
                outRect.set(offset, mostTop, offset, offset)
            }
            pos + span - spanIndex >= childCount -> {//最后一行
                outRect.set(offset, offset, offset, mostBottom)
            }
            else -> {//中间行
                outRect.set(offset, offset, offset, offset)
            }
        }

        if (spanIndex == 0) {//第一个
            outRect.set(mostLeft, outRect.top, outRect.right, outRect.bottom)
        }
        if (spanIndex == span - 1) {//当前行的最后一个
            outRect.set(outRect.left, outRect.top, mostRight, outRect.bottom)
        }
    }

    private var linearManager: LinearLayoutManager? = null

    /**
     * 绘制线性布局的分隔线
     */
    private fun linearDecoration(outRect: Rect, view: View, parent: RecyclerView) {
        if (adapter == null) {
            adapter = parent.adapter
        }
        if (linearManager == null) {
            linearManager = parent.layoutManager as LinearLayoutManager
        }
        if (adapter == null || linearManager == null) return
        val position = parent.getChildAdapterPosition(view);
        childCount = adapter!!.itemCount
        if (Type.HOR == type) {
            when (position) {
                0 -> {
                    outRect.set(mostLeft, mostTop, space, mostBottom)
                }
                childCount - 1 -> {
                    outRect.set(0, mostTop, mostRight, mostBottom)
                }
                else -> {
                    outRect.set(0, mostTop, space, mostBottom)
                }
            }
        } else if (Type.VER == type) {
            when (position) {
                0 -> {
                    outRect.set(mostLeft, mostTop, mostRight, space)
                }
                childCount - 1 -> {
                    outRect.set(mostLeft, 0, mostRight, mostBottom)
                }
                else -> {
                    outRect.set(mostLeft, 0, mostRight, space)
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        for (i in 0 until childCount) {
            val itemView = parent.getChildAt(i)
            val rect = map[i]
            if (itemView == null) return
            rect?.let {
                if (it.left > 0) {
                    leftRect.set(
                        itemView.left - it.left,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    drawLine(c, leftRect, paint)
                }
                if (it.top > 0) {
                    topRect.set(
                        itemView.left - it.left,
                        itemView.top - it.top,
                        itemView.right + it.right,
                        itemView.top
                    )
                    drawLine(c, topRect, paint)
                }
                if (it.right > 0) {
                    rightRect.set(
                        itemView.right,
                        itemView.top,
                        itemView.right + it.right,
                        itemView.bottom
                    )
                    drawLine(c, rightRect, paint)
                }
                if (it.bottom > 0) {
                    bottomRect.set(
                        itemView.left - it.left,
                        itemView.bottom,
                        itemView.right + it.right,
                        itemView.bottom + it.bottom
                    )
                    drawLine(c, bottomRect, paint)
                }
            }
        }
    }

    private fun drawLine(c: Canvas, rect: Rect, paint: Paint) {
        c.drawRect(rect, paint)
    }
}