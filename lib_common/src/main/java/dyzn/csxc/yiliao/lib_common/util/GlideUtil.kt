package dyzn.csxc.yiliao.lib_common.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.config.MatchConfig
import org.apache.commons.lang3.StringUtils


/**
 *@author YLC-D
 *说明: glide 图片加载器的工具类
 */
object GlideUtil {
    fun load(imageView: ImageView, url: String, transform: BitmapTransformation? = null) {
        if (!StringUtils.isEmpty(url) && url.endsWith(MatchConfig.GIF_MARK)) {
            Glide.with(imageView.context).asGif().load(CommonUtil.joinImageUrl(url))
                .transition(GenericTransitionOptions.with(R.anim.base_image_show_anim))
                .apply(getNormalOptions(transform)).into(imageView)
        } else {
            Glide.with(imageView.context).load(CommonUtil.joinImageUrl(url))
//                .transition(GenericTransitionOptions.with(R.anim.base_image_show_anim))
                .apply(getNormalOptions(transform)).into(imageView)
        }
    }

    fun loadAvatar(imageView: ImageView, url: String) {
        if (!StringUtils.isEmpty(url) && url.endsWith(MatchConfig.GIF_MARK)) {
            Glide.with(imageView.context).asGif().load(CommonUtil.joinImageUrl(url))
                .transition(GenericTransitionOptions.with(R.anim.base_image_show_anim))
                .apply(getAvatarOptions()).into(imageView)
        } else {
            Glide.with(imageView.context).load(CommonUtil.joinImageUrl(url))
                .transition(GenericTransitionOptions.with(R.anim.base_image_show_anim))
                .apply(getAvatarOptions()).into(imageView)
        }
    }

    private lateinit var normalOptions: RequestOptions
    private lateinit var withTransformOptions: RequestOptions
    private lateinit var avatarOptions: RequestOptions

    private fun getNormalOptions(transform: BitmapTransformation?): RequestOptions {
        if(!this::normalOptions.isInitialized){
            normalOptions = getOptions(
                R.drawable.vector_drawable_image_placeholder,
                R.drawable.vector_drawable_image_placeholder,
                R.drawable.vector_drawable_image_failed_to_load
            )
        }
       if(!this::withTransformOptions.isInitialized){
           withTransformOptions = getOptions(
               R.drawable.vector_drawable_image_placeholder,
               R.drawable.vector_drawable_image_placeholder,
               R.drawable.vector_drawable_image_failed_to_load
           )
       }
        return if(transform == null){
            normalOptions
        }else{
            withTransformOptions.transform(transform)
        }
    }

    private fun getAvatarOptions(): RequestOptions{
        if(!this::avatarOptions.isInitialized){
            avatarOptions = getOptions(R.drawable.vector_drawable_default_icon_avatar,R.drawable.vector_drawable_default_icon_avatar,R.drawable.vector_drawable_default_icon_avatar)
        }
        return avatarOptions
    }

    private fun getOptions(
        @DrawableRes pResId: Int,
        @DrawableRes fResId: Int,
        @DrawableRes eResId: Int
    ): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .placeholder(pResId)
            .fallback(fResId)
            .error(eResId)
    }
}