package com.hy.gametools.utils;

/**
 * @ClassName: ResponseResultVO
 * @author smile
 */
public class ResponseResultVO
{
    public static final String MESSAGE = "Message";
    public static final String RESPOMSECODE = "Code";
    public static final String TRANSTYPE = "TransType";// 提交类型，登录？提交订单？
    public static final String USERID = "UserId";
    public static final String ORDERID = "OrderId";
    public static final String EXT = "Ext";

    /** 反馈信息 */
    public String message;
    /** 响应码 */
    public String responseCode;
    /** 提交类型，登录？提交订单？ */
    public String transType;
    /** 内容主题 */
    public Object obj;
    /** 用户id */
    public String userId;
    /** 订单号 */
    public String orderId;
    /** 拓展字段  */
    public String ext;
    
    // responseCode响应码 响应码描述
    // 0000 系统操作正常
    // 9999 系统操作异常
    // 1001 channelId无效
    // 1002 ProductId无效
    // 1003 提交参数不能为空
    // 1004 该userId对应的用户不存在
    // 1005 该appId对应的应用不存在
    // 1006 订单号已存在，不能重复提交订单
    // 1007 登录令牌无效
}
