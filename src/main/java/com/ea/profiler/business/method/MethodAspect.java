package com.ea.profiler.business.method;

import com.alibaba.fastjson.JSONObject;
import com.ea.profiler.exception.UMPMonitorException;
import com.ea.profiler.exception.bean.UMPException;
import com.ea.profiler.service.UMPMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 切片所有的包含 UMPMonitor 注解的方法
 * Created by xuliangxiao on 2018/5/16 20:29
 */
@Aspect
@Component
public class MethodAspect {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 可以抓取到 spring配置的内容
     */
//    @Value("${mq.nameSrvAddr}")
//    private String nameSrvAddr;

    /**
     * 定义公共的pointcut 切点
     */

//    @Pointcut( "execution(* com.ea.trade.controller.order.GoodOrderController.getOrderDetails(..))" ) //OK
//    @Pointcut( "execution(* com.ea..*.*(..))" ) // 启动报错
//    @Pointcut( executionStr ) // 使用spring注入方式读取切点表达式，经验证 不通过
//    @Pointcut( " execution( * com..*Controller.*(..) ) || execution(* com..*ServiceImpl.*(..) ) " ) // 切入com包下所有以Controller结尾的类中所有方法 或者 com包下所有以ServiceImpl结尾的类的所有方法
//    @Pointcut( " execution( * com..*"+"Controller"+".*(..) ) || execution(* com..*"+"ServiceImpl"+".*(..) ) " ) // 使用String字符串拼接切点表达式，经验证OK

    @Pointcut( " within( com..* ) && @annotation(com.ea.profiler.service.UMPMonitor) " ) // 切入com包以及所有子包下面的所有方法中待 @UMPMonitor注解的方法(实现类和接口都需要加上@UMPMonitor注解才能生效)
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

//        System.out.println("nameSrvAddr:" + nameSrvAddr);
        Object result = null ;


        long beginTime = System.currentTimeMillis() ;
        try{
            result = pjp.proceed();
        }catch (UMPException e){
            System.out.println( "方法:" + methodName + "程序逻辑异常，异常信息: " + e.getMessage() );
        }catch (Throwable t){
            System.out.println( "方法:" + methodName + "发现其他异常，异常信息: " + t.getMessage() );
        }

        long endTime = System.currentTimeMillis() ;

        if( methodName != null && !StringUtils.isEmpty(methodName) ){
            System.out.println( "方法:" + methodName + ", 运行时间:" + (endTime - beginTime) + "毫秒" + "" );
        }else{
            System.out.println(" 方法没有使用 UMPMonitor 注解 ");
        }

        //TODO 将方法名称、耗时、等信息 批量MQ发送给负责统计的系统。

        JSONObject jsonObject = new JSONObject();
        jsonObject.put( "methodName", methodName );
        jsonObject.put( "exeMillisecond", endTime - beginTime );
        uploadMsg(jsonObject);

        return result;
    }

    /**
     * 发送消息到MQ
     * TODO 后期改为线程池批量实现
     * @param msgJson
     */
    public void uploadMsg( JSONObject msgJson ){
        try{
            rabbitTemplate.convertAndSend("UMP_METHOD_MONITOR",msgJson);
        }catch (Exception e){
            e.printStackTrace();
        }
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
