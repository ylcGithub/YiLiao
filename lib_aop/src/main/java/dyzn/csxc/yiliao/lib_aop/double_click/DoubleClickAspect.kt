package dyzn.csxc.yiliao.lib_aop.double_click

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

/**
 *@author YLC-D
 *@create on 2022/3/18 15
 *说明:检测是否可以访问的注解解析类
 */
@Aspect
class DoubleClickAspect {
    /*
       定义入口 @注解 访问权限 返回值类型 类名.函数名(参数)
    */
    @Pointcut("execution(@dyzn.csxc.yiliao.lib_aop.double_click.DoubleClickCheck * *(..))")
    fun methodAnnotated(){
    }

    @Around("methodAnnotated()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint){
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        //判断此方法是否有AopOnclick注解
        if(!method.isAnnotationPresent(DoubleClickCheck::class.java)){
            return
        }
        val aopClick = method.getAnnotation(DoubleClickCheck::class.java)
        if(!aopClick.canDoubleClick&&!DoubleClickUtil.isFastDoubleClick(aopClick.mills)){
            joinPoint.proceed()
        }else if(aopClick.canDoubleClick && DoubleClickUtil.isFastDoubleClick(aopClick.mills)){
            joinPoint.proceed()
        }
    }
}