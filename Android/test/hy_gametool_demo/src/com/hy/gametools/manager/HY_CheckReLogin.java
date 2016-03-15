package com.hy.gametools.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.UrlRequestCallBack;

import android.app.Activity;

/*******************************************
 * @CLASS:HYCheckReLogin
 * @AUTHOR:smile
 *******************************************/
public class HY_CheckReLogin
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;

    public HY_CheckReLogin(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void checkLogin(HY_User mWXUser, final CheckAfter<String> doAfter)
    {
    	Map<String, String> params = new HashMap<String,String>();
    	params.put("Token", mWXUser.getToken());
    	params.put("UserId", mWXUser.getUserId());
    	HyLog.d(TAG, "验证登录参数:"+params);
    	HyLog.d(TAG, "登录验证地址: "+Constants.URL_CHECKLOGIN+"?Token="+mWXUser.getToken()+"&UserId="+mWXUser.getUserId());
        mHttpUtils.doPost(mActivity, Constants.URL_CHECKLOGIN, params,
                new UrlRequestCallBack()
                {

                    @Override
                    public void urlRequestStart(CallBackResult result)
                    {
                        HyLog.d(TAG, "HY_CheckReLogin-->urlRequestStart");
                    }

                    @Override
                    public void urlRequestException(CallBackResult result)
                    {
                        HyLog.d(TAG, "HY_CheckReLogin-->urlRequestException");

                        doAfter.afterFailed(result.backString, null);
                    }

                    @Override
                    public void urlRequestEnd(CallBackResult result)
                    {
                        HyLog.d(TAG, "HY_CheckReLogin-->result.obj:"+result.obj);

                        try
                        {
       
                               JSONObject json=new JSONObject( result.obj.toString());
                               String resCode=json.getString("Code");
                                // 登录接口
                                if (resCode.equals("0"))
                                {
                                    doAfter.afterSuccess(result.backString);
                                    HyLog.d(TAG, "afterSuccess："
                                            + result.backString);

                                }
                                else
                                {
                                    doAfter.afterFailed(result.backString, null);
                                    HyLog.d(TAG, "afterFailed："
                                            + result.backString+",resCode:"+resCode);
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
