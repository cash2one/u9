package com.example.test.demo;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;

import com.hy.gametools.manager.HY_SplashActivity;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.HyLog;

public class U9SplashMain extends HY_SplashActivity
{
	private String splash_color = "";
	private String TAG = "HY";
    @Override
    public int getBackgroundColor()
    {
    	HyLog.isDebug = true;
    	//从AndroidManifest中获取渠道颜色配置
    	splash_color = HY_Utils.getManifestMeta(this,"HY_GAME_COLOR");
    	HyLog.d(TAG, splash_color);
    	if(!TextUtils.isEmpty(splash_color)){
    		
    		return Color.parseColor(splash_color);
    	}else{
    		 return Color.WHITE;
    	}
       
    }

    @Override
    public void onSplashStop()
    {
    	//闪屏播放结束，跳转游戏主Activity，并结束闪屏Activity
    	Intent intent = new Intent(this, SanguoCardMain.class);
        startActivity(intent);
        this.finish();
    }
}
