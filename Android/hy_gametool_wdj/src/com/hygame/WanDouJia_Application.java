package com.hygame;

import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.HyLog;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;

import android.app.Application;
import android.content.Context;

public class WanDouJia_Application extends Application {
	private String TAG = "HY";
	private static long APP_KEY = 100008237;
	private static String SECURITY_KEY = "10159606448b775c8de9d0e79a4bfff3";

	private static WandouGamesApi wandouGamesApi;

	public static WandouGamesApi getWandouGamesApi() {
		return wandouGamesApi;
	}

	@Override
	public void onCreate() {
		APP_KEY = Long.parseLong(HY_Utils.getManifestMeta(this, "WDJ_APP_KEY"));
		SECURITY_KEY = HY_Utils.getManifestMeta(this, "WDJ_SECURITY_KEY");
		HyLog.d(TAG, "APP_KEY:" + APP_KEY);
		HyLog.d(TAG, "SECURITY_KEY:" + SECURITY_KEY);
		wandouGamesApi = new WandouGamesApi.Builder(this, APP_KEY, SECURITY_KEY)
				.create();
		wandouGamesApi.setLogEnabled(true);
		super.onCreate();
	}
}