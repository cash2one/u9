package com.hygame;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.example.test.demo.MainActivity;
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
import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

public class QiHoo_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static QiHoo_MethodManager instance;
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
    private TokenInfo mTokenInfo;
	
	
	private QiHoo_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static QiHoo_MethodManager getInstance() {
		if (instance == null) {
			instance = new QiHoo_MethodManager();
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

		 dataFromAssets = new DataFromAssets(mActivity);
	        try
	        {

	            HyLog.d(TAG, "mIsLandscape:" + mIsLandscape);
	            HyLog.d(TAG, dataFromAssets.toString());
	        }
	        catch (Exception e)
	        {
	        	HyLog.d(TAG, "初始化参数不能为空");
	        }
		if (mIsLandscape) {
			// 这里是横屏，该渠道没有横竖屏配置,忽略
			HyLog.d(TAG, "这里是横屏");
		} else {
			// 如果有,就通过这个判断来设置相关
			HyLog.d(TAG, "这里是竖屏");
		}

		Matrix.setFlag(0);
		Matrix.init(paramActivity);
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
		this.mLoginCallBack = loginCallBack;
        HyLog.i(TAG, "doLogin-->mIsLandscape=" + mIsLandscape);
        if (isLogout)
        {
        	HyLog.i(TAG, "change account doLogin");
            // 如果登出，则使用360SDK的切换账号接口
            Matrix.invokeActivity(paramActivity,
                    getSwitchAccount(mIsLandscape), mAccountSwitchCallback);

            return;
        }

        Matrix.invokeActivity(paramActivity, getLoginIntent(mIsLandscape),
                mLoginCallback);

	}
	
	 private Intent getSwitchAccount(boolean isLandscape)
	    {
	        Bundle bundle = new Bundle();

	        // 界面相关参数，360SDK界面是否以横屏显示。
	        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
	                isLandscape);

	        // *** 以下非界面相关参数 ***
	        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
	        bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

	        // 必需参数，使用360SDK的切换账号模块。
	        bundle.putInt(ProtocolKeys.FUNCTION_CODE,
	                ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

	        Intent intent = new Intent(mActivity, ContainerActivity.class);
	        intent.putExtras(bundle);
	        return intent;
	    }



	/**
	 * 生成调用360SDK登录接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @return intent
	 */
	private Intent getLoginIntent(boolean isLandScape) {

		Intent intent = new Intent(mActivity, MainActivity.class);

		// 界面相关参数，360SDK界面是否以横屏显示。
		intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 必需参数，使用360SDK的登录模块。
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_LOGIN);

		// 是否显示关闭按钮
		intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, false/*
																	 * getCheckBoxBoolean
																	 * (R.id.
																	 * isShowClose
																	 * )
																	 */);

		// 可选参数，是否支持离线模式，默认值为false
		// intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE,
		// getCheckBoxBoolean(R.id.isSupportOffline));

		// 可选参数，是否在自动登录的过程中显示切换账号按钮
		// intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH,
		// getCheckBoxBoolean(R.id.isShowSwitchButton));

		// 可选参数，是否隐藏欢迎界面
		// intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME,
		// getCheckBoxBoolean(R.id.isHideWellcome));

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE,
		// getUiBackgroundPicPath());
		// 可选参数，指定assets中的图片路径，作为背景图
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS,
		// getUiBackgroundPathInAssets());
		// 可选参数，是否需要用户输入激活码，用于游戏内测阶段。如果不需激活码相关逻辑，客户传false或者不传入该参数。
		intent.putExtra(ProtocolKeys.NEED_ACTIVATION_CODE, false/*
																 * getCheckBoxBoolean
																 * (R.id.
																 * isNeedActivationCode
																 * )
																 */);

		// -- 以下参数仅仅针对自动登录过程的控制
		// 可选参数，自动登录过程中是否不展示任何UI，默认展示。
		// intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI,
		// getCheckBoxBoolean(R.id.isAutoLoginHideUI));

		// 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
		// intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN,
		// getCheckBoxBoolean(R.id.isShowDlgOnFailedAutoLogin));

		// -- 测试参数，发布时要去掉
		// intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG,
		// getCheckBoxBoolean(R.id.isDebugSocialShare));

		return intent;
	}


	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object object) {
		// 注销登录状态
		isLogout = true;
		// 回调给游戏注销成功
		getUserListener().onLogout(HY_SdkResult.SUCCESS, object);
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
			return;
		}
		HyLog.d(TAG, ".....请求应用服务器，开始pay支付");
		if (null == mChannelUserInfo) {
			HyLog.d(TAG, "服务器连接失败。。。  ");
			ToastUtils.show(mActivity, "服务器连接失败。。。");
		} else {
			if (!TextUtils.isEmpty(mChannelUserInfo.getUserId())) {
				// 这里是网络请求方法
				 mUserInfoTask.startWork(paramActivity,
				 HY_Constants.DO_PAY,"", this);
			}else{
				HyLog.e(TAG, "用户id获取失败,请重新登录");
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
		Intent intent = getPayIntent(paramActivity, mPayParsms);
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
                ProtocolConfigs.FUNC_CODE_PAY);
        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

        Matrix.invokeActivity(paramActivity, intent, mQhPayCallback);
	}

	/***
	 * 生成调用360SDK支付接口的Intent
	 * 
	 * @param isLandScape
	 * @param pay
	 * @return Intent
	 */
	protected Intent getPayIntent(Activity paramActivity, HY_PayParams payParsms) {
		mActivity = paramActivity;

        Bundle bundle = new Bundle();

        // mQihooPayInfo = getQihooPayInfo(isFixed);
        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
                mIsLandscape);

        // *** 以下非界面相关参数 ***

        // 设置QihooPay中的参数。
        // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
        bundle.putString(ProtocolKeys.ACCESS_TOKEN,
                mChannelUserInfo.getToken());

        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID,
                mChannelUserInfo.getChannelUserId());

        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, mPayParsms.getAmount()+"");

        // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
        bundle.putString(ProtocolKeys.RATE, mPayParsms.getExchange()+"");

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME,
              mPayParsms.getProductName());

        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID,mPayParsms.getProductId());

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, mPayParsms.getCallBackUrl());

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, HY_Utils.getAppName(paramActivity));

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME,
                mChannelUserInfo.getChannelUserName());

        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, mChannelUserInfo.getChannelUserId());

        // // 可选参数，应用扩展信息1，原样返回，最大255字符。
        // bundle.putString(ProtocolKeys.APP_EXT_1, mQihooPayInfo.getAppExt1());

        /*
         * // 可选参数，应用扩展信息2，原样返回，最大255字符。
         * bundle.putString(ProtocolKeys.APP_EXT_2, mQihooPayInfo.getAppExt2());
         */

        // 必选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID,
                mPayParsms.getOrderId());

        Intent intent = new Intent(paramActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
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

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				mIsLandscape);

		// 必需参数，使用360SDK的退出模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_QUIT);

//		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
//		bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

		Intent intent = new Intent(mActivity, ContainerActivity.class);
		intent.putExtras(bundle);

		Matrix.invokeActivity(mActivity, intent, new IDispatcherCallback() {

			@Override
			public void onFinished(String data) {
				// Log.d(TAG, "mQuitCallback, data is " + data);
				JSONObject json;
				try {
					json = new JSONObject(data);
					int which = json.optInt("which", -1);
					String label = json.optString("label");

					// Toast.makeText(mActivity,
					// "按钮标识：" + which + "，按钮描述:" + label,
					// Toast.LENGTH_LONG).show();

					switch (which) {
					case 0: // 用户关闭退出界面
						return;
					default:// 退出游戏
						// finish();
						mExitCallback.onChannelExit();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		HyLog.d(TAG, "已经执行doExitQuit。。。。");
	}

	/**
	 * 应用服务器通过此方法返回UserInfo
	 * 
	 * @param userInfo
	 */
	public void onGotUserInfo(HY_UserInfoVo userInfo, boolean isDolog) {
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
		updateUserInfoUi(isDolog);
	}

	private void updateUserInfoUi(boolean isDolog) {
		HyLog.d(TAG, "updateUserInfoUi.....");
		if (mChannelUserInfo != null && mChannelUserInfo.isValid()) {
			localXMUser = new HY_User(mChannelUserInfo.getUserId(),
					mChannelUserInfo.getChannelUserId(),
					mChannelUserInfo.getChannelUserName(),
					mChannelUserInfo.getToken());
			if (isDolog) {
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
		public void startWork(Activity mActivity, int state,
				String accessToken, final HY_UserInfoListener listener) {

			if (!isRunning) {
				this.mContext = mActivity;
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
					HyLog.d(TAG, "transType:"+resultVO.transType);
					// 通过返回的渠道类型，判断是调用的支付接口还是登录接口
					if ("CREATE_USER".equals(resultVO.transType)) {
						// 登录接口
						if (resultVO.responseCode.equals("0")) {
								HyLog.d(TAG, "登录接口返回success："
										+ resultVO.message);
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
							mLoginCallBack
									.onLoginFailed(
											HY_SdkResult.FAIL,
											"登录失败:"
													+ Integer
															.parseInt(resultVO.responseCode)
													+ resultVO.message);

						}

					} else if (resultVO.transType.equals(TransType.CREATE_ORDER
							.toString())) {
						// 支付接口
						if (resultVO.responseCode.equals("0")) {
							HyLog.d(TAG, "支付接口返回success：" + resultVO.message);
							mPayParsms.setOrderId(resultVO.orderId);// 传入应用订单号
							HyLog.d(TAG, "创建订单成功后-->mChannel"
									+ "UserInfo："
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
				ToastUtils.show(mActivity, "网络异常，请稍后再试");
			}
		}

		@Override
		public void urlRequestException(CallBackResult result) {
			isRunning = false;
			HyLog.e(TAG, "urlRequestException" + result.url + ","
					+ result.param + "," + result.backString);
			ToastUtils.show(mActivity, "网络异常，请稍后再试");

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
		// 渠道生命周期要求
		/*
		 * if (downjoy != null) { downjoy.resume(paramActivity); }
		 */

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

		Matrix.destroy(paramActivity);
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		// 提供了两个接口
		// 1、游戏上传，我们在这里传给 渠道
		// 2、保存最后一次上传信息，根据渠道需求进行上传信息
		// 以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据

		QiHoo_RoleInfo.typeId = gameRoleInfo.getTypeId();// 上传类型
															// 登录:HY_Constants.ENTER_SERVER
															// 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
		QiHoo_RoleInfo.roleId = gameRoleInfo.getRoleId();// 角色id
		QiHoo_RoleInfo.roleName = gameRoleInfo.getRoleName();// 角色名
		QiHoo_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();// 角色等级
		QiHoo_RoleInfo.zoneId = gameRoleInfo.getZoneId();// 区服id
		QiHoo_RoleInfo.zoneName = gameRoleInfo.getZoneName();// 区服名
		QiHoo_RoleInfo.balance = gameRoleInfo.getBalance(); // 用户余额
		QiHoo_RoleInfo.vip = gameRoleInfo.getVip();// vip等级
		QiHoo_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		QiHoo_RoleInfo.partyName = gameRoleInfo.getPartyName();// 帮派名称
		// 这里是为了显示例子,正式的时候就不要弹Toast了
		// Toast.makeText(paramActivity, gameRoleInfo.toString(),
		// Toast.LENGTH_SHORT).show();
		HyLog.d(TAG, "MethodManager-->setExtData");
	}
	
	
	 // 支付的回调
    protected IDispatcherCallback mQhPayCallback = new IDispatcherCallback()
    {

        @Override
        public void onFinished(String data)
        {

            HyLog.d(TAG, "mPayCallback, onFinished。。已经输出的支付结果 = " + data);
            if (TextUtils.isEmpty(data))
            {
                return;
            }

            boolean isCallbackParseOk = false;
            JSONObject jsonRes;
            try
            {
                jsonRes = new JSONObject(data);
                // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中。
                // error_msg 状态描述
                int errorCode = jsonRes.optInt("error_code");
                String errorMsg = jsonRes.optString("error_msg");
                String text = "状态码:" + errorCode + ", 状态描述：" + errorMsg;
                isCallbackParseOk = true;
                switch (errorCode)
                {
                case 0:
                	  mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, text);
                      HyLog.d(TAG, "isAccessTokenValid=true:" + text);
                      break;
                case 1:
                	  mPayCallBack.onPayCallback(HY_SdkResult.FAIL, text);
                      HyLog.d(TAG, "isAccessTokenValid=true:" + text);
                      break;
                case -1:
                	  mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, text);
                      HyLog.d(TAG, "isAccessTokenValid=true:" + text);
                      break;
                case -2:
                {
                    isAccessTokenValid = true;
                
                   
                    // 回调给用户支付结果
                    mPayCallBack.onPayCallback(HY_SdkResult.ISDEALING, text);
                    HyLog.d(TAG, "isAccessTokenValid=true:" + text);

                }
                    break;
                case 4010201:
                    isAccessTokenValid = false;
                    mPayCallBack.onPayCallback(errorCode,
                            "AccessToken已失效，请重新登录");

//                    ToastUtils.show(mActivity, "AccessToken已失效，请重新登录");
                    break;
                default:
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            // 用于测试数据格式是否异常。
            if (!isCallbackParseOk)
            {
                ToastUtils.show(mActivity, "严重错误！！接口返回数据格式错误！！");
            }
        }
    };
    
	  // 账户切换的回调
    private IDispatcherCallback mAccountSwitchCallback = new IDispatcherCallback()
    {
        public void onFinished(String data)
        {
            HyLog.d(TAG, "mAccountSwitchCallback, data is " + data);
            procGotTokenInfoResult(mActivity, data);
        }
    };
    // 登录、注册的回调
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback()
    {
        public void onFinished(String data)
        {
        	HyLog.d(TAG, "mLoginCallback, data is " + data);
            
            procGotTokenInfoResult(mActivity, data);
        }
    };
    /**
     * procGotTokenInfoResult(解析360渠道返回的登录结果)
     * 
     * @param data
     *            渠道返回的数据
     * @return
     */
    public void procGotTokenInfoResult(final Activity paramActivity, String data)
    {
        mActivity = paramActivity;
     
        boolean isCallbackParseOk = false;

        if (!TextUtils.isEmpty(data))
        {
            JSONObject jsonRes;

            try
            {
                jsonRes = new JSONObject(data);
                // error_code 状态码： 0 登录成功， -1 登录取消， 其他值：登录失败
                int errorCode = jsonRes.optInt("error_code");
                String dataString = jsonRes.optString("data");

                switch (errorCode)
                {
                case 0:
                    isLogout=false;
                    TokenInfo tokenInfo = TokenInfo.parseJson(dataString);
                    if (tokenInfo != null && tokenInfo.isValid())
                    {
                        isCallbackParseOk = true;
                        isAccessTokenValid = true;
                        QiHoo_GetUserInfo getUserInfo = new QiHoo_GetUserInfo(paramActivity);
                        mChannelUserInfo.setToken(tokenInfo.getAccessToken());
                        getUserInfo.getUserInfo(mChannelUserInfo, new CheckAfter<String>() {
							
							@Override
							public void afterSuccess(String arg0) {
								 onGotTokenInfo(paramActivity,HY_Constants.DO_LOGIN);
								 
								HyLog.d(TAG, "获取成功:"+arg0);
							}
							
							@Override
							public void afterFailed(String arg0, Exception arg1) {
								
								HyLog.d(TAG, "获取失败");
							}
						});
//                        mChannelUserInfo.setChannelUserId("");
//                        mChannelUserInfo.setChannelUserName("");
                        
//                       
                    }

                    break;

                case -1:
//                    ToastUtils.show(paramActivity, "用户取消登录");
                	HyLog.d(TAG,  "用户取消登录");
                	mLoginCallBack.onLoginFailed(HY_SdkResult.CANCEL, "用户登录360取消");
                    return;

                default:
                    if (!TextUtils.isEmpty(dataString))
                    {
                    	mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "用户登录360失败");
                        HyLog.d(TAG,  "用户登录360失败");
                    }
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (!isCallbackParseOk)
        {
            ToastUtils.show(mActivity, "未获取到Access Token");
        }

    }
    
}