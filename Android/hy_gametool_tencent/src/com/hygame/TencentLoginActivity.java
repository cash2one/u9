package com.hygame;

import java.io.InputStream;

import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.HyLog;
import com.tencent.open.utils.Util;
import com.tencent.tmgp.mhqxz.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;

import android.widget.LinearLayout;

public class TencentLoginActivity extends Activity {

	private Button mBtnQQ;
	private Button mBtnWX;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(HY_Utils.getId("hygame_tencent_login", "layout", this));
		
		int qqResId = HY_Utils.getId("btn_qq", "id", this);
		mBtnQQ = (Button) findViewById(qqResId);
		
		int wxResId = HY_Utils.getId("btn_wx", "id", this);
		mBtnWX = (Button) findViewById(wxResId);

		initUI();
		initListener();
	}

	private void initUI() {
		
		
		LinearLayout LltRoot = (LinearLayout) findViewById(HY_Utils.getId("root", "id",this));
		LltRoot.setBackgroundResource(HY_Utils.getId("login_bg", "drawable",this));
		
		//mBtnQQ.setBackground(new BitmapDrawable(this.getResources(),"assets/bg.png"));
		//mBtnQQ.setBackgroundResource(R.drawable.bg);
		
		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		LinearLayout LltBottom = (LinearLayout) findViewById(HY_Utils.getId("lltBottom", "id",this));
		LltBottom.setOrientation(mConfiguration.orientation);
		
		android.view.ViewGroup.MarginLayoutParams mlp = (android.view.ViewGroup.MarginLayoutParams) mBtnWX.getLayoutParams();
		
		if(mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE){		
			mlp.rightMargin = 30;
		}else if(mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT){
			mlp.bottomMargin = 30;			
		}
	}

	private void initListener() {
		
		mBtnQQ.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
		mBtnWX.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
	}
}