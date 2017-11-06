package com.wxpay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by yuanl2 on 2017/11/06.
 */
public class Pay {
    // 发货通知接口
    private static final String DELIVERNOTIFY_URL = "https://api.weixin.qq.com/pay/delivernotify?access_token=";
//
//    /**
//     * 参与 paySign 签名的字段包括：appid、timestamp、noncestr、package 以及 appkey。
//     * 这里 signType 并不参与签名微信的Package参数
//     * @param params
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public static String getPackage(Map<String, String> params) throws UnsupportedEncodingException {
//        String partnerKey = ConfKit.get("partnerKey");
//        String partnerId = ConfKit.get("partnerId");
//        String notifyUrl = ConfKit.get("notify_url");
//        // 公共参数
//        params.put("bank_type", "WX");
//        params.put("attach", "yongle");
//        params.put("partner", partnerId);
//        params.put("notify_url", notifyUrl);
//        params.put("input_charset", "UTF-8");
//        return packageSign(params, partnerKey);
//    }

    /**
     * 构造签名
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSign(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (key == null || StringUtils.isEmpty(params.get(key))) // 参数为空参与签名
                continue;
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueString, "UTF-8"));
            } else {
                temp.append(valueString);
            }
        }
        return temp.toString();
    }

    /**
     * @param params
     * @param paternerKey
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String packageSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        String string2 = createSign(params, true);
        return string2 + "&sign=" + signValue;
    }
//
//    /**
//     * 支付签名
//     * @param timestamp
//     * @param noncestr
//     * @param packages
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public static String paySign(String timestamp, String noncestr,String packages) throws UnsupportedEncodingException {
//        Map<String, String> paras = new HashMap<String, String>();
//        paras.put("appid", ConfKit.get("AppId"));
//        paras.put("timestamp", timestamp);
//        paras.put("noncestr", noncestr);
//        paras.put("package", packages);
//        paras.put("appkey", ConfKit.get("paySignKey"));
//        // appid、timestamp、noncestr、package 以及 appkey。
//        String string1 = createSign(paras, false);
//        String paySign = DigestUtils.shaHex(string1);
//        return paySign;
//    }
//
//    /**
//     * 支付回调校验签名
//     * @param timestamp
//     * @param noncestr
//     * @param openid
//     * @param issubscribe
//     * @param appsignature
//     * @return
//     * @throws UnsupportedEncodingException
//     */
//    public static boolean verifySign(long timestamp,
//                                     String noncestr, String openid, int issubscribe, String appsignature) throws UnsupportedEncodingException {
//        Map<String, String> paras = new HashMap<String, String>();
//        paras.put("appid", ConfKit.get("AppId"));
//        paras.put("appkey", ConfKit.get("paySignKey"));
//        paras.put("timestamp", String.valueOf(timestamp));
//        paras.put("noncestr", noncestr);
//        paras.put("openid", openid);
//        paras.put("issubscribe", String.valueOf(issubscribe));
//        // appid、appkey、productid、timestamp、noncestr、openid、issubscribe
//        String string1 = createSign(paras, false);
//        String paySign = DigestUtils.shaHex(string1);
//        return paySign.equalsIgnoreCase(appsignature);
//    }
//
//    /**
//     * 发货通知签名
//     * @param paras
//     * @return
//     * @throws UnsupportedEncodingException
//     *
//     * @参数 appid、appkey、openid、transid、out_trade_no、deliver_timestamp、deliver_status、deliver_msg；
//     */
//    private static String deliverSign(Map<String, String> paras) throws UnsupportedEncodingException {
//        paras.put("appkey", ConfKit.get("paySignKey"));
//        String string1 = createSign(paras, false);
//        String paySign = DigestUtils.shaHex(string1);
//        return paySign;
//    }
//
//
//    /**
//     * 发货通知
//     * @param access_token
//     * @param openid
//     * @param transid
//     * @param out_trade_no
//     * @return
//     * @throws IOException
//     * @throws NoSuchProviderException
//     * @throws NoSuchAlgorithmException
//     * @throws KeyManagementException
//     * @throws InterruptedException
//     * @throws ExecutionException
//     */
//
//    public static boolean delivernotify(String access_token, String openid, String transid, String out_trade_no) throws IOException, ExecutionException, InterruptedException {
//        Map<String, String> paras = new HashMap<String, String>();
//        paras.put("appid", ConfKit.get("AppId"));
//        paras.put("openid", openid);
//        paras.put("transid", transid);
//        paras.put("out_trade_no", out_trade_no);
//        paras.put("deliver_timestamp", (System.currentTimeMillis() / 1000) + "");
//        paras.put("deliver_status", "1");
//        paras.put("deliver_msg", "ok");
//        // 签名
//        String app_signature = deliverSign(paras);
//        paras.put("app_signature", app_signature);
//        paras.put("sign_method", "sha1");
//        String json = HttpKit.post(DELIVERNOTIFY_URL.concat(access_token), JSONObject.toJSONString(paras));
//        if (StringUtils.isNotBlank(json)) {
//            JSONObject object = JSONObject.parseObject(json);
//            if (object.containsKey("errcode")) {
//                int errcode = object.getIntValue("errcode");
//                return errcode == 0;
//            }
//        }
//        return false;
//    }
}
