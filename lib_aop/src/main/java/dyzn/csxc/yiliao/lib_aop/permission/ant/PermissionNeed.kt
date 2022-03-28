package dyzn.csxc.yiliao.lib_aop.permission.ant

/**
 *@author YLC-D
 *@create on 2022/3/21 09
 *说明:权限申请的注解 请求码不能设置成-1
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PermissionNeed(val permissions:Array<String>,val requestCode:Int = 1)
