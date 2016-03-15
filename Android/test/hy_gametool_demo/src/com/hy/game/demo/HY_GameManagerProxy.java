package com.hy.game.demo;

import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.utils.HY_UserInfoVo;

import android.app.Activity;

public abstract interface HY_GameManagerProxy {
	public abstract void init(Activity mActivity,SwitchCallback switchCallback,LogoutCallback logoutCallback,boolean mIsLandscape);
	public abstract void doLogin(Activity mActivity,LoginCallback loginCallback);
	public abstract void doPay(Activity mActivity,HY_UserInfoVo mUserInfoVo,HY_PayParams payParams,PayCallback payCallback);
	public abstract void doLogout(LogoutCallback logoutCallback);
}