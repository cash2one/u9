package com.hy.gametools.manager;

/** 登录回调接口 */
public abstract interface HY_LoginCallBack{
	public abstract void onLoginSuccess(HY_User user);
	public abstract void onLoginFailed(int code,String message);
}