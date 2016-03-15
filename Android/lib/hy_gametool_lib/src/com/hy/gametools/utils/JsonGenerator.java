/**
 * @FILE:JsonGenerator.java
 * @AUTHOR:zhangchi
 * @DATE:2014年12月10日 上午11:05:22
 **/
package com.hy.gametools.utils;

/*******************************************
 * 
 * @CLASS:JsonGenerator	
 * @AUTHOR:smile
 *******************************************/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonGenerator
{
    private static final String TAG = "HY";

    private Map map = new LinkedHashMap();

    /**
     * 添加一个 JSON 属性，值为一个字符串，重复添加时产生数组
     * 
     * add("name", "value"); 添加一个字符串，产生的 JSON 如：{"name":"value"}
     * 
     * add("name", "value1"); add("name", "value2"); 添加两个同属性的字符串，产生的 JSON
     * 如：{"name":["value1", "value2"]}
     * 
     * @param key
     *            JSON 属性名
     * @param str
     *            字符串格式的属性值
     */
    // 添加值为字符串
    public void add(String key, String value)
    {
        addElement(key, value);
    }

    // 添加值为整数
    public void add(String key, int num)
    {
        addElement(key, new Integer(num));
    }

    // 添加值为布尔类型
    public void add(String key, boolean b)
    {
        addElement(key, new Boolean(b));
    }

    // 加入数组
    public void add(String key, Object[] s)
    {
        String temp = "";
        List l = java.util.Arrays.asList(s);
        addElement(key, l);
    }

    /**
     * 添加一个 JSON 属性，值为一个 JSON，重复添加时产生 JSON 数组
     * 
     * Json json1 = new Json(); json1.add("name1", "value1"); json1.add("name2",
     * "value2"); Json json = new Json(); json.add("message", json1); 添加一个
     * JSON，产生的 JSON 如：{"message":{"name1":"value1", "name2":"value2"}}
     * 
     * Json json1 = new Json(); json1.add("name1", "value1"); json1.add("name2",
     * "value2"); Json json2 = new Json(); json2.add("name1", "value3");
     * json2.add("name2", "value4"); Json json = new Json(); json.add("message",
     * json1); json.add("message", json2); 添加两个同属性的 JSON，产生的 JSON
     * 如：{"message":[{"name1":"value1", "name2":"value2"}, {"name1":"value3",
     * "name2":"value4"}]}
     * 
     * @param key
     *            JSON 属性名
     * @param json
     *            JSON 格式的属性值
     */
    public void add(String key, JsonGenerator json)
    {
        addElement(key, json);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int k = 0;
        for (Iterator i = map.keySet().iterator(); i.hasNext();)
        {
            String key = (String) (i.next());
            Object obj = map.get(key);
            if (k > 0)
            {
                sb.append(",");
            }
            appendKey(sb, key);
            if (obj instanceof String)
            {
                appendString(sb, (String) obj);
            }
            else if (obj instanceof List)
            {
                appendList(sb, (List) obj);
            }
            else if (obj instanceof JsonGenerator)
            {
                appendJson(sb, (JsonGenerator) obj);
            }
            else
            {
                appendOther(sb, obj);
            }
            k++;
        }
        sb.append("}");
        return sb.toString();
    }

    public String toInnerString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int k = 0;
        for (Iterator i = map.keySet().iterator(); i.hasNext();)
        {
            String key = (String) (i.next());
            Object obj = map.get(key);
            if (k > 0)
            {
                sb.append(",");
            }
            try
            {
                sb.append("\\\"").append(key).append("\\\":");
                sb.append("\\\"").append((String) obj).append("\\\"");
            }
            catch (Exception e)
            {
                HyLog.d(TAG, "---强制类型转化错误");
            }

            k++;
        }
        sb.append("}");
        return sb.toString();
    }

    private void addElement(String key, Object obj)
    {
        if (!map.containsKey(key))
        {
            if (obj instanceof JsonGenerator)
            {
                // List list = new ArrayList();
                // list.add(obj);
                map.put(key, obj);
            }
            else
            {
                map.put(key, obj);
            }
            return;
        }
        Object o = map.remove(key);
        if (o instanceof List)
        {
            ((List) o).add(obj);
            map.put(key, o);
            return;
        }
        // o is a String
        List list = new ArrayList();
        list.add(o);
        list.add(obj);
        map.put(key, list);
    }

    /**
     * Append JSON property name
     * 
     * @param sb
     * @param key
     */
    private void appendKey(StringBuilder sb, String key)
    {
        sb.append("\"").append(key).append("\":");
    }

    /**
     * Append JSON property value that is a String
     * 
     * @param sb
     * @param strgwt客户端json
     */
    private void appendString(StringBuilder sb, String str)
    {
        sb.append("\"").append(str).append("\"");
    }

    /**
     * Append JSON property value that is a Integer
     * 
     * @param sb
     * @param num
     */
    private void appendOther(StringBuilder sb, Object obj)
    {
        sb.append(obj);
    }

    /**
     * Append JSON property value that is a List
     * 
     * @param sb
     * @param list
     */
    private void appendList(StringBuilder sb, List list)
    {
        sb.append("[");
        for (int j = 0, m = list.size(); j < m; j++)
        {
            if (j > 0)
            {
                sb.append(",");
            }
            Object obj = list.get(j);
            if (obj instanceof String)
            {
                appendString(sb, (String) obj);
            }
            else if (obj instanceof JsonGenerator)
            {
                appendJson(sb, (JsonGenerator) obj);
            }
            else
            {
                appendOther(sb, obj);
            }
        }
        sb.append("]");
    }

    /**
     * Append JSON property value that is a JSON
     * 
     * @param sb
     * @param json
     */
    private void appendJson(StringBuilder sb, JsonGenerator json)
    {
        sb.append(json.toString());
    }
}
