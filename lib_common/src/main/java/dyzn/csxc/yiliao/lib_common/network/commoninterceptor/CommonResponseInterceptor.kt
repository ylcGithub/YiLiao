package dyzn.csxc.yiliao.lib_common.network.commoninterceptor

import dyzn.csxc.yiliao.lib_common.BuildConfig
import dyzn.csxc.yiliao.lib_common.util.LogUtil.log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author YLC-D
 * 说明: 网络响应拦截器
 */
class CommonResponseInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestTime = System.currentTimeMillis()
        val response = chain.proceed(chain.request())
        log("请求耗时=${(System.currentTimeMillis() - requestTime)}ms")
        logResult(response)
        return response
    }

    private fun logResult(response: Response) {
        if (BuildConfig.DEBUG) {
            val body = response.body
            body?.let {
                val source = it.source()
                val buffer = source.buffer
                val bodyStr = buffer.clone().readString(StandardCharsets.UTF_8)
                log(bodyStr)
            }
        }
    }
}