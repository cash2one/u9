/**
 * @FILE:WX_GameProxy.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月1日 下午5:57:14
 **/
package com.hy.gametools.manager;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import com.hy.gametools.utils.HyLog;

/*******************************************
 * 
 * @CLASS:HY_GameProxy
 * @AUTHOR:smile
 * @VERSION:v1.0
 *******************************************/
public class HY_CommonGameProxy implements HY_IGameProxy
{
    private static final String TAG = "HY";
    private HY_UserManager userManager;
    private HY_ActivityStub stub;

    public void setUserManager(HY_UserManager userManager)
    {
        HyLog.d(TAG, "已经读取渠道主控制文件成功，当前读取文件=" + userManager);
        this.userManager = userManager;
    }

    public HY_CommonGameProxy()
    {
        super();
    }

    public HY_CommonGameProxy(HY_ActivityStub stub)
    {
        this.stub = stub;
    }

    @Override
    public void logon(final Activity paramActivity,final HY_LoginCallBack loginCallBack)
    {
        if (null!=userManager)
        {
            paramActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    HY_CommonGameProxy.this.userManager.doLogin(paramActivity, 
                    		loginCallBack);
                }
            });
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->login");

        }

    }

    @Override
    public void logoff(final Activity paramActivity,final  Object paramObject)
    {
        if (null!=userManager)
        {
            paramActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    HY_CommonGameProxy.this.userManager.doLogout(paramActivity,
                    		paramObject);
                }
            });
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->logout");
//            ToastUtils.show(paramActivity, "打渠道包后会调用正式登出接口");
        }
      
    }

   

    @Override
    public void startPay(final Activity paramActivity, final HY_PayParams payParams,
            final HY_PayCallBack payCallBack)
    {
        if (null!=userManager)
        {
            ((Activity) paramActivity).runOnUiThread(new Runnable()
            {
                public void run()
                {
                    HY_CommonGameProxy.this.userManager.doPay(paramActivity,
                    		payParams, payCallBack);
                }
            });
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->startPay");
           
        }
       
    }

    @Override
    public void applicationInit(Activity paramActivity,boolean mIsLandscape)
    {
        HyLog.d(TAG, "------------->applicationInit");

        if (null!=stub)
        {
            HyLog.d(TAG, "stub正常--->applicationInit");
            HY_GameInit.initHYGameInfo(paramActivity);
            this.stub.applicationInit(paramActivity,mIsLandscape);
           
        }
        else
        {
            HyLog.d(TAG, "stub为null--->applicationInit");
        }

    }

    @Override
    public void onCreate(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onCreate(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onCreate");
        }

    }

    @Override
    public void onStop(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onStop(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onStop");
        }
    }

    @Override
    public void onResume(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onResume(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onResume");
        }
    }

    @Override
    public void onPause(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onPause(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onPause");
        }
    }

    @Override
    public void onRestart(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onRestart(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onRestart");
        }
    }

    @Override
    public void onDestroy(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.onDestroy(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onDestroy");
        }
    }

    @Override
    public void applicationDestroy(Activity paramActivity)
    {
        if (null!=stub)
        {
            this.stub.applicationDestroy(paramActivity);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->applicationDestroy");
        }

    }

    @Override
    public void onActivityResult(Activity paramActivity, int requestCode,
            int resultCode, Intent data)
    {
        if (null!=stub)
        {
            this.stub
            .onActivityResult(paramActivity, requestCode, resultCode, data);
            ;
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onCreate");
        }

    }

    @Override
    public void onNewIntent(Intent paramIntent)
    {
        if (null!=stub)
        {
            this.stub.onNewIntent(paramIntent);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onCreate");
        }
    }

    @Override
    public void exit(final Activity paramActivity,
            final HY_ExitCallback paramExitCallback)
    {
        if (null!=userManager)
        {
            paramActivity.runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {
                    HY_CommonGameProxy.this.userManager.doExit(paramActivity, paramExitCallback);
                }
            });
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->exit");
//            ToastUtils.show(paramActivity, "退出界面，详见demo注释介绍");
        }
    

    }

    @Override
    public void setRoleData(final Activity paramActivity,final HY_GameRoleInfo gameRoleInfo)
    {
//        extListener.setExtData(paramActivity, paramString);
        
        if (null!=userManager)
        {
            ((Activity) paramActivity).runOnUiThread(new Runnable()
            {
                public void run()
                {
                    userManager.setRoleData(paramActivity, gameRoleInfo);
                }
            });
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->setExtData");
        }
    }

	@Override
	public void setUserListener(Activity paramActivity, HY_UserListener paramUserListener) {
		if (null!=userManager)
        {
            this.userManager.setUserListener(paramActivity, paramUserListener);
        }
        else
        {
            HyLog.d(TAG, "userManager为null--->setUserListener");
        }	
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        if (null!=stub)
        {
            this.stub.onConfigurationChanged(newConfig);
        }
        else
        {
            HyLog.d(TAG, "stub为null--->onConfigurationChanged");
        }		
	}

}
