package dyzn.csxc.yiliao.lib_common.base

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *@author YLC-D
 *说明:
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {
    /**
     * 是否显示加载框
     */
    private val showLoading = MutableLiveData<Boolean>()

    /**
     * 加载时显示的文字
     */
    val loadingText: MutableLiveData<String> = MutableLiveData("")

    /**
     * 错误消息
     */
    val errorMsg: MutableLiveData<String> = MutableLiveData()


    private val _pageBack = MutableLiveData<Boolean>()
    val pageBack: LiveData<Boolean> = _pageBack

    fun back() {
        _pageBack.value = true
    }

    fun runOnMain(work: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(Dispatchers.Main) {
            work()
        }

    fun runOnThread(work: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            work()
        }

    fun <T> setValueOnMain(data: MutableLiveData<T>?, value: T) = runOnMain {
        data?.value = value
    }

    fun getLoadingState(): LiveData<Boolean> {
        return showLoading
    }

    fun setLoadingState(loading: Boolean) {
        showLoading.value = loading
    }

    override fun onCleared() {
        super.onCleared()
        //取消协程
        viewModelScope.cancel()
    }
}