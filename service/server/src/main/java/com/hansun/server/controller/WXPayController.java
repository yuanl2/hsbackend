package com.hansun.server.controller;

import com.alibaba.fastjson.JSON;
import com.hansun.server.util.ConstantUtil;
import com.hansun.server.util.MD5Util;
import com.hansun.server.util.TenpayUtil;
import com.wxpay.HttpKit;
import com.wxpay.Pay;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 微信支付服务端简单示例
 *
 * @author seven_cm
 * @dateTime 2014-11-29
 */
@RestController
@RequestMapping("/weixin")
public class WXPayController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(WXPayController.class);


    @RequestMapping(value = "savepackage", method = RequestMethod.POST)
    public String doWeinXinRequest(@RequestParam(value = "userId", required = true, defaultValue = "0") String userId,
                                   @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                                   @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                                   @RequestParam(value = "price", required = true, defaultValue = "0") String price, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<Object, Object> resInfo = new HashMap<Object, Object>();
        //接收财付通通知的URL
        String notify_url = "http://127.0.0.1:8180/tenpay_api_b2c/payNotifyUrl.jsp";

        //---------------生成订单号 开始------------------------
        //当前时间 yyyyMMddHHmmss
        String currTime = TenpayUtil.getCurrTime();
        //8位日期
        String strTime = currTime.substring(8, currTime.length());
        //四位随机数
        String strRandom = TenpayUtil.buildRandom(4) + "";
        //10位序列号,可以自行调整。
        String strReq = strTime + strRandom;
        //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
        String out_trade_no = strReq;
        //---------------生成订单号 结束------------------------
        String openId = userId;

        String fee = String.valueOf( (int)(Double.valueOf(price) * 100));
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("appid", ConstantUtil.APP_ID);
        paraMap.put("attach", "测试支付product_id=" + product_id);
        paraMap.put("body", "支付设备ID" + device_id);
        paraMap.put("mch_id", ConstantUtil.PARTNER);
        paraMap.put("nonce_str", create_nonce_str());
        paraMap.put("openid", openId);
        paraMap.put("out_trade_no", out_trade_no);
        paraMap.put("spbill_create_ip", getAddrIp(request));
        paraMap.put("total_fee", fee);
        paraMap.put("trade_type", "JSAPI");
        paraMap.put("notify_url", "http://www.xxx.co/wxpay/pay/appPay_notify.shtml");
        String sign = getSign(paraMap, ConstantUtil.PARTNER_KEY);
        paraMap.put("sign", sign);

        for (Map.Entry entry :
                paraMap.entrySet()) {
            log.info(entry.getKey() + " = {} ", entry.getValue());
        }


        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        String xml = ArrayToXml(paraMap, false);
        String xmlStr = HttpKit.post(url, xml);

        log.info("xmlStr = {} ", xmlStr);
        // 预付商品id
        String prepay_id = "";

        if (xmlStr.indexOf("SUCCESS") != -1) {
            Map<String, String> map = doXMLParse(xmlStr);
            for (Map.Entry entry :
                    map.entrySet()) {
                log.info(entry.getKey() + " = {} ", entry.getValue());
            }
            prepay_id = map.get("prepay_id");
            log.info("prepay_id = {} ", prepay_id);
        }

        resInfo.put("status", "0");
        resInfo.put("orderId", prepay_id);
        SortedMap<String, String> payMap = new TreeMap<String, String>();
        payMap.put("appId", ConstantUtil.APP_ID);
        payMap.put("signType", "MD5");
        payMap.put("package", "prepay_id=" + prepay_id);
        payMap.put("nonceStr", create_nonce_str());
        payMap.put("timeStamp", create_timestamp());
        String paySign = createSign(payMap, ConstantUtil.PARTNER_KEY);

        resInfo.put("paySign",paySign);
        resInfo.put("appId", ConstantUtil.APP_ID);
        resInfo.put("signType", "MD5");
        resInfo.put("package", "prepay_id=" + prepay_id);
        resInfo.put("nonceStr", payMap.get("nonceStr"));
        resInfo.put("timeStamp", payMap.get("timeStamp"));
        resInfo.put("product_id",product_id);
        resInfo.put("device_id",device_id);
        resInfo.put("userId",userId);

        for (Map.Entry entry :
                resInfo.entrySet()) {
            log.info(entry.getKey() + " = {} ", entry.getValue());
        }
        String strJson = JSON.toJSONString(resInfo);
        return strJson;
    }

    /**
     * 获取支付
     */
    @RequestMapping(value = {"payNotify"})
    @ResponseBody
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String resultStr = null;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            resultStr = new String(outSteam.toByteArray(), "utf-8");
            log.info("payNotify resultStr = {} ", resultStr);
            JSONObject jsonObj = JSONObject.fromObject(resultStr);
            Map<String, String> resultMap = (Map<String, String>) JSONObject.toBean(jsonObj, Map.class);

            System.out.println(resultMap.toString());
            //---------------获取的参数
            String result_code = resultMap.get("result_code");
            System.out.println("result_code:" + result_code);
            String out_trade_no = resultMap.get("out_trade_no");
            System.out.println("out_trade_no:" + out_trade_no);
            String sign = resultMap.get("sign");
            String time_end = resultMap.get("time_end");
            String total_fee = resultMap.get("total_fee");
            String transaction_id = resultMap.get("transaction_id");
            String return_code = resultMap.get("return_code");
            System.out.println("return_code:" + return_code);
            //验证签名------------------------------------------------
            System.out.println("微信回传:" + sign);
            resultMap.remove("sign");
            SortedMap<String, String> sortMap = new TreeMap<String, String>(resultMap);
//            String mysign = Sign.createSign("UTF-8", sortMap);
//            System.out.println("我的签名:" + mysign);
            //---------------------------------------------------------

            if ("SUCCESS".equals(return_code)) {
                if ("SUCCESS".equals(result_code)) {
                    //业务

                }

            }
        } catch (Exception e) {

        }
    }
//
//    @RequestMapping(value = "paysuccess", method = RequestMethod.POST)
//    public String doWeinXinPay(@RequestParam(value = "deviceID", required = true, defaultValue = "false") String deviceID,
//                               @RequestParam(value = "product_id", required = true, defaultValue = "false") String product_id,
//                               HttpServletRequest request, HttpServletResponse response) throws Exception {
//
//        return null;
//    }

    private String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }


    private String getAddrIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private String create_nonce_str() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }


//    public static void main(String[] args) throws Exception{
//        Map<String, String> params = new HashMap<>();
//        params.put("appid","wx4b72ad15df4fe82d");
//        params.put("signType","MD5");
//        params.put("package","prepay_id=wx20171107233439af499b0c0e0948388902");
//        params.put("nonceStr","jE2yKp5UN1U4VpNi");
//        params.put("timeStamp","1510068879");
//
//        String sign = getSign(params,"F61CC619B42291E9C7C6A314425571D2");
//        System.out.println(sign);
//    }


    private static String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = Pay.createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        return signValue;
    }


    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public String createSign(SortedMap<String, String> packageParams,String paternerKey) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + paternerKey);
        System.out.println("md5 sb:" + sb+"key="+paternerKey);
        String sign = MD5Util.MD5Encode(sb.toString(), "utf-8")
                .toUpperCase();
        log.info("packge sign value:{}",sign);
        return sign;

    }

    /**
     * map转成xml
     *
     * @param
     * @return
     */
    public String ArrayToXml(Map<String, String> parm, boolean isAddCDATA) {
        StringBuffer strbuff = new StringBuffer("<xml>");
        if (parm != null && !parm.isEmpty()) {
            for (Map.Entry<String, String> entry : parm.entrySet()) {
                strbuff.append("<").append(entry.getKey()).append(">");
                if (isAddCDATA) {
                    strbuff.append("<![CDATA[");
                    if (StringUtils.isNotEmpty(entry.getValue())) {
                        strbuff.append(entry.getValue());
                    }
                    strbuff.append("]]>");
                } else {
                    if (StringUtils.isNotEmpty(entry.getValue())) {
                        strbuff.append(entry.getValue());
                    }
                }
                strbuff.append("</").append(entry.getKey()).append(">");
            }
        }
        return strbuff.append("</xml>").toString();
    }

    private Map<String, String> doXMLParse(String xml) throws XmlPullParserException, IOException {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Map<String, String> map = null;
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        pullParser.setInput(inputStream, "UTF-8"); // 为xml设置要解析的xml数据

        int eventType = pullParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    map = new HashMap<String, String>();
                    break;

                case XmlPullParser.START_TAG:
                    String key = pullParser.getName();
                    if (key.equals("xml"))
                        break;

                    String value = pullParser.nextText();
                    map.put(key, value);

                    break;

                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = pullParser.next();
        }
        return map;
    }
}