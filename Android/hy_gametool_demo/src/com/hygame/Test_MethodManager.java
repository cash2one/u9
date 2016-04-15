package com.hygame;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.hy.game.demo.Demo_UserInfo;
import com.hy.game.demo.FloatManager;
import com.hy.game.demo.HY_CheckPay;
import com.hy.game.demo.HY_GameManager;
import com.hy.game.demo.LoginCallback;
import com.hy.game.demo.LogoutCallback;
import com.hy.game.demo.PayCallback;
import com.hy.game.demo.SwitchCallback;
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
import com.hy.gametools.manager.HY_UserManagerBase;
import com.hy.gametools.manager.HY_AccountListener;
import com.hy.gametools.utils.CallBackResult;
import com.hy.gametools.utils.Constants;
import com.hy.gametools.utils.DataFromAssets;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.utils.HttpUtils;
import com.hy.gametools.utils.ResultJsonParse;
import com.hy.gametools.utils.HyLog;
import com.hy.gametools.utils.ProgressUtil;
import com.hy.gametools.utils.ResponseResultVO;
import com.hy.gametools.utils.TransType;
import com.hy.gametools.utils.ToastUtils;
import com.hy.gametools.utils.UrlRequestCallBack;

public class Test_MethodManager extends HY_UserManagerBase implements
		HY_AccountListener, HY_UserInfoListener {
	private static final String TAG = "HY";
	private static Test_MethodManager instance;
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

	private HY_GameManager hygame = new HY_GameManager();

	private Test_MethodManager() {
		mChannelUserInfo = new HY_UserInfoVo();
	}

	public static Test_MethodManager getInstance() {
		if (instance == null) {
			instance = new Test_MethodManager();
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
	private void initChannelDate(final Activity paramActivity) {
		dataFromAssets = new DataFromAssets(paramActivity);
		try {

			HyLog.d(TAG, "mIsLandscape:" + mIsLandscape);
			HyLog.d(TAG, dataFromAssets.toString());
		} catch (Exception e) {
			HyLog.d(TAG, "初始化参数不能为空");
		}

		hygame.init(paramActivity, new SwitchCallback() {

			@Override
			public void onSuccess(Demo_UserInfo userInfo) {
				isLogout = false;
				mChannelUserInfo.setChannelUserId(userInfo.getUserId());
				mChannelUserInfo.setChannelUserName(userInfo.getUserName());
				mChannelUserInfo.setToken(userInfo.getToken());
				localXMUser = new HY_User(mChannelUserInfo.getUserId(),
						mChannelUserInfo.getChannelUserId(), mChannelUserInfo
								.getChannelUserName(), mChannelUserInfo
								.getToken());
				onGotTokenInfo(paramActivity, HY_Constants.SWITCH_ACCOUNT);

			}
		}, new LogoutCallback() {

			@Override
			public void onSuccess() {
				FloatManager.removeView(paramActivity);
				getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");

			}
		}, mIsLandscape);
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

		hygame.doLogin(paramActivity, new LoginCallback() {

			@Override
			public void onSuccess(Demo_UserInfo userInfo) {

				HyLog.d(TAG, "成功收到登录回调");
				isLogout = false;
				mChannelUserInfo.setChannelUserId(userInfo.getUserId());
				mChannelUserInfo.setChannelUserName(userInfo.getUserName());
				mChannelUserInfo.setToken(userInfo.getToken());
				FloatManager.createView(paramActivity);
				onGotTokenInfo(paramActivity, HY_Constants.DO_LOGIN);
			}
		});
	}

	/**
	 * 注销接口
	 */
	@Override
	public void doLogout(final Activity paramActivity, Object paramObject) {

		// mChannelUserInfo=null;
		hygame.doLogout(new LogoutCallback() {
			@Override
			public void onSuccess() {
				isLogout = true;
				getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");
				FloatManager.removeView(paramActivity);
				HyLog.d(TAG, "执行logout");
			}
		});
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
		HyLog.d(TAG, ".....请求应用服务器，开始pay支付");

		if (null == mChannelUserInfo) {
			HyLog.d(TAG, "服务器连接失败。。。  ");
			ToastUtils.show(mActivity, "服务器连接失败。。。");
		} else {
			if (!TextUtils.isEmpty(mChannelUserInfo.getUserId())) {
				 mUserInfoTask.startWork(paramActivity,HY_Constants.DO_PAY,"", this);
//				startPayAfter(paramActivity);
			} else {

				HyLog.d(TAG, "V5账号登录失败。。。  ");
				ToastUtils.show(mActivity, "V5账号登录失败。。。  ");
			}
		}
	}

	/**
	 * startPayAfterGetOrder 需要获取订单后再开始提交支付
	 * 
	 * @param
	 * @return
	 */
	private void startPayAfter(Activity paramActivity) {
		// 通过订单号调用渠道的支付，与各个渠道的支付端进行交流,在支付之前，在提交的时候，v5服务器返回的订单后调用startPayAfterGetOrder
		HY_GameManager hygame = new HY_GameManager();
		final HY_CheckPay checkPay = new HY_CheckPay(paramActivity);
		hygame.doPay(paramActivity, mChannelUserInfo, mPayParsms, new PayCallback() {

			@Override
			public void onResultCode(int resultCode) {
				switch (resultCode) {
				case 0:
					HyLog.d(TAG, "成功收到:支付成功回调");
					checkPay.checkPayInfo(mPayParsms, mChannelUserInfo.getChannelUserId(), 0);
					mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS, "支付成功");
					break;

				case 1:
					HyLog.d(TAG, "成功收到:支付失败回调");
					checkPay.checkPayInfo(mPayParsms, mChannelUserInfo.getChannelUserId(), 1);
					mPayCallBack.onPayCallback(HY_SdkResult.FAIL, "支付失败");
					break;

				case 2:
					HyLog.d(TAG, "成功收到:支付取消回调");
					mPayCallBack.onPayCallback(HY_SdkResult.CANCEL, "支付取消");
					break;
				}

			}
		});
		HyLog.d(TAG, "调用支付，已经获取到参数。。。。。。。。。");
	}

	/**
	 * 使用SDK的退出接口
	 * 
	 */
	@Override
	public void doExitQuit(final Activity paramActivity,
			HY_ExitCallback paramExitCallback) {
		// 如果没有第三方渠道的接口，则直接回调给用户，让用户自己定义自己的退出界面

		mActivity = paramActivity;
		mExitCallback = paramExitCallback;

		AlertDialog.Builder builder = new Builder(paramActivity);
		builder.setTitle("请选择退出画面");
		builder.setNegativeButton("游戏退出界面",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mExitCallback.onGameExit();
					}
				});
		builder.setPositiveButton("渠道退出页面",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder builder = new Builder(paramActivity);
						builder.setTitle("渠道退出画面");
						builder.setNegativeButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										mExitCallback.onChannelExit();
									}
								});
						builder.setPositiveButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

						builder.show();
					}
				});

		builder.show();
		HyLog.d(TAG, "已经执行doExitQuit。。。。");
	}

	/**
	 * 应用服务器通过此方法返回UserInfo
	 * 
	 * @param userInfo
	 */
	public void onGotUserInfo(HY_UserInfoVo userInfo,boolean isDoLogin) {
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
		updateUserInfoUi(isDoLogin);
	}

	private void updateUserInfoUi(boolean isDoLogin) {
		HyLog.d(TAG, "updateUserInfoUi.....");
		if (mChannelUserInfo != null && mChannelUserInfo.isValid()) {
			localXMUser = new HY_User(mChannelUserInfo.getUserId(),
					mChannelUserInfo.getChannelUserId(),
					mChannelUserInfo.getChannelUserName(),
					mChannelUserInfo.getToken());
			 if(isDoLogin){
				    //回调CP 客户端登录成功
		            mLoginCallBack.onLoginSuccess(localXMUser);
		            }else{
		            	//回调CP切换账号成功
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

	public void onActivityResult(Activity mActivity,int requestCode, int resultCode, Intent data){
		HyLog.d(TAG, "MethodManager-->onActivityResult");
	}
	@Override
	public void onDestroy(Activity paramActivity) {
		FloatManager.removeView(paramActivity);
		HyLog.d(TAG, "MethodManager-->onDestroy");
	}

	@Override
	public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo) {
		Toast.makeText(paramActivity, gameRoleInfo.toString(),
				Toast.LENGTH_SHORT).show();
		HyLog.d(TAG, "MethodManager-->setExtData");
	}

	/**
	 * HY_HttpInfoTask 类描述： 用户信息的网络获取类 创建人：smile
	 */
	class HY_HttpInfoTask implements UrlRequestCallBack {

		private static final String TAG = "WX";
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
						// 登录接口
						if (resultVO.responseCode.equals("0")) {

							HyLog.d(TAG, "登录接口返回success：" + resultVO.message);
							mChannelUserInfo.setUserId(resultVO.userId);
							if (state == HY_Constants.DO_LOGIN) {
								userInfo_listener.onGotUserInfo(mChannelUserInfo, true);
							} else {
								userInfo_listener.onGotUserInfo(mChannelUserInfo, false);
							}
						} else {
							HyLog.d(TAG, "登录接口返回fail：" + resultVO.message);
							if (null != mProgress) {

								ProgressUtil.dismiss(mProgress);
								mProgress = null;
							}
							mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL,
											"登录失败:"+ Integer.parseInt(resultVO.responseCode)
													+ "" + resultVO.message);

						}

					} else if (resultVO.transType.equals(TransType.CREATE_ORDER
							.toString())) {
						// 支付接口
						if (resultVO.responseCode.equals("0")) {
							
								// 获取服务器生成的订单号,并传给游戏
								HyLog.d(TAG, "支付接口返回success：" + resultVO.message);
								HyLog.d(TAG, "订单号:-->"+resultVO.orderId);
								mPayParsms.setOrderId(resultVO.orderId);// 传入应用订单号
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
				mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "网络异常，请稍后再试");
				ToastUtils.show(mActivity, "网络异常，请稍后再试");
			}
		}

		@Override
		public void urlRequestException(CallBackResult result) {
			isRunning = false;
			HyLog.e(TAG, "urlRequestException" + result.url + ","
					+ result.param + "," + result.backString);
			ToastUtils.show(mActivity, "网络异常，请稍后再试");

			mLoginCallBack.onLoginFailed(HY_SdkResult.FAIL, "网络异常,请稍后再试");

			if (null != mProgress) {

				ProgressUtil.dismiss(mProgress);
				mProgress = null;
			}
		}

	}
}