package com.hygame;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.hy.gametools.manager.HY_Utils;
import com.hy.gametools.utils.HY_UserInfoVo;
import com.hy.gametools.manager.HY_UserManagerBase;
import com.hy.gametools.manager.HY_AccountListener;
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
import com.mumayi.paymentmain.business.FindUserDataListener;
import com.mumayi.paymentmain.business.ResponseCallBack;
import com.mumayi.paymentmain.business.onLoginListener;
import com.mumayi.paymentmain.business.onTradeListener;
import com.mumayi.paymentmain.ui.PaymentCenterInstance;
import com.mumayi.paymentmain.ui.PaymentUsercenterContro;
import com.mumayi.paymentmain.ui.pay.MMYInstance;
import com.mumayi.paymentmain.ui.usercenter.PaymentFloatInteface;
import com.mumayi.paymentmain.util.PaymentConstants;
import com.mumayi.paymentmain.util.PaymentLog;
import com.mumayi.paymentmain.vo.UserBean;


public class MUMAYI_MethodManager extends HY_UserManagerBase implements
        HY_AccountListener, HY_UserInfoListener,onLoginListener,onTradeListener
{
    private static final String TAG = "HY";
    private static MUMAYI_MethodManager instance;
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
    private PaymentUsercenterContro userCenter;
   

    private MUMAYI_MethodManager()
    {
        mChannelUserInfo=new HY_UserInfoVo();
    }

    public static MUMAYI_MethodManager getInstance()
    {
        if (instance == null)
        {
            instance = new MUMAYI_MethodManager();
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
       
    }
    
    private PaymentCenterInstance mumayi_instance = null;

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
        if(mIsLandscape){
        	//这里是横屏，该渠道没有横竖屏配置,忽略
        	HyLog.d(TAG, "这里是横屏");
        }else{
        	//如果有,就通过这个判断来设置相关
        	HyLog.d(TAG, "这里是竖屏");
        }
        String appkey = HY_Utils.getManifestMeta(paramActivity, "MUMAYI_APPKEY");
        // 初始化支付SDK用户中心
        mumayi_instance = PaymentCenterInstance.getInstance(mActivity);
        mumayi_instance.initial(appkey,HY_Utils.getAppName(paramActivity)); 
        // 设置是否开启bug模式， true打开可以显示Log日志， false不显示
        mumayi_instance.setTestMode(false);
        // 设置监听器
        mumayi_instance.setListeners(this);
        // 设置切换完账号后是否自动跳转登陆
        mumayi_instance.setChangeAccountAutoToLogin(false);
        
        PaymentFloatInteface floatInitface = mumayi_instance.createFloat();
        floatInitface.show();
        
        mumayi_instance.findUserData(new FindUserDataListener() {
			
			@Override
			public void findUserDataComplete() {
				HyLog.d(TAG, "账号查找流程完成");
			}
		});
        
           userCenter = mumayi_instance.getUsercenterApi(mActivity);
    }



    

    /**
     * 注销接口回调
     */
    @Override
    public void onLoginOut(String loginOutCallBackStr)
    {
        try
        {
            JSONObject json = new JSONObject(loginOutCallBackStr);
            String code = json.getString("loginOutCode");
            if (code.equals("success"))
            {
                // 注销成功
                getUserListener().onLogout(HY_SdkResult.SUCCESS, "注销成功");
            }
            else
            {
                // 注销失败
            	getUserListener().onLogout(HY_SdkResult.FAIL, "注销失败");
            }
        }
        catch (JSONException e)
        {
            PaymentLog.getInstance().E("WelcomeActivity", e);
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
     
        userCenter.go2Login();
    }

    /**
     * 登录接口回调
     */
    @Override
    public void onLoginFinish(String loginResult)
    {
        try
        {
            if (loginResult != null)
            {
                JSONObject loginJson = new JSONObject(loginResult);
                String loginState = loginJson.getString(PaymentConstants.LOGIN_STATE);
                // 登陆成功
                if (loginState != null
                        && loginState.equals(PaymentConstants.STATE_SUCCESS))
                {
                    String uname = loginJson.getString("uname");
                    String uid = loginJson.getString("uid");
                    String token = loginJson.getString("token");
                    String session = loginJson.getString("session");
            		// 登录成功		
                    token = token +"&"+session;
            		isLogout = false;//登录状态
            		
            		//设置辉耀服务器UID
            		//这里的uid是指我们的uid,服务端生成,目前服务端还没OK，我们测试用渠道的,后续可以删除
            		
            		//正常接入
            		mChannelUserInfo.setChannelUserId(uid);//渠道uid
            		mChannelUserInfo.setChannelUserName(uname);//渠道用户名
            		mChannelUserInfo.setToken(token);//登录验证令牌(token)

            		//网络测试,依赖网络环境
            		onGotTokenInfo(mActivity, HY_Constants.DO_LOGIN);
                }
                else
                {
                    // 登录失败
                    String error = loginJson.getString("error");
                    if (error != null && error.trim().length() > 0
                            && error.equals("cancel_login"))
                    {
                        // 用如果用户在登陆界面选择退出登陆界面，应当在此重新调用进入登陆界面
                    	mLoginCallBack.onLoginFailed(HY_SdkResult.CANCEL, "取消登录:"+error);
                    	//mumayi_instance.go2Login(context);
                    }
                    else if (error != null && error.trim().length() > 0)
                    {
                        // 正常登陆失败的原因
                		//登录失败
                		//回调给游戏，登录失败
                        mLoginCallBack.onLoginFailed( HY_SdkResult.FAIL, "登录失败:" + error );
                    }
                }
            }
        }
        catch (JSONException e)
        {
            PaymentLog.getInstance().E("WelcomeActivity", e);
        }
    }
    
    /**
     * 注销接口
     */
    @Override
    public void doLogout(final Activity paramActivity,Object object)
    {
		// 获取当前用户信息
		UserBean user = PaymentConstants.NOW_LOGIN_USER;
		
		userCenter.loginOut(mActivity, user.getName(), new ResponseCallBack() {
			@Override
			public void onSuccess(Object obj) {
				try {
					JSONObject loginoutJson = (JSONObject) obj;
					String loginoutCode = loginoutJson.getString("loginOutCode");
					if (loginoutCode.equals("success")) {
						// 注销成功之后回到登录界面
						//Logout成功:
						isLogout = true;
						getUserListener().onLogout(HY_SdkResult.SUCCESS, "账号注销成功");
					} else {
						// 注销失败
						//Logout失败:
						getUserListener().onLogout(HY_SdkResult.FAIL, obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(Object obj) {
				// 注销失败
				//Logout失败:
				getUserListener().onLogout(HY_SdkResult.FAIL, obj);
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
            }else if (!TextUtils.isEmpty(mChannelUserInfo.getUserId()))
                {
                	//这里是网络请求方法
                    mUserInfoTask.startWork(paramActivity, HY_Constants.DO_PAY,"", this);
					
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
        
        int money = mPayParsms.getAmount();//单位:分
        money = money/100;//换算成: 元
        String productName = mPayParsms.getProductName(); 
        //没有传订单，预留接口 传订单号
        String body = mPayParsms.getOrderId();//这里一般是跟支付回调一起回调根据需求填写
        userCenter.pay(mActivity, productName, String.valueOf(money), body);
    }

	/**
	 * 支付接口回调
	 */
	@Override
	public void onTradeFinish(String tradeType, int tradeCode, Intent intent) {
		// 可在此处获取到提交的商品信息
		if (tradeCode == MMYInstance.PAY_RESULT_SUCCESS) {
			// 在每次支付回调结束时候，调用此接口检查用户是否完善了资料
			userCenter.checkUserState(mActivity);
			//支付成功
			mPayCallBack.onPayCallback(HY_SdkResult.SUCCESS,"支付成功");	
		} else if (tradeCode == MMYInstance.PAY_RESULT_FAILED) {
			//支付失败
			mPayCallBack.onPayCallback(HY_SdkResult.FAIL,"支付失败");
		}
	}    
    
    /**
     * 
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
		
		mExitCallback.onGameExit();
		
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
    	userCenter.finish();
    	mumayi_instance.exit();
        HyLog.d(TAG, "MethodManager-->onDestroy");
    }

    @Override
    public void setRoleData(Activity paramActivity, HY_GameRoleInfo gameRoleInfo)
    {
    	//提供了两个接口
    	//1、游戏上传，我们在这里传给 渠道
    	//2、保存最后一次上传信息，根据渠道需求进行上传信息
    	//以下参数是游戏传入角色信息,如果渠道需要,就根据上传类型判断，传输数据
    	
    	MUMAYI_RoleInfo.typeId = gameRoleInfo.getTypeId();//上传类型 登录:HY_Constants.ENTER_SERVER 、创角:HY_Constants.CREATE_ROLE、升级:HY_Constants。LEVEL_UP
    	MUMAYI_RoleInfo.roleId = gameRoleInfo.getRoleId();//角色id
    	MUMAYI_RoleInfo.roleName = gameRoleInfo.getRoleName();//角色名
    	MUMAYI_RoleInfo.roleLevel = gameRoleInfo.getRoleLevel();//角色等级
    	MUMAYI_RoleInfo.zoneId = gameRoleInfo.getZoneId();//区服id
    	MUMAYI_RoleInfo.zoneName = gameRoleInfo.getZoneName();//区服名
    	MUMAYI_RoleInfo.balance = gameRoleInfo.getBalance(); //用户余额
    	MUMAYI_RoleInfo.vip = gameRoleInfo.getVip();//vip等级
    	MUMAYI_RoleInfo.partyName = gameRoleInfo.getPartyName();//帮派名称
    	//这里是为了显示例子,正式的时候就不要弹Toast了
    	//Toast.makeText(paramActivity, gameRoleInfo.toString(), Toast.LENGTH_SHORT).show();
    	mumayi_instance.setUserArea(MUMAYI_RoleInfo.zoneName);
    	mumayi_instance.setUserName(MUMAYI_RoleInfo.roleName);
    	mumayi_instance.setUserLevel(Integer.valueOf(MUMAYI_RoleInfo.roleLevel));
        HyLog.d(TAG, "MethodManager-->setExtData");
    }

}