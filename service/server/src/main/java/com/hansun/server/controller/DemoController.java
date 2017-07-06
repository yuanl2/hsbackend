package com.hansun.server.controller;

import com.hansun.dto.Consume;
import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.HttpClientUtil;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.db.DataStore;
import com.hansun.server.db.OrderStore;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import com.hansun.server.service.TimerService;
import com.hansun.server.util.ConstantUtil;
import net.sf.json.JSONObject;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;


/**
 * Created by yuanl2 on 2017/5/1.
 */
@Controller
public class DemoController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/device")
    public String hello(Model model, @RequestParam(value = "device_id", required = false, defaultValue = "World") String name,
                        HttpServletRequest request, HttpServletResponse response) {
        String useragent = request.getHeader("User-Agent");
        logger.info("request from " + useragent);
        String url = "index?device_id=" + name;
        //go wechat flow
        String state = name;
        try {
//
//            if (useragent.contains("MicroMessenger")) {
//                // Constant.GET_CODE_URL_WX = https://open.weixin.qq.com/connect/oauth2/authorize 请求WX接口
//                url = ConstantUtil.GET_CODE_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&redirect_uri=" + ConstantUtil.REDIRECT_URI_WX
//                        + "&response_type=code&scope=snsapi_base&state=" + state + ":WXZF"
//                        + "#wechat_redirect";
//            } else {
////                url = Constant.GET_CODE_URL_ZFB + "?app_id=" + Constant.APPID_ZFB + "&scope=auth_base&redirect_uri="
////                        + URLEncoder.encode(REDIRECT_URI_ZFB, "utf-8") + "&state=" + state + ":ZFBZF"
////                        + (payForm.getFrom() == null ? ":ZC" : ":JF");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        model.addAttribute("device_id", name);
//        model.addAttribute("useragent", useragent);
        return "redirect:" + url;
    }


    /**
     * @param code
     * @param state   wx回调 前端不用
     * @param request
     * @return
     */
    @RequestMapping("/callback")
    public String window(String code, String state, String auth_code, HttpServletRequest request) {

        logger.info("alicode====" + auth_code);
        Map requestParams = request.getParameterMap();
        logger.info("PPPPPPPPPPPPPPPPPPPPPPpp" + requestParams.toString());

        String[] split = state.split(":");

        String name = split[0];
        String from = split[1];
        String openid = null;
        String unionid = null;
        //判断用户是否已经消费免费消费过了
        String free = "0";
        try {
            if ("WXZF".equals(split[1])) {
                String url = ConstantUtil.GET_OPENID_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&secret="
                        + ConstantUtil.APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
                String doGet = HttpClientUtil.doGet(url, null);
                Map<Object, Object> jsonToMap = JSONObject.fromObject(doGet);
                openid = (String) jsonToMap.get("openid");
            }
            if ("ZFBZF".equals(split[1])) {
//                AlipayClient alipayClient = new DefaultAlipayClient(Constant.GET_USERID_URL_ZFB, Constant.APPID_ZFB,
//                        Constant.ZFB_APP_PRIVATE_KEY, "json", "GBK", Constant.ZFB_ALIPAY_PUBLIC_KEY);
//                AlipaySystemOauthTokenRequest aliRequest = new AlipaySystemOauthTokenRequest();
//                aliRequest.setCode(auth_code);
//                aliRequest.setGrantType("authorization_code");
//                AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(aliRequest);
//                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + oauthTokenResponse.getUserId());
//                openid = oauthTokenResponse.getUserId();
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
        return "forward:/index?first=" + free + "&device_id=" + name;
    }

    @RequestMapping("/index")
    public String page(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "000000") String name,
                       @RequestParam(value = "first", required = false, defaultValue = "0") String first,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("page " + first + "device_id " + name);
        model.addAttribute("device_id", name);

        List<Consume> consumes = dataStore.queryAllConsume();

        if (first.equals("0")) {
            model.addAttribute("consumes", consumes);
            return "index_0";
        } else {
            consumes.removeIf(k -> k.getPrice() <= 0);
            model.addAttribute("consumes", consumes);
            return "index";
        }
    }


    @RequestMapping("/detail")
    public String detailpage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                             @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                             @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("product_id " + product_id + "device_id " + device_id);
        model.addAttribute("device_id", device_id);
        model.addAttribute("product_id", product_id);
        model.addAttribute("extra", extra);

        Consume consume = dataStore.queryConsume(Integer.valueOf(product_id));
        model.addAttribute("consume", consume);
        return "detail";
    }


    @RequestMapping("/disable")
    public String disablepage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                              @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id " + device_id);
        model.addAttribute("device_id", device_id);
        model.addAttribute("extra", extra);
        return "device_disable";
    }


    @RequestMapping("/finish")
    public String finishpage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                             @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id " + device_id);
        orderService.deleteOrder(Long.valueOf(device_id));
        model.addAttribute("device_id", device_id);
        model.addAttribute("extra", extra);
        return "device_finish";
    }

    @RequestMapping("/testcmd")
    public String testcmd(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                          @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                          @RequestParam(value = "product_id", required = false, defaultValue = "0") String product_id,
                          @RequestParam(value = "divfee", required = false, defaultValue = "0") String divfee,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id " + device_id);

        Consume consume = dataStore.queryConsume(Integer.valueOf(product_id));

        Order order = new Order();
        order.setOrderName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        order.setStartTime(Instant.now());
        order.setCreateTime(Instant.now());
        order.setPayAccount("test-payaccount");
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeviceID(Long.valueOf(device_id));
        order.setPayAccount("1");
        order.setConsumeType(Integer.valueOf(product_id));



        orderService.createOrder(order);

        model.addAttribute("device_id", device_id);
        model.addAttribute("duration", consume.getDuration());
        model.addAttribute("extra", extra);

        order.setOrderStatus(OrderStatus.SERVICE);
        orderService.updateOrder(order);

        return "device_running";
    }

    @RequestMapping("/report")
    public String reportpage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "report";
    }
}


