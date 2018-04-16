package com.hansun.server.controller;

import com.alibaba.fastjson.JSON;
import com.hansun.dto.Consume;
import com.hansun.dto.Order;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.db.DataStore;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import com.hansun.server.util.ConstantUtil;
import com.hansun.server.util.MD5Util;
import com.hansun.server.util.TenpayUtil;
import com.wxpay.HttpKit;
import com.wxpay.Pay;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Instant;
import java.util.*;

/**
 * 微信支付服务端
 *
 * @author
 * @dateTime 2017-11-29
 */
@RestController
@RequestMapping("/weixin")
public class WXPayController {
    private org.slf4j.Logger log = LoggerFactory.getLogger(WXPayController.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "savepackage", method = RequestMethod.POST)
    public String doWeinXinRequest(@RequestParam(value = "userId", required = true, defaultValue = "0") String userId,
                                   @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                                   @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                                   @RequestParam(value = "price", required = true, defaultValue = "0") String price, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<Object, Object> resInfo = new HashMap<Object, Object>();
        //接收财付通通知的URL
        //---------------生成订单号 开始------------------------
        //当前时间 yyyyMMddHHmmss
        String currTime = TenpayUtil.getCurrTime();
        //四位随机数
        String strRandom = TenpayUtil.buildRandom(5) + "";
        //10位序列号,可以自行调整。
        String strReq = currTime + strRandom;
        //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
        String out_trade_no = strReq;
        //---------------生成订单号 结束------------------------
        String openId = userId;

        String fee = String.valueOf((int) (Double.valueOf(price) * 100));
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("appid", ConstantUtil.APP_ID);
        paraMap.put("attach", "test pay product_id=" + product_id);
        paraMap.put("body", "device ID" + device_id);
        paraMap.put("mch_id", ConstantUtil.PARTNER);
        paraMap.put("nonce_str", create_nonce_str());
        paraMap.put("openid", openId);
        paraMap.put("out_trade_no", out_trade_no);
        paraMap.put("spbill_create_ip", getAddrIp(request));
        paraMap.put("total_fee", fee);
        paraMap.put("trade_type", "JSAPI");
        paraMap.put("notify_url", ConstantUtil.PAY_SUCCESS_NOTIFY);
        String sign = getSign(paraMap, ConstantUtil.PARTNER_KEY);
        paraMap.put("sign", sign);

        for (Map.Entry entry :
                paraMap.entrySet()) {
            log.info(entry.getKey() + " = {} ", entry.getValue());
        }

        String xml = ArrayToXml(paraMap, false);
        String xmlStr = HttpKit.post(ConstantUtil.WECHAT_UNIFIEDORDER, xml);

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
        resInfo.put("orderId", out_trade_no);

        SortedMap<String, String> payMap = new TreeMap<String, String>();
        payMap.put("appId", ConstantUtil.APP_ID);
        payMap.put("signType", "MD5");
        payMap.put("package", "prepay_id=" + prepay_id);
        payMap.put("nonceStr", create_nonce_str());
        payMap.put("timeStamp", create_timestamp());
        String paySign = createSign(payMap, ConstantUtil.PARTNER_KEY);

        resInfo.put("paySign", paySign);
        resInfo.put("appId", ConstantUtil.APP_ID);
        resInfo.put("signType", "MD5");
        resInfo.put("package", "prepay_id=" + prepay_id);
        resInfo.put("nonceStr", payMap.get("nonceStr"));
        resInfo.put("timeStamp", payMap.get("timeStamp"));
        resInfo.put("product_id", product_id);
        resInfo.put("device_id", device_id);
        resInfo.put("userId", userId);

        for (Map.Entry entry :
                resInfo.entrySet()) {
            log.info(entry.getKey() + " = {} ", entry.getValue());
        }
        String strJson = JSON.toJSONString(resInfo);
        Consume consume = dataStore.queryConsume(Integer.valueOf(product_id));

        //在预支付时，就生成订单
        Order order = new Order();
        order.setId(Long.valueOf(out_trade_no));
        order.setOrderName(out_trade_no);
        order.setCreateTime(Instant.now());
        order.setStartTime(Instant.now());
        order.setPayAccount(userId);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeviceID(Long.valueOf(device_id));
        order.setPrice(Float.valueOf(price));
        order.setConsumeType(Integer.valueOf(product_id));
        order.setDuration(consume.getDuration());
        orderService.createOrder(order);
        log.info("create order {}", order);
        return strJson;
    }

    @RequestMapping(value = {"payNotify"})
    @ResponseBody
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String resultStr = null;
        String resXml = "";
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            resultStr = new String(outSteam.toByteArray(), "utf-8");
            log.info("payNotify resultStr = {} ", resultStr);

            Map<String, String> resultMap = doXMLParse(resultStr);
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
            //验证签名------------------------------------------------
//            resultMap.remove("sign");

            if (resultMap.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
                log.info("wechat pay return success");
                if (verifyWeixinNotify(resultMap)) {
                    log.info("wechat pay verify sign success");

                    // ====================================================================
                    // 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";


                    // 处理业务 -修改订单支付状态
                    Order order = orderService.getOrderByOrderID(out_trade_no);
                    if (order != null) {
                        log.info("wechat pay callback : modify order = {} status to PAYDONE ", out_trade_no);
                        order.setOrderStatus(OrderStatus.PAYDONE);
                        orderService.updateOrder(order);
                    } else {
                        log.error("no this order {}", out_trade_no);
                        return;
                    }

                } else {
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[sign error]]></return_msg>" + "</xml> ";

                }
                // ------------------------------
                // 处理业务完毕
                // ------------------------------
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            log.error("pay notify exception", e);
        }
    }

    /**
     * 验证签名
     *
     * @param map
     * @return
     */
    public boolean verifyWeixinNotify(Map<String, String> map) {
        SortedMap<String, String> parameterMap = new TreeMap<String, String>();
        String sign = (String) map.get("sign");
        for (Object keyValue : map.keySet()) {
            if (!keyValue.toString().equals("sign")) {
                parameterMap.put(keyValue.toString(), map.get(keyValue).toString());
            }
        }
        String createSign = null;
        try {
            createSign = getSign(parameterMap, ConstantUtil.PARTNER_KEY);
        } catch (UnsupportedEncodingException e) {
            log.error("wechat pay verify sign failed");
            return false;
        }
        if (createSign.equals(sign)) {
            return true;
        } else {
            log.error("wechat pay verify sign failed");
            return false;
        }
    }

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

    private static String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = Pay.createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        return signValue;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public String createSign(SortedMap<String, String> packageParams, String paternerKey) {
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
        System.out.println("md5 sb:" + sb + "key=" + paternerKey);
        String sign = MD5Util.MD5Encode(sb.toString(), "utf-8")
                .toUpperCase();
        log.info("packge sign value:{}", sign);
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