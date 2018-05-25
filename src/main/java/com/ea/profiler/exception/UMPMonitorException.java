package com.ea.profiler.exception;

import com.ea.profiler.exception.bean.UMPException;

/**
 * Created by xuliangxiao on 2018/5/25 10:14
 */
public class UMPMonitorException {

    /**
     * 程序异常报警
     * @param sign 报警标识
     * @param message 报警内容
     */
    public static void warn( String sign, String message ) throws UMPException {
        throw new UMPException( sign, message ) ;
    }

}
