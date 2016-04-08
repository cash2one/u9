package com.hy.game.demo;

import com.hy.gametools.manager.HY_Utils;
import com.test.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class FloatActivity extends Activity {
	private boolean mIsLandscape;
	private Button lbtn1;
	private Button lbtn2;
	private Button lbtn3;
	private TextView demo_title;
	private TextView pay_name;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		mIsLandscape = HY_GameManager.getmIsLandscape();
		if (mIsLandscape) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(getId("hy_demo_activity","layout"));
		initView();
	}

	public void initView() {
		lbtn1 = (Button) findViewById(getId("hy_btn1","id"));
		lbtn2 = (Button) findViewById(getId("hy_btn2","id"));
		lbtn3 = (Button) findViewById(getId("hy_btn3","id"));
		demo_title = (TextView) findViewById(getId("demo_title","id"));
		pay_name = (TextView) findViewById(getId("demo_title","id"));
		lbtn1.setText("切换账号");
		lbtn2.setText("注销账号");
		lbtn3.setVisibility(View.GONE);
		demo_title.setText("悬浮小球模拟页面");
		pay_name.setVisibility(View.GONE);
		lbtn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 切换账号
				Intent intent1 = new Intent(FloatActivity.this,
						HyGameDemo.class);
				intent1.putExtra("type", 1);
				FloatActivity.this.startActivity(intent1);
				FloatActivity.this.finish();

			}
		});
		lbtn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				HY_GameManager.logoutCallback();

			}
		});
	}

	private int getId(String id, String type) {
		String packageName = HY_Utils.getPackageName(this);
		return this.getResources().getIdentifier(id, type, packageName);
	}
}