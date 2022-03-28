package dyzn.csxc.yiliao.lib_common.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 *@author YLC-D
 *说明: 文件的上传下载API
 */
interface FileUpOrDownloadApi {
    //该操作用于下载文件,url传入下载的全路径,Streaming在大文件下载时必须添加,ResponseBody封装下载的流
    @Streaming
    @GET
    fun downloadFile(@Url url:String): Call<ResponseBody>
}