package com.hygame;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hy.game.volley.Request.Method;
import com.hy.game.volley.RequestQueue;
import com.hy.game.volley.Response;
import com.hy.game.volley.VolleyError;
import com.hy.game.volley.toolbox.JsonObjectRequest;
import com.hy.game.volley.toolbox.Volley;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ResponseResultVO;
import com.hy.gametools.utils.ResultJsonParse;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

public class JiuYou_HttpUtils
{

    private static final String TAG = "WX";

    private UrlRequestCallBack urlRequestCallBack = null;

    private CallBackResult result;
    
    private Context mContext;

    private ResultJsonParse parse;
 
    private RequestQueue mQueue;
    
    /**
     * json的post网络访问 .IJsonParse参数为解析器，若传null，则返回的字符串会直接显示在backString,且obj为null
     * */
    public void doPost(Context context, String url, JSONObject params,
            UrlRequestCallBack callback, ResultJsonParse parse)
    {
        mContext=context;
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        this.urlRequestCallBack = callback;
        this.parse = parse;
        // 连接前
        if (urlRequestCallBack != null)
        {
            result = new CallBackResult();
            result.tag = 0;
            result.url = url;
            result.param = params.toString();
            urlRequestCallBack.urlRequestStart(result);
        }
        try
        {
            // 开始网络交互
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Method.POST, url, params, listener, errorlistener);
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
   
}
