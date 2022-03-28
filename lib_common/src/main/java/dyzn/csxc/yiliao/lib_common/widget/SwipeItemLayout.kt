package dyzn.csxc.yiliao.lib_common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @author YLC-D
 * 说明：
 * 侧滑删除的布局文件
 */
class SwipeItemLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {
    enum class Mode {
        /**
         * 重置
         */
        RESET, DRAG, FLING, TAP
    }

    private var mTouchMode: Mode
    private var mMainView: ViewGroup? = null
    private var mSideView: ViewGroup? = null
    private val mScrollRunnable: ScrollRunnable
    private var mScrollOffset: Int
    private var mMaxScrollOffset = 0
    private var mInLayout = false
    val isOpen: Boolean
        get() = mScrollOffset != 0
    var touchMode: Mode
        get() = mTouchMode
        set(mode) {
            when (mTouchMode) {
                Mode.FLING -> mScrollRunnable.abort()
                Mode.RESET -> {
                }
                else -> {
                }
            }
            mTouchMode = mode
        }

    private fun open() {
        if (mScrollOffset != -mMaxScrollOffset) {
            //正在open，不需要处理
            if (mTouchMode == Mode.FLING && mScrollRunnable.isScrollToLeft) {
                return
            }

            //当前正在向右滑，abort
            if (mTouchMode == Mode.FLING) {
                mScrollRunnable.abort()
            }
            mScrollRunnable.startScroll(mScrollOffset, -mMaxScrollOffset)
        }
    }

    fun close() {
        if (mScrollOffset != 0) {
            //正在close，不需要处理
            if (mTouchMode == Mode.FLING && !mScrollRunnable.isScrollToLeft) {
                return
            }

            //当前正向左滑，abort
            if (mTouchMode == Mode.FLING) {
                mScrollRunnable.abort()
            }
            mScrollRunnable.startScroll(mScrollOffset, 0)
        }
    }

    fun fling(xVel: Int) {
        mScrollRunnable.startFling(mScrollOffset, xVel)
    }

    fun revise() {
        if (mScrollOffset < -mMaxScrollOffset / 2) {
            open()
        } else {
            close()
        }
    }

    fun trackMotionScroll(xDelta: Int): Boolean {
        if (xDelta == 0) {
            return false
        }
        var over = false
        var newLeft = mScrollOffset + xDelta
        val allRight = xDelta > 0 && newLeft > 0
        if (allRight || xDelta < 0 && newLeft < -mMaxScrollOffset) {
            over = true
            newLeft = newLeft.coerceAtMost(0)
            newLeft = newLeft.coerceAtLeast(-mMaxScrollOffset)
        }
        offsetChildrenLeftAndRight(newLeft - mScrollOffset)
        mScrollOffset = newLeft
        return over
    }

    private fun ensureChildren(): Boolean {
        val childCount = childCount
        if (childCount != 2) {
            return false
        }
        var childView: View? = getChildAt(0) as? ViewGroup ?: return false
        mMainView = childView as ViewGroup
        childView = getChildAt(1)
        if (childView !is ViewGroup) {
            return false
        }
        mSideView = childView
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!ensureChildren()) {
            throw RuntimeException("SwipeItemLayout的子视图不符合规定")
        }
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            /*
                一般确实就是match_parent，但是，抛异常是否太粗暴，此处直接调用super方法，当然没任何意义
                throw new IllegalArgumentException("SwipeItemLayout must be measured with MeasureSpec.EXACTLY.");
             */
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        setMeasuredDimension(widthSize, heightSize)
        var childWidthSpec: Int
        var childHeightSpec: Int
        var lp: MarginLayoutParams
        val childWidth = widthSize - paddingLeft - paddingRight
        val childHeight = heightSize - paddingTop - paddingBottom

        //main layout占据真个layout frame
        lp = mMainView!!.layoutParams as MarginLayoutParams
        childWidthSpec = MeasureSpec.makeMeasureSpec(
            childWidth - lp.leftMargin - lp.rightMargin,
            MeasureSpec.EXACTLY
        )
        childHeightSpec = MeasureSpec.makeMeasureSpec(
            childHeight - lp.topMargin - lp.bottomMargin,
            MeasureSpec.EXACTLY
        )
        mMainView!!.measure(childWidthSpec, childHeightSpec)

        //side layout大小为自身实际大小
        lp = mSideView!!.layoutParams as MarginLayoutParams
        childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        childHeightSpec = MeasureSpec.makeMeasureSpec(
            childHeight - lp.topMargin - lp.bottomMargin,
            MeasureSpec.EXACTLY
        )
        mSideView!!.measure(childWidthSpec, childHeightSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!ensureChildren()) {
            throw RuntimeException("SwipeItemLayout的子视图不符合规定")
        }
        mInLayout = true
        val pl = paddingLeft
        val pt = paddingTop
        val pr = paddingRight
        val pb = paddingBottom
        val mainLp = mMainView!!.layoutParams as MarginLayoutParams
        val sideParams = mSideView!!.layoutParams as MarginLayoutParams
        var childLeft = pl + mainLp.leftMargin
        var childTop = pt + mainLp.topMargin
        var childRight = width - (pr + mainLp.rightMargin)
        var childBottom = height - (mainLp.bottomMargin + pb)
        mMainView!!.layout(childLeft, childTop, childRight, childBottom)
        childLeft = childRight + sideParams.leftMargin
        childTop = pt + sideParams.topMargin
        childRight =
            childLeft + sideParams.leftMargin + sideParams.rightMargin + mSideView!!.measuredWidth
        childBottom = height - (sideParams.bottomMargin + pb)
        mSideView!!.layout(childLeft, childTop, childRight, childBottom)
        mMaxScrollOffset = mSideView!!.width + sideParams.leftMargin + sideParams.rightMargin
        mScrollOffset = if (mScrollOffset < -mMaxScrollOffset / 2) -mMaxScrollOffset else 0
        offsetChildrenLeftAndRight(mScrollOffset)
        mInLayout = false
    }

    private fun offsetChildrenLeftAndRight(delta: Int) {
        ViewCompat.offsetLeftAndRight(mMainView!!, delta)
        ViewCompat.offsetLeftAndRight(mSideView!!, delta)
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return p as? MarginLayoutParams ?: MarginLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams && super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScrollOffset = 0
        removeCallbacks(mScrollRunnable)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mScrollOffset != 0) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mTouchMode == Mode.TAP && mScrollOffset != 0) {
                    return true
                }
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mScrollOffset != 0) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mTouchMode == Mode.TAP && mScrollOffset != 0) {
                    close()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
            }
        }
        return false
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (getVisibility() != VISIBLE) {
            mScrollOffset = 0
            invalidate()
        }
    }

    internal inner class ScrollRunnable(context: Context?) : Runnable {
        private val mScroller: Scroller = Scroller(context, INTERPOLATOR)
        private var mAbort: Boolean
        private val mMinVelocity: Int

        //是否正在滑动需要另外判断
        var isScrollToLeft: Boolean
            private set

        fun startScroll(xStart: Int, xEnd: Int) {
            if (xStart != xEnd) {
                touchMode = Mode.FLING
                mAbort = false
                isScrollToLeft = xEnd < xStart
                mScroller.startScroll(xStart, 0, xEnd - xStart, 0, 400)
                ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
            }
        }

        fun startFling(xStart: Int, xVel: Int) {
            if (xVel > mMinVelocity && xStart != 0) {
                startScroll(xStart, 0)
                return
            }
            if (xVel < -mMinVelocity && xStart != -mMaxScrollOffset) {
                startScroll(xStart, -mMaxScrollOffset)
                return
            }
            startScroll(xStart, if (xStart > -mMaxScrollOffset / 2) 0 else -mMaxScrollOffset)
        }

        fun abort() {
            if (!mAbort) {
                mAbort = true
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                    removeCallbacks(this)
                }
            }
        }

        override fun run() {
            if (!mAbort) {
                val more = mScroller.computeScrollOffset()
                val curX = mScroller.currX
                val atEdge = trackMotionScroll(curX - mScrollOffset)
                if (more && !atEdge) {
                    ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
                    return
                }
                if (atEdge) {
                    removeCallbacks(this)
                    if (!mScroller.isFinished) {
                        mScroller.abortAnimation()
                    }
                    touchMode = Mode.RESET
                }
                if (!more) {
                    touchMode = Mode.RESET
                    //绝对不会出现这种意外的！！！可以注释掉
                    if (mScrollOffset != 0) {
                        mScrollOffset = if (abs(mScrollOffset) > mMaxScrollOffset / 2) {
                            -mMaxScrollOffset
                        } else {
                            0
                        }
                        ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
                    }
                }
            }
        }

        init {
            mAbort = false
            isScrollToLeft = false
            val configuration = ViewConfiguration.get(context)
            mMinVelocity = configuration.scaledMinimumFlingVelocity
        }
    }

    class OnSwipeItemTouchListener(context: Context?) : RecyclerView.OnItemTouchListener {
        private var mCaptureItem: SwipeItemLayout? = null
        private var mLastMotionX = 0f
        private var mLastMotionY = 0f
        private var mVelocityTracker: VelocityTracker? = null
        private var mActivePointerId: Int
        private val mTouchSlop: Int
        private val mMaximumVelocity: Int
        private var mDealByParent: Boolean
        override fun onInterceptTouchEvent(rv: RecyclerView, ev: MotionEvent): Boolean {
            var intercept = false
            val action = ev.actionMasked
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(ev)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mActivePointerId = ev.getPointerId(0)
                    val x = ev.x
                    val y = ev.y
                    mLastMotionX = x
                    mLastMotionY = y
                    var pointOther = false
                    var pointItem: SwipeItemLayout? = null
                    //首先知道ev针对的是哪个item
                    val pointView = findTopChildUnder(rv, x.toInt(), y.toInt())
                    if (pointView !is SwipeItemLayout) {
                        //可能是head view或bottom view
                        pointOther = true
                    } else {
                        pointItem = pointView
                    }

                    //此时的pointOther=true，意味着点击的view为空或者点击的不是item
                    //还没有把点击的是item但是不是capture item给过滤出来
                    val isRight = mCaptureItem == null || mCaptureItem !== pointItem
                    if (!pointOther && isRight) {
                        pointOther = true
                    }

                    //点击的是capture item
                    if (!pointOther) {
                        val touchMode = mCaptureItem!!.touchMode

                        //如果它在fling，就转为drag
                        //需要拦截，并且requestDisallowInterceptTouchEvent
                        var disallowIntercept = false
                        if (touchMode == Mode.FLING) {
                            mCaptureItem!!.touchMode = Mode.DRAG
                            disallowIntercept = true
                            intercept = true
                        } else { //如果是expand的，就不允许parent拦截
                            mCaptureItem!!.touchMode = Mode.TAP
                            if (mCaptureItem!!.isOpen) {
                                disallowIntercept = true
                            }
                        }
                        if (disallowIntercept) {
                            val parent = rv.parent
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    } else { //capture item为null或者与point item不一样
                        //直接将其close掉
                        if (mCaptureItem != null && mCaptureItem!!.isOpen) {
                            mCaptureItem!!.close()
                            intercept = true
                        }
                        if (pointItem != null) {
                            mCaptureItem = pointItem
                            mCaptureItem!!.touchMode = Mode.TAP
                        }
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val actionIndex = ev.actionIndex
                    mActivePointerId = ev.getPointerId(actionIndex)
                    mLastMotionX = ev.getX(actionIndex)
                    mLastMotionY = ev.getY(actionIndex)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val actionIndex = ev.actionIndex
                    val pointerId = ev.getPointerId(actionIndex)
                    if (pointerId == mActivePointerId) {
                        val newIndex = if (actionIndex == 0) 1 else 0
                        mActivePointerId = ev.getPointerId(newIndex)
                        mLastMotionX = ev.getX(newIndex)
                        mLastMotionY = ev.getY(newIndex)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (activePointerIndex == -1) return false

                    //在down时，就被认定为parent的drag，所以，直接交给parent处理即可
                    if (mDealByParent) {
                        if (mCaptureItem != null && mCaptureItem!!.isOpen) {
                            mCaptureItem!!.close()
                        }
                        return false
                    }
                    val x = (ev.getX(activePointerIndex) + .5f).toInt()
                    val y = (ev.getY(activePointerIndex).toInt() + .5f).toInt()
                    var xDelta = (x - mLastMotionX).toInt()
                    val yDelta = (y - mLastMotionY).toInt()
                    val xDiff = abs(xDelta)
                    val yDiff = abs(yDelta)
                    if (mCaptureItem != null && !mDealByParent) {
                        var touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.TAP) {
                            //如果capture item是open的，下拉有两种处理方式：
                            //  1、下拉后，直接close item
                            //  2、只要是open的，就拦截所有它的消息，这样如果点击open的，就只能滑动该capture item
                            //网易邮箱，在open的情况下，下拉直接close
                            //QQ，在open的情况下，下拉也是close。但是，做的不够好，没有达到该效果。
                            if (xDiff > mTouchSlop && xDiff > yDiff) {
                                mCaptureItem!!.touchMode = Mode.DRAG
                                val parent = rv.parent
                                parent.requestDisallowInterceptTouchEvent(true)
                                xDelta =
                                    if (xDelta > 0) xDelta - mTouchSlop else xDelta + mTouchSlop
                            } else if (yDiff > mTouchSlop) {
                                //表明不是水平滑动，即不判定为SwipeItemLayout的滑动
                                //但是，可能是下拉刷新SwipeRefreshLayout或者RecyclerView的滑动
                                //一般的下拉判定，都是yDiff>mTouchSlop，所以，此处这么写不会出问题
                                //这里这么做以后，如果判定为下拉，就直接close
                                mDealByParent = true
                                mCaptureItem!!.close()
                            }
                        }
                        touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            intercept = true
                            mLastMotionX = x.toFloat()
                            mLastMotionY = y.toFloat()

                            //对capture item进行拖拽
                            mCaptureItem!!.trackMotionScroll(xDelta)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mCaptureItem != null) {
                        val touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            val velocityTracker = mVelocityTracker
                            velocityTracker!!.computeCurrentVelocity(
                                1000,
                                mMaximumVelocity.toFloat()
                            )
                            val xVel = velocityTracker.getXVelocity(mActivePointerId).toInt()
                            mCaptureItem!!.fling(xVel)
                            intercept = true
                        }
                    }
                    cancel()
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mCaptureItem != null) {
                        mCaptureItem!!.revise()
                    }
                    cancel()
                }
                else -> {
                }
            }
            return intercept
        }

        override fun onTouchEvent(recyclerView: RecyclerView, ev: MotionEvent) {
            val action = ev.actionMasked
            val actionIndex = ev.actionIndex
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(ev)
            when (action) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    mActivePointerId = ev.getPointerId(actionIndex)
                    mLastMotionX = ev.getX(actionIndex)
                    mLastMotionY = ev.getY(actionIndex)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerId = ev.getPointerId(actionIndex)
                    if (pointerId == mActivePointerId) {
                        val newIndex = if (actionIndex == 0) 1 else 0
                        mActivePointerId = ev.getPointerId(newIndex)
                        mLastMotionX = ev.getX(newIndex)
                        mLastMotionY = ev.getY(newIndex)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (activePointerIndex != -1) {
                        val x = ev.getX(activePointerIndex)
                        val y = ev.getY(activePointerIndex)
                        val xDelta = (x - mLastMotionX).toInt()
                        if (mCaptureItem != null && mCaptureItem!!.touchMode == Mode.DRAG) {
                            mLastMotionX = x
                            mLastMotionY = y

                            //对capture item进行拖拽
                            mCaptureItem!!.trackMotionScroll(xDelta)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mCaptureItem != null) {
                        val touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            val velocityTracker = mVelocityTracker
                            velocityTracker!!.computeCurrentVelocity(
                                1000,
                                mMaximumVelocity.toFloat()
                            )
                            val xVel = velocityTracker.getXVelocity(mActivePointerId).toInt()
                            mCaptureItem!!.fling(xVel)
                        }
                    }
                    cancel()
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mCaptureItem != null) {
                        mCaptureItem!!.revise()
                    }
                    cancel()
                }
                else -> {
                }
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        fun cancel() {
            mDealByParent = false
            mActivePointerId = -1
            if (mVelocityTracker != null) {
                mVelocityTracker!!.recycle()
                mVelocityTracker = null
            }
        }

        init {
            val configuration = ViewConfiguration.get(context)
            mTouchSlop = configuration.scaledTouchSlop
            mMaximumVelocity = configuration.scaledMaximumFlingVelocity
            mActivePointerId = -1
            mDealByParent = false
        }
    }

    companion object {
        private val INTERPOLATOR = Interpolator { t: Float ->
            val i = t - 1.0f
            i * i * i * i * i + 1.0f
        }

        fun findTopChildUnder(parent: ViewGroup, x: Int, y: Int): View? {
            val childCount = parent.childCount
            for (i in childCount - 1 downTo 0) {
                val child = parent.getChildAt(i)
                if (x >= child.left && x < child.right && y >= child.top && y < child.bottom) {
                    return child
                }
            }
            return null
        }

        fun closeAllItems(recyclerView: RecyclerView) {
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                if (child is SwipeItemLayout) {
                    if (child.isOpen) {
                        child.close()
                    }
                }
            }
        }
    }

    init {
        mTouchMode = Mode.RESET
        mScrollOffset = 0
        mScrollRunnable = ScrollRunnable(context)
    }
}