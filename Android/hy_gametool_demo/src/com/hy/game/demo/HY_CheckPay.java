package com.hy.game.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.manager.HY_User;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.UrlRequestCallBack;

public class HY_CheckPay {
	protected static final String TAG = "HY";
	private Activity mActivity;
	private HttpUtils mHttpUtils;

	public HY_CheckPay(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		mHttpUtils = new HttpUtils();
	}

	public void checkPayInfo(HY_PayParams mPayParams,String channelUserId,int result) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat(
	                "yyyyMMddHHmmssSSS");
	    String time = dateFormat.format(new Date());
	    String orderTime = time.substring(0, 14);
		String order="test"+time; //模拟渠道订单号
		String money=mPayParams.getAmount()/100+"";//单位:分
		String signConstan = "order="+order+"&money="+money+"&mid="+
		channelUserId+"&time="+orderTime+"&result="+result+"&ext="+mPayParams.getOrderId()+"&key=test";
		String md5 = HY_Utils.getMD5Hex(signConstan);
		String gameId = "1000";
		String channleCode = "100";
		if(!TextUtils.isEmpty(HY_Utils.getHYGameId(mActivity))){
			 gameId = HY_Utils.getHYGameId(mActivity);
		}
		if(!TextUtils.isEmpty(HY_Utils.getHYChannelCode(mActivity))){
			channleCode = HY_Utils.getHYChannelCode(mActivity);
		}
		String url = Constants.URL_CHECKPAY+"/"+gameId+"/"+channleCode;
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("result", result+"");
		params.put("money",money);
		params.put("order", order);
		params.put("mid",channelUserId);
		params.put("time", orderTime);
		params.put("ext", mPayParams.getOrderId());
		params.put("signature", md5);
		HyLog.d(TAG, "支付回调地址: " +url + "?result="
				+ result + "&money=" + money+"&order="+order+"&mid="+
				channelUserId+"&time="+orderTime+"&ext="+mPayParams.getOrderId()+"&signature="+md5);
		
		mHttpUtils.doPost(mActivity,url , params,
				new UrlRequestCallBack() {

					@Override
					public void urlRequestStart(CallBackResult result) {
						HyLog.d(TAG, "HY_CheckRePay-->urlRequestStart:"+result);
					}

					@Override
					public void urlRequestException(CallBackResult result) {
						HyLog.d(TAG, "HY_CheckRePay-->urlRequestException:"+result);

					}

					@Override
					public void urlRequestEnd(CallBackResult result) {
						HyLog.d(TAG, "HY_CheckRePay-->result.obj:"
								+ result.obj);

						try {

							JSONObject json = new JSONObject(result.obj
									.toString());
							String resCode = json.getString("code");
							//成功
							if (resCode.equals("0")) {
								HyLog.d(TAG, "成功:"+ result.backString);

							} else {
								HyLog.d(TAG, "失败:" + result.backString
										+ ",resCode:" + resCode);
							}
						}

						catch (Exception e) {
							HyLog.d(TAG, "Exception-->afterFailed："
									+ result.backString);
						}

					}
				}, null);
	}
}
