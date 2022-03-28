package dyzn.csxc.yiliao.lib_aop.permission.ant

/**
 *@author YLC-D
 *@create on 2022/3/21 11
 *说明:权限拒绝注解,请求码不能设置成-1
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PermissionDefied(val requestCode:Int=1)
