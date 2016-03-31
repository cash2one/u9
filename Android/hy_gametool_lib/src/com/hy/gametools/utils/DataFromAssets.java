/**
 * @FILE:QihooInitPayData.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月13日 下午5:43:10
 **/
package com.hy.gametools.utils;

import com.hy.gametools.manager.HY_Utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

/*******************************************
 * 
 * @CLASS:QihooInitPayData
 * @AUTHOR:smile
 *******************************************/
public class DataFromAssets
{
    private Activity mActivity;
    // 测试模式
    private String isDebug;
    //修改请求地址
    private String host;
    //	渠道回调地址
    private String channelCallbackUrl;
    // 可选参数，获取其他参数信息1
    private String mReservedParam1;
    // 可选参数，获取其他参数信息2
    private String mReservedParam2;
    // 可选参数，获取其他参数信息3
    private String mReservedParam3;

    public String getmReservedParam1()
    {
        return mReservedParam1;
    }

    public void setmReservedParam1(String mReservedParam1)
    {
    	mReservedParam1 = HY_Utils.getHYConfig(mActivity, "" + mReservedParam1);

        this.mReservedParam1 = mReservedParam1;
    }

    public String getmReservedParam2()
    {
        return mReservedParam2;
    }

    public void setmReservedParam2(String mReservedParam2)
    {
    	mReservedParam2 = HY_Utils.getHYConfig(mActivity, "" + mReservedParam2);

        this.mReservedParam2 = mReservedParam2;
    }

    
    public String getmReservedParam3()
    {
        return mReservedParam3;
    }

    public void setmReservedParam3(String mReservedParam3)
    {
    	mReservedParam3 = HY_Utils.getHYConfig(mActivity, "" + mReservedParam3);

        this.mReservedParam3 = mReservedParam3;
    }


    public DataFromAssets(Activity paramActivity)
    {
        mActivity = paramActivity;
        initData(paramActivity);
    }

    public void initData(Activity paramActivity)
    {
        mActivity = paramActivity;

        isDebug = HY_Utils.getHYConfigDecide(paramActivity,"isDebug");
        channelCallbackUrl = HY_Utils.getHYConfigDecide(paramActivity, "channelCallbackUrl");
        
        if(!TextUtils.isEmpty(isDebug+"")){
			HyLog.isDebug = Boolean.parseBoolean(isDebug);
			if(HyLog.isDebug){
				Constants.URL_HOST = "http://115.159.73.234";
				Constants.URL_LOGIN = Constants.URL_HOST + "/api/gameLoginRequest";
				Constants.URL_PAY = Constants.URL_HOST +  "/api/gamePayRequest";
				Constants.URL_PAY_CALLBACK = Constants.URL_HOST + "/api/channelPayNotify";
				Constants.URL_CHECKLOGIN = Constants.URL_HOST + "/api/validateGameLogin";
				Constants.URL_CHECKPAY = Constants.URL_HOST + "/api/channelPayNotify";
			}
//        	Toast.makeText(paramActivity, "isDebug="+isDebug, Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(channelCallbackUrl)){
        	this.channelCallbackUrl = HY_Utils.getPayCallbackUrl(paramActivity);
        	HyLog.d("HY", "url Init:"+this.channelCallbackUrl);
        }
        
    }

    public String getIsDebug()
    {
        return isDebug;
    }

    public void setIsDebug(String isDebug)
    {
        this.isDebug = isDebug;
    }

  

    /*
     * public String getProductId() { return productId; } public void
     * setProductId(String productId) { this.productId = productId; }
     */
    public String getChannelCallbackUrl()
    {
        return channelCallbackUrl;
    }

    public void setChannelCallbackUrl(String channelCallbackUrl)
    {
        this.channelCallbackUrl = channelCallbackUrl;
    }

	public void setHost(String host){
    	this.host = host;
    }
    public String getHost(){
	  return this.host;
    }
    
    @Override
    public String toString()
    {
        return "DataFromAssets [isDebuge=" + isDebug
                +", channelCallbackUrl=" + channelCallbackUrl
                + ", mReservedParam1=" + mReservedParam1+ ", mReservedParam2=" 
                + mReservedParam2 + ", mReservedParam3="+ mReservedParam3 + "]";
    }

}
