package dyzn.csxc.yiliao.lib_aop.permission

/**
 *@author YLC-D
 *@create on 2022/3/21 09
 *说明:
 */
interface IPermission {
    //所有申请权限都通过
    fun onPermissionGranted()
    //拒绝权限申请 requestCode 本次申请的表识别码
    fun onPermissionDefied(requestCode:Int)
    //放弃权限申请 requestCode 本次申请的表识别码
    fun onPermissionCancel(requestCode: Int)
}