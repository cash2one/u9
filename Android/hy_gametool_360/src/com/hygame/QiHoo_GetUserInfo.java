package com.hygame;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.UrlRequestCallBack;

import android.app.Activity;
import android.text.TextUtils;

/*******************************************
 * @CLASS:HYCheckReLogin
 * @AUTHOR:smile
 *******************************************/
public class QiHoo_GetUserInfo
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;
    private String url = "https://openapi.360.cn/user/me";

    public QiHoo_GetUserInfo(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void getUserInfo(final HY_UserInfoVo mChannelUserInfo, final CheckAfter<String> doAfter)
    {
    	Map<String, String> params = new HashMap<String,String>();
    	params.put("access_token", mChannelUserInfo.getToken());
    	params.put("fields", "id,name,avatar,sex,area");
    	HyLog.d(TAG, "登录验证地址: "+url+"?token="+mChannelUserInfo.getToken()+"&fields=id,name,avatar,sex,area");
        mHttpUtils.doPost(mActivity, url, params,
                new UrlRequestCallBack()
                {

                    @Override
                    public void urlRequestStart(CallBackResult result)
                    {
                        HyLog.d(TAG, "QiHoo_GetUserInfo-->urlRequestStart");
                    }

                    @Override
                    public void urlRequestException(CallBackResult result)
                    {
                        HyLog.d(TAG, "QiHoo_GetUserInfo-->urlRequestException");

                        doAfter.afterFailed(result.backString, null);
                    }

                    @Override
                    public void urlRequestEnd(CallBackResult result)
                    {
                        HyLog.d(TAG, "QiHoo_GetUserInfo-->result.obj:"+result.obj);

                        try
                        {
       
                               JSONObject json=new JSONObject( result.obj.toString());
                               String userId=json.getString("id");
                               String userName=json.getString("name");
                               if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(userName)){
                               mChannelUserInfo.setChannelUserId(userId);
                               mChannelUserInfo.setChannelUserName(userName);;
                               doAfter.afterSuccess(result.backString);
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
