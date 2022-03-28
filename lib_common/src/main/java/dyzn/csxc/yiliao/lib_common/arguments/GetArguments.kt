package dyzn.csxc.yiliao.lib_common.arguments

/**
 *@author YLC-D
 *说明: 用于获取fragment通过navigation传递的参数 注解
 */
@Target(AnnotationTarget.FIELD)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class GetArguments(val key:String)