package com.plugin.youdao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author hongyuzhou
 * @version V1.0
 * @date 2017/7/14
 * @since JDK1.8
 */
public class Util {
    public static String parseAnswer(JSONObject translate) {
        StringBuilder ans = new StringBuilder();
        String errorCode = (String) translate.get("errorCode");
        if (errorCode.equals("0")) {
            JSONArray array = (JSONArray) translate.get("translation");
            for (int i = 0; i < array.size(); i++) {
                ans.append(array.get(i).toString());
            }
            // ans.append(translate.get("translation").toString());
        } else {
            switch (errorCode) {
                case "101":
                    return "error：翻译错误" + "\n" + "缺少必填的参数";
                case "102":
                    return "error：翻译错误" + "\n" + "不支持的语言类型";
                case "103":
                    return "error：翻译错误" + "\n" + "翻译文本过长";
                case "104":
                    return "error：翻译错误" + "\n" + "不支持的API类型";
                case "105":
                    return "error：翻译错误" + "\n" + "不支持的签名类型";
                case "106":
                    return "error：翻译错误" + "\n" + "不支持的响应类型";
                case "107":
                    return "error：翻译错误" + "\n" + "不支持的传输加密类型";
                case "108":
                    return "error：翻译错误" + "\n" + "appKey无效";
                case "109":
                    return "error：翻译错误" + "\n" + "batchLog格式不正确";
                case "110":
                    return "error：翻译错误" + "\n" + "无相关服务的有效实例";
                case "111":
                    return "error：翻译错误" + "\n" + "开发者账号无效";
                case "201":
                    return "error：翻译错误" + "\n" + "解密失败";
                case "202":
                    return "error：翻译错误" + "\n" + "签名检验失败";
                case "203":
                    return "error：翻译错误" + "\n" + "访问IP地址不在可访问IP列表";
                case "301":
                    return "error：翻译错误" + "\n" + "辞典查询失败";
                case "302":
                    return "error：翻译错误" + "\n" + "翻译查询失败";
                case "303":
                    return "error：翻译错误" + "\n" + "服务端的其它异常";
                case "401":
                    return "error：翻译错误" + "\n" + "账户已经欠费停";
            }
        }
        return ans.toString();
    }

    /**
     * 请求有道词典api的地址，返回翻译结果
     *
     * @param requestUrl
     * @param method
     * @return
     */
    public static JSONObject httpRequest(String requestUrl, String method) {
        JSONObject json = null;
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(method);
            if ("GET".equalsIgnoreCase(method)) {
                connection.connect();
            }
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"));
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                builder.append(str);
            }
            bufferedReader.close();
            inputStream.close();
            connection.disconnect();
            json = JSONObject.parseObject(builder.toString());
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("Youdao server connection timed out");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("http request error:{}");
        } finally {
            try {
                if (inputStream == null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}
