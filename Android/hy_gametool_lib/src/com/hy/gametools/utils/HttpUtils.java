package com.hy.gametools.utils;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.hy.game.volley.RequestQueue;
import com.hy.game.volley.Response;
import com.hy.game.volley.VolleyError;
import com.hy.game.volley.Request.Method;
import com.hy.game.volley.toolbox.Volley;
import com.hy.gametools.manager.HY_Constants;
import com.hy.gametools.manager.HY_PayParams;



/**
 * 
 *  类名称：HttpUtils 类描述： 网络交互帮助类 创建人：smile
 * 
 * @version
 * 
 */
public class HttpUtils
{

    private static final String TAG = "HY";

    private UrlRequestCallBack urlRequestCallBack = null;

    private CallBackResult result;
    
    private Context mContext;

    private ResultJsonParse parse;
    
    private String params;
 
    private RequestQueue mQueue;
    /**
     * json的post网络访问 .IJsonParse参数为解析器，若传null，则返回的字符串会直接显示在backString,且obj为null
     * */
    public void doPost(Context context, String url, Map<String, String> params,
            UrlRequestCallBack callback, ResultJsonParse parse)
    {
        mContext=context;
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        this.urlRequestCallBack = callback;
        this.parse = parse;
        this.params = appendParameter(url,params);
        // 连接前
        if (urlRequestCallBack != null)
        {
            result = new CallBackResult();
            result.tag = 0;
            result.url = url;
            result.param = params.toString();
            urlRequestCallBack.urlRequestStart(result);
           	HyLog.d(TAG, "请求数据:"+params.toString());
        }
        try
        {
            // 开始网络交互
            MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest(
            		Method.POST,url, this.params, listener, errorlistener);
            mQueue = Volley.newRequestQueue(context);
            mQueue.add(jsonObjectRequest);
            mQueue.start();  
        }
        catch (Exception e)
        {
           ToastUtils.show(mContext, "网络交互异常doPost");
        }
       
    }

    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>()
    {

        @Override
        public void onResponse(JSONObject arg0)
        {
            if (urlRequestCallBack != null)
            {
                if (parse != null)
                {
                    ResponseResultVO rrv = parse.parseJesonByUrl(arg0
                            .toString());
                    result.obj = rrv;
                }
                else
                {
                    result.obj = arg0.toString();
                }
                result.backString = arg0.toString();
                HyLog.d(TAG, "onResponse-->result.backString="
                        + result.backString);
                urlRequestCallBack.urlRequestEnd(result);
            }else{
                ToastUtils.show(mContext, "网络请求回调为空");
            }
        }
    };

    Response.ErrorListener errorlistener = new Response.ErrorListener()
    {

        @Override
        public void onErrorResponse(VolleyError error)
        {
            if (urlRequestCallBack != null)
            {
                // 连接出错
                if (urlRequestCallBack != null)
                {
                    result.tag = -1;
                    result.backString = error.getMessage();
                    HyLog.d(TAG, "onErrorResponse-->result.backString="
                            + result.backString);
                    urlRequestCallBack.urlRequestException(result);
                }
            }
        }
    };

    /** 判断网络是否可用 */
    public static boolean isNetworkAvaiable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo(); // 获取代表联网状态的NetWorkInfo对象
        return (info != null && info.isConnected());
    }

    /** 判断是否wifi环境 */
   
    /**
     * createLoginRequest 生成登录状态的请求json参数
     * 
     * @param accessToken
     *            渠道返回的Token
     * @param channelUserId
     *            渠道用户ID,如果登录后，渠道不返回该用户id，传空字符串，如""，千万不要传null
     * @return
     */
//    @SuppressLint("SimpleDateFormat")
//    public static JsonGenerator getLoginInfoRequest(
//            HY_UserInfoVo mUserInfoVo)
//    {
//        JsonGenerator json1 = new JsonGenerator();
//        json1.add("channelId", HY_Constants.HY_channelCode);// 渠道code
//        json1.add("isDebug", Constants.isDebug);// 相对于渠道的调试模式
//        json1.add("token", mUserInfoVo.getToken());//accessToken);
//        json1.add("productId", HY_Constants.HY_gameId);
//        json1.add("channelUserId", mUserInfoVo.getChannelUserId());
//        json1.add("channelUserName", mUserInfoVo.getChannelUserName());
//        
//        return json1;
//    }
    
    @SuppressLint("SimpleDateFormat")
    public static Map<String, String> getLoginInfoRequest(
            HY_UserInfoVo mUserInfoVo)
    {
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("ChannelId", HY_Constants.HY_channelCode);
    	map.put("IsDebug", Constants.isDebug);
    	map.put("Token", mUserInfoVo.getToken());
    	map.put("ProductId", HY_Constants.HY_gameId);
    	map.put("ChannelUserId", mUserInfoVo.getChannelUserId());
    	map.put("ChannelUserName", mUserInfoVo.getChannelUserName());  
//    	HyLog.d(TAG, "登录请求信息: "+Constants.URL_LOGIN+"?ChannelId="+HY_Constants.HY_channelCode+
//    			"&IsDebug="+Constants.isDebug+"&Token="+mUserInfoVo.getToken()+"&ProductId="+
//    			HY_Constants.HY_gameId+"&ChannelUserId="+mUserInfoVo.getChannelUserId()+
//    			"&ChannelUserName="+mUserInfoVo.getChannelUserName());
    	return map;
    }
    
    @SuppressLint("SimpleDateFormat")
	public static Map<String, String> getPayInfoRequest(HY_PayParams mPayParsms,
            HY_UserInfoVo mUserInfoVo)
    {
    	
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("ChannelId", HY_Constants.HY_channelCode);
    	map.put("UserId", mUserInfoVo.getUserId());
    	map.put("ProductId", HY_Constants.HY_gameId);
    	map.put("ProductOrderId", mPayParsms.getGameOrderId());
    	map.put("Amount", mPayParsms.getAmount()+"");
    	map.put("CallbackUrl", mPayParsms.getCallBackUrl());
    	map.put("AppExt", mPayParsms.getAppExtInfo());
//    	HyLog.d(TAG, "支付请求信息: "+Constants.URL_PAY+"?ChannelId="+HY_Constants.HY_channelCode+
//    			"&UserId="+mUserInfoVo.getUserId()+"&ProductId="+HY_Constants.HY_gameId+"&ProductOrderId="+
//    			mPayParsms.getGameOrderId()+"&Amount="+mPayParsms.getAmount()+
//    			"&CallbackUrl="+mPayParsms.getCallBackUrl());
    	
        return map;
    }
    
    private String appendParameter(String url,Map<String,String> params){  
        Uri uri = Uri.parse(url);  
        Uri.Builder builder = uri.buildUpon();  
        for(Map.Entry<String,String> entry:params.entrySet()){  
            builder.appendQueryParameter(entry.getKey(),entry.getValue());  
        }  
        return builder.build().getQuery();  
    }  
}
