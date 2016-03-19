package com.hy.gametools.utils;

import org.json.JSONObject;

/**
 * @ClassName: IJsonParse
 * @Description: TODO Json网络返回字段解析接口
 * @author smile
 */
public interface ResultJsonParse
{

    ResponseResultVO parseJesonByUrl(String json);

    Object toObject(JSONObject json);
}
