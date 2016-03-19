package com.hy.gametools.utils;


import android.text.TextUtils;

/**
 * HY_UserInfoVo，用户登录、支付请求参数
 */
public class HY_UserInfoVo 
{

	
    /** 登录部分 */
    private String userId=""; // 互娱的用户id
    private String channelUserId=""; // 渠道用户ID。
    private String channelUserName=""; // 渠道用户账号。
    private String token=""; // 接入用到的token

   
    /**个别渠道可能用到，如：魅族）*/
    private String customInfo="";
    private String errorMessage=""; // 错误码和错误信息


    public boolean isValid()
    {
        return !TextUtils.isEmpty(userId);
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getChannelUserId()
    {
        return channelUserId;
    }

    public void setChannelUserId(String channelUserId)
    {
        this.channelUserId = channelUserId;
    }

    public String getChannelUserName()
    {
        return channelUserName;
    }

    public void setChannelUserName(String channelUserName)
    {
        this.channelUserName = channelUserName;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getCustomInfo()
    {
        return customInfo;
    }

    public void setCustomInfo(String customInfo)
    {
        this.customInfo = customInfo;
    }

    @Override
    public String toString()
    {
        return "HY_UserInfoVo {userId=" + userId + ", channelUserId="
                + channelUserId + ", channelUserName=" + channelUserName
                + ", token=" + token +  ", customInfo="
                + customInfo + ", errorMessage=" + errorMessage + "}";
    }


    
}
