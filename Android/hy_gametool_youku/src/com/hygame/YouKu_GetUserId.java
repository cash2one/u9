package com.hygame;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import android.app.Activity;

import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

public class YouKu_GetUserId {
	private String URL = "http://sdk.api.gamex.mobile.youku.com/game/user/infomation";
	private String TAG = "HY";
	private Activity mActivity;
	private HttpUtils mHttpUtils;

	public YouKu_GetUserId(Activity mActivity) {
		super();
		this.mActivity = mActivity;
		mHttpUtils = new HttpUtils();
	}

	public void getUserId(Activity paramActivity,
			final HY_UserInfoVo mChannelUserInfo,
			final CheckAfter<String> doAfter) {
		String appkey = HY_Utils
				.getManifestMeta(paramActivity, "YKGAME_APPKEY");
		String key = HY_Utils.getManifestMeta(paramActivity, "YKGAME_PAYKEY");
		
		String sign = "appkey=" + appkey + "&sessionid="
				+ mChannelUserInfo.getToken();
		try {
			sign = HmacMd5.byteArrayToHexString(HmacMd5.encryptHMAC(sign.getBytes(), key));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("sessionid", mChannelUserInfo.getToken());
		params.put("appkey", appkey);
		params.put("sign", sign);

//		URL = URL + "?appkey=" + appkey + "&token="
//				+ mChannelUserInfo.getToken();
		HyLog.d(TAG, "登录验证地址: " + URL + "?appkey=" + appkey + "&sessionid="
				+ mChannelUserInfo.getToken() + "&sign=" + sign);
		mHttpUtils.doPost(paramActivity, URL, params, new UrlRequestCallBack() {

			@Override
			public void urlRequestStart(CallBackResult result) {
				HyLog.d(TAG, "QiHoo_GetUserInfo-->urlRequestStart");
			}

			@Override
			public void urlRequestException(CallBackResult result) {
				HyLog.d(TAG, "QiHoo_GetUserInfo-->urlRequestException");

				doAfter.afterFailed(result.backString, null);
			}

			@Override
			public void urlRequestEnd(CallBackResult result) {
				HyLog.d(TAG, "QiHoo_GetUserInfo-->result.obj:" + result.obj);

				try {

					JSONObject json = new JSONObject(result.obj.toString());
					String status = json.getString("status");
					HyLog.d(TAG, status);
					if ("success".equals(status)) {
						String userId = json.getString("uid");
						mChannelUserInfo.setChannelUserId(userId);
						doAfter.afterSuccess(result.backString);
					}else{
						ToastUtils.show(mActivity, "用户信息获取失败");
					}

				} catch (Exception e) {
					doAfter.afterFailed(result.backString, e);
					HyLog.d(TAG, "Exception-->afterFailed：" + result.backString);

				}

			}
		}, null);
	}

}


class HmacMd5{
	public static final String KEY_MAC = "HmacMD5";

    /**
     * HMAC加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);

        return mac.doFinal(data);

    }

    /*byte数组转换为HexString*/
    public static String byteArrayToHexString(byte[] b) {
       StringBuffer sb = new StringBuffer(b.length * 2);
       for (int i = 0; i < b.length; i++) {
         int v = b[i] & 0xff;
         if (v < 16) {
           sb.append('0');
         }
         sb.append(Integer.toHexString(v));
       }
       return sb.toString();
     }
}