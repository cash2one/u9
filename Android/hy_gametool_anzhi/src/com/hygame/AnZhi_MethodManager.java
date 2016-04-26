package com.hygame;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.AzOutGameInter;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.example.test.anzhi.R;
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
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.TransType;
import com.hy.gametools.utils.UrlRequestCallBack;

public class AnZhi_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static AnZhi_MethodManager instance;
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
	/** 支付参数 */
	private HY_PayParams mPayParsms;
	/** 登录回调 */
	private HY_LoginCallBack mLoginCallBack;
	/** 支付回调 */
	private HY_PayCallBack mPayCallBack;
	/** 退出回调 */
	private HY_ExitCallback mExitCallback;
	private DataFromAssets dataFromAssets;

	private AnZhi_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static AnZhi_MethodManager getInstance() {
		if (instance == null) {
			instance = new AnZhi_MethodManager();
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
	 * 用户中心管理实例
	 */
	private AnzhiUserCenter mAnzhiCenter;

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
		String Appkey = HY_Utils.getManifestMeta(mActivity, "ANZHI_APPKEY");// SDK
																			// 初始化参数
		String AppSecret = HY_Utils.getManifestMeta(mActivity,
				"ANZHI_APPSECRET");
		HyLog.d(TAG, "安智初始化");
		final CPInfo info = new CPInfo();
		info.setOpenOfficialLogin(false);// 是否开启游戏官方账号登录，默认为关闭
		info.setAppKey(Appkey);
		info.setSecret(AppSecret);
		info.setChannel("AnZhi");
		info.setGameName(HY_Utils.getAppName(paramActivity));
		mAnzhiCenter = AnzhiUserCenter.getInstance();
		mAnzhiCenter.setKeybackCall(keyCall);// 设置返回游戏的通知
		mAnzhiCenter.azinitSDK(mActivity, info, initSDKCallback, gameInter);
		mAnzhiCenter.setCallback(mCallback);// 设置 登录、登出、支付 回调

		if (mIsLandscape) {
			// 这里是横屏，该渠道没有横竖屏配置,忽略
			mAnzhiCenter.setActivityOrientation(0);// 0横屏,1竖屏,4根据物理感应来选择方向
			HyLog.d(TAG, "这里是横屏");
		} else {
			// 如果有,就通过这个判断来设置相关
			mAnzhiCenter.setActivityOrientation(1);// 0横屏,1竖屏,4根据物理感应来选择方向
			HyLog.d(TAG, "这里是竖屏");
		}
		HyLog.d(TAG, "安智初始化成功");
	}

	private static final String KEY_PAY = "key_pay";// 支付的key
	private static final String KEY_LOGIN = "key_login";// 登录的key
	private static final String KEY_LOGOUT = "key_logout";// 登出的KEY

	/**
	 * 登录、登出、支付通知
	 */
	AnzhiCallback mCallback = new AnzhiCallback() {

		@Override
		public void onCallback(CPInfo cpInfo, final String result) {
			Log.e(TAG, "result " + result);
			try {
				JSONObject json = new JSONObject(result);
				String key = json.optString(JS_CALLBACK_KEY);
				if (KEY_PAY.equals(key)) {// 支付结果通知

					mHandler.obtainMessage(CODE_PAY_RESULT, json)
							.sendToTarget();

				} else if (KEY_LOGOUT.equals(key)) {// 切换或退出账号的通知

					mHandler.sendEmptyMessage(CODE_LOGOUT_SUCCESS);

				} else if (KEY_LOGIN.equals(key)) {// 登录游戏的方法

					mHandler.obtainMessage(CODE_LOGIN_RESULT, json)
							.sendToTarget();

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	private static final String JS_LOGIN_DESC = "code_desc";

	private static final String JS_CODE = "code";

	private static final String JS_UID = "uid"; //

	private static final String JS_NICK = "nick_name";//

	private static final String JS_SID = "sid";//

	private static final String JS_NAME = "login_name";

	private static final String JS_CALLBACK_KEY = "callback_key";

	private static final int CODE_LOGIN_RESULT = 0x00;

	private static final int CODE_PAY_RESULT = CODE_LOGIN_RESULT + 1;

	private static final int CODE_LOGOUT_SUCCESS = CODE_PAY_RESULT + 1;

	/**
	 * 用户当前的UID
	 */

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case CODE_PAY_RESULT:
				JSONObject payJson = (JSONObject) msg.obj;
				int code = payJson.optInt(JS_CODE);
				String desc = payJson.optString("desc");// 支付说明
				String orderId = payJson.optString("order_id");// 安智支付订单
				String price = payJson.optString("price");// 支付金额
				String time = payJson.optString("time");
				if (code == 200 || code == 201) {// 当支付的返回码是200时 表示 支付成功，
													// 当支付的返回码是201 时
													// 表示支付中此时需要通过服务端来验证支付结果
					// 支付成功
					mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, desc);
				} else {
					// ToastUtils.show(mActivity, desc);
					// 支付取消
					mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, desc);
				}
				break;
			case CODE_LOGOUT_SUCCESS:
				getUserListener().onLogout(HY_SdkResult.SUCCESS, "安智已退出账户");
				break;
			case CODE_LOGIN_RESULT:
				JSONObject loginJson = (JSONObject) msg.obj;
				int loginCode = loginJson.optInt(JS_CODE);
				String loginDesc = loginJson.optString(JS_LOGIN_DESC);
				if (loginCode == 200) {
					String uid = loginJson.optString(JS_UID);// uid 是安智平台用户的唯一标示
					String loginName = loginJson.optString(JS_NAME);// 获得用户名
					String sid = loginJson.optString(JS_SID);// 获得SID
					String Nick = loginJson.optString(JS_NICK);// 获得昵称
					// 登录成功
					isLogout = false;// 登录状态
					// 设置辉耀服务器UID
					// 这里的uid是指我们的uid,服务端生成,目前服务端还没OK，我们测试用渠道的,后续可以删除
					// 正常接入
					mChannelUserInfo.setChannelUserId(uid);// 渠道uid
					mChannelUserInfo.setChannelUserName(loginName);// 渠道用户名
					mChannelUserInfo.setToken(sid);// 登录验证令牌(token)

					// 网络测试,依赖网络环境
					onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);

					// 本地模拟测试
					// updateUserInfoUi(true); // true表示登录,false表示切换账号
					Log.e(TAG, "登录成功,返回消息: " + msg.toString());
				} else {
					// ToastUtils.show(mActivity, loginDesc);
					// 登录失败
					// 回调给游戏，登录失败
					mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "登录失败:"
							+ loginDesc);
				}
				break;
			}
		}
	};

	/**
	 * 页面返回回调 ，包括登录、支付等；
	 * 
	 * Login 登录界面 / GamePay 支付界面 / UserCenter 个人中心界面 / GameGift 礼包页面 / anzhiBbs
	 * 论坛页面 / Message 消息页面/ Feedback 客服中心页面 / Login 登录页面/ gamePay 充值页面 /
	 * AnzhiCurrent 安智币充值界面
	 * 
	 */
	KeybackCall keyCall = new KeybackCall() {
		@Override
		public void KeybackCall(String Call) {
			HyLog.e(TAG, "页面返回不做操作");
		}
	};

	/**
	 * 初始化完成后的回调
	 */
	InitSDKCallback initSDKCallback = new InitSDKCallback() {
		@Override
		public void initSdkCallcack() {
			HyLog.d(TAG, "初始化成功");
		}
	};

	/**
	 * hhh退出游戏的接口，开发者在本接口中实现退出游戏的方法。 在安智的退出弹窗页点退出游戏或在三秒内连续两次调用
	 * azoutGame(boolean)方法会调用本接口
	 * 
	 * 根据标示码判读是否退出游戏
	 * 
	 */
	AzOutGameInter gameInter = new AzOutGameInter() {
		@Override
		public void azOutGameInter(int arg) {

			switch (arg) {
			case AzOutGameInter.KEY_OUT_GAME:// 退出游戏
				if("1002".equals(HY_Utils.getHYGameId(mActivity))){
					 HyLog.d(TAG, "1");
//					 mAnzhiCenter.removeFloaticon(mActivity);
//			        	android.os.Process.killProcess(android.os.Process.myPid());
					 mActivity.finish();
//					 System.exit(0);
			       }
				mAnzhiCenter.removeFloaticon(mActivity);
				mExitCallback.onChannelExit();
				break;
			case AzOutGameInter.KEY_CANCEL: // 取消退出游戏
				HyLog.d(TAG, "取消退出游戏");
				break;
			default:
				break;
			}
		}
	};

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

		mAnzhiCenter.login(mActivity, true);// 登录方法
		mAnzhiCenter.showFloaticon();
	}

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		mAnzhiCenter.logout(mActivity);// 退出游戏的方法
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
		float money = mPayParsms.getAmount();// 单位:元
		money = money / 100;
		String payDesc = mPayParsms.getProductName();
		mAnzhiCenter.pay(mActivity, 0, money, payDesc, mPayParsms.getOrderId());

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

		/**
		 * 1、调用本方法或触发退出逻辑 2、在账号登录的情况下，首次调用本方法或展示后台配置的推广位，如果没有则弹Toast
		 * 3、三秒内重复调用本方法会发出退出游戏的通知回调。
		 */
		if(mAnzhiCenter != null){
			mAnzhiCenter.azoutGame(true);
		}
		
		HyLog.d(TAG, "已经执行doExitQuit。。。。");

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
				// ToastUtils.show(mActivity, "网络异常，请稍后再试");
			}
		}

		@Override
		public void urlRequestException(CallBackResult result) {
			isRunning = false;
			HyLog.e(TAG, "urlRequestException:" + result.url + ","
					+ result.param + "," + result.backString);
			// ToastUtils.show(mActivity, "网络异常，请稍后再试");

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
		HyLog.d(TAG, "MethodManager-->onStop");
	}

	@Override
	public void onPause(Activity paramActivity) {
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
		HyLog.d(TAG, "MethodManager-->onDestroy");
	    if("1002".equals(HY_Utils.getHYGameId(paramActivity))){
				 HyLog.d(TAG, "1");
		        	android.os.Process.killProcess(android.os.Process.myPid());
//				 paramActivity.finish();
//				 System.exit(0);
		        }
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据

		AnZhi_RoleInfo.typeId = gameRoleInfo.getTypeId();// 上传类型
															// 登录:HY_Constants.ENTER_SERVER
															// 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
		AnZhi_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		AnZhi_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		AnZhi_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		AnZhi_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		AnZhi_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		AnZhi_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		AnZhi_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		AnZhi_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		// Toast.makeText(paramActivity, gameRoleInfo.toString(),
		// Toast.LENGTH_SHORT).show();
		if (AnZhi_RoleInfo.typeId == HY_Constants.ENTER_SERVER) {
			UserGameInfo gameInfo = new UserGameInfo();
			gameInfo.setNickName(mChannelUserInfo.getChannelUserName());
			gameInfo.setUid(mChannelUserInfo.getChannelUserId());
			gameInfo.setAppName(HY_Utils.getAppName(paramActivity));
			gameInfo.setGameArea(AnZhi_RoleInfo.zoneId);// 游戏的服务器区
			gameInfo.setGameLevel(AnZhi_RoleInfo.roleLevel);// 游戏角色等级
			gameInfo.setUserRole(AnZhi_RoleInfo.roleName);// 角色名称
			gameInfo.setMemo("");// 备注
			mAnzhiCenter.submitGameInfo(mActivity, gameInfo);
		}
		HyLog.d(TAG, "MethodManager-->setExtData");
	}

}