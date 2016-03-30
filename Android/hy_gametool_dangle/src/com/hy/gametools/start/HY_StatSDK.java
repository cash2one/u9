package com.hy.gametools.start;


public class HY_StatSDK extends HY_StatBaseSDK
{
    private static HY_StatSDK instance;

    public static HY_StatSDK getInstance()
    {
        if (instance == null)
        {
            synchronized (HY_StatSDK.class)
            {
                if (instance == null)
                {
                    instance = new HY_StatSDK();
                }
            }
        }
        return instance;
    }
}
