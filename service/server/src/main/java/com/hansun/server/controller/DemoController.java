package com.hansun.server.controller;

import com.hansun.server.common.Utils;
import com.hansun.server.dto.Consume;
import com.hansun.server.dto.Device;
import com.hansun.server.dto.Order;
import com.hansun.server.HttpClientUtil;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.db.DataStore;
import com.hansun.server.metrics.HSServiceMetricsService;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import com.hansun.server.util.ConstantUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
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

    @Autowired
    private HSServiceMetricsService hsServiceMetricsService;

    /**
     * 页面访问入口，调用微信的authorize接口，获取用户在该公众号下的openid
     * @param model
     * @param device_id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/device")
    public String dispatch(Model model, @RequestParam(value = "device_id", required = false, defaultValue = "1") String device_id,
                           HttpServletRequest request, HttpServletResponse response) {
        String useragent = request.getHeader("User-Agent");
        logger.info("request from {}", useragent);

        Device device = deviceService.getDevice(Long.valueOf(device_id));
        if (device == null) {
            logger.error("device {} not exists", device_id);
            return "device_not_exist";
        }

        String url = "index?device_id=" + device_id;
        //go wechat flow
        String state = device_id;
        String reg = ".*micromessenger.*";
        try {

            if (useragent.toLowerCase().matches(reg)) {
                // Constant.GET_CODE_URL_WX = https://open.weixin.qq.com/connect/oauth2/authorize 请求WX接口
                url = ConstantUtil.GET_CODE_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&redirect_uri=" + ConstantUtil.REDIRECT_URI_WX
                        + "&response_type=code&scope=snsapi_base&state=" + state + ":WXZF"
                        + "&connect_redirect=1#wechat_redirect";
                logger.info("dispatch wx url = {}", url);
            } else {
//                url = Constant.GET_CODE_URL_ZFB + "?app_id=" + Constant.APPID_ZFB + "&scope=auth_base&redirect_uri="
//                        + URLEncoder.encode(REDIRECT_URI_ZFB, "utf-8") + "&state=" + state + ":ZFBZF"
//                        + (payForm.getFrom() == null ? ":ZC" : ":JF");
            }
        } catch (Exception e) {
            logger.error("get user openid error",e);
        }
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
        logger.info("alicode = {}", auth_code);
        logger.info("state = {}", state);
        logger.info("code = {}", code);

        Map requestParams = request.getParameterMap();
        logger.debug("requestParams.toString()" + requestParams.toString());

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
                logger.info("WXZF callback openid = {}", openid);
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
            logger.error("ZF error ", e);
        }

        return "redirect:index?openid=" + openid + "&device_id=" + name;
    }

    @RequestMapping("/index")
    public String page(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "000000") String name,
                       @RequestParam(value = "openid", required = false, defaultValue = "0") String openid,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("openid {} device_id {} ", openid, name);
        model.addAttribute("device_id", name);
        model.addAttribute("openid", openid);

        Device d = dataStore.queryDeviceByDeviceID(Long.valueOf(name));

        if (d != null) {
            List<Consume> consumes = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()));
            if (!openid.equals('0')) {
                model.addAttribute("consumes", consumes);
                model.addAttribute("store", d.getAdditionInfo());
                return "device_index";
            } else {
                consumes.removeIf(k -> k.getPrice() <= 0);
                model.addAttribute("consumes", consumes);
                model.addAttribute("store", d.getAdditionInfo());
                return "device_index";
            }
        } else {
            return "error";
        }
    }


    @RequestMapping("/detail")
    public String detailpage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                             @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                             @RequestParam(value = "user_id", required = false, defaultValue = "0") String user_id,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("product_id {} device_id {}", product_id, device_id);
        model.addAttribute("device_id", device_id);
        model.addAttribute("product_id", product_id);
        model.addAttribute("openid", user_id);

        Consume consume = dataStore.queryConsume(Short.valueOf(product_id));
        model.addAttribute("consume", consume);
        return "device_detail";
    }


    @RequestMapping("/disable")
    public String disablepage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                              @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id {}", device_id);
        model.addAttribute("device_id", device_id);
        model.addAttribute("extra", extra);
        return "device_disable";
    }


    @RequestMapping("/finish")
    public String finishpage(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                             @RequestParam(value = "extra", required = false, defaultValue = "0") String extra,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id {}", device_id);
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
        logger.info("device_id {}", device_id);

        Consume consume = dataStore.queryConsume(Short.valueOf(product_id));

        Order order = new Order();
        order.setOrderName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        order.setStartTime(Utils.getNowTime());
        order.setCreateTime(Utils.getNowTime());
        order.setPayAccount("test-payaccount");
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeviceID(Long.valueOf(device_id));
        order.setPayAccount("1");
        order.setConsumeType(Short.valueOf(product_id));


        orderService.createOrder(order);

        model.addAttribute("device_id", device_id);
        model.addAttribute("duration", consume.getDuration());
        model.addAttribute("extra", extra);

        order.setOrderStatus(OrderStatus.SERVICE);
        orderService.updateOrder(order);

//        hsServiceMetricsService.sendMetrics();

        return "device_running";
    }

    @RequestMapping(value = "/paysuccess")
    public String doWeinXinPay(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                               @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                               @RequestParam(value = "orderId", required = true, defaultValue = "0") String orderId,
                               @RequestParam(value = "userId", required = true, defaultValue = "0") String userId,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("paysuccess  userId = {} orderId = {} device_id = {}", userId, orderId, device_id);

        long deviceID = Long.valueOf(device_id);
        Order o = orderService.getOrder(deviceID);
        Consume consume = dataStore.queryConsume(Short.valueOf(product_id));

        //订单在用户点击微信支付的时候就创建了订单，但是如果用户cancel了订单，也会有订单数据，只是状态不一样
        if(o!=null && o.getOrderStatus() == OrderStatus.CREATED){
            o.setOrderStatus(OrderStatus.NOTSTART);
        }
        //deny access multi times
        if (o != null && o.getPayAccount().equals(userId) &&
                (o.getOrderStatus() == OrderStatus.SERVICE) && (Instant.now().isBefore(Utils.convertToInstant(o.getCreateTime()).plus(Duration.ofMinutes(o.getDuration())))
                || Instant.now().isBefore(Utils.convertToInstant(o.getStartTime()).plus(Duration.ofMinutes(o.getDuration()))))) {
            model.addAttribute("device_id", device_id);
//            model.addAttribute("duration", consume.getDuration() * 60);
//            model.addAttribute("startTime", o.getCreateTime().toEpochMilli());
            model.addAttribute("orderId", o.getId());
            logger.info(" device {} orderId {} now forward device_running again", device_id, orderId);
            return "device_finish";
        }

        if (orderId.equals('0')) {
            orderId = orderService.getOrderName();
        }

        int count = 20;
        Order order1 = null;
        while (count > 0) {
            count--;
            order1 = orderService.getOrderByOrderID(orderId);
            if(order1!=null && order1.getOrderStatus() == OrderStatus.FINISH){
                //重复收到了调用，直接返回
                model.addAttribute("device_id", device_id);
                model.addAttribute("duration", consume.getDuration() * 60);
                model.addAttribute("startTime", order1.getCreateTime().toString());
                model.addAttribute("orderId", orderId);
                logger.info(" device {} orderId {} now forward device_run_error", device_id, orderId);
                return "device_run_error";
            }
            if (order1 == null || order1.getOrderStatus() != OrderStatus.PAYDONE) {
                logger.info(" device {} not receive pay success notify count = {}", device_id, count);
                Thread.sleep(500);
            } else {
                logger.info(" device {} receive pay success notify count = {}", device_id, count);
                break;
            }
        }

        if (count > 0) {
            orderService.createStartMsgToDevice(order1);
            //update order start time
            order1.setStartTime(Utils.getNowTime());
        } else {
            model.addAttribute("device_id", device_id);
            model.addAttribute("duration", consume.getDuration() * 60);
            model.addAttribute("startTime", order1.getCreateTime().toEpochSecond(ZoneOffset.of("+8")));
            model.addAttribute("orderId", orderId);
            order1.setOrderStatus(OrderStatus.USER_NOT_PAY);
            orderService.updateOrder(order1);
            logger.info(" device {} orderId {} now forward device_run_error", device_id, orderId);
            return "device_run_error";
        }

        count = 20;
        while (count > 0) {
            count--;
            int status = deviceService.getDevice(deviceID).getStatus();
            logger.info("device status {} ", status);
            if ( status != DeviceStatus.SERVICE) {
                logger.info(" device {} not running count = {}", device_id, count);
                Thread.sleep(500);
            } else {
                logger.info(" device {} is running count = {}", device_id, count);
                break;
            }
        }

        if (count > 0) {
            model.addAttribute("device_id", device_id);
//            model.addAttribute("duration", consume.getDuration() * 60);
//            model.addAttribute("startTime", order1.getStartTime().toEpochMilli());
            model.addAttribute("orderId", orderId);
            order1.setOrderStatus(OrderStatus.SERVICE);
            orderService.updateOrder(order1);
            logger.info(" device {} now forward device_running", device_id);
            return "device_finish";
        } else {
            model.addAttribute("device_id", device_id);
            model.addAttribute("duration", consume.getDuration() * 60);
            model.addAttribute("startTime", order1.getCreateTime().toEpochSecond(ZoneOffset.of("+8")));
            model.addAttribute("orderId", orderId);
            order1.setOrderStatus(OrderStatus.DEVICE_ERROR);
            orderService.updateOrder(order1);
            logger.info(" device {} orderId {} now forward device_run_error", device_id, orderId);
            return "device_run_error";
        }
    }

    @RequestMapping("/report")
    public String reportpage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "report";
    }


    @RequestMapping("/device_monitor")
    public String device_monitor(Model model, @RequestParam(value = "userid", required = true, defaultValue = "-1") int userid,
                                 HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("userid {}", userid);
        model.addAttribute("userid", userid);

        if (userid == -1) {
            List<Device> list = deviceService.getAllDevices();
            model.addAttribute("devices", list);
        } else {
            List<Device> list = deviceService.getDevicesByOwner(userid);
            model.addAttribute("devices", list);
        }
        return "device_monitor";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "welcome";
    }
}


