package dyzn.csxc.yiliao.lib_common.network.data


/**
 * @author YLC-D
 * 说明: 网络数据返回的普通类型 基本参数 提取封装基类
 */
data class BaseListResponse<out T>(
    val hasNextPage: Boolean = false,
    /**
     * 下一页数据是第几页
     */
    val nextPage: Int = 0,
    val isFirstPage: Boolean = false,
    val isLastPage: Boolean = false,

    /**
     * 这个接口总共可以返回的数据量
     */

    val total: Int = 0,
    /**
     * 这一次访问返回的数据条数
     */

    val pageSize: Int = 0,
    /**
     * 数据实体
     */
    val list: List<T>? = null
)