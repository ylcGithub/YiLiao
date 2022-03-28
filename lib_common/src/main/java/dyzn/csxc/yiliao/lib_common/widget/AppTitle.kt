package dyzn.csxc.yiliao.lib_common.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.databinding.BaseAppTitleBinding
import dyzn.csxc.yiliao.lib_common.type.DefaultPageType
import dyzn.csxc.yiliao.lib_common.util.ResUtil
import com.jaeger.library.StatusBarUtil
import org.apache.commons.lang3.StringUtils

/**
 *@author YLC-D
 *说明: 自定义的APP标题栏，其中包含有 APP的缺省页的实现，
 * 就是一个标题栏和缺省页的集合实现控件
 */
class AppTitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RelativeLayout(
    context,
    attrs,
    defStyleAttr
) {

    private var titleBackgroundColor: Int
    private var binding: BaseAppTitleBinding = BaseAppTitleBinding.inflate(LayoutInflater.from(context),this,true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppTitle)
        val noTitle = typedArray.getBoolean(R.styleable.AppTitle_no_title, false)
        val backIcon = typedArray.getResourceId(
            R.styleable.AppTitle_back_icon,
            R.drawable.vector_drawable_nav_icon_back
        )
        val backIconColor = typedArray.getColor(R.styleable.AppTitle_back_icon_color,-1)
        val noBackIcon = typedArray.getBoolean(R.styleable.AppTitle_no_back_icon, false)
        val rightOneIcon = typedArray.getResourceId(R.styleable.AppTitle_right_one_icon, -1)
        val rightTwoIcon = typedArray.getResourceId(R.styleable.AppTitle_right_two_icon, -1)
        val title = typedArray.getString(R.styleable.AppTitle_title)
        val rightOneText = typedArray.getString(R.styleable.AppTitle_right_one_text)
        val rightTwoText = typedArray.getString(R.styleable.AppTitle_right_two_text)
        val titleColor = typedArray.getColor(
            R.styleable.AppTitle_title_color,
            ResUtil.getColor(R.color.color_333,context)
        )
        val titleSize: Float = typedArray.getDimension(
            R.styleable.AppTitle_title_size,
            ResUtil.getDimen(R.dimen.font_size_16,context)
        )
        titleBackgroundColor = typedArray.getColor(
            R.styleable.AppTitle_title_background,
            ResUtil.getColor( R.color.white,context)
        )
        val rightOneTextColor = typedArray.getColor(R.styleable.AppTitle_right_one_text_color, -1)
        val rightTwoTextColor = typedArray.getColor(R.styleable.AppTitle_right_two_text_color, -1)
        val rightOneTextPressedColor =
            typedArray.getColor(R.styleable.AppTitle_right_one_text_pressed_color, -1)
        val rightTwoTextPressedColor =
            typedArray.getColor(R.styleable.AppTitle_right_two_text_pressed_color, -1)

        typedArray.recycle()

        if (noTitle) {
            binding.tvTitle.visibility = GONE
            binding .ivBack.visibility = GONE
        } else {
            binding.tvTitle.text = title
            binding.tvTitle.setTextColor(titleColor)
            binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
            binding.rlTitle.setBackgroundColor(titleBackgroundColor)
            if (noBackIcon) {
                binding .ivBack.visibility = GONE
            } else {
                binding .ivBack.setImageResource(backIcon)
                if(backIconColor != -1){
                    binding.ivBack.setColorFilter(backIconColor)
                }
            }
            if (StringUtils.isNotEmpty(rightOneText)) {
                binding.tvRightOne.text = rightOneText
                binding.tvRightOne.visibility = VISIBLE
                setTextViewColor(rightOneTextColor,rightOneTextPressedColor,binding.tvRightOne)
            }
            if (StringUtils.isNotEmpty(rightTwoText)) {
                binding.tvRightTwo.text = rightTwoText
                binding.tvRightTwo.visibility = VISIBLE
                setTextViewColor(rightTwoTextColor,rightTwoTextPressedColor,binding.tvRightTwo)
            }
            if (rightOneIcon != -1) {
                binding.ivRightOne.setImageResource(rightOneIcon)
                binding.ivRightOne.visibility = VISIBLE
            }
            if (rightTwoIcon != -1) {
                binding.ivRightTwo.setImageResource(rightTwoIcon)
                binding.ivRightTwo.visibility = VISIBLE
            }
        }
    }

    private fun setTextViewColor(
        @ColorInt color: Int,
        @ColorInt pressedColor: Int,
        tv: AppTextView
    ) {
        if (color != -1) {
            tv.setTextColor(color)
        }
        if (pressedColor != -1) {
            tv.setPressedColor(pressedColor)
        }
    }

    fun setTitle(title: String?) {
        title?.let { binding.tvTitle.text = it }
    }

    fun showDefaultPage(type: DefaultPageType, action: (View) -> Unit) {
        val defaultPage = findViewById<LinearLayout>(R.id.default_page)
        when (type) {
            DefaultPageType.NETWORK_ERROR -> {
                defaultPage.visibility = VISIBLE
                val ivHint = defaultPage.findViewById<AppImageView>(R.id.default_page_hint_icon)
                val tvHint = defaultPage.findViewById<AppCompatTextView>(R.id.default_page_tv_hint)
                ivHint.setImageResource(R.drawable.vector_drawable_network_difference)
                tvHint.setText(R.string.network_fail_please_click)
                ivHint.setOnClickListener(action)
                tvHint.setOnClickListener(action)
            }

            DefaultPageType.PAGE_EMPTY -> {
                defaultPage.visibility = VISIBLE
                val ivHint = defaultPage.findViewById<AppImageView>(R.id.default_page_hint_icon)
                val tvHint = defaultPage.findViewById<AppCompatTextView>(R.id.default_page_tv_hint)
                ivHint.setImageResource(R.drawable.vector_drawable_page_empty)
                tvHint.setText(R.string.it_is_empty)
            }
            DefaultPageType.HIDE_DEFAULT_PAGE -> {
                defaultPage.visibility = GONE
            }
        }
    }

    fun setClickBackListener(listener: OnClickListener) {
        binding.ivBack.setOnClickListener(listener)
    }

    fun setRightOneTextBtnClickListener(listener: OnClickListener) {
        binding.tvRightOne.setOnClickListener(listener)
    }


    fun setRightTwoTextBtnClickListener(listener: OnClickListener) {
        binding.tvRightTwo.setOnClickListener(listener)
    }


    fun setRightOneImageBtnClickListener(listener: OnClickListener) {
        binding.ivRightOne.setOnClickListener(listener)
    }


    fun setRightTwoImageBtnClickListener(listener: OnClickListener) {
        binding.ivRightTwo.setOnClickListener(listener)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        //这时控件已经加载完成
        if (!isInEditMode) {
            StatusBarUtil.setTranslucent(context as Activity?, 0)
            StatusBarUtil.setColorNoTranslucent(context as Activity?, titleBackgroundColor)
        }
    }
}