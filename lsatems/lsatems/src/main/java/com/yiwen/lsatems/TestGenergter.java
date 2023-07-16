package com.yiwen.lsatems;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-06
 * @see TestGenergter
 **/
public class TestGenergter
{
    public static void main(String[] args)
    {

//        System.out.println(System.getProperty("user.dir"));

//        JSONObject test = JSONUtil.createObj();
//        test.append("type", "success");
//        test.append("title", "连接成功");
//        test.append("msg", "socket连接成功");
//        System.out.println(JSONUtil.toJsonPrettyStr(test));

        List<String> res = new ArrayList<>();
        res.add("156165");
        res.add("666");
        res.add("777");
        String record ="[156165,666,777]";
        String[] splitUserIds = record.substring(1, record.length()-1).split("[|,|]");
        for (String splitUserId : splitUserIds) {
            System.out.println(splitUserId);
        }
       /* E:\JAVA_test\lsatems\src\main\java*/
    }
}
