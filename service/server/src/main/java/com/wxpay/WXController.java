//package com.wxpay;
//
///**
// * Created by yuanl2 on 2017/11/05.
// */
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Random;
//
//import javax.servlet.ServletInputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.hansun.server.controller.SessionUtil;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//import com.fasterxml.jackson.databind.JsonNode;
//
//@Controller
//@RequestMapping("/pay")
//public class WXController {
//
//    @RequestMapping(value = "wxprepay")
//    public void jspay(HttpServletRequest request, HttpServletResponse response, String callback) throws Exception {
//        // 获取openid
//        String openId = SessionUtil.getAtt(request, "openId");
//        if (openId == null) {
//            openId = getUserOpenId(request);
//        }
//
//        String appid = "wx16691fcb0523c1a4";
//        String partnerid = "22223670";
//        String paternerKey = "fjfjfjfjf1234567FFFFFFFFF1234567";
//
//        String out_trade_no = getTradeNo();
//        Map<String, String> paraMap = new HashMap<String, String>();
//        paraMap.put("appid", appid);
//        paraMap.put("attach", "测试支付");
//        paraMap.put("body", "测试购买Beacon支付");
//        paraMap.put("mch_id", partnerid);
//        paraMap.put("nonce_str", create_nonce_str());
//        paraMap.put("openid", openId);
//        paraMap.put("out_trade_no", out_trade_no);
//        paraMap.put("spbill_create_ip", getAddrIp(request));
//        paraMap.put("total_fee", "1");
//        paraMap.put("trade_type", "JSAPI");
//        paraMap.put("notify_url", "http://www.xxx.co/wxpay/pay/appPay_notify.shtml");
//        String sign = getSign(paraMap, paternerKey);
//        paraMap.put("sign", sign);
//
//        // 统一下单 https://api.mch.weixin.qq.com/pay/unifiedorder
//        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//
//        String xml = ArrayToXml(paraMap, false);
//
//        String xmlStr = HttpKit.post(url, xml);
//
//        // 预付商品id
//        String prepay_id = "";
//
//        if (xmlStr.indexOf("SUCCESS") != -1) {
//            Map<String, String> map = doXMLParse(xmlStr);
//            prepay_id = (String) map.get("prepay_id");
//        }
//
//        Map<String, String> payMap = new HashMap<String, String>();
//        payMap.put("appId", appid);
//        payMap.put("timeStamp", create_timestamp());
//        payMap.put("nonceStr", create_nonce_str());
//        payMap.put("signType", "MD5");
//        payMap.put("package", "prepay_id=" + prepay_id);
//        String paySign = getSign(payMap, paternerKey);
//
//        payMap.put("pg", prepay_id);
//        payMap.put("paySign", paySign);
//
//        WebUtil.response(response, WebUtil.packJsonp(callback,
//                JsonUtil.warpJsonNodeResponse(JsonUtil.objectToJsonNode(payMap)).toString()));
//    }
//
//    @RequestMapping(value = "appPay")
//    public void appPay(HttpServletRequest request, HttpServletResponse response, String body, String detail,
//                       String total_fee, String spbill_create_ip, String notify_url, String trade_type, String callback)
//            throws Exception {
//
//        String appid = "wx16691fcb0523c1a4";
//        String partnerid = "22223670";
//        String paternerKey = "fjfjfjfjf1234567FFFFFFFFF1234567";
//
//        String out_trade_no = getTradeNo();
//        Map<String, String> paraMap = new HashMap<String, String>();
//        paraMap.put("appid", appid);
//        paraMap.put("body", body);
//        paraMap.put("mch_id", partnerid);
//        paraMap.put("nonce_str", create_nonce_str());
//        paraMap.put("out_trade_no", out_trade_no);
//        paraMap.put("spbill_create_ip", spbill_create_ip);
//        paraMap.put("total_fee", total_fee);
//        paraMap.put("trade_type", trade_type);
//        paraMap.put("notify_url", notify_url);
//        String sign = getSign(paraMap, paternerKey);
//        paraMap.put("sign", sign);
//
//        // 统一下单 https://api.mch.weixin.qq.com/pay/unifiedorder
//        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//
//        String xml = ArrayToXml(paraMap, false);
//
//        String xmlStr = HttpKit.post(url, xml);
//
//        // 预付商品id
//        String prepay_id = "";
//
//        Map<String, String> map = doXMLParse(xmlStr);
//        if (xmlStr.indexOf("SUCCESS") != -1) {
//            prepay_id = (String) map.get("prepay_id");
//        }
//
//        String result_code = map.get("result_code");
//        String err_code_des = map.get("err_code_des");
//        Map<String, String> payMap = new HashMap<String, String>();
//        payMap.put("appid", appid);
//        payMap.put("partnerid", partnerid);
//        payMap.put("prepayid", prepay_id);
//        payMap.put("package", "Sign=WXPay");
//        payMap.put("noncestr", create_nonce_str());
//        payMap.put("timestamp", create_timestamp());
//        String paySign = getSign(payMap, paternerKey);
//
//        payMap.put("sign", paySign);
//        payMap.put("result_code", result_code);
//        payMap.put("err_code_des", err_code_des);
//
//        WebUtil.response(response, WebUtil.packJsonp(callback,
//                JsonUtil.warpJsonNodeResponse(JsonUtil.objectToJsonNode(payMap)).toString()));
//    }
//
//    @RequestMapping("/appPay_notify")
//    public void appPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        // String xml =
//        // "<xml><appid><![CDATA[wxb4dc385f953b356e]]></appid><bank_type><![CDATA[CCB_CREDIT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[1228442802]]></mch_id><nonce_str><![CDATA[1002477130]]></nonce_str><openid><![CDATA[o-HREuJzRr3moMvv990VdfnQ8x4k]]></openid><out_trade_no><![CDATA[1000000000051249]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[1269E03E43F2B8C388A414EDAE185CEE]]></sign><time_end><![CDATA[20150324100405]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[1009530574201503240036299496]]></transaction_id></xml>";
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/xml");
//        ServletInputStream in = request.getInputStream();
//        String xmlMsg = Tools.inputStream2String(in);
//
//        Map<String, String> map = doXMLParse(xmlMsg);
//        String return_code = map.get("return_code");
//        String return_msg = map.get("return_msg");
//
//        map = new HashMap<String, String>();
//        map.put("return_code", return_code);
//        map.put("return_msg", return_msg);
//
//        // 响应xml
//        String resXml = ArrayToXml(map, true);
//        response.getWriter().write(resXml);
//    }
//
//    @RequestMapping("/orderquery")
//    public void orderquery(HttpServletRequest request, HttpServletResponse response, String transaction_id,
//                           String out_trade_no, String callback) throws Exception {
//
//        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
//
//        String appid = "wx16691fcb0523c1a4";
//        String partnerid = "22223670";
//        String paternerKey = "fjfjfjfjf1234567FFFFFFFFF1234567";
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("appid", appid);
//        map.put("mch_id", partnerid);
//        if (transaction_id != null && !transaction_id.equals("")) {
//            map.put("transaction_id", transaction_id);
//        } else {
//            map.put("out_trade_no", out_trade_no);
//        }
//        map.put("nonce_str", create_nonce_str());
//        String paySign = getSign(map, paternerKey);
//        map.put("sign", paySign);
//
//        String xml = ArrayToXml(map, false);
//        String xmlStr = HttpKit.post(url, xml);
//
//        Map<String, String> orderMap = doXMLParse(xmlStr);
//
//        WebUtil.response(response, WebUtil.packJsonp(callback,
//                JsonUtil.warpJsonNodeResponse(JsonUtil.objectToJsonNode(orderMap)).toString()));
//    }
//
//    /**
//     * map转成xml
//     *
//     * @param arr
//     * @return
//     */
//    public String ArrayToXml(Map<String, String> parm, boolean isAddCDATA) {
//        StringBuffer strbuff = new StringBuffer("<xml>");
//        if (parm != null && !parm.isEmpty()) {
//            for (Entry<String, String> entry : parm.entrySet()) {
//                strbuff.append("<").append(entry.getKey()).append(">");
//                if (isAddCDATA) {
//                    strbuff.append("<![CDATA[");
//                    if (StringUtil.isNotEmpty(entry.getValue())) {
//                        strbuff.append(entry.getValue());
//                    }
//                    strbuff.append("]]>");
//                } else {
//                    if (StringUtil.isNotEmpty(entry.getValue())) {
//                        strbuff.append(entry.getValue());
//                    }
//                }
//                strbuff.append("</").append(entry.getKey()).append(">");
//            }
//        }
//        return strbuff.append("</xml>").toString();
//    }
//
//    // 获取openId
//    private String getUserOpenId(HttpServletRequest request) throws Exception {
//        String code = request.getParameter("code");
//        if (code == null) {
//            String openId = request.getParameter("openId");
//            return openId;
//        }
//        Oauth o = new Oauth();
//        String token = o.getToken(code);
//        JsonNode node = JsonUtil.StringToJsonNode(token);
//        String openId = node.get("openid").asText();
//        return openId;
//    }
//
//    private String create_nonce_str() {
//        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        String res = "";
//        for (int i = 0; i < 16; i++) {
//            Random rd = new Random();
//            res += chars.charAt(rd.nextInt(chars.length() - 1));
//        }
//        return res;
//    }
//
//    private String getAddrIp(HttpServletRequest request) {
//        return request.getRemoteAddr();
//    }
//
//    private String create_timestamp() {
//        return Long.toString(System.currentTimeMillis() / 1000);
//    }
//
//    private String getTradeNo() {
//        String timestamp = DatetimeUtil.formatDate(new Date(), DatetimeUtil.DATETIME_PATTERN);
//        return "HZNO" + timestamp;
//    }
//
//    private String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
//        String string1 = Pay.createSign(params, false);
//        String stringSignTemp = string1 + "&key=" + paternerKey;
//        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
//        return signValue;
//    }
//
//    private Map<String, String> doXMLParse(String xml) throws XmlPullParserException, IOException {
//        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
//        Map<String, String> map = null;
//        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
//        pullParser.setInput(inputStream, "UTF-8"); // 为xml设置要解析的xml数据
//
//        int eventType = pullParser.getEventType();
//
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//            switch (eventType) {
//                case XmlPullParser.START_DOCUMENT:
//                    map = new HashMap<String, String>();
//                    break;
//
//                case XmlPullParser.START_TAG:
//                    String key = pullParser.getName();
//                    if (key.equals("xml"))
//                        break;
//
//                    String value = pullParser.nextText();
//                    map.put(key, value);
//
//                    break;
//
//                case XmlPullParser.END_TAG:
//                    break;
//            }
//            eventType = pullParser.next();
//        }
//        return map;
//    }
//}