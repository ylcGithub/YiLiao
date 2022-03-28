package dyzn.csxc.yiliao.lib_common.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import dyzn.csxc.yiliao.lib_common.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import java.util.*


/**
 *@author YLC-D
 *说明:
 */
object PictureUtil {
    /**
     * 显示图片预览
     *
     * @param context fragment 或者 activity 的上下文
     * @param index   当前的下标
     * @param urls    图片地址的数组
     */
    fun showImageGallery(context: Activity, index: Int, urls: Array<String>) {
        if (urls.isEmpty() || urls.size <= index) {
            return
        }
        PictureSelector.create(context)
            .themeStyle(R.style.picture_default_style)
            .isNotPreviewDownload(true)
            .openExternalPreview(index, getLocalMediaList(urls))
    }

    fun showImageGallery(context: Fragment, index: Int, urls: Array<String>) {
        if (urls.isEmpty() || urls.size <= index) {
            return
        }
        PictureSelector.create(context)
            .themeStyle(R.style.picture_default_style)
            .isNotPreviewDownload(true)
            .imageEngine(GlideEngine)
            .openExternalPreview(index, getLocalMediaList(urls))
    }

    /**
     * 图片的地址 转出 预览需要的
     */
    private fun getLocalMediaList(urls: Array<String>): List<LocalMedia>{
        val localMedia: MutableList<LocalMedia> = ArrayList<LocalMedia>()
        if (urls.isNotEmpty()) {
            var media: LocalMedia
            for (url in urls) {
                media = LocalMedia()
                media.path = CommonUtil.joinImageUrl(url)
                localMedia.add(media)
            }
        }
        return localMedia
    }
}