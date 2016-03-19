/**
 * @FILE:WX_UserManagerBase.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月5日 上午10:24:25
 **/
package com.hy.gametools.manager;

import android.app.Activity;

import com.hy.gametools.start.HY_StatBaseSDK;
import com.hy.gametools.utils.HyLog;

/*******************************************
 * @DESCRIPTION: 所有渠道用户接入登录注册的基类
 * @AUTHOR:smile
 *******************************************/
public abstract class HY_UserManagerBase extends HY_StatBaseSDK implements
         HY_UserManager,HY_UserListener
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HY_UserListener mUserLinstener;
    
    protected abstract void doStartPay(Activity paramActivity,
            HY_PayParams paramPayParams, HY_PayCallBack payCallBack);

    /** 退出接口 */
    protected abstract void doExitQuit(Activity paramActivity,
            HY_ExitCallback paramExitCallback);


    /** 支付接口 */
    public void doPay(Activity paramActivity,HY_PayParams payParams, HY_PayCallBack payCallBack)
    {
        HY_UserManagerBase.this.doStartPay(paramActivity, payParams, payCallBack);
    }
    /** 退出接口 */
    public void doExit(Activity paramActivity, HY_ExitCallback paramExitCallback)
    {
        HyLog.d(TAG, "已经执行exit");

        HY_UserManagerBase.this.doExitQuit(paramActivity, paramExitCallback);
    }

    public HY_UserListener getUserListener()
    {
        return this;
    }

    @Override
    public void setUserListener(Activity paramActivity,
    		HY_UserListener paramUserListener)
    {
        mActivity = paramActivity;
        this.mUserLinstener = paramUserListener;

    }
    public void onLogout(int resultCode,Object paramObject)
    {
        HyLog.d(TAG, "已经执行onlogout");

        HY_Utils.setLoginedUser(null);
        HY_UserManagerBase.this.mUserLinstener.onLogout(resultCode,paramObject);

    }
    public void onSwitchUser(HY_User user,int resultCode){
    	 HyLog.d(TAG, "已经执行onlogout");

         HY_UserManagerBase.this.mUserLinstener.onSwitchUser(user, resultCode);
    }

}
