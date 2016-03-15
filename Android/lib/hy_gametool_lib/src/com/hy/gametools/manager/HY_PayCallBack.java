package com.hy.gametools.manager;

/**
 * 
 *  类名称：PayCallBack 类描述： 支付回调接口
 * 
 * @version
 * 
 */
public abstract interface HY_PayCallBack
{
    /**
     * onPayCallback(支付回调函数) TODO(这里描述这个方法适用条件 – 可选)
     * 
     * @param retCode
     *            状态码： 0 成功， -1 取消， 1 失败， -2 进行中
     * @param paramString
     *            返回给用用户的状态信息
     * @return
     */
    public abstract void onPayCallback(int retCode, String paramString);

}
