package com.hy.gametools.start;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public abstract interface HY_StatCommonSDK
{
    /** applicationInit(初始化一些必须的参数) */
    public abstract void applicationInit(Activity paramActivity,boolean mIsLandscape);

    public abstract void onCreate(Activity paramActivity);

    public abstract void onStop(Activity paramActivity);

    public abstract void onResume(Activity paramActivity);

    public abstract void onPause(Activity paramActivity);

    public abstract void onRestart(Activity paramActivity);

    public abstract void onDestroy(Activity paramActivity);

    public abstract void applicationDestroy(Activity paramActivity);

    public abstract void onActivityResult(Activity paramActivity, int requestCode, int resultCode, Intent data);

    public abstract void onNewIntent(Intent paramIntent);
    
    public abstract void onConfigurationChanged(Configuration newConfig);
}
