package com.example.test.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.qihoo360.R;
import com.hy.gametools.manager.HY_Constants;
import com.hy.gametools.manager.HY_ExitCallback;
import com.hy.gametools.manager.HY_GameProxy;
import com.hy.gametools.manager.HY_GameRoleInfo;
import com.hy.gametools.manager.HY_LoginCallBack;
import com.hy.gametools.manager.HY_PayCallBack;
import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.manager.HY_SdkResult;
import com.hy.gametools.manager.HY_User;
import com.hy.gametools.manager.HY_UserListener;
import com.hy.gametools.utils.HyLog;

public class MainActivity extends Activity implements OnClickListener
{
    protected static final String TAG = "HY";
    private Button mLogonBtn;
    private Button mPayBtn;
    private Button mUpRoleBtn;
    private Button mLogoffBtn;
    private Button mExitBtn;
    private TextView mUserInfoTv;
    private HY_User mUser;
/**
 *    提示:demo的提示信息使用的是Toast,游戏接入的时候,请替换成游戏Toast.
 *    否则可能去与渠道Toast提示 冲突,导致崩溃
 **/
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        HY_GameProxy.getInstance().applicationInit(this,false);//true为横屏,false为竖屏
        HY_GameProxy.getInstance().onCreate(this);
        HY_GameProxy.getInstance().setUserListener(this,new HY_UserListener() {
			
			@Override
			public void onSwitchUser(HY_User user,int resultCode){
				switch (resultCode) {
				
				case HY_SdkResult.SUCCESS:
					mUser = user;
					//切换账号成功,回到游戏登录场景,并重置角色信息
					//如果切换统一角色,默认回传切换取消
					 HyLog.d(TAG, "切换账号成功");
					 mUserInfoTv.setText("userId: " + mUser.getUserId() + "\n渠道用户id:"
			                    + mUser.getChannelUserId() + "\n渠道用户名: " + mUser.getChannelUserName()
			                    + "\ntoken: " + mUser.getToken());
					 Toast.makeText(MainActivity.this, "切换账号成功", Toast.LENGTH_SHORT).show();
					break;
				case HY_SdkResult.CANCEL:
					//切换账号取消不做任何处理
					 HyLog.d(TAG, "切换账号取消");
					 Toast.makeText(MainActivity.this, "切换账号取消", Toast.LENGTH_SHORT).show();
					break;
				default:
					//切换账号失败不做任何处理
					 HyLog.d(TAG, "切换账号失败");
					 Toast.makeText(MainActivity.this, "切换账号失败", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
			
			@Override
			public void onLogout(int resultCode,Object paramObject) {

				 switch (resultCode) {
				 case HY_SdkResult.SUCCESS  :
						mUser = null;
						mUserInfoTv.setText("userId:\n渠道用户id:\n渠道用户名:\ntoken:\n");
				        HyLog.d(TAG,"注销成功");
				        Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_LONG).show();
				        break;

				default:
						Toast.makeText(MainActivity.this, "注销失败", Toast.LENGTH_LONG).show();
						HyLog.d(TAG,"注销失败");
						break;
				 }
			}
		});
        mLogonBtn = (Button) findViewById(R.id.login_button);
        mPayBtn = (Button) findViewById(R.id.pay_button);
        mLogoffBtn = (Button) findViewById(R.id.logout_button);
        mUpRoleBtn = (Button) findViewById(R.id.up_role_button);
        mExitBtn = (Button) findViewById(R.id.exit_button);
        mUserInfoTv = (TextView) findViewById(R.id.user_info_text);
        mLogonBtn.setOnClickListener(this);	
        mPayBtn.setOnClickListener(this);
        mUpRoleBtn.setOnClickListener(this);
        mLogoffBtn.setOnClickListener(this);
        mExitBtn.setOnClickListener(this);
        
    }

    @Override
    public void onStop()
    {
        super.onStop();
        HY_GameProxy.getInstance().onStop(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        HY_GameProxy.getInstance().onDestroy(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        HY_GameProxy.getInstance().onResume(this);
    }

    @Override
    public void onPause()
    {
	    HY_GameProxy.getInstance().onPause(this);
        super.onPause();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        HY_GameProxy.getInstance().onRestart(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        HY_GameProxy.getInstance().onActivityResult(this, requestCode,resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        doExit();
    }

    @Override
    public void onClick(View v)
    {
        if (mLogonBtn == v)
        {
        	doLogOn();
        }
        else if (mPayBtn == v)
        {
            doPay();
        }
        
        else if (mLogoffBtn == v)
        {
        	doLogOff();
        }
        else if (mExitBtn == v)
        {
            doExit();
        }
        else if (mUpRoleBtn == v){
        	doSetRoleData();
        }
    }
    
    private void doLogOn()
    {
    	
    	
        HY_GameProxy.getInstance().logon(MainActivity.this, new HY_LoginCallBack() {
			
			 @Override
		        public void onLoginSuccess(HY_User user)
		        {
		            	//登录
		            	
			            Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
			            mUser = user;
			            mUserInfoTv.setText("userId: " + mUser.getUserId() + "\n渠道用户id:"
			                    + mUser.getChannelUserId() + "\n渠道用户名: " + mUser.getChannelUserName()
			                    + "\ntoken: " + mUser.getToken());
			            // 登录成功后，进行登录信息校验，此步为必须完成操作，若不完成用户信息验证，平台拒绝提包()
		        }

		        @Override
		        public void onLoginFailed(int code ,String message)
		        {
		            
		            switch (code) {
					case HY_SdkResult.CANCEL:
						//取消登录
						Toast.makeText(MainActivity.this, "message:"+message, Toast.LENGTH_LONG).show();
						HyLog.d(TAG,"取消登录");
						break;

					default:
						//登录失败
						Toast.makeText(MainActivity.this, "message:"+message, Toast.LENGTH_LONG).show();
						HyLog.d(TAG,"登录失败:"+message);
						break;
					}
		            
		        }
		});
    }

    private void doPay()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        String time = dateFormat.format(new Date());
        HY_PayParams payParams = new HY_PayParams();
        payParams.setAmount(100);//充值金额
        payParams.setExchange(10);//兑换率
        payParams.setProductId("");//商品id
        payParams.setProductName("钻石");//商品名称
        payParams.setCallBackUrl("http://www.baidu.com");//回调地址
        payParams.setGameOrderId( "game"+time);//订单号
        payParams.setAppExtInfo("支付回调拓展字段");
        
        HY_GameProxy.getInstance().startPay(MainActivity.this, payParams, new HY_PayCallBack()
        {
            @Override
            public void onPayCallback(int retCode, String paramString)
            {
                //retCode 状态码： 0 支付成功， 1 支付失败，  2 支付取消，  3 支付进行中。
               switch (retCode) {
			case 0:
				 Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_LONG).show();
				break;
			case 2:
				 Toast.makeText(MainActivity.this, "支付取消", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(MainActivity.this, "支付进行中", Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_LONG).show();
				break;
               }
            }
        });
    }

    /**
     * 注销接口说明：
     * 
     * @param activity
     *            当前activity
     * @param customObject
     *            用户自定义参数，在登陆回调中原样返回
     */
    private void doLogOff()
    {
        HY_GameProxy.getInstance().logoff(MainActivity.this, "注销");
    }

    /**
     * 退出接口说明：
     * 
     * @param context
     *            当前activity
     * @param callback
     *            退出回调
     */
    private void doExit()
    {
        HY_GameProxy.getInstance().exit(this, new HY_ExitCallback()
        {

            @Override
            public void onGameExit()
            {
                // 渠道不存在退出界面，如百度移动游戏等，此时需在此处弹出游戏退出确认界面，否则会出现渠道审核不通过情况
               //退出确认窗口需要游戏自定义，并且实现资源回收，杀死进程等退出逻辑 
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setTitle("游戏自带退出界面");
                builder.setPositiveButton("退出",
                        new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                HY_GameProxy.getInstance().applicationDestroy(
                                        MainActivity.this);
                                MainActivity.this.finish();
                            }
                        });
                builder.show();
            }

            @Override
            public void onChannelExit()
            {
                // 渠道存在退出界面，此处只需进行退出游戏操作即可，无需再弹游戏退出界面；
              //退出确认窗口由渠道提供，游戏只须实现资源回收，杀死进程等退出逻辑
                Toast.makeText(MainActivity.this, "由渠道退出界面退出",
                        Toast.LENGTH_LONG).show();
                HY_GameProxy.getInstance()
                        .applicationDestroy(MainActivity.this);
                MainActivity.this.finish();

            }
        });
    }


    private void doSetRoleData()
    {
    	HY_GameRoleInfo gameRoleInfo =new HY_GameRoleInfo();
    	gameRoleInfo.setTypeId(HY_Constants.ENTER_SERVER);
    	gameRoleInfo.setRoleId("1235698465");
    	gameRoleInfo.setRoleName("欧阳锋");
    	gameRoleInfo.setRoleLevel("11");
    	gameRoleInfo.setZoneId("1358");
    	gameRoleInfo.setZoneName("狂战一区");
    	gameRoleInfo.setBalance(10);
    	gameRoleInfo.setVip("3");
    	gameRoleInfo.setPartyName("丐帮");
        HY_GameProxy.getInstance().setRoleData(this,gameRoleInfo);
    }

}
