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

import com.google.gson.JsonArray;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.HyLog;

public class CC_Config {
	private static final String TAG = "HY";
	private static CC_Config xmConfig;
	private Map<String, String> map = new HashMap<String, String>();

	public static CC_Config getInstance(Context context) {
		if (xmConfig == null) {
			synchronized (CC_Config.class) {
				if (xmConfig == null) {
					xmConfig = new CC_Config(context);
				}
			}
		}
		return xmConfig;
	}

	private CC_Config(Context context) {
		try {
			InputStream is = context.getAssets().open("cc_pay.json");
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
			HyLog.d(TAG, "assets下已经存在渠道参数配置的文件：" + "cc_pay.json");

		} catch (IOException e) {
			HyLog.d(TAG, "打包工具没有将信息 " + "cc_pay.json" + "配置在assets下");
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
			InputStream is = context.getAssets().open("cc_pay.json");
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
			HyLog.d(TAG, "cc_pay.json:" + arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
}
