package com.hygame;

import java.util.Map;

import net.ouwan.umipay.android.api.AccountCallbackListener;
import net.ouwan.umipay.android.api.ExitDialogCallbackListener;
import net.ouwan.umipay.android.api.GameParamInfo;
import net.ouwan.umipay.android.api.GameRolerInfo;
import net.ouwan.umipay.android.api.GameUserInfo;
import net.ouwan.umipay.android.api.InitCallbackListener;
import net.ouwan.umipay.android.api.PayCallbackListener;
import net.ouwan.umipay.android.api.UmipayFloatMenu;
import net.ouwan.umipay.android.api.UmipaySDKManager;
import net.ouwan.umipay.android.api.UmipaySDKStatusCode;
import net.ouwan.umipay.android.api.UmipaymentInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
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

public class OuWan_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static OuWan_MethodManager instance;
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

	private OuWan_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static OuWan_MethodManager getInstance() {
		if (instance == null) {
			instance = new OuWan_MethodManager();
		}
		return instance;
	}

	/**
	 * clearLoginResult 清除登录信息
	 * 
	 * @param
	 * @return
	 */
	private void clearLoginResult() {
		this.mChannelUserInfo = null;
	}

	/**
	 * applicationInit(初始化一些必须的参数)
	 * 
	 * @param
	 * @return
	 */
	@Override
	public void applicationInit(Activity paramActivity, boolean mIsLandscape) {
		this.mIsLandscape = mIsLandscape;
		mActivity = paramActivity;
		HyLog.d(TAG, "MethodManager-->applicationInit");

		initChannelDate(paramActivity);
	}

	// ---------------------------------调用渠道SDK接口------------------------------------
	@Override
	public void onCreate(Activity paramActivity) {
		mActivity = paramActivity;

	}

	/**
	 * 初始化的时候获取渠道的一些信息
	 */
	private void initChannelDate(Activity paramActivity) {
		dataFromAssets = new DataFromAssets(paramActivity);
		try {

			HyLog.d(TAG, "mIsLandscape:" + mIsLandscape);
			HyLog.d(TAG, dataFromAssets.toString());
		} catch (Exception e) {
			HyLog.d(TAG, "初始化参数不能为空");
		}
		if (mIsLandscape) {
			// 这里是横屏，该渠道没有横竖屏配置,忽略
			HyLog.d(TAG, "这里是横屏");
		} else {
			// 如果有,就通过这个判断来设置相关
			HyLog.d(TAG, "这里是竖屏");
		}
		initChannelInfo(paramActivity);
	}
	private void initChannelInfo(Activity paramActivity){
		GameParamInfo gameParamInfo = new GameParamInfo();

		// 获取AndroidManifest.xml 中 meta-data 中配置的渠道 参数
		String appid = HY_Utils.getManifestMeta(paramActivity,
				"UMIPAY_APP_ID");
		String appSecret = HY_Utils.getManifestMeta(paramActivity,
				"UMIPAY_APP_SERCRET");

		gameParamInfo.setAppId(appid);// 设置AppId
		gameParamInfo.setAppSecret(appSecret);// 设置AppSecret
		gameParamInfo.setTestMode(false); // 正式环境接入即可，false
	UmipaySDKManager.initSDK(mActivity, gameParamInfo, new InitCallbackListener() {
			
			@Override
			public void onSdkInitFinished(int code, String msg) {
				  if (code == UmipaySDKStatusCode.SUCCESS) {
	                    HyLog.d(TAG, "初始化完成");
	                	UmipayFloatMenu.getInstance().create(mActivity);
	                } else {
	                	
	                   HyLog.e(TAG, "初始化失败");
	                }
			}
		}, new AccountCallbackListener() {
			@Override
			public void onLogout(int code, Object object) {
				if (code == UmipaySDKStatusCode.SUCCESS) {
                    // 客户端成功退出
                    // 通过自定义数据object判断onLogout回调来自主动调用还是SDK调用
                    if (object != null) {
                    	isLogout = true;
        				getUserListener().onLogout(HY_SdkResult.SUCCESS, object);
                    } else {
                    	isLogout = true;
        				getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");
                    }
                }else{
                	getUserListener().onLogout(HY_SdkResult.FAIL, "注销失败");
                }
			}
			
			@Override
			public void onLogin(int code, GameUserInfo userInfo) {
				if (code == UmipaySDKStatusCode.SUCCESS && userInfo!=null) {
					HyLog.d(TAG, "登录成功");
					isLogout = false;
					mChannelUserInfo.setChannelUserId(userInfo.getOpenId());
					mChannelUserInfo.setChannelUserName(userInfo.getOpenId());
					mChannelUserInfo.setToken(userInfo.getSign());
				    UmipayFloatMenu.getInstance().show(mActivity);
				    onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);
                } else {
                	HyLog.i(TAG, "取消登录");
                	mLoginCallBack.onLoginFailed(HY_SdkResult.CANCEL, "取消登录");
                }
			}
		});
	}
	@Override
	public void onGotAuthorizationCode(HY_User localXMUser) {
		if (null == localXMUser) {
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
	public void doLogin(final Activity paramActivity,
			final HY_LoginCallBack loginCallBack) {

		this.mActivity = paramActivity;
		mLoginCallBack = loginCallBack;
		HyLog.i(TAG, "doLogin-->mIsLandscape=" + mIsLandscape);
		UmipaySDKManager.showLoginView(mActivity);
	}

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		UmipaySDKManager.logoutAccount(mActivity, "logout");
	}

	/**
	 * 支付接口
	 * 
	 * @param payParams
	 *            支付参数类
	 * 
	 */
	@Override
	public void doStartPay(Activity paramActivity, HY_PayParams payParams,
			HY_PayCallBack payCallBack) {
		mActivity = paramActivity;
		mPayParsms = payParams;
		mPayCallBack = payCallBack;
		if (isLogout) {
			HyLog.d(TAG, "用户已经登出");
			 ToastUtils.show(paramActivity, "用户没有登录，请重新登录后再支付");
			return;
		}
		HyLog.d(TAG, ".....请求应用服务器，开始pay支付");
		if (null == mChannelUserInfo) {
			HyLog.d(TAG, "服务器连接失败。。。  ");
			ToastUtils.show(mActivity, "服务器连接失败。。。");
		} else {
			if (!TextUtils.isEmpty(mChannelUserInfo.getUserId())) {
				// 这里是网络请求方法
				mUserInfoTask.startWork(paramActivity, HY_Constants.DO_PAY, "",
						this);
			}
		}
	}

	/**
	 * startPayAfter 生成订单后,执行渠道支付方法
	 * 
	 * @param
	 * @return
	 */
	private void startPayAfter(final Activity paramActivity) {
		HyLog.d(TAG, "调用支付，已经获取到参数。。。。。。。。。");
		
		int money = mPayParsms.getAmount();//单位:分
		money = money/100;//换算成: 元
		
		String productName = mPayParsms.getProductName(); 
		int exchange = mPayParsms.getExchange();
		String body = productName = money * exchange + productName;
		String orderId = mPayParsms.getOrderId();//订单号
		
		UmipaymentInfo paymentInfo = new UmipaymentInfo();
		// 业务类型，SERVICE_TYPE_QUOTA(固定额度模式，充值金额在支付页面不可修改)，SERVICE_TYPE_RATE(汇率模式，充值金额在支付页面可修改）
		paymentInfo.setServiceType(UmipaymentInfo.SERVICE_TYPE_QUOTA);
		// 定额支付金额，单位RMB
		paymentInfo.setPayMoney(money);
		// 订单描述
		paymentInfo.setDesc(body);
		// paymentInfo.setTradeno("TN2014041014461234");//（暂不开放）【可选】外部订单号
		paymentInfo.setRoleGrade(OuWan_RoleInfo.roleLevel); // 【必填】设置用户的游戏角色等级
		paymentInfo.setRoleId(OuWan_RoleInfo.roleId);// 【必填】设置用户的游戏角色的ID
		paymentInfo.setRoleName(OuWan_RoleInfo.roleName);// 【必填】设置用户的游戏角色名字
		paymentInfo.setServerId(OuWan_RoleInfo.zoneId);// 【必填】设置用户所在的服务器ID
		paymentInfo.setCustomInfo(orderId);// 【可选】游戏开发商自定义数据。该值将在用户充值成功后，在充值回调接口通知给游戏开发商时携带该数据
		UmipaySDKManager.showPayView(mActivity, paymentInfo, new PayCallbackListener() {
			
			@Override
			public void onPay(int code) {
				//该接口只表示充值流程完成与否，充值结果需以服务器回调为准
                if (code == UmipaySDKStatusCode.PAY_FINISH) {
                    //订单完成
                	mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, "订单完成");
                } else {
                    //订单未完成
                	mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, "订单取消");
                }
			}
		});// 调用充值接口
	}

	/**
	 * 退出接口
	 * 
	 */
	@Override
	public void doExitQuit(Activity paramActivity,
			HY_ExitCallback paramExitCallback) {
		// 如果没有第三方渠道的接口，则直接回调给用户，让用户自己定义自己的退出界面
		// paramExitCallback.onNo3rdExiterProvide();

		mActivity = paramActivity;
		mExitCallback = paramExitCallback;
		HyLog.d(TAG, "已经执行doExitQuit。。。。");

		UmipaySDKManager.exitSDK(mActivity, new ExitDialogCallbackListener() {

			@Override
			public void onExit(int code) {
				if (code == UmipaySDKStatusCode.EXIT_FINISH) {
					// 用户成功退出，应执行关闭游戏逻辑
					//ToastUtils.show(mActivity, "用户成功退出", Toast.LENGTH_SHORT);
					UmipayFloatMenu.getInstance().cancel(mActivity);
					mExitCallback.onChannelExit();
				} else {
					// 用户取消退出，应执行继续游戏逻辑
					HyLog.d(TAG, "取消退出");
					//ToastUtils.show(mActivity, "用户取消退出", Toast.LENGTH_SHORT);
				}
			}
		});

	}

	/**
	 * 应用服务器通过此方法返回UserInfo
	 * 
	 * @param userInfo
	 */
	public void onGotUserInfo(HY_UserInfoVo userInfo, boolean isDologin) {
		ProgressUtil.dismiss(mProgress);

		mChannelUserInfo = userInfo;

		if (userInfo == null) {
			HyLog.d(TAG, "未获取到渠道 UserInfo");
		} else {
			if (!userInfo.isValid()) {
				if (TextUtils.isEmpty(userInfo.getErrorMessage())) {
					HyLog.d(TAG, "未获取到渠道     UserInfo");
				} else {
					HyLog.d(TAG, "getError:" + userInfo.getErrorMessage());
				}
			}
		}
		updateUserInfoUi(isDologin);
	}

	private void updateUserInfoUi(boolean isDoLogin) {
		HyLog.d(TAG, "updateUserInfoUi.....");
		if (mChannelUserInfo != null && mChannelUserInfo.isValid()) {
			localXMUser = new HY_User(mChannelUserInfo.getUserId(),
					mChannelUserInfo.getChannelUserId(),
					mChannelUserInfo.getChannelUserName(),
					mChannelUserInfo.getToken());
			if (isDoLogin) {
				mLoginCallBack.onLoginSuccess(localXMUser);
			} else {
				getUserListener().onSwitchUser(localXMUser,
						HY_SdkResult.SUCCESS);
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
	public void onGotTokenInfo(Activity paramActivity, int state) {
		mActivity = paramActivity;

		mUserInfoTask = new HY_HttpInfoTask();

		// 提示用户进度
		mProgress = ProgressUtil.showByString(mActivity, "登录验证信息",
				"正在请求服务器，请稍候……", new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						if (mUserInfoTask != null) {
							mUserInfoTask = null;
						}
					}
				});

		if (null != mUserInfoTask) {
			HyLog.d(TAG, ".....请求应用服务器，用AccessToken换取UserInfo");
			// ToastUtils.show(mActivity,
			// ".....请求应用服务器，用AccessToken换取UserInfo");
			// 请求应用服务器，用AccessToken换取UserInfo
			mUserInfoTask.startWork(paramActivity, state,
					mChannelUserInfo.getToken(), this);
		}
	}

	// ----------------------------------------------------------------

	@Override
	public void onGotError(int paramInt) {
		HyLog.d(TAG, "onGotError,..... ");

		clearLoginResult();
	}

	/**
	 * HY_HttpInfoTask 类描述： 用户信息的网络获取类 创建人：smile
	 */
	class HY_HttpInfoTask implements UrlRequestCallBack {

		private static final String TAG = "HY";
		private HttpUtils mHttpUtils;
		public boolean isRunning = false;
		private Activity mContext;
		private int state;
		private HY_UserInfoListener userInfo_listener;

		public HY_HttpInfoTask() {
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
		public void startWork(Activity mActivity, int state, String token,
				final HY_UserInfoListener listener) {

			if (!isRunning) {
				this.mContext = mActivity;
				this.state = state;
				userInfo_listener = listener;
				// 获取应用服务器的demo
				// String postUrl = Constants.URL_GET_TOKEN;
				ResultJsonParse channel_parser = new HY_UserInfoParser();

				if (HY_Constants.DO_LOGIN == state
						|| HY_Constants.SWITCH_ACCOUNT == state) {
					Map<String, String> map = HttpUtils
							.getLoginInfoRequest(mChannelUserInfo);
					mHttpUtils.doPost(mContext, Constants.URL_LOGIN, map, this,
							channel_parser);
				} else {

					Map<String, String> map = HttpUtils.getPayInfoRequest(
							mPayParsms, mChannelUserInfo);
					mHttpUtils.doPost(mContext, Constants.URL_PAY, map, this,
							channel_parser);
				}

			} else {
				HyLog.e("TAG", "登录重新获取工作正在进行");
			}
		}

		@Override
		public void urlRequestStart(CallBackResult result) {
			isRunning = true;
		}

		@Override
		public void urlRequestEnd(CallBackResult result) {
			isRunning = false;
			if (null != mProgress) {

				ProgressUtil.dismiss(mProgress);
				mProgress = null;
			}

			try {
				if (null != result && result.obj != null) {
					ResponseResultVO resultVO = (ResponseResultVO) result.obj;
					// 通过返回的渠道类型，判断是调用的支付接口还是登录接口
					if (resultVO.transType.equals(TransType.CREATE_USER
							.toString())) {
						HyLog.d(TAG, "登录接口-->resultCode:"
								+ resultVO.responseCode);
						// 登录接口
						if (resultVO.responseCode.equals("0")) {
							HyLog.d(TAG, "登录接口返回success：" + resultVO.message);
							mChannelUserInfo.setUserId(resultVO.userId);
							if (state == HY_Constants.DO_LOGIN) {
								userInfo_listener.onGotUserInfo(
										mChannelUserInfo, true);
							} else {
								userInfo_listener.onGotUserInfo(
										mChannelUserInfo, false);
							}
						} else {
							HyLog.d(TAG, "登录接口返回fail：" + resultVO.message);
							if (null != mProgress) {

								ProgressUtil.dismiss(mProgress);
								mProgress = null;
							}
							mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,
									"登录失败:" + resultVO.responseCode
											+ resultVO.message);

						}

					} else if (resultVO.transType.equals(TransType.CREATE_ORDER
							.toString())) {
						HyLog.d(TAG, "支付接口-->resultCode:"
								+ resultVO.responseCode);
						// 支付接口
						if (resultVO.responseCode.equals("0")) {
							HyLog.d(TAG, "支付接口返回success：" + resultVO.message);
							mPayParsms.setOrderId(resultVO.orderId);// 传入应用订单号
							HyLog.d(TAG, "创建订单成功后-->mChannelUserInfo："
									+ mChannelUserInfo.toString());
							startPayAfter(mContext);
						} else {
							HyLog.d(TAG, "支付接口返回fail：" + resultVO.message);
							if (null != mProgress) {

								ProgressUtil.dismiss(mProgress);
								mProgress = null;
							}
							mPayCallBack.onPayCallback(HY_SdkResult.FAIL,
									resultVO.message);
						}

					} else {
						HyLog.d(TAG, "接口传输不对，既不是登录也不是支付：" + resultVO.message);
					}
				}
			} catch (Exception e) {
				HyLog.e(TAG, "网络异常，请稍后再试" + e.toString());
				mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "网络异常，请稍后再试:");
				//ToastUtils.show(mActivity, "网络异常，请稍后再试");
			}
		}

		@Override
		public void urlRequestException(CallBackResult result) {
			isRunning = false;
			HyLog.e(TAG, "urlRequestException:" + result.url + ","
					+ result.param + "," + result.backString);
			//ToastUtils.show(mActivity, "网络异常，请稍后再试");

			mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "网络异常,请稍后再试:");

			if (null != mProgress) {

				ProgressUtil.dismiss(mProgress);
				mProgress = null;
			}
		}

	}

	@Override
	public void onStop(Activity paramActivity) {
		HyLog.d(TAG, "MethodManager-->onStop");

	}

	@Override
	public void onResume(Activity paramActivity) {
		UmipayFloatMenu.getInstance().show(paramActivity);
		HyLog.d(TAG, "MethodManager-->onStop");
	}

	@Override
	public void onPause(Activity paramActivity) {
		UmipayFloatMenu.getInstance().hide(paramActivity);
		HyLog.d(TAG, "MethodManager-->onPause");
	}

	@Override
	public void onRestart(Activity paramActivity) {
		HyLog.d(TAG, "MethodManager-->onRestart");

	}

	@Override
	public void applicationDestroy(Activity paramActivity) {
		HyLog.d(TAG, "MethodManager-->applicationDestroy");

	}

	@Override
	public void onDestroy(Activity paramActivity) {
		//使用浮动菜单的activity销毁时调用
        UmipayFloatMenu.getInstance().cancel(paramActivity);
		HyLog.d(TAG, "MethodManager-->onDestroy");
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据

		OuWan_RoleInfo.typeId = gameRoleInfo.getTypeId();	//上传类型
															//登录:HY_Constants.ENTER_SERVER
															//创角:HY_Constants.CREATE_ROLE、
															//升级:HY_Constants。LEVEL_UP
		OuWan_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		OuWan_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		OuWan_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		OuWan_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		OuWan_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		OuWan_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		OuWan_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		OuWan_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		//Toast.makeText(paramActivity, gameRoleInfo.toString(),
		//		Toast.LENGTH_SHORT).show();
		HyLog.d(TAG, "MethodManager-->setExtData");
		
		
//		  // Field descriptor #6 I
//		  public static final int ENTER_SERVER = 0;
//		  
//		  // Field descriptor #6 I
//		  public static final int CREATE_ROLE = 1;
//		  
//		  // Field descriptor #6 I
//		  public static final int LEVEL_UP = 2;
		
		
		//初始化游戏角色信息
		GameRolerInfo rolerInfo = new GameRolerInfo();
		rolerInfo.setBalance(OuWan_RoleInfo.balance);//游戏币当前余额
		rolerInfo.setRoleId(OuWan_RoleInfo.roleId);//游戏角色的id
		rolerInfo.setRoleLevel(OuWan_RoleInfo.roleLevel);//游戏角色等级
		rolerInfo.setRoleName(OuWan_RoleInfo.roleName);//游戏角色名字
		rolerInfo.setServerId(OuWan_RoleInfo.zoneId);//用户所在服务器ID
		rolerInfo.setServerName(OuWan_RoleInfo.zoneName);//用户所在服务器名称
		rolerInfo.setVip(OuWan_RoleInfo.vip);//用户vip等级
		
		
		int action = 0;
		
		if (OuWan_RoleInfo.typeId == HY_Constants.ENTER_SERVER ) {
			action = GameRolerInfo.AT_LOGIN_GAME;
		}else if (OuWan_RoleInfo.typeId == HY_Constants.CREATE_ROLE) {
			action = GameRolerInfo.AT_CREATE_ROLE;
		}else if( OuWan_RoleInfo.typeId == HY_Constants.LEVEL_UP ){
			action = GameRolerInfo.AT_LEVEL_UP;
		}
				
		/**
		 *  上报角色信息：
		 *      GameRolerInfo.AT_LOGIN_GAME 登录游戏的时候上报
		 *      GameRolerInfo.AT_CREATE_ROLE 创建角色的时候上报
		 *      GameRolerInfo.AT_LEVEL_UP 角色升级的时候上报
		 *
		 *  请开发商务必在相应的时机上报游戏角色信息
		 */
		UmipaySDKManager.setGameRolerInfo(mActivity, action ,rolerInfo);
		
		
		
	}
}