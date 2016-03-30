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
import com.hy.gametools.utils.UrlRequestCallBack;

import android.app.Activity;
import android.text.TextUtils;

/*******************************************
 * @CLASS:HYCheckReLogin
 * @AUTHOR:smile
 *******************************************/
public class MuZhiWan_GetUsersInfo
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;
    private String url = " http://sdk.muzhiwan.com/oauth2/getuser.php";

    public MuZhiWan_GetUsersInfo(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void getUserInfo(Activity paramActivity,final HY_UserInfoVo mChannelUserInfo, final CheckAfter<String> doAfter)
    {
    	String appkey = HY_Utils.getManifestMeta(paramActivity, "MZWAPPKEY");

    	Map<String, String> params = new HashMap<String,String>();
    	params.put("token", mChannelUserInfo.getToken());
    	params.put("appkey",appkey);

    	url = url+"?appkey="+appkey+"&token="+mChannelUserInfo.getToken();
    	HyLog.d(TAG, "登录验证地址: "+url+"?"+params.toString());
//    	mHttpUtils.setPost(1);
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
                               String user=json.getString("user");
                               JSONObject json1 = new JSONObject(user);
                               String userName = json1.getString("username");
                               String userId = json1.getString("uid");
                              if(!TextUtils.isEmpty(userId)) {
                            	  mChannelUserInfo.setChannelUserId(userId);
                              	}
                               if(!TextUtils.isEmpty(userName)){
                            	   mChannelUserInfo.setChannelUserName(userName);
                               }else{
                            	   mChannelUserInfo.setChannelUserName(userId);
                               }
//                              	mHttpUtils.setPost(0);
                               doAfter.afterSuccess(result.backString);
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
