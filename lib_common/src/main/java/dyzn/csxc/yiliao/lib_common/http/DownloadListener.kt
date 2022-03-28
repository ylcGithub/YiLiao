package dyzn.csxc.yiliao.lib_common.http

interface DownloadListener {
    fun onStart() //下载开始
    fun onProgress(schedule: Int) //下载进度
    fun onFinish(path: String) //下载完成
    fun onFail(errorInfo: String) //下载失败
}