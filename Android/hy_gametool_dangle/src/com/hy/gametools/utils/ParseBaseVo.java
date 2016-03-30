package com.hy.gametools.utils;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ParseBaseVo
{

    /** 解释JSONObject */
    public abstract void parseJson(JSONObject obj);

    public String getString(JSONObject obj, String key)
    {
        String str = "";
        if (!obj.isNull(key))
        {
            try
            {
                str = obj.getString(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return str;
    }

    public int getInt(JSONObject obj, String key)
    {
        int str = -1;
        if (!obj.isNull(key))
        {
            try
            {
                str = obj.getInt(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return str;
    }

    public long getLong(JSONObject obj, String key)
    {
        long str = 0;
        if (!obj.isNull(key))
        {
            try
            {
                str = obj.getLong(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return str;
    }

    public boolean getBoolean(JSONObject obj, String key)
    {
        boolean str = false;
        if (!obj.isNull(key))
        {
            try
            {
                str = obj.getBoolean(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return str;
    }
}
