package com.hy.gametools.manager;

import com.hy.gametools.utils.HY_UserInfoVo;

/**
 * 此接口由应用客户端与应用服务器协商决定。
 */
public interface HY_UserInfoListener
{

    public void onGotUserInfo(HY_UserInfoVo userInfo,boolean isDoLogin);

}
