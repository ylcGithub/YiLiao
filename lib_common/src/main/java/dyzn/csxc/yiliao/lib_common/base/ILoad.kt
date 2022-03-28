package dyzn.csxc.yiliao.lib_common.base

/**
 *@author YLC-D
 *说明: 加载动画的接口
 */
interface ILoad {
    fun showLoading(message: String = "")

    fun dismissLoading()
}