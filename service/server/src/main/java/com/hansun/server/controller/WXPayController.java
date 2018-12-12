package com.hansun.server.controller;

import com.alibaba.fastjson.JSON;
import com.hansun.server.common.OrderType;
import com.hansun.server.common.Utils;
import com.hansun.server.dto.Consume;
import com.hansun.server.dto.Device;
import com.hansun.server.dto.OrderInfo;
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
import java.util.*;

import static com.hansun.server.common.Utils.*;

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



    @RequestMapping(value = "refund", method = RequestMethod.POST)
    public String doWeiXinRefund(@RequestParam(value = "userId", required = false, defaultValue = "0") String userId,
                                 @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                                 @RequestParam(value = "total_fee", required = true, defaultValue = "0") String total_fee,
                                 @RequestParam(value = "refund_fee", required = true, defaultValue = "0") String refund_fee,
                                 @RequestParam(value = "out_trade_no", required = true, defaultValue = "0") String out_trade_no, HttpServletRequest request, HttpServletResponse response) {



        orderService.requestRefund(device_id, total_fee, refund_fee, out_trade_no);


        return null;
    }




    @RequestMapping(value = "/paycancel", method = RequestMethod.POST)
    public String paycancel(@RequestParam(value = "orderId", required = true, defaultValue = "0") long orderId,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {

        log.debug("paycancel  orderId = {} ", orderId);
        OrderInfo o = orderService.getOrderByOrderID(orderId);
        o.setEndTime(Utils.getNowTime());
        o.setOrderStatus(OrderStatus.USER_NOT_PAY);

        Device device = deviceService.getDevice(o.getDeviceID());
        orderService.removeOrder(o.getDeviceID());
        log.debug("user cancel order delete from cache {}", o);
        String deviceStatus = String.valueOf(device.getStatus());
        log.debug("device_id {} deviceStatus {}", device.getDeviceID(), deviceStatus);
        return deviceStatus;
    }

    @RequestMapping(value = "savepackage", method = RequestMethod.POST)
    public String doWeinXinRequest(@RequestParam(value = "userId", required = true, defaultValue = "0") String userId,
                                   @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                                   @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                                   @RequestParam(value = "price", required = true, defaultValue = "0") String price, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<Object, Object> resInfo = new HashMap<Object, Object>();
        //---------------生成订单号 开始------------------------
        String out_trade_no = getOutTradeNoByTime();

        //---------------生成订单号 结束------------------------
        String openId = userId;

        Device device = deviceService.getDevice(Long.valueOf(device_id));
        String body = "device ID" + device_id;

        String attach = openId + "_" + product_id;
        if (device != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(device.getUser()).append("_").append(device.getAreaName()).append("_").append(device_id);
            body = stringBuilder.toString();

            stringBuilder = new StringBuilder();
            stringBuilder.append(device.getUser()).append("_").append(device.getAreaName()).append("_").append(openId).append("_").append(product_id);
            attach = stringBuilder.toString();
        }

        String fee = String.valueOf((int) (Double.valueOf(price) * 100));
        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("appid", ConstantUtil.APP_ID);
        paraMap.put("attach", attach);
        paraMap.put("body", body);
        paraMap.put("mch_id", ConstantUtil.PARTNER);
        paraMap.put("nonce_str", create_nonce_str());
        paraMap.put("openid", openId);
        paraMap.put("out_trade_no", out_trade_no);
        paraMap.put("spbill_create_ip", getAddrIp(request));
        paraMap.put("total_fee", fee);
        paraMap.put("fee_type", ConstantUtil.FEE_TYPE);
        paraMap.put("trade_type", ConstantUtil.TRADE_TYPE);
        paraMap.put("notify_url", ConstantUtil.PAY_SUCCESS_NOTIFY);
        String sign = getSign(paraMap, ConstantUtil.PARTNER_KEY);
        paraMap.put("sign_type", ConstantUtil.SIGN_TYPE);
        paraMap.put("sign", sign);

        for (Map.Entry entry :
                paraMap.entrySet()) {
            log.debug(entry.getKey() + " = {} ", entry.getValue());
        }

        String xml = ArrayToXml(paraMap, false);
        String xmlStr = HttpKit.post(ConstantUtil.WECHAT_UNIFIEDORDER, xml);

        log.debug("xmlStr = {} ", xmlStr);
        // 预付商品id
        String prepay_id = "";

        if (xmlStr.indexOf("SUCCESS") != -1) {
            Map<String, String> map = doXMLParse(xmlStr);
            for (Map.Entry entry :
                    map.entrySet()) {
                log.debug(entry.getKey() + " = {} ", entry.getValue());
            }
            prepay_id = map.get("prepay_id");
            log.debug("prepay_id = {} ", prepay_id);
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
            log.debug(entry.getKey() + " = {} ", entry.getValue());
        }
        String strJson = JSON.toJSONString(resInfo);
        Consume consume = dataStore.queryConsume(Short.valueOf(product_id));

        //在预支付时，就生成订单
        OrderInfo order = new OrderInfo();
        order.setOrderID(Long.valueOf(out_trade_no));
        order.setOrderName(out_trade_no);
        order.setCreateTime(Utils.getNowTime());
//        order.setStartTime(Instant.now());
        order.setPayAccount(userId);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeviceID(Long.valueOf(device_id));
        order.setPrice(Float.valueOf(price));
        order.setConsumeType(Short.valueOf(product_id));
        order.setDuration(consume.getDuration());
        order.setOrderType(OrderType.OPERATIONS.getType());
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
            log.debug("payNotify resultStr = {} ", resultStr);

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

            if (resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                log.debug("wechat pay return success");
                if (verifyWeixinNotify(resultMap)) {
                    log.debug("wechat pay verify sign success");

                    // ====================================================================
                    // 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";


                    // 处理业务 -修改订单支付状态
                    OrderInfo order = orderService.getOrderByOrderID(Long.valueOf(out_trade_no));
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
            } else {
                log.error(" pay return fail {}", resultMap.get("result_code"));

                // 处理业务 -修改订单支付状态 支付失败
                OrderInfo order = orderService.getOrderByOrderID(Long.valueOf(out_trade_no));
                if (order != null) {
                    log.info("wechat pay callback : modify order = {} status to USER_PAY_FAIL ", out_trade_no);
                    order.setOrderStatus(OrderStatus.USER_PAY_FAIL);
                    orderService.updateOrder(order);
                } else {
                    log.error("no this order {}", out_trade_no);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("pay notify exception", e);
        }
    }


}