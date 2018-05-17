package com.ea.profiler.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xuliangxiao on 2018/3/20 10:17
 */
@Retention(RetentionPolicy.RUNTIME) //JVM会读取注解，同时会保存到class文件中
@Target(ElementType.METHOD) //作用于方法，不包含构造方法
public @interface UMPMonitor {
    //方法名称
    String methodName() default "";
    //运行时间
//    long executeTime() default 0;


}
