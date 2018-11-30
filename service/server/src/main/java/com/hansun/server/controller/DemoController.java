package com.hansun.server.controller;

import com.hansun.server.common.*;
import com.hansun.server.db.dao.SuperAccountDao;
import com.hansun.server.dto.Consume;
import com.hansun.server.dto.Device;
import com.hansun.server.dto.Location;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.HttpClientUtil;
import com.hansun.server.db.DataStore;
import com.hansun.server.metrics.HSServiceMetricsService;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import com.hansun.server.util.ConstantUtil;
import com.hansun.server.util.TenpayUtil;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.hansun.server.common.Utils.checkListNotNull;


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
    private SuperAccountDao superAccountDao;

    @Autowired
    private HSServiceMetricsService hsServiceMetricsService;

    /**
     * 页面访问入口，调用微信的authorize接口，获取用户在该公众号下的openid
     *
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
        long deviceID = Long.valueOf(device_id);
        Device device = deviceService.getDevice(deviceID);
        model.addAttribute("device_id", device_id);
        if (device == null) {
            logger.error("device {} not exists", device_id);
            return "device_not_exist";
        }

        //判断设备是测试状态，则跳过微信支付
        if (device.getManagerStatus() == DeviceManagerStatus.TEST.getStatus()) {
            if (device.getStatus() == DeviceStatus.FAULT || device.getStatus() == DeviceStatus.INVALID) {
                model.addAttribute("error", "设备故障");
                return "device_test_error";
            }

            if (device.getStatus() == DeviceStatus.SERVICE) {
                model.addAttribute("error", "设备运行中");
                return "device_test_error";
            }

            if (device.getStatus() == DeviceStatus.DISCONNECTED) {
                model.addAttribute("error", "设备离线");
                return "device_test_error";
            }
            if (device.getStatus() == DeviceStatus.BADNETWORK) {
                model.addAttribute("error", "设备网络故障");
                return "device_test_error";
            }

            String store = device.getAdditionInfo();
            if (store == null) {
                store = "";
            }
            Random random = new Random();
            List<Consume> consumeList = getConsumesForSuperUser(device, false);
            if (consumeList == null || consumeList.size() == 0) {
                model.addAttribute("error", "设备没有对应的消费类型");
                return "device_test_error";
            }

            int type = random.nextInt(consumeList.size());
            Consume consume = consumeList.get(type);

            OrderInfo order = new OrderInfo();
            //---------------生成订单号 开始------------------------
            //当前时间 yyyyMMddHHmmss
            String currTime = TenpayUtil.getCurrTime();
            //四位随机数
            String strRandom = TenpayUtil.buildRandom(5) + "";
            //10位序列号,可以自行调整。
            String strReq = currTime + strRandom;
            //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
            String out_trade_no = strReq;
            order.setOrderID(Long.valueOf(out_trade_no));
            order.setOrderName("ordername-" + orderService.getSequenceNumber());
            order.setStartTime(Utils.getNowTime());
            order.setCreateTime(Utils.getNowTime());
            order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
            order.setOrderStatus(OrderStatus.PAYDONE);
            order.setDeviceID(deviceID);
            order.setDeviceName(device.getName());
            order.setConsumeType(Short.valueOf(consume.getId()));
            order.setOrderType(OrderType.TEST.getType());
            OrderInfo result = orderService.createOrder(order);
            orderService.createStartMsgToDevice(result);
            logger.info("device_id = " + deviceID + " start order " + result);

            model.addAttribute("duration", consume.getDuration());
            model.addAttribute("store", store);
            model.addAttribute("orderId", out_trade_no);
            return "device_testdevice";
        } else if (device.getManagerStatus() == DeviceManagerStatus.INACTIVATED.getStatus()) {
            model.addAttribute("error", "设备还未激活，不可使用");
            return "device_test_error";
        } else if (device.getManagerStatus() == DeviceManagerStatus.MAINTENANCE.getStatus()) {
            model.addAttribute("error", "设备目前处于维护中，请稍后再试");
            return "device_test_error";
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

                return "device_not_support_ali";
            }
        } catch (Exception e) {
            logger.error("get user openid error", e);
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
        logger.debug("alicode = {}", auth_code);
        logger.debug("state = {}", state);
        logger.debug("code = {}", code);

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
                logger.debug("WXZF callback openid = {}", openid);
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
    public String page(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "000000") String device_id,
                       @RequestParam(value = "openid", required = false, defaultValue = "0") String openid,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("openid {} device_id {} ", openid, device_id);
        model.addAttribute("device_id", device_id);
        model.addAttribute("openid", openid);

        Device d = dataStore.queryDeviceByDeviceID(Long.valueOf(device_id));
        boolean containPayAccount = dataStore.containPayAccount(openid);

        if (d != null) {
            String store = d.getAdditionInfo();
            if (store == null) {
                store = "";
            }
            boolean isSuperUser = dataStore.containSuperAccount(openid);
            List<Consume> consumeList = getConsumesForSuperUser(d, isSuperUser).stream().sorted((a, b) -> {
                if (a.getPrice() < b.getPrice()) {
                    return -1;
                } else {
                    return 0;
                }
            }).collect(Collectors.toList());
            if (!checkListNotNull(consumeList)) {
                model.addAttribute("error", "设备没有对应的消费类型");
                return "device_test_error";
            }

            if (isSuperUser) {
                model.addAttribute("consumes", consumeList);
                model.addAttribute("store", store);
                model.addAttribute("link", d.getStore());
                return "device_index";
            } else {
                if (containPayAccount) {
                    consumeList.removeIf(k -> k.getPrice() <= 0);
                }
                model.addAttribute("consumes", consumeList);
                model.addAttribute("store", store);
                model.addAttribute("link", d.getStore());
                return "device_index";
            }
        } else {
            return "error";
        }
    }

    private List<Consume> getConsumesForSuperUser(Device d, boolean isSuperUser) {
        byte consumeType = d.getConsumeType();
        List<Consume> consumes;

        if (isSuperUser) {
            consumes = dataStore.queryAllConsume().stream().filter(consume ->
                    consume.getDeviceType().equals(String.valueOf(d.getType())) && consume.getType() == ConsumeType.SUPERUSER.getValue()
            ).collect(Collectors.toList());
            consumes.stream().forEach(k->logger.info("super user consume {}", k));
            return consumes;
        } else {
            consumes = dataStore.queryAllConsumeByDeviceType(String.valueOf(d.getType()), consumeType);
            Location location = dataStore.queryLocationByLocationID(d.getLocationID());
            if (location == null) {
                return null;
            }
            /**
             * 根据Device配置的ConsumType，过滤得到匹配的Consume List
             *
             */
            consumes = consumes.stream().filter(consume -> {
                if (consume.getType() == ConsumeType.NORMAL.getValue()) {
                    return true;
                } else if (consume.getType() == ConsumeType.USER.getValue()) {
                    return consume.getValue().contains(d.getUserID() + "");
                } else if (consume.getType() == ConsumeType.LOCATION.getValue()) {
                    return consume.getValue().contains(location.getId() + "");
                } else if (consume.getType() == ConsumeType.AREA.getValue()) {
                    return consume.getValue().contains(location.getAreaID() + "");
                } else if (consume.getType() == ConsumeType.CITY.getValue()) {
                    return consume.getValue().contains(location.getCityID() + "");
                } else if (consume.getType() == ConsumeType.DEVICE.getValue()) {
                    return consume.getValue().contains(d.getDeviceID() + "");
                }
                return true;

            }).collect(Collectors.toList());
            return consumes;
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

        OrderInfo order = new OrderInfo();
        order.setOrderName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        order.setStartTime(Utils.getNowTime());
        order.setCreateTime(Utils.getNowTime());
        order.setPayAccount("test-payaccount");
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDeviceID(Long.valueOf(device_id));
        order.setPayAccount("1");
        order.setConsumeType(Short.valueOf(product_id));
        order.setOrderType(OrderType.TEST.getType());

        orderService.createOrder(order);

        model.addAttribute("device_id", device_id);
        model.addAttribute("duration", consume.getDuration());
        model.addAttribute("extra", extra);

        order.setOrderStatus(OrderStatus.SERVICE);
        orderService.updateOrder(order);

//        hsServiceMetricsService.sendMetrics();

        return "device_running";
    }


    @RequestMapping("/testdevice")
    public String testdevice(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "World") String device_id,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("device_id {}", device_id);

        long deviceID = Long.valueOf(device_id);

        Device d = dataStore.queryDeviceByDeviceID(deviceID);
        model.addAttribute("device_id", device_id);
        if (d != null) {

            if (d.getManagerStatus() != DeviceManagerStatus.TEST.getStatus()) {

                model.addAttribute("error", "设备管理状态不是测试状态");
                return "device_test_error";
            }
            if (d.getStatus() == DeviceStatus.FAULT || d.getStatus() == DeviceStatus.INVALID) {
                model.addAttribute("error", "设备故障");
                return "device_test_error";
            }

            if (d.getStatus() == DeviceStatus.SERVICE) {
                model.addAttribute("error", "设备运行中");
                return "device_test_error";
            }

            if (d.getStatus() == DeviceStatus.DISCONNECTED) {
                model.addAttribute("error", "设备离线");
                return "device_test_error";
            }
            if (d.getStatus() == DeviceStatus.BADNETWORK) {
                model.addAttribute("error", "设备网络故障");
                return "device_test_error";
            }

            String store = d.getAdditionInfo();
            if (store == null) {
                store = "";
            }
            Random random = new Random();
            List<Consume> consumeList = getConsumesForSuperUser(d, false);
            if (consumeList == null || consumeList.size() == 0) {
                model.addAttribute("error", "设备没有对应的消费类型");
                return "device_test_error";
            }
            int type = random.nextInt(consumeList.size());
            Consume consume = consumeList.get(type);

            OrderInfo order = new OrderInfo();
            //---------------生成订单号 开始------------------------
            //当前时间 yyyyMMddHHmmss
            String currTime = TenpayUtil.getCurrTime();
            //四位随机数
            String strRandom = TenpayUtil.buildRandom(5) + "";
            //10位序列号,可以自行调整。
            String strReq = currTime + strRandom;
            //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
            String out_trade_no = strReq;
            order.setOrderID(Long.valueOf(out_trade_no));
            order.setOrderName("ordername-" + orderService.getSequenceNumber());
            order.setStartTime(Utils.getNowTime());
            order.setCreateTime(Utils.getNowTime());
            order.setPayAccount("test-payaccount-" + orderService.getSequenceNumber());
            order.setOrderStatus(OrderStatus.PAYDONE);
            order.setDeviceID(deviceID);
            order.setDeviceName(d.getName());
            order.setConsumeType(Short.valueOf(consume.getId()));
            order.setOrderType(OrderType.TEST.getType());
            OrderInfo result = orderService.createOrder(order);
            orderService.createStartMsgToDevice(result);
            logger.info("device_id = " + deviceID + " start order " + result);

            model.addAttribute("duration", consume.getDuration());
            model.addAttribute("store", store);
            model.addAttribute("orderId", out_trade_no);
            return "device_testdevice";
        }

        model.addAttribute("error", "设备不存在");
        return "device_test_error";
    }

    @RequestMapping(value = "/paysuccess")
    public String paysuccess(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "0") String device_id,
                             @RequestParam(value = "product_id", required = true, defaultValue = "0") String product_id,
                             @RequestParam(value = "orderId", required = true, defaultValue = "0") long orderId,
                             @RequestParam(value = "userId", required = true, defaultValue = "0") String userId,
                             HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("paysuccess  userId = {} orderId = {} device_id = {} product_id = {}", userId, orderId, device_id, product_id);

        long deviceID = Long.valueOf(device_id);
        Device d = dataStore.queryDeviceByDeviceID(deviceID);
        OrderInfo o = orderService.getOrder(deviceID);
        Consume consume = dataStore.queryConsume(Short.valueOf(product_id));
        String store = d.getAdditionInfo();
        if (store == null) {
            store = "";
        }

        /**
         * 如果价格为0，说明没有走微信支付通道，直接下发任务给设备
         */
        if (consume.getPrice() <= 0) {
            if(o == null) {
                OrderInfo order = new OrderInfo();
                //---------------生成订单号 开始------------------------
                //当前时间 yyyyMMddHHmmss
                String currTime = TenpayUtil.getCurrTime();
                //四位随机数
                String strRandom = TenpayUtil.buildRandom(5) + "";
                //10位序列号,可以自行调整。
                String strReq = currTime + strRandom;
                //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
                String out_trade_no = strReq;
                order.setOrderID(Long.valueOf(out_trade_no));
                order.setOrderName("ordername-" + orderService.getSequenceNumber());
                order.setStartTime(Utils.getNowTime());
                order.setCreateTime(Utils.getNowTime());
                order.setPayAccount(userId);
                order.setOrderStatus(OrderStatus.PAYDONE);
                order.setDeviceID(deviceID);
                order.setDeviceName(d.getName());
                order.setConsumeType(Short.valueOf(consume.getId()));
                order.setOrderType(OrderType.OPERATIONS.getType());
                OrderInfo result = orderService.createOrder(order);
                orderService.createStartMsgToDevice(result);
                logger.info("device_id = " + deviceID + " start free order " + result);
                model.addAttribute("device_id", device_id);
                model.addAttribute("duration", consume.getDuration());
                model.addAttribute("store", store);
                model.addAttribute("orderId", order.getOrderID());
                model.addAttribute("link", d.getStore());
                return "device_start_running";
            }
        }

        if (o != null && o.getOrderStatus() == OrderStatus.NOTSTART) {
            o.setOrderStatus(OrderStatus.PAYDONE);
        }
        //deny access multi times
        if (o != null && o.getPayAccount().equals(userId) &&
                (o.getOrderStatus() == OrderStatus.SERVICE) && Utils.isOrderNotFinished(o)) {
            model.addAttribute("device_id", device_id);
            model.addAttribute("duration", consume.getDuration());
//            model.addAttribute("startTime", o.getCreateTime().toEpochMilli());
            model.addAttribute("orderId", o.getId());
            model.addAttribute("link", d.getStore());
            logger.debug(" device {} orderId {} now forward device_running again", device_id, orderId);
            return "device_start_running";
        }

//        o.setStartTime(Utils.getNowTime());
        orderService.createStartMsgToDevice(o);
        orderService.updateOrder(o);

        model.addAttribute("device_id", device_id);
        model.addAttribute("duration", consume.getDuration());
        model.addAttribute("store", store);
        model.addAttribute("orderId", orderId);
        model.addAttribute("link", d.getStore());
        logger.info(" device {} now forward device_running", device_id);
        return "device_start_running";
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
            List<Device> list = deviceService.getDevicesByUser(userid);
            model.addAttribute("devices", list);
        }
        return "device_monitor";
    }
//
//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String login(Model model, String error, String logout) {
//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");
//
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");
//
//        return "login";
//    }

    @RequestMapping(value = {"/ui"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "index";
    }
}


