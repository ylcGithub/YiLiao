package dyzn.csxc.yiliao.lib_common.network.commoninterceptor

import dyzn.csxc.yiliao.lib_common.config.HawkKey
import com.orhanobut.hawk.Hawk
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author YLC-D
 * 说明: 请求拦截器 添加token
 */
class CommonRequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("os", "android")
        builder.addHeader("DToken", Hawk.get(HawkKey.TOKEN, ""))
        return chain.proceed(builder.build())
    }
}