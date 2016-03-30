package com.hy.gametools.manager;


public class HY_User
{

    /**
     * userId:HyGame 用户id
     */
    private String userId; 

    /**
     * channelUserId: 渠道用户ID，
     */
    private String channelUserId; 

    /**
     * channelUserName:渠道登录用户名
     */
    private String channelUserName; 

    /**
     * token:接入用到的token
     */
    private String token; 

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

    public HY_User(String userId, String channelUserId, String channelUserName,
            String token)
    {
        super();
        this.userId = userId;
        this.channelUserId = channelUserId;
        this.channelUserName = channelUserName;
        this.token = token;
    }
}
