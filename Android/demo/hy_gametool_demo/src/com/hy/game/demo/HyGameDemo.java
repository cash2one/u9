package com.hy.game.demo;

import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HyLog;
import com.test.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HyGameDemo extends Activity implements OnClickListener {
	private String TAG = "HY";
	private String version ="v1.0";
	private Button lbtn1;
	private Button lbtn2;
	private Button lbtn3;
	private Button sbtn1;
	private Button sbtn2;
	private Button sbtn3;
	private Button pbtn1;
	private Button pbtn2;
	private Button pbtn3;
	private TextView version_title;
	private TextView demo_title;
	private TextView pay_name;
	private boolean mIsLandscape;
	private HY_PayParams payParams = new HY_PayParams();
	private HY_UserInfoVo mUserInfoVo = new HY_UserInfoVo();
	private Demo_UserInfo userInfo = new Demo_UserInfo();

	@Override
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
		setContentView(R.layout.hy_demo_activity);

		Intent intent = this.getIntent();
		int type = intent.getIntExtra("type", 0);
		if (type == 2) {
			payParams = HY_GameManager.getPayParams();
			mUserInfoVo = HY_GameManager.getUserInfoVo();
		}
		initView(type);

	}

	public void initView(int type) {
		demo_title = (TextView) findViewById(R.id.demo_title);
		pay_name = (TextView) findViewById(R.id.pay_name);
		version_title = (TextView) findViewById(R.id.version_title);
		version_title.setText("version:"+version);
		switch (type) {
		case 0:
			try{
			lbtn1 = (Button) findViewById(R.id.hy_btn1);
			lbtn2 = (Button) findViewById(R.id.hy_btn2);
			lbtn3 = (Button) findViewById(R.id.hy_btn3);
			lbtn1.setOnClickListener(this);
			lbtn2.setOnClickListener(this);
			lbtn3.setOnClickListener(this);
			demo_title.setText("模拟登录界面");
			pay_name.setText("");
			}catch(Exception e){
				Toast.makeText(this, "初始化界面失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case 1:
			demo_title.setText("模拟悬浮小球");
			sbtn1 = (Button) findViewById(R.id.hy_btn1);
			sbtn2 = (Button) findViewById(R.id.hy_btn2);
			sbtn3 = (Button) findViewById(R.id.hy_btn3);
			sbtn1.setGravity(Gravity.CENTER);
			sbtn2.setGravity(Gravity.CENTER);
			sbtn1.setOnClickListener(this);
			sbtn2.setOnClickListener(this);
			sbtn1.setText("切换账号");
			sbtn2.setText("注销账号");
			sbtn3.setVisibility(View.GONE);
			pay_name.setVisibility(View.GONE);
			break;
		case 2:
			demo_title.setText("模拟支付界面");
			pay_name.setText("商品名称:" + payParams.getProductName() + "\n充值金额:"
					+ payParams.getAmount() + "分" + "\n支付回调地址:"
					+ payParams.getCallBackUrl() +"\nU9订单号:" 
					+ payParams.getOrderId()+"\n游戏订单号:"
					+ payParams.getGameOrderId() + "\n商品id:"
					+ payParams.getProductId() + "\n拓展回传信息:"
					+ payParams.getAppExtInfo());
			pbtn1 = (Button) findViewById(R.id.hy_btn1);
			pbtn2 = (Button) findViewById(R.id.hy_btn2);
			pbtn3 = (Button) findViewById(R.id.hy_btn3);
			pbtn1.setOnClickListener(this);
			pbtn2.setOnClickListener(this);
			pbtn3.setOnClickListener(this);
			pbtn1.setText("支付成功");
			pbtn2.setText("支付失败");
			pbtn3.setText("支付取消");
			break;
		}
	}

	@Override
	public void onClick(View v) {

		if (lbtn1 == v) {
			userInfo.setUserId("test10086001");
			userInfo.setUserName("测试用户1");
			userInfo.setToken("abcd12341");
			try {
				HY_GameManager.logCallback(userInfo);
			} catch (Exception e) {
				HyLog.e(TAG, "回调获取异常");
			}
			HyGameDemo.this.finish();
		}
		if (lbtn2 == v) {
			userInfo.setUserId("test10086002");
			userInfo.setUserName("测试用户2");
			userInfo.setToken("abcd12342");
			try {
				HY_GameManager.logCallback(userInfo);
			} catch (Exception e) {
				HyLog.e(TAG, "回调获取异常");
			}
			HyGameDemo.this.finish();
		}
		if (lbtn3 == v) {
			userInfo.setUserId("test10086003");
			userInfo.setUserName("测试用户3");
			userInfo.setToken("abcd12343");
			try {
				HY_GameManager.logCallback(userInfo);
			} catch (Exception e) {
				HyLog.e(TAG, "回调获取异常");
			}
			HyGameDemo.this.finish();
		}

		if (sbtn1 == v) {
			userInfo.setUserId("test10086004");
			userInfo.setUserName("测试用户4");
			userInfo.setToken("abcd12344");
			try {
				HY_GameManager.switchCallback(userInfo);
			} catch (Exception e) {
				HyLog.e(TAG, "悬浮小球切换账号回调获取异常");
			}
			HyGameDemo.this.finish();
		}
		if (sbtn2 == v) {
			userInfo = null;
			try {
				HY_GameManager.logoutCallback();
			} catch (Exception e) {
				HyLog.e(TAG, "悬浮小球注销回调获取异常");
			}
			HyGameDemo.this.finish();
		}
		if (pbtn1 == v) {
			HY_GameManager.payCallback(0);
			this.finish();
		}
		if (pbtn2 == v) {
			HY_GameManager.payCallback(1);
			this.finish();
		}
		if (pbtn3 == v) {
			HY_GameManager.payCallback(2);
			this.finish();
		}
	}
}