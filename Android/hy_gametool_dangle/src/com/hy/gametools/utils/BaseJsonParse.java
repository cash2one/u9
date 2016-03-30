package com.hy.gametools.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public abstract class BaseJsonParse implements ResultJsonParse
{


    public ResponseResultVO parseJesonByUrl(String string)
    {
        if (TextUtils.isEmpty(string))
        {
            return null;
        }
        ResponseResultVO result = new ResponseResultVO();
        try
        {
            JSONObject jsonObjects = new JSONObject(string);
            if (!jsonObjects.isNull(ResponseResultVO.MESSAGE))
            {
                result.message = jsonObjects
                        .getString(ResponseResultVO.MESSAGE);
            }
            if (!jsonObjects.isNull(ResponseResultVO.RESPOMSECODE))
            {
                result.responseCode = jsonObjects
                        .getString(ResponseResultVO.RESPOMSECODE);
            }

            if (!jsonObjects.isNull(ResponseResultVO.TRANSTYPE))
            {
                result.transType = jsonObjects
                        .getString(ResponseResultVO.TRANSTYPE);
            }
            if(!jsonObjects.isNull(ResponseResultVO.USERID))
            {
            	result.userId = jsonObjects
            			.getString(ResponseResultVO.USERID);
            }
            if(!jsonObjects.isNull(ResponseResultVO.ORDERID))
            {
            	result.orderId = jsonObjects
            			.getString(ResponseResultVO.ORDERID);
            }


            result.obj = toObject(jsonObjects);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }

        return result;
    }

}