/**
 * @FILE:WX_GameProxy.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月1日 下午5:57:14
 **/
package com.hy.gametools.manager;


import android.text.TextUtils;
import com.hy.gametools.utils.HyLog;

/*******************************************
 * 
 * @CLASS:HY_GameProxy
 * @AUTHOR:smile
 *******************************************/
public class HY_GameProxy extends HY_CommonGameProxy
{
    private static final String TAG = "HY";
    private static HY_GameProxy instance;

    private HY_GameProxy(HY_ActivityStub stub)
    {
        super(stub);
    }

    public static HY_IGameProxy getInstance()
    {
        if (null == instance)
        {
            
            //如果hy渠道为null,则
            if (!TextUtils.isEmpty(HY_Constants.HY_channelType))
            {
                synchronized (HY_GameProxy.class)
                {
                    if (null == instance)
                    {
                        instance = new HY_GameProxy(new HY_StateActivityStub());
                        try
                        {
                                Object object = HY_Utils.getMainClassByChannelName();
                                instance.setUserManager((HY_UserManager) object);
                                HyLog.d(TAG, "--->已经配置渠道信息，可以正常处理");

                        }
                        catch (ClassNotFoundException e)
                        {
                            HyLog.e(TAG, "ClassNotFoundException:");
                        }
                        catch (NoSuchMethodException e)
                        {
                            HyLog.e(TAG, "NoSuchMethodException:");
                        }
                        catch (Exception e)
                        {
                            HyLog.e(TAG, "Exception:" + e.toString());
                        }
                    }
                }
            }

           
            
        }
        return instance;
    }

    
}
