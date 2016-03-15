/**
 * @FILE:WX_IGameProxy.java
 * @AUTHOR:zhangchi
 * @DATE:2014年11月28日 下午4:20:47
 **/
package com.hy.gametools.manager;

import android.app.Activity;
import android.content.Intent;

/*******************************************
 * @CLASS:HY_MyGameProxy
 * @AUTHOR:smile
 *******************************************/
public abstract interface HY_IGameProxy
{
    /** 1. interface HY_UserManager */
    /**
     * login(登录接口)
     * 
     * @param mActivity
     *            ：上下文
     * @param paramObject
     *            ：遗留的接口，后续扩展使用
     * @return
     */
    public abstract void logon(Activity mActivity,HY_LoginCallBack loginCallBack);

    /**
     * logout(这里用一句话描述这个方法的作用)
     * 
     * @param mActivity
     *            ：上下文
     * @param paramObject
     *            ：遗留的接口，后续扩展使用
     * @return
     */
    public abstract void logoff(Activity mActivity, Object paramObject);


    /**
     * startPay 支付接口说明
     * 
     * @param mActivity
     *            上下文
     * @param amount
     *            金额数，使用者可以自由设定数额。金额数为100的整数倍，运行定额支付流程； 金额数为0，SDK运行不定额支付流程
     * @param gameOrderId
     *            游戏订单号 （限制30个字符）
     * @param appExtInfo
     *            可选参数，应用扩展信息，原样返回，
     * @param payCallBack
     *            支付回调接口
     * @return
     */

    public abstract void setUserListener(Activity paramActivity,
            HY_UserListener paramUserListener);
    public abstract void applicationInit(Activity mActivity,boolean mIsLandscape);

    public abstract void onCreate(Activity mActivity);

    public abstract void onStop(Activity mActivity);

    public abstract void onResume(Activity mActivity);

    public abstract void onPause(Activity mActivity);

    public abstract void onRestart(Activity mActivity);

    public abstract void onDestroy(Activity mActivity);

    public abstract void applicationDestroy(Activity mActivity);

    public abstract void onActivityResult(Activity mActivity,
            int paramInt1, int paramInt2, Intent paramIntent);

    public abstract void setRoleData(Activity mActivity, HY_GameRoleInfo gameRoleInfo);

    public abstract void onNewIntent(Intent paramIntent);

    /** 5. interface WXExiter */
    /**
     * exit 游戏退出接口
     * 
     * @param paramExitCallback
     *            回调退出接口
     * @return
     */
    public abstract void exit(Activity mActivity,
            HY_ExitCallback paramExitCallback);

	void startPay(Activity paramActivity, HY_PayParams payParams,
			HY_PayCallBack payCallBack);
}
