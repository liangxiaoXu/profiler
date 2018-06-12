package com.ea.profiler.utils;

import java.net.InetAddress;

/**
 * Created by xuliangxiao on 2018/6/12 20:07
 */
public class IpUtils {


    /**
     * 获取本机IP
     * @return
     */
    public static String getLocalIp(){
        InetAddress ia=null;
        String localip = null;
        try {
            ia=ia.getLocalHost();
            localip=ia.getHostAddress();
//            System.out.println("本机的ip是 ："+localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localip;
    }

}
