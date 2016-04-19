package com.hygame;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.UrlRequestCallBack;
import android.app.Activity;


public class KaoPu_CheckUsersInfo
{
    protected static final String TAG = "HY";
    private Activity mActivity;
    private HttpUtils mHttpUtils;

    public KaoPu_CheckUsersInfo(Activity mActivity)
    {
        super();
        this.mActivity = mActivity;
        mHttpUtils = new HttpUtils();
    }

    public void checkUserInfo(Activity paramActivity,String url , final CheckAfter<String> doAfter)
    {
    	Map<String, String> params = new HashMap<String,String>();
    	HyLog.d(TAG, "登录验证地址: "+url);
        mHttpUtils.doPost(mActivity, url, params,
                new UrlRequestCallBack()
                {

                    @Override
                    public void urlRequestStart(CallBackResult result)
                    {
                        HyLog.d(TAG, "Kaopu_CheckUserInfo-->urlRequestStart");
                    }

                    @Override
                    public void urlRequestException(CallBackResult result)
                    {
                        HyLog.d(TAG, "Kaopu_CheckUserInfo-->urlRequestException");

                        doAfter.afterFailed(result.backString, null);
                    }

                    @Override
                    public void urlRequestEnd(CallBackResult result)
                    {
                        HyLog.d(TAG, "Kaopu_CheckUserInfo-->result.obj:"+result.obj);

                        try
                        {
       
                               JSONObject json=new JSONObject( result.obj.toString());
                               String code=json.getString("code");
                               HyLog.d(TAG, "code:"+code);
                               if (code =="1"){
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
