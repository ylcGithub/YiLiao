package dyzn.csxc.yiliao.lib_common.util

import android.content.Context
import android.graphics.Typeface
import com.orhanobut.hawk.Hawk

object FontCustom {
    //  fontUrl 自定义字体分类的名称
    const val FONT_URL_KEY = "font_url_key"

    //Typeface是字体，这里我们创建一个对象
    var tf: Typeface? = null

    /**
     * 设置字体
     */
    fun setFont(context: Context): Typeface? {
        val fontUrl = Hawk.get(FONT_URL_KEY, "")
        if (tf == null && fontUrl.isNotEmpty()) {
            //给它设置你传入的自定义字体文件，再返回回来
            tf = Typeface.createFromAsset(context.assets, fontUrl)
        }else if(fontUrl.isEmpty()){
            tf = null
        }
        return tf
    }
}