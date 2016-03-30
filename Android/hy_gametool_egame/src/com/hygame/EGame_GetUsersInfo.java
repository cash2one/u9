package com.hygame;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.UrlRequestCallBack;
import android.app.Activity;
import android.text.TextUtils;


public class EGame_GetUsersInfo
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;
    private String url = "https://open.play.cn/oauth/token";

    public EGame_GetUsersInfo(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void getUserInfo(Activity paramActivity,final HY_UserInfoVo mChannelUserInfo , final CheckAfter<String> doAfter)
    {
    	String clientId = HY_Utils.getManifestMeta(paramActivity, "EGAME_CLIENT_ID");
    	String client_secret = HY_Utils.getManifestMeta(paramActivity, "EGAME_CLIENT_SECRET");
    	String sdk_version = "v2.1.0";
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS"
				+ HY_Utils.createRandomText());
		String time = dateFormat.format(new Date());
		String timestamp = time.subSequence(0, 13).toString();
		String code = mChannelUserInfo.getToken();
		HyLog.d(TAG, "tiem:"+timestamp);
    	String sign_sort = "";
		try {
			sign_sort = java.net.URLEncoder.encode("client_id&sign_method&version&timestamp&client_secret", "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	String signature = HY_Utils.getMD5Hex(clientId + "MD5" + sdk_version +
    			timestamp + client_secret);
    	HyLog.d(TAG, "MD5:"+sign_sort);
    	url =url+"?client_id="+clientId+"&client_secret="+client_secret+"&code="+code+"&grant_type=authorization_code&sign_method=MD5"
    	+"&version="+sdk_version+"&timestamp="+timestamp+"&client_secret="+client_secret+"&sign_sort="+sign_sort+"&signature="+signature;
    	Map<String, String> params = new HashMap<String,String>();
    	params.put("Content-Type", "application/x-www-form-urlencoded");
//    	params.put("client_id", clientId);
//    	params.put("client_secret  ",client_secret);
//    	params.put("code", mChannelUserInfo.getToken());
//    	params.put("grant_type", "authorization_code");
//    	params.put("sign_method", "MD5");
//    	params.put("version", sdk_version);
//    	params.put("timestamp", timestamp);
//    	params.put("client_secret", client_secret);
//    	params.put("sign_sort", sign_sort);
//    	params.put("signature", signature);
    	
    	HyLog.d(TAG, "登录验证地址: "+url);
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
                               String userId=json.getString("user_id");
                               String token = json.getString("access_token");
                               String refres_token = json.getString("refresh_token");
                               if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(token)){
                            	   mChannelUserInfo.setChannelUserId(userId);
                            	   mChannelUserInfo.setChannelUserName(userId);;
                            	   mChannelUserInfo.setToken(token);
                            	   HyLog.d(TAG, "userId:"+userId);
                            	   HyLog.d(TAG, "token:"+token);
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
