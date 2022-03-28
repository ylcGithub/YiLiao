package dyzn.csxc.yiliao.lib_common.base

import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.network.data.BaseResponse
import dyzn.csxc.yiliao.lib_common.network.errorhandler.CustomException
import dyzn.csxc.yiliao.lib_common.network.errorhandler.ExceptionHandle
import dyzn.csxc.yiliao.lib_common.util.LogUtil
import kotlinx.coroutines.*

/**
 *@author YLC-D
 *说明: 数据请求基类
 */

open class BaseRepository(model: BaseViewModel, private val scope: CoroutineScope) {
    val showLoading:Boolean = true

    private val baseViewModel:BaseViewModel = model

    suspend fun <T : Any> request(call: suspend () -> BaseResponse<T>): T? {
        val result = withContext(Dispatchers.IO) { call.invoke() }.apply {
            //这儿可以对返回结果errorCode做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回errorCode 为 100，下面代码实现,再到baseActivity通过观察error来处理
            if (rescode != 0) {
                LogUtil.log("resCode:$rescode,$errorMsg")
                throw CustomException("网络访问错误:$errorMsg")
            }
        }
        return result.body
    }

    fun accessNetwork(
        work: suspend CoroutineScope.() -> Unit,
        catch: suspend CoroutineScope.(e: Throwable) -> Unit = {},
        end: suspend CoroutineScope.() -> Unit = {}
    ) = scope.launch(Dispatchers.Main) {
        try {
            baseViewModel.setLoadingState(true)
            withTimeout(10000) {
                work()
            }
        } catch (e: java.lang.Exception) {
            baseViewModel.setLoadingState(false)
            handleException(e)
            catch(e)
        } finally {
            end()
            baseViewModel.setLoadingState(false)
        }
    }

    fun runOnThread(
        work: suspend CoroutineScope.() -> Unit,
        catch: suspend CoroutineScope.(e: Throwable) -> Unit = {},
        end: suspend CoroutineScope.() -> Unit = {}
    ) = scope.launch(Dispatchers.IO) {
        try {
            work()
        } catch (e: java.lang.Exception) {
            catch(e)
            e.printStackTrace()
        } finally {
            end()
        }
    }

    private fun handleException(e: Throwable) {
        e.printStackTrace()
        val errorMsg = ExceptionHandle.exceptionHandler(e)
        LogUtil.log(errorMsg)
        toast(errorMsg)
    }
}