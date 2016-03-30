/**
 * @FILE:WXPayParams.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月4日 下午2:18:17
 **/
package com.hy.gametools.manager;

/*******************************************
 * @AUTHOR:smile
 * @VERSION:v1.0
 *******************************************/
public class HY_PayParams
{
    /** amount:充值总金额  */
    private int amount;
    /** 兑换率 */
    private int exchange;
    /** U9订单号 */
    private String orderId;
    /** gameOrderId:游戏订单号 */
    private String gameOrderId;
    /** appExtInfo:CP方拓展信息，比如区服角色名之类的 */
    private String appExtInfo;
    /**　商品id */
    private String productId;
    /**　商品名称 */
    private String productName;
    /** callBackUrl:支付回调地址（正式环境优先读取客户端传入的地址，如果为空，则从后台配置读取）*/
    private String callBackUrl;
   


    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public int getExchange()
    {
        return exchange;
    }

    public void setExchange(int exchange)
    {
        this.exchange = exchange;
    }
    
    public String getProductName(){
    	return productName;
    }
    
    public void setProductName(String productName){
    	this.productName = productName;
    }
    
    public String getProductId(){
    	return productId;
    }
    
    public void setProductId(String productId){
    	this.productId = productId;
    }
    
    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    
    public String getGameOrderId()
    {
        return gameOrderId;
    }

    public void setGameOrderId(String gameOrderId)
    {
        this.gameOrderId = gameOrderId;
    }

    public String getAppExtInfo()
    {
        return appExtInfo;
    }

    public void setAppExtInfo(String appExtInfo)
    {
        this.appExtInfo = appExtInfo;
    }
    
        
    public String getCallBackUrl()
    {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl)
    {
        this.callBackUrl = callBackUrl;
    }

    @Override
    public String toString()
    {
        return "支付信息:[amount=" + amount + ", gameOrderId=" + gameOrderId
                + ", appExtInfo=" + appExtInfo + ", callBackUrl=" + callBackUrl + "]";
    }

}
