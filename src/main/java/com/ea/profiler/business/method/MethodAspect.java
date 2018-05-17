package com.ea.profiler.business.method;

import com.ea.profiler.service.UMPMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
     * 环绕所有的方法
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around( "execution(* com.ea.trade.controller.order.GoodOrderController.getOrderDetails(..))" )
//    @Around( "@annotation( UMPMonitor ) " )
    public Object around(ProceedingJoinPoint pjp ) throws Throwable{

        //获取注解中的标识（方法名称）
        String methodName = getMethodName( pjp );

        long beginTime = System.currentTimeMillis() ;
        Object result = pjp.proceed();
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


}
