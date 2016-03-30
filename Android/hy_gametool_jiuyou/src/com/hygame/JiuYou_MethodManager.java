package com.hygame;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import cn.uc.gamesdk.UCCallbackListener;
import cn.uc.gamesdk.UCCallbackListenerNullException;
import cn.uc.gamesdk.UCFloatButtonCreateException;
import cn.uc.gamesdk.UCGameSDK;
import cn.uc.gamesdk.UCGameSDKStatusCode;
import cn.uc.gamesdk.UCLogLevel;
import cn.uc.gamesdk.UCLoginFaceType;
import cn.uc.gamesdk.UCOrientation;
import cn.uc.gamesdk.info.FeatureSwitch;
import cn.uc.gamesdk.info.GameParamInfo;
import cn.uc.gamesdk.info.OrderInfo;
import cn.uc.gamesdk.info.PaymentInfo;

import com.hy.gametools.manager.HY_Constants;
import com.hy.gametools.manager.HY_ExitCallback;
import com.hy.gametools.manager.HY_GameRoleInfo;
import com.hy.gametools.manager.HY_LoginCallBack;
import com.hy.gametools.manager.HY_PayCallBack;
import com.hy.gametools.manager.HY_PayParams;
import com.hy.gametools.manager.HY_SdkResult;
import com.hy.gametools.manager.HY_User;
import com.hy.gametools.manager.HY_UserInfoListener;
import com.hy.gametools.manager.HY_UserInfoParser;
import com.hy.gametools.start.CheckAfter;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.manager.HY_UserManagerBase;
import com.hy.gametools.manager.HY_AccountListener;
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.DataFromAssets;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.ResultJsonParse;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ProgressUtil;
import com.hy.gametools.utils.ResponseResultVO;
import com.hy.gametools.utils.TransType;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;


public class JiuYou_MethodManager extends HY_UserManagerBase implements
        HY_AccountListener, HY_UserInfoListener
{
    private static final String TAG = "HY";
    private static JiuYou_MethodManager instance;
    private Activity mActivity;
    /** isAccessTokenValid:token时效性是否有效 */
    protected static boolean isAccessTokenValid = true;
    /** mIsLandscape:是否是横屏 */
    protected boolean mIsLandscape;
    /** 判断用户是否登出 */
    private boolean isLogout = false;
    /** TokenInfo 保存渠道返回的Token信息 */
    /** 用户信息 */
    private HY_UserInfoVo mChannelUserInfo;
    /** 用户信息的网络获取类 */
    private HY_HttpInfoTask mUserInfoTask;
    /** localXMUser 保存的微讯一键sdk的用户信息,用户回调给CP用户 */
    private HY_User localXMUser;
    /** 保存是否为定额支付 */
    // private boolean mIsFixed;
    private ProgressDialog mProgress;
    private DataFromAssets dataFromAssets;
    /** 支付参数 */
    private HY_PayParams mPayParsms;
    /** 登录回调 */
    private HY_LoginCallBack mLoginCallBack;
    /** 支付回调 */
    private HY_PayCallBack mPayCallBack;
    /** 退出回调 */
    private HY_ExitCallback mExitCallback;
   

    private JiuYou_MethodManager()
    {
        mChannelUserInfo=new HY_UserInfoVo();
    }

    public static JiuYou_MethodManager getInstance()
    {
        if (instance == null)
        {
            instance = new JiuYou_MethodManager();
        }
        return instance;
    }

    /**
     * clearLoginResult 清除登录信息
     * 
     * @param
     * @return
     */
    private void clearLoginResult()
    {
        this.mChannelUserInfo = null;
    }

    /**
     * applicationInit(初始化一些必须的参数)
     * 
     * @param
     * @return
     */
    @Override
    public void applicationInit(Activity paramActivity,boolean mIsLandscape)
    {
    	this.mIsLandscape = mIsLandscape;
        mActivity = paramActivity;
        HyLog.d(TAG, "MethodManager-->applicationInit");
        
        initChannelDate(paramActivity);
    }

    // ---------------------------------调用渠道SDK接口------------------------------------
    @Override
    public void onCreate(Activity paramActivity)
    {
        mActivity = paramActivity;
        ucSdkInit();
    }

    /**
     * 初始化的时候获取渠道的一些信息
     */
    private void initChannelDate(Activity paramActivity)
    {
    	dataFromAssets = new DataFromAssets(paramActivity);
		try {

			HyLog.d(TAG, "mIsLandscape:" + mIsLandscape);
			HyLog.d(TAG, dataFromAssets.toString());
		} catch (Exception e) {
			HyLog.d(TAG, "初始化参数不能为空");
		}
       
       
    }
    
	/**
	 * 必接功能<br>
	 * sdk初始化功能<br>
	 */
	private void ucSdkInit() {
		try {
			GameParamInfo gpi = new GameParamInfo();// 下面的值仅供参考
//			gpi.setCpId(0);// 此参数已废弃,默认传入0即可
			String gameId = HY_Utils.getManifestMeta(mActivity, "UC_GAME_ID");
			HyLog.d(TAG, "gameId:"+gameId);
			gpi.setGameId( Integer.valueOf(gameId) );
//			gpi.setServerId(0); // 此参数已废弃,默认传入0即可
			// gpi.setChannelId(2); // 渠道号统一处理，已不需设置，此参数已废弃，服务端此参数请设置值为2

			// 在九游社区设置显示查询充值历史和显示切换账号按钮，
			// 在不设置的情况下，默认情况情况下，生产环境显示查询充值历史记录按钮，不显示切换账户按钮
			// 测试环境设置无效
			gpi.setFeatureSwitch(new FeatureSwitch(true, false));

			// 设置SDK登录界面为横屏，个人中心及充值页面默认为强制竖屏，无法修改
			

			// 设置SDK登录界面为竖屏
			 if(mIsLandscape){
		        	//这里是横屏，该渠道没有横竖屏配置,忽略
				 UCGameSDK.defaultSDK().setOrientation(UCOrientation.LANDSCAPE);
		        	HyLog.d(TAG, "这里是横屏");
		        }else{
		        	//如果有,就通过这个判断来设置相关
		        	UCGameSDK.defaultSDK().setOrientation(UCOrientation.PORTRAIT);
		        	HyLog.d(TAG, "这里是竖屏");
		        }
			

			// 设置登录界面：
			// USE_WIDGET - 简版登录界面
			// USE_STANDARD - 标准版登录界面
			UCGameSDK.defaultSDK().setLoginUISwitch(UCLoginFaceType.USE_WIDGET);

			// setUIStyle已过时，不需调用。
			// UCGameSDK.defaultSDK().setUIStyle(UCUIStyle.STANDARD);
			
			
			UCGameSDK.defaultSDK().initSDK(mActivity, UCLogLevel.DEBUG,
					/*UCSdkConfig.debugMode*/false, gpi,
					new UCCallbackListener<String>() {
						@Override
						public void callback(int code, String msg) {
							Log.e("UCGameSDK", "UCGameSDK初始化接口返回数据 msg:" + msg
									+ ",code:" + code + "\n");
							switch (code) {
							// 初始化成功,可以执行后续的登录充值操作
							case UCGameSDKStatusCode.SUCCESS:
								setLister();
								break;
							default:
								break;
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    
    
	private void setLister(){
		// 监听用户注销登录消息
		// 九游社区-退出当前账号功能执行时会触发此监听
		try {
			UCGameSDK.defaultSDK().setLogoutNotifyListener(
					new UCCallbackListener<String>() {
						@Override
						public void callback(int statuscode, String msg) {
							String s = "游戏接收到用户退出通知。" + msg + statuscode;
							Log.e("UCGameSDK", s);
							// 未成功初始化
							if (statuscode == UCGameSDKStatusCode.NO_INIT) {
								// 调用SDK初始化接口
								ucSdkInit();
							}
							// 未登录成功
							if (statuscode == UCGameSDKStatusCode.NO_LOGIN) {
								// 调用SDK登录接口
								ucSdkLogin();
							}
							// 退出账号成功
							if (statuscode == UCGameSDKStatusCode.SUCCESS) {
								// 执行销毁悬浮按钮接口
								ucSdkDestoryFloatButton();
								isLogout = true;
								getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");
							}
							// 退出账号失败
							if (statuscode == UCGameSDKStatusCode.FAIL) {
								// 调用SDK退出当前账号接口
								getUserListener().onLogout(HY_SdkResult.FAIL, "注销失败");
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			// 处理异常
		}
	}
	/**
	 * 必接功能<br>
	 * 悬浮按钮销毁<br>
	 * 悬浮按钮销毁需要在UI线程中调用<br>
	 */
	private void ucSdkDestoryFloatButton() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				// 悬浮按钮销毁功能
				UCGameSDK.defaultSDK().destoryFloatButton(mActivity);
			}
		});
	}
	
	/**
	 * 必接功能<br>
	 * SDK登录功能<br>
	 * SDK客户端登录成功后，游戏客户端通过getsid()方法获取SDK客户端的sid，发送给游戏服务器，
	 * 游戏服务器使用此sid进行服务端接口调用，即可获取用户标示， 随后游戏服务器向游戏客户端发送用户标示即可。
	 * （注：游戏客户端无法直接从SDK客户端获取用户标示）
	 * 详细流程可见接入文档“02-技术文档-SDK总体机制\UC游戏_SDK_开发参考说明书_总体机制_vXXX.pdf”。
	 */
	private void ucSdkLogin() {

		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					boolean gameAccountEnable = false; // 游戏老账号是否启用，设置为false即可

					// 登录接口回调。从这里可以获取登录结果。
					UCCallbackListener<String> loginCallbackListener = new UCCallbackListener<String>() {
						@Override
						public void callback(int code, String msg) {
							Log.e("UCGameSDK", "UCGameSdk登录接口返回数据:code=" + code
									+ ",msg=" + msg);

							// 登录成功。此时可以获取sid。并使用sid进行游戏的登录逻辑。
							// 客户端无法直接获取UCID
							if (code == UCGameSDKStatusCode.SUCCESS) {
								
								// 登录成功		
								isLogout = false;//登录状态

								//设置辉耀服务器UID
								//这里的uid是指我们的uid,服务端生成,目前服务端还没OK，我们测试用渠道的,后续可以删除
								// 获取sid。（注：ucid需要使用sid作为身份标识去SDK的服务器获取）
//								mChannelUserInfo.setUserId(UCGameSDK.defaultSDK().getSid());
								
								//正常接入
								mChannelUserInfo.setToken(UCGameSDK.defaultSDK().getSid());
	                            //进行网络请求                    当前activity          请求模式    目前没有就
								JiuYou_GetUsersInfo jiuyou = new JiuYou_GetUsersInfo(mActivity);
								jiuyou.getUserInfo(mActivity, mChannelUserInfo, new CheckAfter<String>() {
									
									@Override
									public void afterSuccess(String arg0) {
										onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);										
									}
									
									@Override
									public void afterFailed(String arg0, Exception arg1) {
										
									}
								});
								
	                            
								
	                            //测试直接回调给游戏端
	                            //true表示登录,false表示切换账号
//								updateUserInfoUi(true);								
								
								// 执行悬浮按钮创建方法
								ucSdkCreateFloatButton();
								// 执行悬浮按钮显示方法
								ucSdkShowFloatButton();
							}

							// 登录失败。应该先执行初始化成功后再进行登录调用。
							if (code == UCGameSDKStatusCode.NO_INIT) {
								// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
								ucSdkInit();
							}

							// 登录退出。该回调会在登录界面退出时执行。
							if (code == UCGameSDKStatusCode.LOGIN_EXIT) {
								// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
								//登录失败
								//回调给游戏，登录失败
								if(isLogout){
									mLoginCallBack.onLoginFailed(HY_SdkResult.CANCEL,"");
								}
							}
						}
					};

					// 启用游戏老账号登录
					if (gameAccountEnable) {
						
					}else {// 未启用官方老账号登录
						UCGameSDK.defaultSDK().login(mActivity, loginCallbackListener);
					}
				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	
	/**
	 * 必接功能<br>
	 * 悬浮按钮创建及显示<br>
	 * 悬浮按钮必须保证在SDK进行初始化成功之后再进行创建需要在UI线程中调用<br>
	 */
	private void ucSdkCreateFloatButton() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					// 创建悬浮按钮。该悬浮按钮将悬浮显示在GameActivity页面上，点击时将会展开悬浮菜单，菜单中含有
					// SDK 一些功能的操作入口。
					// 创建完成后，并不自动显示，需要调用showFloatButton(Activity,
					// double, double, boolean)方法进行显示或隐藏。
					UCGameSDK.defaultSDK().createFloatButton(mActivity,
							new UCCallbackListener<String>() {

								@Override
								public void callback(int statuscode, String data) {
									Log.d("SelectServerActivity`floatButton Callback",
											"statusCode == " + statuscode
													+ "  data == " + data);
								}
							});

				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				} catch (UCFloatButtonCreateException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮显示<br>
	 * 悬浮按钮显示需要在UI线程中调用<br>
	 */
	private void ucSdkShowFloatButton() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				// 显示悬浮图标，游戏可在某些场景选择隐藏此图标，避免影响游戏体验
				try {
					UCGameSDK.defaultSDK().showFloatButton(mActivity, 100, 50, true);
				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				}
			}
		});
	}	
	
	
	/**
	 * 选接功能<br>
	 * 游戏可通过调用下面方法，退出当前登录的账号<br>
	 * 通过退出账号侦听器（此侦听器在初始化前经由setLogoutNotifyListener 方法设置）<br>
	 * 把退出消息返回给游戏，游戏可根据状态码进行相应的处理。<br>
	 */
	private void ucSdkLogout() {
		try {
			UCGameSDK.defaultSDK().logout();
		} catch (UCCallbackListenerNullException e) {
			// 未设置退出侦听器
		}
	}

    @Override
    public void onGotAuthorizationCode(HY_User localXMUser)
    {
        if (null == localXMUser)
        {
            HyLog.i(TAG, "localXMUser:null");
            return;
        }

        // clearLoginResult();
        HyLog.i(TAG, "localXMUser=" + localXMUser);
        // 回调给CP用户的的一些信息
        mLoginCallBack.onLoginSuccess(localXMUser);
    }

    /**
     * 登录接口 
     */
    @Override
    public void doLogin(final Activity paramActivity,final HY_LoginCallBack loginCallBack)
    {
    	
        this.mActivity = paramActivity;
        mLoginCallBack = loginCallBack;
        HyLog.i(TAG, "doLogin-->mIsLandscape=" + mIsLandscape);
       
        ucSdkLogin();		
    }

    /**
     * 注销接口
     */
    @Override
    public void doLogout(final Activity paramActivity,Object object)
    {
    	ucSdkLogout();
		getUserListener().onLogout(HY_SdkResult.SUCCESS, "九游账号注销成功");        
    }

    /**
     * 支付接口
     * 
     * @param payParams
     *            支付参数类
     * 
     */
    @Override
    public void doStartPay(Activity paramActivity, HY_PayParams payParams,HY_PayCallBack payCallBack)
    {
        mActivity = paramActivity;
        mPayParsms = payParams;
        mPayCallBack = payCallBack;
        if (isLogout)
        {
            HyLog.d(TAG, "用户已经登出");
             ToastUtils.show(paramActivity, "用户没有登录，请重新登录后再支付");
            return;
        }
            HyLog.d(TAG, ".....请求应用服务器，开始pay支付");

            if (null==mChannelUserInfo)
            {
                HyLog.d(TAG, "服务器连接失败。。。  ");
                ToastUtils.show(mActivity, "服务器连接失败。。。");
            }else {
                if (!TextUtils.isEmpty(mChannelUserInfo.getUserId()))
                {
                	//这里是网络请求方法
                    mUserInfoTask.startWork(paramActivity, HY_Constants.DO_PAY,"", this);
                }
            }
    }

    /**
     * startPayAfter 生成订单后,执行渠道支付方法
     * 
     * @param
     * @return
     */
    private void startPayAfter(final Activity paramActivity)
    {
        HyLog.d(TAG, "调用支付，已经获取到参数。。。。。。。。。");
		ucSdkPay();
    }
    
	/**
	 * 必接功能<br>
	 * SDK支付功能<br>
	 * 调用SDK支付功能 <br>
	 * 如你在调用支付页面时，没有显示正确的支付页面 <br>
	 * 请参考自助解决方案：http://bbs.9game.cn/thread-6074095-1-1.html <br>
	 * 在联调环境中进行支付，可使用无效卡进行支付，只需符合卡号卡密长度位数即可<br>
	 * 当卡号卡密全部输入为1时，是支付失败的订单，服务器将会收到订单状态为F的订单信息<br>
	 */
	private void ucSdkPay() {
		
		float money = mPayParsms.getAmount();//单位:分
		money = money/100;//换算成: 元
		String orderId = mPayParsms.getOrderId();//游戏订单号
		PaymentInfo pInfo = new PaymentInfo(); // 创建Payment对象，用于传递充值信息
		// 设置充值自定义参数，此参数不作任何处理，
		// 在充值完成后，sdk服务器通知游戏服务器充值结果时原封不动传给游戏服务器传值，字段为服务端回调的callbackInfo字段
		pInfo.setCustomInfo("");
		// 非必选参数，可不设置，此参数已废弃,默认传入0即可。
		// 如无法支付，请在开放平台检查是否已经配置了对应环境的支付回调地址，如无请配置，如有但仍无法支付请联系UC技术接口人。
//		pInfo.setServerId(Integer.valueOf(JiuYou_RoleInfo.zoneId));
		pInfo.setRoleId(JiuYou_RoleInfo.roleId); // 设置用户的游戏角色的ID，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setRoleName(JiuYou_RoleInfo.roleName); // 设置用户的游戏角色名字，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setGrade(JiuYou_RoleInfo.roleLevel); // 设置用户的游戏角色等级，此为可选参数
		// 非必填参数，设置游戏在支付完成后的游戏接收订单结果回调地址，必须为带有http头的URL形式。
		pInfo.setNotifyUrl(dataFromAssets.getChannelCallbackUrl());
		pInfo.setTransactionNumCP(orderId);
		// 当传入一个amount作为金额值进行调用支付功能时，SDK会根据此amount可用的支付方式显示充值渠道
		// 如你传入6元，则不显示充值卡选项，因为市面上暂时没有6元的充值卡，建议使用可以显示充值卡方式的金额
		pInfo.setAmount(money);// 设置充值金额，此为可选参数
		HyLog.d(TAG, "money:"+money);

		try {
			UCGameSDK.defaultSDK().pay(mActivity, pInfo,
					payResultListener);
		} catch (UCCallbackListenerNullException e) {
			HyLog.e(TAG, e+toString());
		}
	}

	private UCCallbackListener<OrderInfo> payResultListener = new UCCallbackListener<OrderInfo>() {
		@Override
		public void callback(int statudcode, OrderInfo orderInfo) {
			if (statudcode == UCGameSDKStatusCode.NO_INIT) {
				
			}
			if (statudcode == UCGameSDKStatusCode.SUCCESS) {
				// 成功充值
				
				if (orderInfo != null) {
					//支付成功
					mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS,"支付成功");
				}
			}
			if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
				// 用户退出充值界面。
				//支付取消
				mPayCallBack.onPayCallback(HY_SdkResult.CANCEL,"支付取消:");				
			}
		}

	};	
	
    /**
     *退出接口
     * 
     */
    @Override
    public void doExitQuit(Activity paramActivity,
            HY_ExitCallback paramExitCallback)
    {
        // 如果没有第三方渠道的接口，则直接回调给用户，让用户自己定义自己的退出界面
        // paramExitCallback.onNo3rdExiterProvide();

        mActivity = paramActivity;
        mExitCallback = paramExitCallback;
        
        HyLog.d(TAG, "已经执行doExitQuit。。。。");
		try{
        UCGameSDK.defaultSDK().exitSDK(mActivity, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码

				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					ucSdkDestoryFloatButton();
					mExitCallback.onChannelExit();
				}
			}
		});
		}catch (Exception e){
			e.printStackTrace();
		}
    }
    
    

    /**
     * 应用服务器通过此方法返回UserInfo
     * 
     * @param userInfo
     */
    public void onGotUserInfo(HY_UserInfoVo userInfo,boolean isDologin)
    {
        ProgressUtil.dismiss(mProgress);

        mChannelUserInfo = userInfo;

        if (userInfo == null)
        {
            HyLog.d(TAG, "未获取到渠道 UserInfo");
        }
        else
        {
            if (!userInfo.isValid())
            {
                if (TextUtils.isEmpty(userInfo.getErrorMessage()))
                {
                    HyLog.d(TAG, "未获取到渠道     UserInfo");
                }
                else
                {
                    HyLog.d(TAG, "getError:" + userInfo.getErrorMessage());
                }
            }
        }
        updateUserInfoUi(isDologin);
    }

    private void updateUserInfoUi(boolean isDoLogin)
    {
        HyLog.d(TAG, "updateUserInfoUi.....");
        if (mChannelUserInfo != null && mChannelUserInfo.isValid())
        {
            localXMUser = new HY_User(mChannelUserInfo.getUserId(),
                    mChannelUserInfo.getChannelUserId(),
                    mChannelUserInfo.getChannelUserName(),
                    mChannelUserInfo.getToken());
            if(isDoLogin){
            mLoginCallBack.onLoginSuccess(localXMUser);
            }else{
            getUserListener().onSwitchUser(localXMUser, HY_SdkResult.SUCCESS);
            }
        }

    }

    /**
     * onGotTokenInfo(首先需要保存AccessToken，然后需要用AccessToken换取UserInfo)
     * 
     * @param tokenInfo
     *            用户的token信息
     * @return
     */
    public void onGotTokenInfo(Activity paramActivity,int state)
    {
        mActivity = paramActivity;

            mUserInfoTask = new HY_HttpInfoTask();

            // 提示用户进度
            mProgress = ProgressUtil.showByString(mActivity,
                    "登录验证信息", "正在请求服务器，请稍候……",
                    new DialogInterface.OnCancelListener()
                    {

                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            if (mUserInfoTask != null)
                            {
                                mUserInfoTask = null;
                            }
                        }
                    });

            if (null != mUserInfoTask)
            {
                HyLog.d(TAG, ".....请求应用服务器，用AccessToken换取UserInfo");
                // ToastUtils.show(mActivity,
                // ".....请求应用服务器，用AccessToken换取UserInfo");
                // 请求应用服务器，用AccessToken换取UserInfo
                mUserInfoTask.startWork(paramActivity,
                		state , mChannelUserInfo.getToken(), this);
            }
        }
      

    // ----------------------------------------------------------------


    @Override
    public void onGotError(int paramInt)
    {
        HyLog.d(TAG, "onGotError,..... ");

        clearLoginResult();
    }

  

    /**
     * HY_HttpInfoTask 类描述： 用户信息的网络获取类 创建人：smile
     */
    class HY_HttpInfoTask implements UrlRequestCallBack
    {

        private static final String TAG = "HY";
        private HttpUtils mHttpUtils;
        public boolean isRunning = false;
        private Activity mContext;
        private int state;
        private HY_UserInfoListener userInfo_listener;

        public HY_HttpInfoTask()
        {
            super();
            mHttpUtils = new HttpUtils();

        }

        /**
         * startWork(启动网络请求)
         * 
         * @param accessToken
         *            传值非空为登录，传值空字符串""为支付
         * @return
         */
        public void startWork(Activity mActivity,
        		int state , String token,
                final HY_UserInfoListener listener)
        {

            if (!isRunning)
            {
                this.mContext = mActivity;
                this.state = state;
                userInfo_listener = listener;
                // 获取应用服务器的demo
                // String postUrl = Constants.URL_GET_TOKEN;
                ResultJsonParse channel_parser = new HY_UserInfoParser();
                
             
                	 if(HY_Constants.DO_LOGIN == state || HY_Constants.SWITCH_ACCOUNT == state){
                     Map<String,String>	map = HttpUtils.getLoginInfoRequest(mChannelUserInfo);
                     	mHttpUtils.doPost(mContext, Constants.URL_LOGIN,
                     			map, this, channel_parser);
                     }
                	 else{
                     	
                		 Map<String,String>	map = HttpUtils.getPayInfoRequest(mPayParsms,
                                 mChannelUserInfo);
                       	mHttpUtils.doPost(mContext, Constants.URL_PAY,
                       			map, this, channel_parser);
                     }
         
            }
            else
            {
                HyLog.e("TAG", "登录重新获取工作正在进行");
            }
        }

        @Override
        public void urlRequestStart(CallBackResult result)
        {
            isRunning = true;
        }

        @Override
        public void urlRequestEnd(CallBackResult result)
        {
            isRunning = false;
            if (null != mProgress)
            {

                ProgressUtil.dismiss(mProgress);
                mProgress = null;
            }

            try
            {
                if (null != result && result.obj != null)
                {
                    ResponseResultVO resultVO = (ResponseResultVO) result.obj;
                    // 通过返回的渠道类型，判断是调用的支付接口还是登录接口
                    if (resultVO.transType.equals(TransType.CREATE_USER
                            .toString()))
                    {
                    	HyLog.d(TAG, "登录接口-->resultCode:"+resultVO.responseCode);
                        // 登录接口
                        if (resultVO.responseCode.equals("0") )
                        {
                                HyLog.d(TAG, "登录接口返回success：" + resultVO.message);
                                mChannelUserInfo.setUserId(resultVO.userId) ;
                                if(state == HY_Constants.DO_LOGIN){
                                userInfo_listener.onGotUserInfo(mChannelUserInfo,true);
                                }else{
                                userInfo_listener.onGotUserInfo(mChannelUserInfo,false);
                                }
                        }
                        else
                        {
                            HyLog.d(TAG, "登录接口返回fail：" + resultVO.message);
                            if (null != mProgress)
                            {

                                ProgressUtil.dismiss(mProgress);
                                mProgress = null;
                            }
                            mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,
                            		"登录失败:"+resultVO.responseCode + resultVO.message);
                            
                        }

                    }
                    else if (resultVO.transType.equals(TransType.CREATE_ORDER
                            .toString()))
                    {
                    	HyLog.d(TAG, "支付接口-->resultCode:"+resultVO.responseCode);
                        // 支付接口
                        if (resultVO.responseCode.equals("0"))
                        {
                                HyLog.d(TAG, "支付接口返回success：" + resultVO.message);
                                mPayParsms.setOrderId(resultVO.orderId);// 传入应用订单号
                                HyLog.d(TAG, "创建订单成功后-->mChannelUserInfo："
                                        + mChannelUserInfo.toString());
                                startPayAfter(mContext);
                            }
                        else
                        {
                            HyLog.d(TAG, "支付接口返回fail：" + resultVO.message);
                            if (null != mProgress)
                            {

                                ProgressUtil.dismiss(mProgress);
                                mProgress = null;
                            }
                            mPayCallBack.onPayCallback(HY_SdkResult.FAIL,
                                    resultVO.message);
                        }

                    }
                    else
                    {
                        HyLog.d(TAG, "接口传输不对，既不是登录也不是支付：" + resultVO.message);
                    }
                }
            }
            catch (Exception e)
            {
                HyLog.e(TAG, "网络异常，请稍后再试" + e.toString());
                mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,"网络异常，请稍后再试:");
                //ToastUtils.show(mActivity, "网络异常，请稍后再试");
            }
        }

        @Override
        public void urlRequestException(CallBackResult result)
        {
            isRunning = false;
            HyLog.e(TAG, "urlRequestException:" + result.url + "," + result.param
                    + "," + result.backString);
            //ToastUtils.show(mActivity, "网络异常，请稍后再试");

            mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,"网络异常,请稍后再试:");
           
            if (null != mProgress)
            {

                ProgressUtil.dismiss(mProgress);
                mProgress = null;
            }
        }

    }

    @Override
    public void onStop(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->onStop");

    }

    @Override
    public void onResume(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->onStop");
    }

    @Override
    public void onPause(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->onPause");
    }

    @Override
    public void onRestart(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->onRestart");

    }

    @Override
    public void applicationDestroy(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->applicationDestroy");

    }
    @Override
    public void onDestroy(Activity paramActivity)
    {
        HyLog.d(TAG, "MethodManager-->onDestroy");
        UCGameSDK.defaultSDK().destoryFloatButton(paramActivity);

    }

    @Override
    public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo)
    {
    	//提供了两个接口
    	//1、游戏上传，我们在这里传给 渠道
    	//2、保存最后一次上传信息，根据渠道需求进行上传信息
    	//以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据
    	
    	JiuYou_RoleInfo.typeId = gameRoleInfo.getTypeId();//上传类型 登录:HY_Constants.ENTER_SERVER 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
    	JiuYou_RoleInfo.roleId = gameRoleInfo.getRoleId();//角色id
    	JiuYou_RoleInfo.roleName = gameRoleInfo.getRoleName();//角色名
    	JiuYou_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();//角色等级
    	JiuYou_RoleInfo.zoneId = gameRoleInfo.getZoneId();//区服id
    	JiuYou_RoleInfo.zoneName = gameRoleInfo.getZoneName();//区服名
    	JiuYou_RoleInfo.balance = gameRoleInfo.getBalance(); //用户余额
    	JiuYou_RoleInfo.vip = gameRoleInfo.getVip();//vip等级
    	JiuYou_RoleInfo.partyName = gameRoleInfo.getPartyName();//帮派名称
    	//这里是为了显示例子,正式的时候就不要弹Toast了
    	//Toast.makeText(paramActivity, gameRoleInfo.toString(), Toast.LENGTH_SHORT).show();
    	if(JiuYou_RoleInfo.typeId == HY_Constants.ENTER_SERVER||JiuYou_RoleInfo.typeId == HY_Constants.CREATE_ROLE){
    		JSONObject jsonExData = new JSONObject();
    		try {
				jsonExData.put("roleId", JiuYou_RoleInfo.roleId);
				jsonExData.put("roleName", JiuYou_RoleInfo.roleName);
	    		jsonExData.put("roleLevel", JiuYou_RoleInfo.roleLevel);
	    		jsonExData.put("zoneId", JiuYou_RoleInfo.zoneId);
	    		jsonExData.put("zoneName", JiuYou_RoleInfo.zoneName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		
    		UCGameSDK.defaultSDK().submitExtendData("loginGameRole", jsonExData);
    	}
        HyLog.d(TAG, "MethodManager-->setExtData");
    }

}