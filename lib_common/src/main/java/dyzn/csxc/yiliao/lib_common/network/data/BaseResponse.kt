package dyzn.csxc.yiliao.lib_common.network.data

/**
 * @author YLC-D
 * 说明: 网络数据返回的普通类型 基本参数 提取封装基类
 */
data class BaseResponse<T>(val rescode: Int, val errorMsg: String?, val body: T?)
