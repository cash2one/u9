package com.hygame;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.hy.gametools.utils.UrlRequestCallBack;
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayCallback;
import com.mappn.sdk.pay.GfanPayInitCallback;
import com.mappn.sdk.pay.model.Order;
import com.mappn.sdk.uc.GfanUCCallback;
import com.mappn.sdk.uc.GfanUCenter;
import com.mappn.sdk.uc.User;

public class GFan_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static GFan_MethodManager instance;
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

	private GFan_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static GFan_MethodManager getInstance() {
		if (instance == null) {
			instance = new GFan_MethodManager();
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
		// GfanPayAgent.init(paramActivity);

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
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					GfanPay.getInstance(mActivity).init(mActivity,
							new GfanPayInitCallback() {

								@Override
								public void onSuccess() {
									HyLog.d(TAG, "初始化成功");
								}

								@Override
								public void onError() {
									HyLog.e(TAG, "初始化失败");
								}
							});
				} catch (Exception e) {
					HyLog.e(TAG, "初始化异常:" + e);
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
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				HyLog.d(TAG, "调用登录");
				try {
					GfanUCenter.login(mActivity, new GfanUCCallback() {

						/**
					 * 
					 */
						private static final long serialVersionUID = 6240244856169279090L;

						@Override
						public void onSuccess(User user, int loginType) {
							// 登录成功
							isLogout = false;// 登录状态
							HyLog.d(TAG, "登录成功:" + user);
							// 正常接入
							mChannelUserInfo.setChannelUserId(user.getUid()
									+ "");// 渠道uid
							if (TextUtils.isEmpty(user.getUserName())) {
								mChannelUserInfo.setChannelUserName(user
										.getUserName());// 渠道用户名
							} else {
								mChannelUserInfo.setChannelUserName(user
										.getUid() + "");// 渠道用户名
							}
							mChannelUserInfo.setToken(URLEncoder.encode(user.getToken()));// 登录验证令牌(token)
							// 网络测试,依赖网络环境
							onGotTokenInfo(paramActivity, HY_Constants.DO_LOGIN);
						}

						@Override
						public void onError(int loginType) {
							// 登录失败
							// 回调给游戏，登录失败

							mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,
									"登录失败:");
							HyLog.e(TAG, "登录失败:" + loginType);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				GfanUCenter.logout(mActivity);
			}
		});

		isLogout = true;
		// Logout成功:
		getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");

		// Logout失败:
		// getUserListener().onLogout(HY_SdkResult.FAIL, arg0);
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
			// ToastUtils.show(paramActivity, "用户没有登录，请重新登录后再支付");
			return;
		}
		// if (null != mUserInfoTask)
		// {
		HyLog.d(TAG, ".....请求应用服务器，开始pay支付");
		// mUserInfoTask.startWork(paramActivity, "", this);

		if (null == mChannelUserInfo) {
			HyLog.d(TAG, "服务器连接失败。。。  ");
			// ToastUtils.show(mActivity, "服务器连接失败。。。");
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
		float money = mPayParsms.getAmount();// 单位:分
		money = money / 100;// 换算成: 元
		String AppName = HY_Utils.getAppName(paramActivity);
		String productName = mPayParsms.getProductName();
		String orderId = mPayParsms.getOrderId();
		HyLog.d(TAG, money + "");
		int number = (int) (money * 10);
		final Order order = new Order(AppName, productName, number, orderId);
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				GfanPay.getInstance(mActivity).pay(
						order, new GfanPayCallback() {

							@Override
							public void onSuccess(User user, Order order) {
								// 支付成功
								HyLog.d(TAG, "支付成功");
								mPayCallBack.onPayCallback(
										HY_SdkResult.SUCCESS, "支付成功");
							}

							@Override
							public void onError(User user) {
								if (user != null) {
									// 支付取消
									HyLog.d(TAG, "支付失败");
									mPayCallBack.onPayCallback(
											HY_SdkResult.CANCEL, "支付失败 user："
													+ user.getUserName());
								} else {
									// 支付取消
									HyLog.d(TAG, "支付失败");
									mPayCallBack.onPayCallback(
											HY_SdkResult.CANCEL, "用户未登录");
								}

							}
						});
			}
		});

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
		mExitCallback.onGameExit();
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
		// GfanPayAgent.onResume(mActivity);

	}

	@Override
	public void onPause(Activity paramActivity) {
		HyLog.d(TAG, "MethodManager-->onPause");
		// GfanPayAgent.onPause(mActivity);
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
		// GfanPay.getInstance(mActivity.getApplicationContext()).onDestroy();
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据

		GFan_RoleInfo.typeId = gameRoleInfo.getTypeId();// 上传类型
														// 登录:HY_Constants.ENTER_SERVER
														// 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
		GFan_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		GFan_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		GFan_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		GFan_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		GFan_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		GFan_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		GFan_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		GFan_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		// Toast.makeText(paramActivity, gameRoleInfo.toString(),
		// Toast.LENGTH_SHORT).show();
		HyLog.d(TAG, "MethodManager-->setExtData");
	}

}