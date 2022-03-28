package dyzn.csxc.yiliao.lib_common.http

import androidx.lifecycle.MutableLiveData
import dyzn.csxc.yiliao.lib_common.base.BaseRepository
import dyzn.csxc.yiliao.lib_common.base.BaseViewModel
import dyzn.csxc.yiliao.lib_common.network.HttpFactory
import dyzn.csxc.yiliao.lib_common.util.CacheFileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

/**
 *@author YLC-D
 *说明:文件上传下载的封装类
 */
class FileUpOrDownloadRepository(
    private val viewModel: BaseViewModel,
    scope: CoroutineScope,
    val showProgress: MutableLiveData<Boolean>,
    val scheduleData: MutableLiveData<Int>,
    val finishAndReturnUrl:MutableLiveData<String>?
) : BaseRepository(viewModel, scope) {

    private val fileApi by lazy {
        HttpFactory.create(FileUpOrDownloadApi::class.java)
    }

    private suspend fun downloadFile(
        downloadUrl: String,
        fileCachePath: String,
        listener: DownloadListener
    ) {
        withContext(Dispatchers.IO) {
            val call = fileApi.downloadFile(downloadUrl)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //将Response写入到从磁盘
                    writeResponseToDisk(fileCachePath, response, listener)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onFail("网络错误:${t.message}")
                }
            })
        }
    }

    private val sBufferSize = 8192
    private fun writeResponseToDisk(
        path: String,
        response: Response<ResponseBody>,
        downloadListener: DownloadListener
    ) {
        val file = File(path)
        val inS = response.body()?.byteStream()
        val totalLength = response.body()?.contentLength() ?: 1L
        //开始下载
        downloadListener.onStart()
        //创建文件
        if (!file.exists()) {
            if (file.parentFile?.exists() != true) {
                file.parentFile?.mkdirs()
            }
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                downloadListener.onFail("文件在本地创建失败：${e}")
            }
        }

        var os: OutputStream? = null
        var currentLength: Long = 0
        try {
            os = BufferedOutputStream(FileOutputStream(file))
            val data = ByteArray(sBufferSize)
            var len: Int?
            while (inS?.read(data, 0, sBufferSize).also { len = it } != -1) {
                os.write(data, 0, len ?: 0)
                currentLength += len?.toLong() ?: 0
                //计算当前下载进度
                downloadListener.onProgress((100 * currentLength / totalLength).toInt())
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
            downloadListener.onFail("IOException")
        } finally {
            try {
                inS?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun download(fileName: String) = accessNetwork(
        work = {
            viewModel.setLoadingState(true)
            downloadFile(
                UrlUtil.getFileUrl(fileName),
                CacheFileUtil.getFileInSdCardOfPath(fileName),
                object : DownloadListener {
                    override fun onStart() {
                        viewModel.setValueOnMain(showProgress, true)
                        viewModel.setValueOnMain(viewModel.loadingText, "下载中...")
                    }

                    override fun onProgress(schedule: Int) {
                        viewModel.setValueOnMain(scheduleData, schedule)
                    }

                    override fun onFinish(path: String) {
                        viewModel.setValueOnMain(finishAndReturnUrl,path)
                    }

                    override fun onFail(errorInfo: String) {
                        viewModel.setValueOnMain(viewModel.errorMsg, errorInfo)
                        viewModel.setValueOnMain(showProgress, false)
                    }
                })
        }
    )
}