package com.hygame;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.hy.gametools.utils.HyLog;

public class HY_GamePayConfig {
	private static final String TAG = "HY";
	private static HY_GamePayConfig xmConfig;
	private Map<String, String> map = new HashMap<String, String>();

	public static HY_GamePayConfig getInstance(Context context) {
		if (xmConfig == null) {
			synchronized (HY_GamePayConfig.class) {
				if (xmConfig == null) {
					xmConfig = new HY_GamePayConfig(context);
				}
			}
		}
		return xmConfig;
	}

	private HY_GamePayConfig(Context context) {
		try {
			InputStream is = context.getAssets().open("hygame_pay.json");
			InputStreamReader reader = new InputStreamReader(is);
			char[] buf = new char[1024];
			StringBuffer buffer = new StringBuffer();
			while (reader.read(buf) > 0) {
				buffer.append(buf);
			}
			is.close();
			JSONObject json = new JSONObject(buffer.toString());
			Iterator<?> iter = json.keys();
			String key = null;
			while (iter.hasNext()) {
				key = (String) iter.next();
				HyLog.d(TAG, key);
				this.map.put(key, json.get(key).toString());
			}
			HyLog.d(TAG, "assets下已经存在渠道参数配置的文件：" + "hygame_pay.json");

		} catch (IOException e) {
			HyLog.d(TAG, "打包工具没有将信息 " + "hygame_pay.json" + "配置在assets下");
			throw new IllegalStateException(e);
		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
	}

	public String get(String key) {
		return (String) this.map.get(key);
	}

	public JSONArray getKey(Context context) {
		JSONArray arr = null;
		try {
			InputStream is = context.getAssets().open("hygame_pay.json");
			InputStreamReader reader = new InputStreamReader(is);
			char[] buf = new char[1024];
			StringBuffer buffer = new StringBuffer();
			while (reader.read(buf) > 0) {
				buffer.append(buf);
			}
			is.close();
			JSONObject json = null;
			try {
				json = new JSONObject(buffer.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr = json.names();
			HyLog.d(TAG, "hygame_pay.json:" + arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
}
