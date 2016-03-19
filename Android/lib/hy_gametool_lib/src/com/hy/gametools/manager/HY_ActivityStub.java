/**
 * @FILE:WXActivityStub.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月1日 下午6:18:10
 **/
package com.hy.gametools.manager;

/*******************************************
 * @CLASS:HYActivityStub	
 * @AUTHOR:smile
 *******************************************/
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public abstract interface HY_ActivityStub
{
    public abstract void applicationInit(Activity paramActivity,boolean mIsLandscape);

    public abstract void onCreate(Activity paramActivity);

    public abstract void onStop(Activity paramActivity);

    public abstract void onResume(Activity paramActivity);

    public abstract void onPause(Activity paramActivity);

    public abstract void onRestart(Activity paramActivity);

    public abstract void onDestroy(Activity paramActivity);

    public abstract void applicationDestroy(Activity paramActivity);

    public abstract void onActivityResult(Activity paramActivity,
            int paramInt1, int paramInt2, Intent paramIntent);

    public abstract void onNewIntent(Intent paramIntent);
    
    public abstract void onConfigurationChanged(Configuration newConfig);
}
