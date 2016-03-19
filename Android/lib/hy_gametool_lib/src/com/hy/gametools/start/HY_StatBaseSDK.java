package com.hy.gametools.start;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.hy.gametools.manager.HY_Constants;
import com.hy.gametools.manager.HY_UserManagerBase;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.HyLog;

public class HY_StatBaseSDK implements HY_StatCommonSDK
{
    private static final String TAG = "HY";


    public static HY_UserManagerBase mUserManager;

    private void getInitClassName()
    {
        try
        {

            Object object=HY_Utils.getMainClassByChannelName();
            mUserManager = ((HY_UserManagerBase) object);
            HyLog.d(TAG, "HY_StatBaseSDK-->init主业务类" + mUserManager);
            // instance.setUserManager((WX_UserManager)object);
        }
        catch (ClassNotFoundException e)
        {
            HyLog.e(TAG, "HY_StatBaseSDK-->ClassNotFoundException:"+e);
        }
        catch (NoSuchMethodException e)
        {
            HyLog.e(TAG, "HY_StatBaseSDK-->NoSuchMethodException:");
        }
        catch (Exception e)
        {
            HyLog.e(TAG, "HY_StatBaseSDK-->Exception:" + e.toString());
        }
    }

    public void applicationInit(Activity context,boolean mIsLandscape)
    {
        if (!TextUtils.isEmpty(HY_Constants.HY_channelType))
        {
            getInitClassName();
            mUserManager.applicationInit(context,mIsLandscape);
            HyLog.d(TAG, "HY_StatBaseSDK = " + "....applicationInit");
        }


    }

    public void onCreate(Activity context)
    {
        mUserManager.onCreate(context);
        HyLog.d(TAG, "HY_StatBaseSDK = " + "....onCreate");

    }

    public void onStop(Activity context)
    {
        mUserManager.onStop(context);

    }

    public void onResume(Activity context)
    {
        mUserManager.onResume(context);

    }

    public void onPause(Activity context)
    {
        mUserManager.onPause(context);

    }

    public void onRestart(Activity context)
    {
        mUserManager.onRestart(context);

    }

    public void onDestroy(Activity context)
    {
        mUserManager.onDestroy(context);

    }

    public void applicationDestroy(Activity context)
    {
        mUserManager.applicationDestroy(context);

    }

    public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data)
    {
        mUserManager.onActivityResult(context, requestCode, resultCode,
                data);

    }

    public void onNewIntent(Intent paramIntent)
    {
        mUserManager.onNewIntent(paramIntent);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mUserManager.onConfigurationChanged(newConfig);	
	}
}
