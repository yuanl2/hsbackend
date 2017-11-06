package com.hansun.server.controller;

import com.hansun.server.util.ConstantUtil;
import com.hansun.server.util.TenpayUtil;
import com.wxpay.HttpKit;
import com.wxpay.Pay;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付服务端简单示例
 * @author seven_cm
 * @dateTime 2014-11-29
 */
@RestController
@RequestMapping("/weixin")
public class WXPayController {

	private org.slf4j.Logger log = LoggerFactory.getLogger(WXPayController.class);

	
	@RequestMapping(value ="savepackage", method = RequestMethod.POST)
	public String doWeinXinRequest(@RequestParam(value = "userId", required = true, defaultValue = "false") String userId,
								   @RequestParam(value = "deviceID", required = true, defaultValue = "false") String deviceID,
								   @RequestParam(value = "product_id", required = true, defaultValue = "false") String product_id,
								   @RequestParam(value = "price", required = true, defaultValue = "false") String price, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<Object,Object> resInfo = new HashMap<Object, Object>();
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

		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("appid", ConstantUtil.APP_ID);
		paraMap.put("attach", "测试支付product_id=" + product_id);
		paraMap.put("body", "支付设备ID"+deviceID);
		paraMap.put("mch_id", ConstantUtil.PARTNER);
		paraMap.put("nonce_str", create_nonce_str());
		paraMap.put("openid", openId);
		paraMap.put("out_trade_no", out_trade_no);
		paraMap.put("spbill_create_ip", getAddrIp(request));
		paraMap.put("total_fee", "1");
		paraMap.put("trade_type", "JSAPI");
		paraMap.put("notify_url", "http://www.xxx.co/wxpay/pay/appPay_notify.shtml");
		String sign = getSign(paraMap, ConstantUtil.PARTNER_KEY);
		paraMap.put("sign", sign);

		for (Map.Entry entry:
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
			for (Map.Entry entry:
				 map.entrySet()) {
				log.info(entry.getKey() + " = {} ", entry.getValue());
			}
			prepay_id = map.get("prepay_id");
			log.info("prepay_id = {} ", prepay_id);
		}

		return prepay_id;
//
//		Map<String, String> payMap = new HashMap<String, String>();
//		payMap.put("appId", ConstantUtil.APP_ID);
//		payMap.put("timeStamp", create_timestamp());
//		payMap.put("nonceStr", create_nonce_str());
//		payMap.put("signType", "MD5");
//		payMap.put("package", "prepay_id=" + prepay_id);
//		String paySign = getSign(payMap, ConstantUtil.PARTNER_KEY);
//
//		payMap.put("pg", prepay_id);
//		payMap.put("paySign", paySign);
//
//		Map<String, String> orderMap = doXMLParse(xmlStr);
//		WebUtil.response(response, WebUtil.packJsonp(callback,
//				JsonUtil.warpJsonNodeResponse(JsonUtil.objectToJsonNode(orderMap)).toString()));
////
//		PackageRequestHandler packageReqHandler = new PackageRequestHandler(request, response);//生成package的请求类
//		PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler(request, response);//获取prepayid的请求类
//		ClientRequestHandler clientHandler = new ClientRequestHandler(request, response);//返回客户端支付参数的请求类
//		packageReqHandler.setKey(ConstantUtil.PARTNER_KEY);
//
//		int retcode ;
//		String retmsg = "";
//		String xml_body = "";
//		//获取token值
//
//		String token = AccessTokenRequestHandler.getAccessToken();
//
//		log.info("get token value =  " + token);
//
//		if (!"".equals(token)) {
//
//			String deviceID = request.getParameter("deviceID");
//			//设置package订单参数
//			packageReqHandler.setParameter("bank_type", "WX");//银行渠道
//			packageReqHandler.setParameter("body", "测试" + deviceID); //商品描述
//			packageReqHandler.setParameter("notify_url", notify_url); //接收财付通通知的URL
//			packageReqHandler.setParameter("partner", ConstantUtil.PARTNER); //商户号
//			packageReqHandler.setParameter("out_trade_no", out_trade_no); //商家订单号
//			packageReqHandler.setParameter("total_fee", "0.01"); //商品金额,以分为单位
//			packageReqHandler.setParameter("spbill_create_ip",request.getRemoteAddr()); //订单生成的机器IP，指用户浏览器端IP
//			packageReqHandler.setParameter("fee_type", "1"); //币种，1人民币   66
//			packageReqHandler.setParameter("input_charset", "GBK"); //字符编码
//
//			//获取package包
//			String packageValue = packageReqHandler.getRequestURL();
//			resInfo.put("package", packageValue);
//
//			log.info("get package = " + packageValue);
//
//			String noncestr = WXUtil.getNonceStr();
//			String timestamp = WXUtil.getTimeStamp();
//			String traceid = "";
//			////设置获取prepayid支付参数
//			prepayReqHandler.setParameter("appid", ConstantUtil.APP_ID);
//			prepayReqHandler.setParameter("appkey", ConstantUtil.APP_KEY);
//			prepayReqHandler.setParameter("noncestr", noncestr);
//			prepayReqHandler.setParameter("package", packageValue);
//			prepayReqHandler.setParameter("timestamp", timestamp);
//			prepayReqHandler.setParameter("traceid", traceid);
//
//			//生成获取预支付签名
//			String sign = prepayReqHandler.createSHA1Sign();
//			//增加非参与签名的额外参数
//			prepayReqHandler.setParameter("app_signature", sign);
//			prepayReqHandler.setParameter("sign_method",
//					ConstantUtil.SIGN_METHOD);
//			String gateUrl = ConstantUtil.GATEURL + token;
//			prepayReqHandler.setGateUrl(gateUrl);
//
//			//获取prepayId
//			String prepayid = prepayReqHandler.sendPrepay();
//
//			log.info("get prepayid = " + prepayid);
//
//			//吐回给客户端的参数
//			if (null != prepayid && !"".equals(prepayid)) {
//				//输出参数列表
//				clientHandler.setParameter("appid", ConstantUtil.APP_ID);
//				clientHandler.setParameter("appkey", ConstantUtil.APP_KEY);
//				clientHandler.setParameter("noncestr", noncestr);
//				//clientHandler.setParameter("package", "Sign=" + packageValue);
//				clientHandler.setParameter("package", "Sign=WXPay");
//				clientHandler.setParameter("partnerid", ConstantUtil.PARTNER);
//				clientHandler.setParameter("prepayid", prepayid);
//				clientHandler.setParameter("timestamp", timestamp);
//				//生成签名
//				sign = clientHandler.createSHA1Sign();
//				clientHandler.setParameter("sign", sign);
//
//				xml_body = clientHandler.getXmlBody();
//				resInfo.put("entity", xml_body);
//				retcode = 0;
//				retmsg = "OK";
//			} else {
//				retcode = -2;
//				retmsg = "error：get prepayId fail";
//			}
//		} else {
//			retcode = -1;
//			retmsg = "error：can't get Token";
//		}
//
//		resInfo.put("retcode", retcode);
//		resInfo.put("retmsg", retmsg);
//		String strJson = JSON.toJSONString(resInfo);
//		return responseAjax(request, strJson);
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

	private String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
		String string1 = Pay.createSign(params, false);
		String stringSignTemp = string1 + "&key=" + paternerKey;
		String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
		return signValue;
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