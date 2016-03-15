package com.hy.gametools.utils;

/**
 * 
 * @ClassName: UrlRequestCallBack
 * @Description: TODO 网络连接时的回调
 * @author smile
 * 
 */
public interface UrlRequestCallBack
{

    /** 请求开始 */
    public void urlRequestStart(CallBackResult result);

    /** 请求结束 result 请求返回结果 */
    public void urlRequestEnd(CallBackResult result);

    /** 请求异常 */
    public void urlRequestException(CallBackResult result);
}
