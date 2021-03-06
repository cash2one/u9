package com.hygame;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;
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
import com.kaopu.supersdk.api.KPSuperSDK;
import com.kaopu.supersdk.callback.KPAuthCallBack;
import com.kaopu.supersdk.callback.KPGetCheckUrlCallBack;
import com.kaopu.supersdk.callback.KPLoginCallBack;
import com.kaopu.supersdk.callback.KPLogoutCallBack;
import com.kaopu.supersdk.callback.KPPayCallBack;
import com.kaopu.supersdk.model.UserInfo;
import com.kaopu.supersdk.model.params.PayParams;

public class KaoPu_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static KaoPu_MethodManager instance;
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

	private String checkUrl;

	private KaoPu_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static KaoPu_MethodManager getInstance() {
		if (instance == null) {
			instance = new KaoPu_MethodManager();
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
		int screenType = 1;
		dataFromAssets = new DataFromAssets(paramActivity);
		try {
			HyLog.d(TAG, dataFromAssets.toString());
		} catch (Exception e) {
			HyLog.d(TAG, "初始R化参数不能为空");
		}
		if (mIsLandscape) {
			// 这里是横屏，该渠道没有横竖屏配置,忽略
			screenType = 1;
			HyLog.d(TAG, "这里是横屏");
		} else {
			// 如果有,就通过这个判断来设置相关
			screenType = 2;
			HyLog.d(TAG, "这里是竖屏");
		}

		// 判断是否MUI, 启动开启浮窗引导页
		KPSuperSDK.startGuide(mActivity);

//		// 设置获取登录验证URL 的回调对象
		KPSuperSDK.setGetCheckUrlCallBack(new KPGetCheckUrlCallBack() {

			@Override
			public void onGetCheckUrlSuccess(String arg0) {
				checkUrl = arg0;
				HyLog.d(TAG, "check url:" + arg0);
				onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);
			}

			@Override
			public void onGetCheckUrlFailed() {
				HyLog.d(TAG, "check url:error" );
			}
		});

		HashMap<String, String> configData = new HashMap<String, String>();
		// 若在此时传入配置数据，则会相对应的覆盖 kaopu_game_config.json 中的数据
		// configData.put("KP_Channel ", "kaopu");// 请不要在这里put此参数.即使put
		// 了，也是读取kaopu_game_config.json中的参数值
		// configData.put("ChannelKey", "k  aopu"); // 请不要在这里put此参数.即使put
		// 了，也是读取kaopu_game_config.json中的参数值
		configData.put("gameName", HY_Utils.getAppName(paramActivity)); // 游戏名 :
																		// 将在支付页面中显示,
																		// 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
		configData.put("screenType", screenType + ""); // 屏幕方向 横屏：1 竖屏：2,
														// 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
		configData.put("fullScreen", "true"); // 是否全屏（全屏即: 不带状态栏） 全屏：true
												// 不全屏：false,
												// 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
		configData.put("param", ""); // 自定义参数,这里设置的话，将会覆盖kaopu_game_config.json中的参数值

		// 对应用进行授权,若无授权或者是授权失败,将导致无法使用本支付SDK
		// 无特殊需要，第二个参数(configData)直接传 null 即可
		KPSuperSDK.auth(mActivity, configData, new KPAuthCallBack() {
			@Override
			public void onAuthSuccess() {
				HyLog.d(TAG, "初始化成功");

				// 注册注销账号时的回调,在授权成功之后，用户注销账号前调用都可以
				KPSuperSDK.registerLogoutCallBack(new KPLogoutCallBack() {
					@Override
					public void onSwitch() {

					}

					@Override
					public void onLogout() {
						// 注销成功
						isLogout = true;
						getUserListener()
								.onLogout(HY_SdkResult.SUCCESS, "注销成功");
					}
				});
			}

			@Override
			public void onAuthFailed() {

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

		KPSuperSDK.login(mActivity, new KPLoginCallBack() {
			@Override
			public void onLoginSuccess(UserInfo info) {
				HyLog.d(TAG, "登录成功");
		
				// 登录成功
				isLogout = false;// 登录状态
				// 正常接入
				mChannelUserInfo.setChannelUserId(info.getUserid());// 渠道uid
				mChannelUserInfo.setChannelUserName(info.getUsername());// 渠道用户名
				mChannelUserInfo.setToken(info.getToken());// 登录验证令牌(token)
				// 网络测试,依赖网络环境
				KPSuperSDK.getCheckUrl();
			}

			@Override
			public void onLoginFailed() {
				// Toast.makeText(mActivity, "登录失败！", Toast.LENGTH_LONG).show();
				// 登录失败
				// 回调给游戏，登录失败
				mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "登录失败:");
			}

			@Override
			public void onLoginCanceled() {
				// Toast.makeText(mActivity, "登录取消！", Toast.LENGTH_LONG).show();
				// 登录取消
				mLoginCallBack.onLoginFailed(HY_SdkResult.CANCEL, "登录取消:");
			}
		});
	}

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		KPSuperSDK.logoutAccount();
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
		// }
		// else
		// {
		// HyLog.d(TAG, "用户没有登录，请重新登录后再支付");
		// ToastUtils.show(paramActivity, "用户没有登录，请重新登录后再支付");
		// return;
		// }

	}

	/**
	 * startPayAfter 生成订单后,执行渠道支付方法
	 * 
	 * @param
	 * @return
	 */
	private void startPayAfter(final Activity paramActivity) {
		HyLog.d(TAG, "调用支付，已经获取到参数。。。。。。。。。");

		if (!KPSuperSDK.isLogin()) {
			Toast.makeText(mActivity, "请先登录", Toast.LENGTH_LONG).show();
			return;
		}

		int money = mPayParsms.getAmount();// 单位:分
		money = money / 100;// 换算成: 元

		String productName = mPayParsms.getProductName();
		productName = money * 10 + productName;
		int exchange = mPayParsms.getExchange();
		HyLog.d(TAG, "exchange:" + exchange);
		PayParams payParams = new PayParams();
		payParams.setAmount(money); // 充值金额
		payParams.setGamename(HY_Utils.getAppName(paramActivity)); // 充值游戏
		payParams.setGameserver(KaoPu_RoleInfo.zoneName); // 充值游戏服务器
		payParams.setRolename(KaoPu_RoleInfo.roleName); // 充值角色名称
		payParams.setOrderid(mPayParsms.getOrderId()); // 唯一订单号

		// 创建订单配置
		payParams.setCurrencyname(mPayParsms.getProductName()); // 虚拟货币名称
		payParams.setProportion(mPayParsms.getExchange()); // RMB和虚拟货币的比,例:10表示比例为1RMB:10虚拟货币
		payParams.setCustomPrice(false);// 这个设置为true， setCustomText才会生效
		// payParams.setCustomText(productName);
		HyLog.d(TAG, "money:" + money);
		HyLog.d(TAG, "appName:" + HY_Utils.getAppName(paramActivity));
		HyLog.d(TAG, "zoneName:" + KaoPu_RoleInfo.zoneName);
		HyLog.d(TAG, "roleName:" + KaoPu_RoleInfo.roleName);
		HyLog.d(TAG, "orderid:" + mPayParsms.getOrderId());
		HyLog.d(TAG, "Currencyname:" + mPayParsms.getProductName());
		HyLog.d(TAG, "getExchange:" + mPayParsms.getExchange());

		/**
		 * 支付接口,需要同时提供支付和登录的回调接口,若用户没用登录,将直接跳转至登录界面 如果不使用回调,传null即可
		 */
		KPSuperSDK.pay(mActivity, payParams, new KPPayCallBack() {
			@Override
			public void onPaySuccess() {
				// 支付成功
				mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, "支付成功:");
			}

			@Override
			public void onPayFailed() {
				// 支付取消
				mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, "支付失败:");
			}

			@Override
			public void onPayCancle() {
				// 支付取消
				mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, "支付取消:");
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
		mExitCallback = paramExitCallback;
		HyLog.d(TAG, "已经执行doExitQuit。。。。");
		KPSuperSDK.closeFloatView(mActivity);
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
//					checkUrl = checkUrl.substring(checkUrl.indexOf("?")+1);
					checkUrl = checkUrl.replaceAll("&",  ",");
//					checkUrl = checkUrl.replaceAll("\\",  "");
					HyLog.d(TAG, "checkUrl:"+checkUrl);
					JSONObject json = new JSONObject();
					try {
						json.put("checkUrl", checkUrl);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Map<String, String> map = HttpUtils
							.getLoginInfoRequest(mChannelUserInfo);
					map.put("Ext", json.toString());
					HyLog.d(TAG, "map:"+map.toString());
					
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
		KPSuperSDK.closeFloatView(paramActivity);
	}

	@Override
	public void onResume(Activity paramActivity) {
		HyLog.d(TAG, "MethodManager-->onStop");
		KPSuperSDK.showFloatView(mActivity);
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
		KPSuperSDK.release();
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据
		// KaoPu_RoleInfo kaopu = new KaoPu_RoleInfo();
		KaoPu_RoleInfo.typeId = gameRoleInfo.getTypeId();// 上传类型
															// 登录:HY_Constants.ENTER_SERVER
															// 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
		KaoPu_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		KaoPu_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		KaoPu_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		KaoPu_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		KaoPu_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		KaoPu_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		KaoPu_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		KaoPu_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		// Toast.makeText(paramActivity, gameRoleInfo.toString(),
		// Toast.LENGTH_SHORT).show();
		// HyLog.d(TAG, kaopu.toString());
		HyLog.d(TAG, "MethodManager-->setExtData");

		if (KPSuperSDK.isLogin()) {
			if (KaoPu_RoleInfo.typeId == HY_Constants.ENTER_SERVER) {
				int roleLevel = Integer.valueOf(KaoPu_RoleInfo.roleLevel);// 角色等级
				String serviceName = KaoPu_RoleInfo.zoneName; // 服务器名称
				String roleName = KaoPu_RoleInfo.roleName; // 角色名称
				String id = KaoPu_RoleInfo.roleId;// 游戏方对外用户
													// ID，即：cp自身用户的userid（不可为空）
				KPSuperSDK.setUserGameRole(mActivity, serviceName, roleName,
						id, roleLevel);
			} else {
			}
		} else {
			Toast.makeText(mActivity, "请先登录", Toast.LENGTH_LONG).show();
		}
	}

}