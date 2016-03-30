package com.hygame;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUserObsv;
import com.hy.gametools.manager.HY_Constants;
import com.hy.gametools.manager.HY_ExitCallback;
import com.hy.gametools.manager.HY_GameProxy;
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
import com.hy.gametools.utils.JsonGenerator;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ProgressUtil;
import com.hy.gametools.utils.ResponseResultVO;
import com.hy.gametools.utils.TransType;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

public class GuoPan_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static GuoPan_MethodManager instance;
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

	private GuoPan_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static GuoPan_MethodManager getInstance() {
		if (instance == null) {
			instance = new GuoPan_MethodManager();
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

	private String APP_ID;
	private String APP_KEY;
	private boolean mIsInitSuc = false;

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
		APP_ID = HY_Utils.getManifestMeta(paramActivity, "GUOPAN_APPID");
		APP_KEY = HY_Utils.getManifestMeta(paramActivity, "GUOPAN_CLIENT_SECRETKEY");
		GPApiFactory.getGPApi().setLogOpen(false);
		if (!mIsInitSuc) {
			GPApiFactory.getGPApi().initSdk(mActivity, APP_ID, APP_KEY,
					mInitObsv);
		} else {
			HyLog.d(TAG, "初始化已经成功");
		}
	}

	/**
	 * 初始化回调接口
	 */
	private IGPSDKInitObsv mInitObsv = new IGPSDKInitObsv() {
		@Override
		public void onInitFinish(GPSDKInitResult initResult) {
			Log.i(TAG, "GPSDKInitResult mInitErrCode: "
					+ initResult.mInitErrCode);
			Log.i(TAG, "loginToken" + GPApiFactory.getGPApi().getLoginToken());
			switch (initResult.mInitErrCode) {
			case GPSDKInitResult.GPInitErrorCodeConfig:
				// ToastUtils.show(mActivity,"初始化回调:初始化配置错误");
				retryInit();
				break;
			case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
				// ToastUtils.show(mActivity,"初始化回调:游戏需要更新");
				break;
			case GPSDKInitResult.GPInitErrorCodeNet:
				// ToastUtils.show(mActivity,"初始化回调:初始化网络错误");
				retryInit();
				break;
			case GPSDKInitResult.GPInitErrorCodeNone:
				// ToastUtils.show(mActivity,"初始化回调:初始化成功");
				mIsInitSuc = true;
				break;
			}
		}
	};

	private int mInitCount = 0;

	/**
	 * 重试初始化3次
	 */
	public void retryInit() {
		if (mInitCount >= 3) {
			return;
		}
		mInitCount++;
		HyLog.i(TAG, "初始化失败，进行第" + mInitCount + "次初始化重试");
		// ToastUtils.show(mActivity,"初始化失败，进行第" + mInitCount + "次初始化重试");
		GPApiFactory.getGPApi().initSdk(mActivity, APP_ID, APP_KEY, mInitObsv);
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

		// 调用登录
		if (mIsInitSuc) {
			GPApiFactory.getGPApi()
					.login(mActivity.getApplication(), mUserObsv);
		} else {
			HyLog.i(TAG, "请在初始化成功后再调用登录");
		}

	}

	/**
	 * 登录回调接口
	 */
	private IGPUserObsv mUserObsv = new IGPUserObsv() {
		@Override
		public void onFinish(final GPUserResult result) {
			switch (result.mErrCode) {
			case GPUserResult.USER_RESULT_LOGIN_FAIL:
				HyLog.i(TAG, "登录回调:登录失败");
				// 登录失败
				// 回调给游戏，登录失败
				mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "登录失败");
				break;
			case GPUserResult.USER_RESULT_LOGIN_SUCC:
				HyLog.i(TAG, "登录成功");
				// 登录成功
				isLogout = false;// 登录状态
				// 正常接入
				mChannelUserInfo.setChannelUserId(GPApiFactory.getGPApi()
						.getLoginUin());// 渠道uid
				mChannelUserInfo.setChannelUserName(GPApiFactory.getGPApi()
						.getAccountName());// 渠道用户名
				mChannelUserInfo.setToken(GPApiFactory.getGPApi()
						.getLoginToken());// 登录验证令牌(token)
				// 网络测试,依赖网络环境
				onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);
				break;
			}
		}
	};

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		GPApiFactory.getGPApi().logout();
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
		String productName = mPayParsms.getProductName();
		productName = money * mPayParsms.getExchange() + productName;
		GPSDKGamePayment payParam = new GPSDKGamePayment();
		payParam.mItemName = productName; // 订单商品的名称
		payParam.mPaymentDes = money*mPayParsms.getExchange() + productName;// 订单的介绍
		payParam.mItemPrice = money;
		payParam.mItemCount = Integer.valueOf("1");
		payParam.mCurrentActivity = mActivity;// 用户当前的activity
		payParam.mSerialNumber = mPayParsms.getOrderId();// 订单号，这里用时间代替（用户需填写订单的订单号）
		if(TextUtils.isEmpty( mPayParsms.getProductId())){
			payParam.mItemId = "1";
		}else{
			payParam.mItemId = mPayParsms.getProductId();// 商品编号ID
		}
		
		payParam.mReserved = "reserved string-&&" + System.currentTimeMillis();// 透传字段
		
		GPApiFactory.getGPApi().buy(payParam, mPayObsv);
	}

	/**
	 * 支付回调接口
	 */
	private IGPPayObsv mPayObsv = new IGPPayObsv() {
		@Override
		public void onPayFinish(GPPayResult payResult) {
			if (payResult == null) {
				return;
			}
			showPayResult(payResult);
		}
	};

	void showPayResult(final GPPayResult result) {
		switch (result.mErrCode) {
		case GPPayResult.GPSDKPayResultCodeSucceed:
			HyLog.d(TAG, "支付回调:购买成功");
			// 支付成功
			mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, "支付成功:");
			break;
		case GPPayResult.GPSDKPayResultCodePayBackground:
			HyLog.d(TAG, "支付回调:后台正在轮循购买");
			break;
		case GPPayResult.GPSDKPayResultCodeBackgroundSucceed:
			HyLog.d(TAG, "支付回调:后台购买成功");
			break;
		case GPPayResult.GPSDKPayResultCodeBackgroundTimeOut:
			HyLog.d(TAG, "支付回调:后台购买超时");
			break;
		case GPPayResult.GPSDKPayResultCodeCancel:
			HyLog.d(TAG, "支付回调:用户取消");
			// 支付取消
			mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, "支付取消:");
			break;
		case GPPayResult.GPSDKPayResultCodeNotEnough:
			HyLog.d(TAG, "支付回调:余额不足");
			break;
		case GPPayResult.GPSDKPayResultCodeOtherError:
			mPayCallBack.onPayCallback(HY_SdkResult.FAIL, "支付失败");
			HyLog.d(TAG, "支付回调:其他错误");
			break;
		case GPPayResult.GPSDKPayResultCodePayForbidden:
			HyLog.d(TAG, "支付回调:用户被限制");
			break;
		case GPPayResult.GPSDKPayResultCodePayHadFinished:
			HyLog.d(TAG, "支付回调:该订单已经完成");
			break;
		case GPPayResult.GPSDKPayResultCodeServerError:
			HyLog.d(TAG, "支付回调:服务器错误");
			break;
		case GPPayResult.GPSDKPayResultNotLogined:
			HyLog.d(TAG, "支付回调:无登陆");
			break;
		case GPPayResult.GPSDKPayResultParamWrong:
			HyLog.d(TAG, "支付回调:参数错误");
			break;
		case GPPayResult.GPSDKPayResultCodeLoginOutofDate:
			HyLog.d(TAG, "支付回调:登录态失效");
			break;
		default:
			HyLog.d(TAG, "支付回调:未知错误 " + result.toString());
			break;
		}
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
		GPApiFactory.getGPApi().exit(mExitObsv);

	}

	/**
	 * 退出界面回调接口
	 */
	private IGPExitObsv mExitObsv = new IGPExitObsv() {
		@Override
		public void onExitFinish(GPExitResult exitResult) {
			switch (exitResult.mResultCode) {
			case GPExitResult.GPSDKExitResultCodeError:
				HyLog.d(TAG, "退出回调:调用退出弹框失败");
				break;
			case GPExitResult.GPSDKExitResultCodeExitGame:
				HyLog.d(TAG, "退出回调:调用退出游戏，请执行退出逻辑");
				mExitCallback.onChannelExit();
				break;
			case GPExitResult.GPSDKExitResultCodeCloseWindow:
				HyLog.d(TAG, "退出回调:调用关闭退出弹框");
				break;
			}
		}
	};

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
		private String token;
		private int state;
		private JSONObject paramsJson;
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
				this.token = token;
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

	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据

		GuoPan_RoleInfo.typeId = gameRoleInfo.getTypeId();// 上传类型
															// 登录:HY_Constants.ENTER_SERVER
															// 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
		GuoPan_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		GuoPan_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		GuoPan_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		GuoPan_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		GuoPan_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		GuoPan_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		GuoPan_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		GuoPan_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		// Toast.makeText(paramActivity, gameRoleInfo.toString(),
		// Toast.LENGTH_SHORT).show();
		HyLog.d(TAG, "MethodManager-->setExtData");
	}

}