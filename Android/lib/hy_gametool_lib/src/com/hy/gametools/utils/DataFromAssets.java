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

        isDebug = HY_Utils.getHYConfig(paramActivity,"isDebug");
        channelCallbackUrl = HY_Utils.getHYConfig(paramActivity, "channelCallbackUrl");
        
        if(!TextUtils.isEmpty(isDebug+"")){
        	HyLog.isDebug = Boolean.parseBoolean(isDebug);
//        	Toast.makeText(paramActivity, "isDebug="+isDebug, Toast.LENGTH_SHORT).show();
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

    @Override
    public String toString()
    {
        return "DataFromAssets [isDebuge=" + isDebug
                +", channelCallbackUrl=" + channelCallbackUrl
                + ", mReservedParam1=" + mReservedParam1+ ", mReservedParam2=" 
                + mReservedParam2 + ", mReservedParam3="+ mReservedParam3 + "]";
    }

}
