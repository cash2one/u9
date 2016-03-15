package com.hygame;

import com.hy.gametools.manager.HY_SdkResult;
import com.hy.gametools.manager.HY_UserManagerBase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 登出广播
 * 
 * @author 巍
 * 
 */
public class LoginOutBroadcastReceiver extends BroadcastReceiver { // notice 注销

	public static final String LOGINOUT_ACTION = "CCPAY_LOGINOUT_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		HY_UserManagerBase.mUserManager.getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销登录");
	}
}
