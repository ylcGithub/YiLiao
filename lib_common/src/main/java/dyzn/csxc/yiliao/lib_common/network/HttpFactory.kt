package dyzn.csxc.yiliao.lib_common.network

import dyzn.csxc.yiliao.lib_common.BuildConfig
import dyzn.csxc.yiliao.lib_common.base.BaseApplication
import dyzn.csxc.yiliao.lib_common.config.UrlConfig
import dyzn.csxc.yiliao.lib_common.network.commoninterceptor.CommonRequestInterceptor
import dyzn.csxc.yiliao.lib_common.network.commoninterceptor.CommonResponseInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *@author YLC-D
 *说明: 网络访问的retrofit封装工具
 */
object HttpFactory {
    private var mOkHttpClient: OkHttpClient? = null
    private val retrofitHashMap: HashMap<String, Retrofit> = HashMap(6)

    private fun getRetrofit(service: Class<*>): Retrofit? {
        if (retrofitHashMap[UrlConfig.HTTP_SERVER + service.name] != null) {
            return retrofitHashMap[UrlConfig.HTTP_SERVER + service.name]
        }
        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl(UrlConfig.HTTP_SERVER)
        retrofitBuilder.client(getOkHttpClient())
        //json 解析工具支持
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
        //协程支持
        retrofitBuilder.addCallAdapterFactory(CoroutineCallAdapterFactory())
        val retrofit = retrofitBuilder.build()
        retrofitHashMap[UrlConfig.HTTP_SERVER + service.name] = retrofit
        return retrofit
    }


    private fun getOkHttpClient(): OkHttpClient {
        if (mOkHttpClient == null) {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS)
            builder.writeTimeout(60, TimeUnit.SECONDS)

            val cacheSize = 100 * 1024 * 1024L
            builder.cache(Cache(BaseApplication.getAppContext().cacheDir, cacheSize))
            builder.addInterceptor(CommonRequestInterceptor())
            builder.addInterceptor(CommonResponseInterceptor())
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                builder.addInterceptor(httpLoggingInterceptor)
            }
            mOkHttpClient = builder.build()
        }
        return mOkHttpClient!!
    }

    fun <T> create(api: Class<T>): T {
        return getRetrofit(api)!!.create(api)
    }

}