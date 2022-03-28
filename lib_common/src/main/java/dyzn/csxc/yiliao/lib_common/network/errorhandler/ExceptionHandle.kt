package dyzn.csxc.yiliao.lib_common.network.errorhandler

import android.net.ParseException
import android.os.NetworkOnMainThreadException
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author YLC-D
 * 说明: 网络访问错误处理
 */
object ExceptionHandle {
    private const val NUM_300 = 300
    private const val NUM_400 = 400
    private const val NUM_401 = 401
    private const val NUM_500 = 500
    private const val NUM_600 = 600
    fun exceptionHandler(e: Throwable?): String {
        return if (e is UnknownHostException) {
            "网络不可用"
        } else if (e is SocketTimeoutException) {
            "请求网络超时"
        } else if (e is HttpException) {
            convertStatusCode(e)
        } else if (e is ParseException || e is JSONException) {
            "数据解析错误"
        } else if (e is NumberFormatException) {
            "数字格式异常"
        } else if (e is NetworkOnMainThreadException) {
            "网络访问不能在主线程进行"
        } else if (e is TimeoutCancellationException) {
            "网络访问超时"
        } else if (e is ConnectException) {
            "网络连接异常，没有服务!"
        }else if(e is CustomException){
            e.message!!
        } else {
            "未知错误"
        }
    }

    private fun convertStatusCode(httpException: HttpException): String {
        return if (httpException.code() in NUM_500 until NUM_600) {
            "服务器处理请求出错"
        } else if (httpException.code() in NUM_400 until NUM_500) {
            if (httpException.code() == NUM_401) {
                "Token无效，或者Token为空"
            } else {
                "服务器无法处理请求"
            }
        } else if (httpException.code() in NUM_300 until NUM_400) {
            "请求被重定向到其他页面"
        } else {
            httpException.message()
        }
    }
}