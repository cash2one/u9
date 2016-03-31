package com.hy.gametools.utils;

public class Constants {
	/** 接口地址(公用一个) */
	public static final String URL_HOST = 
//	"http://api1.hygame.cc";
	// "http://api2.hygame.cc";// 发布2
	 "http://115.159.73.234";//发布2
	// "http://192.168.0.185";//测试
	// 登录地址 /
	public static final String URL_LOGIN = URL_HOST + "/api/gameLoginRequest";
	// http://192.168.0.185/api/gameLoginRequest
	// 支付地址
	public static final String URL_PAY = URL_HOST + "/api/gamePayRequest";
	// "http://192.168.1.191:8080/sdk_service/process";
	/** 检查登录地址 */
	public static String URL_CHECKLOGIN = URL_HOST + "/api/validateGameLogin";
	// /** demo获取支付信息地址 */
	public static String URL_CHECKPAY = URL_HOST + "/api/channelPayNotify";

	/** 闪屏assets下文件夹路径 */
	public static final String SPLASH_PIC_ASSETS = "splash_photo";

	/**
	 * HU_CONFIG_FILENAME 微讯一键支付sdk的配置信息 ,保存到assets下面"wx_onekey_config_"+
	 * HYChannelName + ".json
	 */
	public static String HY_GAME_CONFIG = "hy_game.json";

	/** 测试环境 */
	public static final String isDebug = "true";// true为测试环境

	/**
	 * 预留接口 SDK 版本信息
	 **/
	public static String HY_SDK_VERSION = "1.0";
}
