package com.hy.gametools.manager;

public abstract interface HY_AccountListener
{
    /**
     * onGotAuthorizationCode 回调给CP客户端的账户信息
     * 
     * @param localXMUser
     *            用户信息
     * @return
     */
    public abstract void onGotAuthorizationCode(HY_User localXMUser);

    public abstract void onGotError(int paramInt);
}
