package dyzn.csxc.yiliao.lib_aop.double_click

/**
 *@author YLC-D
 *@create on 2022/3/18 14
 *说明: mills 点击间隔时间 canDoubleClick为false时 mills时间内不能访问第二次
 * canDoubleClick 为true时 mills时间内访问才有效
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DoubleClickCheck(val mills:Long = 500,val canDoubleClick:Boolean = false)