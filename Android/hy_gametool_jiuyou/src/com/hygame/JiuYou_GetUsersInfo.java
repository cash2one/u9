package com.hygame;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

import android.app.Activity;
import android.text.TextUtils;

/*******************************************
 * @CLASS:HYCheckReLogin
 * @AUTHOR:smile
 *******************************************/
public class JiuYou_GetUsersInfo {
	protected static final String TAG = "HY";
	private Activity mActivity;
	private JiuYou_HttpUtils mHttpUtils;
	private String url = "http://sdk.g.uc.cn/cp/account.verifySession";

	public JiuYou_GetUsersInfo(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		mHttpUtils = new JiuYou_HttpUtils();
	}

	public void getUserInfo(Activity paramActivity,final HY_UserInfoVo mChannelUserInfo, final CheckAfter<String> doAfter)
    {
    	String appkey = HY_Utils.getManifestMeta(paramActivity, "UC_APPKEY");
    	String gameId = HY_Utils.getManifestMeta(paramActivity, "UC_GAME_ID");
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
    	String time = dateFormat.format(new Date());
    	 time = time.substring(0, 10);
    	 String sign = HY_Utils.getMD5Hex("sid="+mChannelUserInfo.getToken()+appkey);
    	JSONObject json = new JSONObject();
    	JSONObject data = new JSONObject();
    	JSONObject game = new JSONObject();
     	try {
     		data.put("sid",mChannelUserInfo.getToken());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	try {
    		game.put("gameId",gameId);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	
    	
    	try {
			json.put("id", Long.valueOf(time));
			json.put("data", data);
	    	json.put("game", game);
	    	json.put("sign", sign);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    
    	HyLog.d(TAG, "请求参数:"+json.toString());

//    	url = url+"?appkey="+appkey+"&token="+mChannelUserInfo.getToken();
    	HyLog.d(TAG, "登录验证地址: "+url+"?");
        mHttpUtils.doPost(mActivity, url, json,
                new UrlRequestCallBack()
                {

                    @Override
                    public void urlRequestStart(CallBackResult result)
                    {
                        HyLog.d(TAG, "JiuYou_GetUsersInfo-->urlRequestStart");
                    }

                    @Override
                    public void urlRequestException(CallBackResult result)
                    {
                        HyLog.d(TAG, "JiuYou_GetUsersInfo-->urlRequestException");

                        doAfter.afterFailed(result.backString, null);
                    }

                    @Override
                    public void urlRequestEnd(CallBackResult result)
                    {
                        HyLog.d(TAG, "JiuYou_GetUsersInfo-->result.obj:"+result.obj);

                        try
                        {
       
                               JSONObject json=new JSONObject( result.obj.toString());
                               JSONObject state = json.getJSONObject("state");
                               int code = state.getInt("code");
                               if(code == 1){
                            	   JSONObject data = json.getJSONObject("data");
                            	   String uid = data.getString("accountId");
                            	   String nickName = data.getString("nickName");
                            	   if(!TextUtils.isEmpty(uid)){
                            		   mChannelUserInfo.setChannelUserId(uid);
                            		   if(!TextUtils.isEmpty(nickName)){
                                		   mChannelUserInfo.setChannelUserName(nickName);
                                	   }else{
                                		   mChannelUserInfo.setChannelUserName(uid);
                                	   }
                            		   HyLog.d(TAG, "uc_uid:"+uid);
                            		   HyLog.d(TAG, "uc_nickName:"+nickName);
                            		   doAfter.afterSuccess(result.backString);
                            	   }else{
                            		   ToastUtils.show(mActivity, "用户信息获取失败");
                            	   }
                               }else{
                            	   ToastUtils.show(mActivity, "用户信息获取失败");
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
