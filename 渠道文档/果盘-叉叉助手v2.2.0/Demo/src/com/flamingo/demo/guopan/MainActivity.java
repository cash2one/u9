package com.flamingo.demo.guopan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPSDKPlayerInfo;
import com.flamingo.sdk.access.GPUploadPlayerInfoResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUploadPlayerInfoObsv;
import com.flamingo.sdk.access.IGPUserObsv;

import java.lang.reflect.Field;


public class MainActivity extends Activity {

    String TAG = "MainActivity";
    View mGoToLogin;
    TextView mTvLog;

    private static final String APP_ID = "101101";
    private static final String APP_KEY = "GuopanSDK8^(Llad";

    private boolean mIsInitSuc = false;

    private boolean mIsTest = true;

    // TODO 请注意demo中标记TODO的地方
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.goto_login_btn).setOnClickListener(listener);
        findViewById(R.id.buy_btn).setOnClickListener(listener);
        findViewById(R.id.btn_logout).setOnClickListener(listener);
        findViewById(R.id.btn_invoke_init_and_login).setOnClickListener(listener);
        findViewById(R.id.btn_invoke_sdk_version).setOnClickListener(listener);
        findViewById(R.id.btn_invoke_login_info).setOnClickListener(listener);
        findViewById(R.id.btn_invoke_exit).setOnClickListener(listener);
        findViewById(R.id.btn_switch_environment).setOnClickListener(listener);
        findViewById(R.id.btn_upload_player_info).setOnClickListener(listener);
        mGoToLogin = findViewById(R.id.goto_login_btn);
        mTvLog = (TextView) findViewById(R.id.tv_log);

        //TODO 打开日志、发布状态切记不要打开
        GPApiFactory.getGPApi().setLogOpen(false);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buy_btn:
                    //TODO 调用购买
                    invokeBuy();
                    break;
                case R.id.goto_login_btn:
                    //TODO 调用登录
                    if (mIsInitSuc) {
                        GPApiFactory.getGPApi().login(MainActivity.this.getApplication(), mUserObsv);
                    } else {
                        writeLog("请在初始化成功后再调用登录");
                    }
                    break;
                case R.id.btn_invoke_exit:
                    //TODO 调用退出
                    GPApiFactory.getGPApi().exit(mExitObsv);
                    break;
                case R.id.btn_invoke_init_and_login:
                    //TODO 调用初始化
                    if (!mIsInitSuc) {
                        GPApiFactory.getGPApi().initSdk(MainActivity.this, APP_ID, APP_KEY, mInitObsv);
                    } else {
                        writeLog("初始化已完成，请调用登录");
                    }
                    break;
                case R.id.btn_invoke_sdk_version:
                    writeLog("SDK版本为：" + GPApiFactory.getGPApi().getVersion());
                    break;
                case R.id.btn_invoke_login_info:
                    writeLog("用户UIN：" + GPApiFactory.getGPApi().getLoginUin() + "\n" +
                            "用户LoginToken：" + GPApiFactory.getGPApi().getLoginToken() + "\n" +
                            "用户AccountName：" + GPApiFactory.getGPApi().getAccountName());
                    break;
                case R.id.btn_logout:
                    GPApiFactory.getGPApi().logout();
                    break;
                case R.id.btn_switch_environment:
                    if (!mIsInitSuc) {
                        try {
                            mIsTest = !mIsTest;
                            Field field = Class.forName("com.flamingo.sdk.config.CheckList").getField("isUrlTest");
                            field.setAccessible(true);
                            field.set(null, mIsTest);
                            if (mIsTest) {
                                ((Button) findViewById(R.id.btn_switch_environment)).setText("当前为测试环境");
                            } else {
                                ((Button) findViewById(R.id.btn_switch_environment)).setText("当前为正式环境");
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "请在初始化之前切换环境", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_upload_player_info:
                    GPSDKPlayerInfo gpsdkPlayerInfo = new GPSDKPlayerInfo();
                    gpsdkPlayerInfo.mGameLevel = ((EditText) findViewById(R.id.level)).getText().toString();
                    gpsdkPlayerInfo.mPlayerId = ((EditText) findViewById(R.id.player_id)).getText().toString();
                    gpsdkPlayerInfo.mPlayerNickName = ((EditText) findViewById(R.id.player_nickname)).getText().toString();
                    gpsdkPlayerInfo.mServerId = ((EditText) findViewById(R.id.server_id)).getText().toString();
                    gpsdkPlayerInfo.mServerName = ((EditText) findViewById(R.id.server_name)).getText().toString();
                    writeLog("上报的信息为:" + gpsdkPlayerInfo.mGameLevel + ";" + gpsdkPlayerInfo.mPlayerId + ";" + gpsdkPlayerInfo.mPlayerNickName + ";" + gpsdkPlayerInfo.mServerId + ";" + gpsdkPlayerInfo.mServerName);
                    GPApiFactory.getGPApi().uploadPlayerInfo(gpsdkPlayerInfo, mGPUploadPlayerInfoObsv);
                    break;
            }
        }
    };

    /**
     * 调用SDK的购买接口，请参考填入参数
     */
    private void invokeBuy() {
        GPSDKGamePayment payParam = new GPSDKGamePayment();
        String itemName = ((EditText) findViewById(R.id.item_name)).getText().toString();
        payParam.mItemName = TextUtils.isEmpty(itemName) ? "人品1斤" : itemName; // 订单商品的名称

        String paymentDes = ((EditText) findViewById(R.id.item_description)).getText().toString();
        payParam.mPaymentDes = TextUtils.isEmpty(paymentDes) ? "jammy要买人品1斤" : paymentDes;// 订单的介绍

        String priceStr = ((EditText) findViewById(R.id.price)).getText().toString();

        if (TextUtils.isEmpty(priceStr.trim())) {
            show(MainActivity.this, "请输入购买金额");
            return;
        }
        payParam.mItemPrice = Float.valueOf(priceStr);// 订单的价格（以元为单位）

        try {
            String countStr = ((EditText) findViewById(R.id.item_count)).getText().toString();
            payParam.mItemCount = Integer.valueOf(countStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        payParam.mCurrentActivity = MainActivity.this;// 用户当前的activity

        String serialNum = ((EditText) findViewById(R.id.serial_number)).getText().toString();
        payParam.mSerialNumber = TextUtils.isEmpty(serialNum) ? "" + System.currentTimeMillis() : serialNum;// 订单号，这里用时间代替（用户需填写订单的订单号）

        String itemId = ((EditText) findViewById(R.id.item_id)).getText().toString();
        payParam.mItemId = TextUtils.isEmpty(itemId) ? "" + System.currentTimeMillis() : itemId;// 商品编号ID

        payParam.mReserved = "reserved string-&&" + System.currentTimeMillis();// 透传字段
        Log.e(TAG, payParam.mReserved);
        GPApiFactory.getGPApi().buy(payParam, mPayObsv);
    }

    /**
     * 初始化回调接口
     */
    private IGPSDKInitObsv mInitObsv = new IGPSDKInitObsv() {
        @Override
        public void onInitFinish(GPSDKInitResult initResult) {
            Log.i(TAG, "GPSDKInitResult mInitErrCode: " + initResult.mInitErrCode);
            Log.i(TAG, "loginToken" + GPApiFactory.getGPApi().getLoginToken());
            switch (initResult.mInitErrCode) {
                case GPSDKInitResult.GPInitErrorCodeConfig:
                    writeLog("初始化回调:初始化配置错误");
                    retryInit();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
                    writeLog("初始化回调:游戏需要更新");
                    break;
                case GPSDKInitResult.GPInitErrorCodeNet:
                    writeLog("初始化回调:初始化网络错误");
                    retryInit();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNone:
                    //TODO 只有回调是成功的时候才能进行下面的操作
                    writeLog("初始化回调:初始化成功");
                    mIsInitSuc = true;
                    mGoToLogin.performClick();
                    break;
            }
        }
    };

    /**
     * 登录回调接口
     */
    private IGPUserObsv mUserObsv = new IGPUserObsv() {
        @Override
        public void onFinish(final GPUserResult result) {
            switch (result.mErrCode) {
                case GPUserResult.USER_RESULT_LOGIN_FAIL:
                    writeLog("登录回调:登录失败");
                    break;
                case GPUserResult.USER_RESULT_LOGIN_SUCC:
                    writeLog("登录回调:登录成功");
                    break;
            }
        }
    };

    /**
     * 上报用户信息回调接口
     */
    private IGPUploadPlayerInfoObsv mGPUploadPlayerInfoObsv = new IGPUploadPlayerInfoObsv() {
        @Override
        public void onUploadFinish(final GPUploadPlayerInfoResult gpUploadPlayerInfoResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (gpUploadPlayerInfoResult.mResultCode == GPUploadPlayerInfoResult.GPSDKUploadSuccess)
                        writeLog("上报数据回调:成功");
                    else
                        writeLog("上报数据回调:失败");
                }
            });

        }
    };

    /**
     * 退出界面回调接口
     */
    private IGPExitObsv mExitObsv = new IGPExitObsv() {
        @Override
        public void onExitFinish(GPExitResult exitResult) {
            switch (exitResult.mResultCode) {
                case GPExitResult.GPSDKExitResultCodeError:
                    writeLog("退出回调:调用退出弹框失败");
                    break;
                case GPExitResult.GPSDKExitResultCodeExitGame:
                    writeLog("退出回调:调用退出游戏，请执行退出逻辑");
                    Toast.makeText(MainActivity.this, "GPSDKExitResultCodeExitGame", Toast.LENGTH_SHORT).show();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                    break;
                case GPExitResult.GPSDKExitResultCodeCloseWindow:
                    writeLog("退出回调:调用关闭退出弹框");
                    break;
            }
        }
    };

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


    private int mInitCount = 0;

    /**
     * 重试初始化3次
     */
    public void retryInit() {
        if (mInitCount >= 3) {
            writeLog("初始化失败，请检查网络");
            showToast("初始化失败，请检查网络");
            return;
        }
        mInitCount++;
        writeLog("初始化失败，进行第" + mInitCount + "次初始化重试");
        showToast("初始化失败，进行第" + mInitCount + "次初始化重试");
        GPApiFactory.getGPApi().initSdk(this, APP_ID, APP_KEY, mInitObsv);
    }


    /**
     * TODO 拦截横竖屏切换，(可根据游戏需要，或设死横竖屏)
     * 如果接入方的游戏只支持横屏或竖屏中的一种，建议在manifest写死调用SDK初始化的activity方向
     * 如果同时支持横屏和竖屏，在调用SDK初始化的activity需做横竖屏切换处理，避免重复初始化SDK
     */
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            GPApiFactory.getGPApi().exit(mExitObsv);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private int mLogCount = 0;
    private static final int MAX_LOG_COUNT = 30;
    private String mLogStr = "";

    private void writeLog(String log) {
        mLogStr = log + "\n" + mLogStr;
        mLogCount++;
        if (mLogCount > MAX_LOG_COUNT) {
            int lastIndexOf = mLogStr.lastIndexOf("\n");
            mLogStr = mLogStr.substring(0, lastIndexOf);
        }
        mTvLog.setText(mLogStr);
    }

    void showPayResult(final GPPayResult result) {
        String tips = "";
        switch (result.mErrCode) {
            case GPPayResult.GPSDKPayResultCodeSucceed:
                writeLog("支付回调:购买成功");
                show(MainActivity.this, tips + "succ");
                break;
            case GPPayResult.GPSDKPayResultCodePayBackground:
                writeLog("支付回调:后台正在轮循购买");
                show(MainActivity.this, tips + "后台正在轮循购买");
                break;
            case GPPayResult.GPSDKPayResultCodeBackgroundSucceed:
                writeLog("支付回调:后台购买成功");
                show(MainActivity.this, tips + "后台购买成功");
                break;
            case GPPayResult.GPSDKPayResultCodeBackgroundTimeOut:
                writeLog("支付回调:后台购买超时");
                show(MainActivity.this, tips + "后台购买超时");
                break;
            case GPPayResult.GPSDKPayResultCodeCancel:
                writeLog("支付回调:用户取消");
                show(MainActivity.this, tips + "用户取消");
                break;
            case GPPayResult.GPSDKPayResultCodeNotEnough:
                writeLog("支付回调:余额不足");
                show(MainActivity.this, tips + "余额不足");
                break;
            case GPPayResult.GPSDKPayResultCodeOtherError:
                writeLog("支付回调:其他错误");
                show(MainActivity.this, tips + "其他错误");
                break;
            case GPPayResult.GPSDKPayResultCodePayForbidden:
                writeLog("支付回调:用户被限制");
                show(MainActivity.this, tips + "用户被限制");
                break;
            case GPPayResult.GPSDKPayResultCodePayHadFinished:
                writeLog("支付回调:该订单已经完成");
                show(MainActivity.this, tips + "该订单已经完成");
                break;
            case GPPayResult.GPSDKPayResultCodeServerError:
                writeLog("支付回调:服务器错误");
                show(MainActivity.this, tips + "服务器错误");
                break;
            case GPPayResult.GPSDKPayResultNotLogined:
                writeLog("支付回调:无登陆");
                show(MainActivity.this, tips + "无登陆");
                break;
            case GPPayResult.GPSDKPayResultParamWrong:
                writeLog("支付回调:参数错误");
                show(MainActivity.this, tips + "参数错误");
                break;
            case GPPayResult.GPSDKPayResultCodeLoginOutofDate:
                writeLog("支付回调:登录态失效");
                show(MainActivity.this, tips + "登录态失效");
                break;
            default:
                writeLog("支付回调:未知错误 " + result.toString());
                show(MainActivity.this, tips + "fail " + result.toString());
                break;
        }
    }

    public static void show(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
