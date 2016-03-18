package com.hy.gametools.manager;



import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HyLog;

import android.app.Activity;
import android.text.TextUtils;

public class HY_GameInit 
{

    private static final String TAG = "HY";
    private static String channelType;
    private static  String channelCode;
    private static String hyGameId;

    public static void initHYGameInfo(Activity mActivity)
    {
    	 try
         {
             channelCode = HY_Utils.getHYChannelCode(mActivity.getApplicationContext());
         }
         catch (Exception e)
         {
         	channelCode="100";
         }
         try
         {
         	channelType = HY_Utils.getHYChannelType(mActivity.getApplicationContext());
         }
         catch (Exception e)
         {
         	channelType="Test";
         }
         try
         {
         	hyGameId = HY_Utils.getHYGameId(mActivity.getApplicationContext());
         }
         catch (Exception e)
         {
         	hyGameId="1000";
      
         }
         if (TextUtils.isEmpty(channelCode))
         {
         	channelCode="100";
         }
         if (TextUtils.isEmpty(channelType))
         {
         	channelType="Test";
         }
         if (TextUtils.isEmpty(hyGameId))
         {
         	hyGameId="1000";
         }

         HY_Constants.HY_channelCode = channelCode;
         HY_Constants.HY_channelType = channelType;
         HY_Constants.HY_gameId = hyGameId;
         Constants.HY_GAME_CONFIG = "hy_game.json";
         HyLog.d(TAG, "HY_GameInit--->channelCode--->"+channelCode+"channelType--->"+channelType+ ",HYGameId--->"+hyGameId);
         if (TextUtils.isEmpty(channelCode))
         {
         	HyLog.d(TAG, "请在AndroidManifest.xml文件中配置渠道信息(HY_CHANNEL_CODE)");
         }
         if (TextUtils.isEmpty(channelType))
         {
         	HyLog.d(TAG, "请在AndroidManifest.xml文件中配置渠道信息(HY_CHANNEL_TYPE)");
         }
         if (TextUtils.isEmpty(hyGameId))
         {
         	HyLog.d(TAG, 
                     "请在AndroidManifest.xml文件中配置渠道信息(HY_GAME_ID)");
         }
     }
}
