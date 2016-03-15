/**
 * @FILE:WX_UserInfoParser.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月10日 下午5:23:07
 **/
package com.hy.gametools.manager;

import org.json.JSONObject;

import com.hy.gametools.utils.BaseJsonParse;

/*******************************************
 * @CLASS:HY_UserInfoParser
 * @AUTHOR:smile
 *******************************************/
public class HY_UserInfoParser extends BaseJsonParse
{
    @Override
    public Object toObject(JSONObject json)
    {
        try
        {
            String dataJsonObj;
            if (json.has("body"))
            {
                dataJsonObj = json.getString("body");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
