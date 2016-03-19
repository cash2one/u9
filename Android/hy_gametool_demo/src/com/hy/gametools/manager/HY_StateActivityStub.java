/**
 * @FILE:WXStateActivityStub.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月2日 下午4:37:13
 **/
package com.hy.gametools.manager;

import com.hy.gametools.start.HY_StatSDK;
import android.app.Activity;
import android.content.Intent;

/*******************************************
 * @CLASS:HY_StateActivityStub
 * @AUTHOR:smile
 *******************************************/
public class HY_StateActivityStub implements HY_ActivityStub
{


    public void applicationInit(Activity context,boolean mIsLandscape)
    {
//    	SimResolve.getDeviceInfo(context);
        HY_StatSDK.getInstance().applicationInit(context,mIsLandscape);
    }

    public void onCreate(Activity context)
    {
        HY_StatSDK.getInstance().onCreate(context);
    }

    public void onStop(Activity context)
    {
        HY_StatSDK.getInstance().onStop(context);
    }

    public void onResume(Activity context)
    {
        HY_StatSDK.getInstance().onResume(context);
    }

    public void onPause(Activity context)
    {
        HY_StatSDK.getInstance().onPause(context);
    }

    public void onRestart(Activity context)
    {
        HY_StatSDK.getInstance().onRestart(context);
    }

    public void onDestroy(Activity context)
    {
        HY_StatSDK.getInstance().onDestroy(context);
    }

    public void applicationDestroy(Activity activity)
    {
        HY_StatSDK.getInstance().applicationDestroy(activity);
    }

    public void onActivityResult(Activity context, int requestCode,
            int resultCode, Intent data)
    {
        HY_StatSDK.getInstance().onActivityResult(context, requestCode,
                resultCode, data);
    }

    public void onNewIntent(Intent intent)
    {
        HY_StatSDK.getInstance().onNewIntent(intent);
    }

}
