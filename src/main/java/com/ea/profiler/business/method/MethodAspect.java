package com.ea.profiler.business.method;

import com.ea.profiler.exception.UMPMonitorException;
import com.ea.profiler.exception.bean.UMPException;
import com.ea.profiler.service.UMPMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 切片所有的包含 UMPMonitor 注解的方法
 * Created by xuliangxiao on 2018/5/16 20:29
 */
@Aspect
@Component
public class MethodAspect {


    /**
     * 定义公共的pointcut 切点
     */

    @Pointcut( "execution(* com.ea.trade.controller.order.GoodOrderController.getOrderDetails(..))" )
    public void mypoint(){ } //用来标注切入点的方法，必须是一个空方法

    /**
     * 环绕所有的方法
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around( "mypoint()" )
    public Object around( ProceedingJoinPoint pjp ) throws Throwable{
        //获取注解中的标识（方法名称）
        String methodName = getMethodName( pjp );

        long beginTime = System.currentTimeMillis() ;
        Object result = null ;
        try{
            result = pjp.proceed();
        }catch (UMPException e){
            System.out.println( "方法:" + methodName + "程序逻辑异常，异常信息: " + e.getMessage() );
        }catch (Throwable t){
            System.out.println( "方法:" + methodName + "发现其他异常，异常信息: " + t.getMessage() );
        }

        long endTime = System.currentTimeMillis() ;
        System.out.println( "方法:" + methodName + ", 运行时间:" + (endTime - beginTime) + "毫秒" + "" );

        //TODO 将方法名称、耗时、等信息 批量MQ发送给负责统计的系统。

        return result;
    }

    /**
     * 获取该方法的标识串（一般和方法名称保持一致）
     * @param pjp
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    private String getMethodName( ProceedingJoinPoint pjp ) throws ClassNotFoundException, NoSuchMethodException {

        String methodName = null ;
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();
        Annotation[] annotations = targetMethod.getAnnotations();


        for(Annotation annotation : annotations){
            if( annotation.annotationType().equals(UMPMonitor.class) ){
                methodName = ( (UMPMonitor)annotation ).methodName();
                System.out.println( "方法监控名 :" + methodName );

//                System.out.println( "执行时间 :" + ( (UMPMonitor)annotation ).executeTime() );
//                logger.info( "方法名 :" + ( (UMPMonitor)annotation ).methodName() );
//                logger.info( "执行时间 :" + ( (UMPMonitor)annotation ).executeTime() );
            }
        }

        return methodName ;

    }


     /*@AfterThrowing( pointcut = "mypoint()", throwing="ex" )
    public void afterThrowing( Throwable ex  ) throws NoSuchMethodException, ClassNotFoundException {
        System.out.println("方法:" *//*+ methodName*//* + "抛出异常，异常信息: " + ex.getMessage() );
    }*/


}
