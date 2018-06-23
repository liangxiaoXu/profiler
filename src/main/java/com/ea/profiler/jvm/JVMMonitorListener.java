package com.ea.profiler.jvm;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * JVM监听器
 * 定时上报JVM的内存等使用情况
 * Created by xuliangxiao on 2018/6/19 21:32
 */

public class JVMMonitorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // demo，使用线程处理。生产环境要使用线程池或者定时任务完善！


        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        while( true ){
                            // https://www.cnblogs.com/c-lm/p/6735909.html -- 获取CPU占用率、线程等信息

                            //获取JVM使用情况，并上传至UMP服务端(MQ)

                            //显示JVM尝试使用的最大内存
                            long maxMemory = Runtime.getRuntime().maxMemory();
                            //空闲内存
                            long freeMemory  = Runtime.getRuntime().freeMemory();
                            //显示JVM总内存
                            long totalMemory = Runtime.getRuntime().totalMemory();

                            System.out.println( "maxMemory : " + maxMemory + " Bytes" );
                            System.out.println( "freeMemory : " + freeMemory  + " Bytes" );
                            System.out.println( "totalMemory : " + totalMemory  + " Bytes" );

                            try {
    //                            Thread.sleep(1 * 60 * 1000l); //1分钟一次
                                Thread.sleep(1 * 10 * 1000l); //10秒一次
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }
        ).start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // do nothing
    }
}
