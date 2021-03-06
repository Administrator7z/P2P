package com.bjpowernode.web.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.contans.P2PRedis;
import com.bjpowernode.web.config.CloudConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

@Service
public class Massage106 {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CloudConfig cloudConfig;

    public static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
        String sig = new BASE64Encoder().encode(hash);

        String auth = "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
        return auth;
    }

    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }

    public boolean invokeSmsApi(String phone) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        boolean sendResult = false;
        //????????????????????????Id
        String secretId = cloudConfig.getSecretId();
        //????????????????????????Key
        String secretKey = cloudConfig.getSecretKey();
        String source = "market";

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // ??????
        String auth = calcAuthorization(source, secretId, secretKey, datetime);
        // ????????????
        String method = "GET";
        // ?????????
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Source", source);
        headers.put("X-Date", datetime);
        headers.put("Authorization", auth);
        String num  = makeCode(6);
        // ????????????
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("mobile", phone);
        queryParams.put("param", "**code**:"+num +"**minute**:15");
        queryParams.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        queryParams.put("templateId", "a09602b817fd47e59e7c6e603d3f088d");
        // body??????
        Map<String, String> bodyParams = new HashMap<String, String>();

        // url????????????
        String url = "https://service-m6t5cido-1256923570.gz.apigw.tencentcs.com/release/sms/send";
        if (!queryParams.isEmpty()) {
            url += "?" + urlencode(queryParams);
        }

        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            // request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // request body
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(urlencode(bodyParams));
                out.flush();
                out.close();
            }

            // ?????? BufferedReader??????????????????URL?????????
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }

            //???redis???
            stringRedisTemplate.opsForValue().set(P2PRedis.USER_REGISTER_SMSCODE+phone,num,15, TimeUnit.MINUTES);
            //???????????????json
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null){
                if ("??????".equals(jsonObject.getString("msg"))){
                    sendResult = true;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        return sendResult;


    }

    //???????????????
    public String makeCode(int num) {
        StringJoiner stringJoiner = new StringJoiner("");
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < num; i++) {
            stringJoiner.add(((Integer) random.nextInt(10)).toString());
        }
        return stringJoiner.toString();

    }

    //??????redis
    public boolean checkRedisCode(String phone,String code){
        ValueOperations<String, String> vo = stringRedisTemplate.opsForValue();
        String integer = vo.get(P2PRedis.USER_REGISTER_SMSCODE+phone);
        if (code.equals(integer)){
            return true;
        }
        return false;
    }


}