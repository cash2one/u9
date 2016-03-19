/**
 * @FILE:WX_IGameProxy.java
 * @AUTHOR:zhangchi
 * @DATE:2014年11月28日 下午4:20:47
 **/
package com.hy.gametools.manager;

/*******************************************
 * @CLASS:HY_ExitCallback
 * @AUTHOR:smile
 *******************************************/
public abstract interface HY_ExitCallback
{
	/** 使用渠道退出接口 */
    public abstract void onChannelExit();
    /** 使用游戏退出接口 */
    public abstract void onGameExit();
}
