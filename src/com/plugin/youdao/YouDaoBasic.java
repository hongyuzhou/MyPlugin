package com.plugin.youdao;

import org.jetbrains.annotations.Contract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongyuzhou
 * @version V1.0
 * @date 2017/7/13
 * @since JDK1.8
 */
public class YouDaoBasic {
    private final static String appKey = "12b86de63ab132bb";
    private final static String appSecret = "xa8dNhj5e28HcjC8AhMfSmBmhldDCndv";
    private final static String preUrl = "https://openapi.youdao.com/api";


    @Contract(pure = true)
    public static String getAppKey() {
        return appKey;
    }

    @Contract(pure = true)
    public static String getAppSecret() {
        return appSecret;
    }

    @Contract(pure = true)
    public static String getPreUrl() {
        return preUrl;
    }

    /*
    public static void main(String[] args) {
        String query = "peach";
        String from = "en";
        String to = "zh_CHS";
        Map<String, String> params = queryInfo(query, from, to);
        String url = getUrlWithQueryString(preUrl, params);
        System.out.println(url);
        JSONObject object = Util.httpRequest(url, "POST");
        System.out.println(Util.parseAnswer(object));
    }*/

    /**
     * 组装请求参数params
     *
     * @param query
     * @param from
     * @param to
     * @return
     */
    public static Map<String, String> queryInfo(String query, String from, String to) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", md5(appKey + query + salt + appSecret));
        params.put("salt", salt);
        params.put("appKey", appKey);
        return params;
    }

    /**
     * 生成32位MD5摘要
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes();
        try {
            /* 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /* 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /* 获得密文 */
            byte[] md = mdInst.digest();
            /* 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 根据api地址和参数生成请求URL
     *
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    /**
     * 进行URL编码
     *
     * @param input
     * @return
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }
}
