package com.hy.gametools.manager;

public abstract interface HY_UserListener
{   /** onLoginSuccess(用户登录成功) */
	public abstract void onSwitchUser(HY_User user,int resultCode);
    /** onLogout(用户注销) */
    public abstract void onLogout(int resultCode,Object paramObject);
}
