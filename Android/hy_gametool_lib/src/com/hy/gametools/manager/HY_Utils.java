package com.hy.gametools.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.hy.gametools.utils.HyLog;

public class HY_Utils
{
    private static final String TAG = "HY";
    public static boolean isLandscape = true;
    private static HY_User loginedUser;
    private static Object xmCustomParams;

    public static String createRandomText()
    {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4)
        {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set)
        {
            sb.append("" + i);
        }

        return sb.toString();
    }

    public static String getMD5Hex(String str)
    {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        return getBase16String(byteArray);
    }

    public static String getMD5HexOfFile(String path)
    {
        String result = "";
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(path);

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = fis.read(buffer)) > 0)
            {
                messageDigest.update(buffer, 0, byteCount);
            }

            byte[] byteArray = messageDigest.digest();

            result = getBase16String(byteArray);
        }
        catch (FileNotFoundException e)
        {
            HyLog.d(TAG, "error in get file md5");
        }
        catch (NoSuchAlgorithmException e)
        {
            HyLog.d(TAG, "error in get file md5");
        }
        catch (IOException e)
        {
            HyLog.d(TAG, "error in get file md5");
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getBase16String(byte[] byteArray)
    {
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++)
        {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
            {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            }
            else
            {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getOutTradeNo()
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        String strKey = format.format(date);

        Random r = new Random();
        strKey = strKey + r.nextInt();
        strKey = strKey.substring(0, 15);
        return strKey;
    }

    /**
     * 
     * getHYChannelName(获取渠道名称)
     */
    public static String getHYChannelType(Context context)
    {
        return getManifestMeta(context, "HY_CHANNEL_TYPE");//渠道名
    }

    /**
     * 
     * getHYChannelCode(获取游戏渠道号)
     */
    public static String getHYChannelCode(Context context)
    {
        return getManifestMeta(context, "HY_CHANNEL_CODE");// 渠道号
                                                                  
    }
    /**
     * 
     * getHYGameId(获取游戏号)
     */
    public static String getHYGameId(Context context)
    {

        return getManifestMeta(context, "HY_GAME_ID");// 游戏号，用于区分游戏

    }

    public static String getManifestMeta(Context context, String key)
    {
        String result;
        try
        {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 128);

            result = appInfo.metaData.get(key) + "";//可能为null
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new IllegalStateException("read meta " + key + " error", e);
        }catch (Exception e)
        {
            result="";
//            throw new IllegalStateException("read meta " + key + " error", e);
        }
        if ((result == null) || (result.length() == 0))
        {
//            throw new IllegalStateException("no meta " + key + " found");
            HyLog.d(TAG, "-->IllegalStateException(no meta " + key + " found");
        }
        if (result.equalsIgnoreCase("null"))
        {
            result="";
        }
        return result;
    }

    /**
     * 
     * getHYConfig(读取配置文件的配置信息)
     * 
     */
    public static String getHYConfig(Context context, String key)
    {
        HyLog.d(TAG, "key : " + key + " value : "
                + HY_Config.getInstance(context).get(key));
        String returnStr = HY_Config.getInstance(context).get(key);
        if (!TextUtils.isEmpty(returnStr))
        {
            return returnStr;
        }
        else
        {
            return "字段不能为空值-->key : " + key + " value : "
                    + HY_Config.getInstance(context).get(key);
        }

    }

    public static String getAppName(Context context)
    {
        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            String packageNames = (String) info.applicationInfo
                    .loadLabel(context.getPackageManager());
            return packageNames;
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return "找不到应用名称";

    }

    public static String getPackageName(Context context)
    {
        return context.getPackageName();
    }

    public static void setLoginedUser(HY_User xmUser)
    {
        loginedUser = xmUser;
    }

    public static HY_User getLoginedUser()
    {
        return loginedUser;
    }

    public static void setCustomParams(Object customParams)
    {
        xmCustomParams = customParams;
    }

    public static Object getCustomParams()
    {
        return xmCustomParams;
    }

    public static int dip2px(Context context, float dpValue)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
    /**    
     * getMainClassByChannelName (通过渠道名获取主业务类的方法)    
     * 
     * @param    
     * @return    
    */
    public static Object getMainClassByChannelName()
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException
    {
        String hy_channelName = HY_Constants.HY_channelType;
        Class<?> channelNameclazz = Class.forName("com.hygame."
               + hy_channelName + "_MethodManager");
        Method method = channelNameclazz
                .getMethod("getInstance");
        Object object = method.invoke(method);
        return object;
    }

}
