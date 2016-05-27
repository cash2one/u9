package com.hygame;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

/*******************************************
 * @CLASS:HYCheckReLogin
 * @AUTHOR:smile
 *******************************************/
public class QMYX_GetUserInfo
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;
    private String url = 
//    		"http://dev.api.17168.com/api/hbsdk/validateLogin/"; //test
    		"http://api.17168.com/api/hbsdk/validateLogin/";

    public QMYX_GetUserInfo(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void getUserInfo(final HY_UserInfoVo mChannelUserInfo, final CheckAfter<String> doAfter)
    {
    	String gameId = HY_Utils.getManifestMeta(mActivity, "GAME_ID");
    	String gameKey = HY_Utils.getManifestMeta(mActivity, "GAME_KEY");
    	String sign = HY_Utils.getMD5Hex(mChannelUserInfo.getToken() + gameId + gameKey );
    	Map<String, String> params = new HashMap<String,String>();
    	params.put("session_id", mChannelUserInfo.getToken());
    	params.put("game_id", gameId);
    	params.put("sign", sign);
    	
    	HyLog.d(TAG, "登录验证地址: "+url+"?token="+mChannelUserInfo.getToken()+"&fields=id,name,avatar,sex,area");
        mHttpUtils.doPost(mActivity, url, params,
                new UrlRequestCallBack()
                {

                    @Override
                    public void urlRequestStart(CallBackResult result)
                    {
                        HyLog.d(TAG, "QMYX_GetUserInfo-->urlRequestStart");
                    }

                    @Override
                    public void urlRequestException(CallBackResult result)
                    {
                        HyLog.d(TAG, "QMYX_GetUserInfo-->urlRequestException");

                        doAfter.afterFailed(result.backString, null);
                    }

                    @Override
                    public void urlRequestEnd(CallBackResult result)
                    {
                        HyLog.d(TAG, "QMYX_GetUserInfo-->result.obj:"+result.obj);

                        try
                        {
       
                               JSONObject json=new JSONObject( result.obj.toString());
                               String ret=json.getString("ret");
                               HyLog.d(TAG, "ret:"+ret);
                               int retInt = Integer.valueOf(ret);
                               String data=json.getString("data");
                               if (retInt == 1){
                            		   JSONObject json1 = new JSONObject(data);
                            		   String userId = json1.getString("user_key");
                            		   String userName = json1.getString("nick_name");
                            		   mChannelUserInfo.setChannelUserId(userId);
                            		   mChannelUserInfo.setChannelUserName(userName);
                            		   doAfter.afterSuccess(result.backString);
                               }else {
                            	HyLog.d(TAG, "登录验证失败");
								ToastUtils.show(mActivity, "登录验证失败");
							}
                            }

                        catch (Exception e)
                        {
                            doAfter.afterFailed(result.backString, e);
                            HyLog.d(TAG, "Exception-->afterFailed：" + result.backString);

                        }

                    }
                }, null);
    }
}
