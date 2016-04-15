package com.hy.game.demo;

import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HyLog;

import android.app.Activity;
import android.content.Intent;

public class HY_GameManager implements HY_GameManagerProxy{
	private static String TAG = "HY";
	private static LoginCallback mloginCallback;
	private static PayCallback mpayCallback;
	private static  LogoutCallback logoutCallback;
	private static boolean mIsLandscape;
	private static HY_PayParams payParams;
	private static SwitchCallback switchCallback;
	private static HY_UserInfoVo mUserInfoVo;

	@Override
	public void init(Activity mActivity, SwitchCallback switchCallback,
			LogoutCallback logoutCallback,boolean mIsLandscape) {
			this.logoutCallback = logoutCallback;
			this.switchCallback = switchCallback;
			this.mIsLandscape = mIsLandscape;
	}
	
	
	@Override
	public void doLogin(final Activity mActivity,final LoginCallback mloginCallback) {
		this.mloginCallback =mloginCallback;
		HyLog.d(TAG, "Log"+this.mloginCallback);
			 mActivity.runOnUiThread(new Runnable() {
				 
					@Override
					public void run() {
						Intent intent = new Intent(mActivity,HyGameDemo.class);
						intent.putExtra("type", 0);
						mActivity.startActivity(intent);	
						HyLog.d(TAG, "执行登录方法,跳转到登录demo界面");
					}
			 });
	}
	
	@Override
	public void doPay(final Activity mActivity,HY_UserInfoVo mUserInfoVo, HY_PayParams payParams,
			PayCallback payCallback) {
		this.payParams = payParams;
		this.mpayCallback = payCallback;
		 mActivity.runOnUiThread(new Runnable() {
			 
				@Override
				public void run() {
					Intent intent = new Intent(mActivity,HyGameDemo.class);
					intent.putExtra("type", 2);
					mActivity.startActivity(intent);	
					HyLog.d(TAG, "执行支付方法,跳转到支付demo界面");
		}
	});
	}
	
	public static HY_PayParams getPayParams(){
		return payParams;
	}
	public static HY_UserInfoVo getUserInfoVo(){
		return mUserInfoVo;
	}
	public static void logCallback(Demo_UserInfo userInfo){
		try{
				mloginCallback.onSuccess(userInfo);
				HyLog.d(TAG, "demo登录:成功");
		}catch(Exception e){
			HyLog.e(TAG, "登录回调异常");
		}
	}
	public static void payCallback(int resultCode){
		try{
				mpayCallback.onResultCode(resultCode);
				HyLog.d(TAG, "demo支付:成功");
		}catch(Exception e){
			HyLog.e(TAG, "支付回调异常");
		}
	}
	public static boolean getmIsLandscape(){
		return  mIsLandscape;
	}

	@Override
	public void doLogout(LogoutCallback logoutCallback) {
		this.logoutCallback = logoutCallback;
		logoutCallback.onSuccess();
	}
	public static void logoutCallback(){
		logoutCallback.onSuccess();
	}
	public static void switchCallback(Demo_UserInfo userInfo){
		switchCallback.onSuccess(userInfo);
	}

}

