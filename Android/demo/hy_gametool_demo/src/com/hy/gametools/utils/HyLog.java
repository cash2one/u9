package com.hy.gametools.utils;

import android.util.Log;

public class HyLog
{

    /** 调试开关 */
    public  static boolean isDebug = false;

    /** 错误级别日志 */
    public static void e(String tag, String log)
    {
        if (HyLog.isDebug)
        {
            Log.e(tag, log);
        }
    }

    /** 警告级别日志 */
    public static void w(String tag, String log)
    {
        if (HyLog.isDebug)
        {
            Log.w(tag, log);
        }
    }

    /** 信息级别日志 */
    public static void i(String tag, String log)
    {
        if (HyLog.isDebug)
        {
            Log.i(tag, log);
        }
    }

    /** 调试级别日志 */
    public static void d(String tag, String log)
    {
        if (HyLog.isDebug)
        {
            Log.d(tag, log);
        }
    }

}
