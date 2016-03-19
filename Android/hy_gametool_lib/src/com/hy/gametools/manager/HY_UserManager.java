/**
 * @FILE:WX_360UserManager.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月1日 下午3:51:44
 **/
package com.hy.gametools.manager;

import android.app.Activity;

/*******************************************
 * @CLASS:HY_UserManager
 * @AUTHOR:smile
 *******************************************/
public abstract interface HY_UserManager
{
	/** 登录接口 */
    public abstract void doLogin(Activity paramActivity,HY_LoginCallBack loginCallBack);
    /** 注销接口 */
    public abstract void doLogout(Activity paramActivity,Object paramObject);
    /** 支付接口 */
    public abstract void doPay(Activity paramActivity,HY_PayParams payParams, HY_PayCallBack payCallBack);
    /** 退出接口 */
    public abstract void doExit(Activity paramActivity,HY_ExitCallback paramExitCallback);
    /** 角色信息接口 */
    public abstract void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo);
    /** 设置用户信息监听 */
    public abstract void setUserListener(Activity paActivity,HY_UserListener paramUserListener);
}
