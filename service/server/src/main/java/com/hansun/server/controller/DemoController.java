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
import org.springframework.web.bind.annotation.RequestMethod;
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
    public String dispatch(Model model, @RequestParam(value = "device_id", required = false, defaultValue = "World") String name,
                        HttpServletRequest request, HttpServletResponse response) {
        String useragent = request.getHeader("User-Agent");
        logger.info("request from " + useragent);
        String url = "index?device_id=" + name;
        //go wechat flow
        String state = name;
        String reg =  ".*micromessenger.*";
        try {

            if (useragent.toLowerCase().matches(reg)) {
                // Constant.GET_CODE_URL_WX = https://open.weixin.qq.com/connect/oauth2/authorize 请求WX接口
                url = ConstantUtil.GET_CODE_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&redirect_uri=" + ConstantUtil.REDIRECT_URI_WX
                        + "&response_type=code&scope=snsapi_base&state=" + state + ":WXZF"
                        + "&connect_redirect=1#wechat_redirect";
                logger.info("dispatch wx url = " + url);
            } else {
//                url = Constant.GET_CODE_URL_ZFB + "?app_id=" + Constant.APPID_ZFB + "&scope=auth_base&redirect_uri="
//                        + URLEncoder.encode(REDIRECT_URI_ZFB, "utf-8") + "&state=" + state + ":ZFBZF"
//                        + (payForm.getFrom() == null ? ":ZC" : ":JF");
            }
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

        logger.info(request.getContextPath());
        logger.info(request.getPathInfo());
        logger.info(request.getRequestURI());
        logger.info("alicode====" + auth_code);
        logger.info("state====" + state);
        logger.info("code====" + code);

        Map requestParams = request.getParameterMap();
        logger.info("requestParams.toString()" + requestParams.toString());

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
                logger.info("WXZF callback openid = "  + openid);
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

        return "redirect:index?openid=" + openid + "&device_id=" + name;
    }


//    /**
//     * @param payForm
//     * @param request
//     * @return //到页面 内部用 前端不用
//     */
//    @RequestMapping("/pn")
//    public String pn(PayForm payForm, HttpServletRequest request) {
//        /** 获取参数 */
//        String sn = payForm.getSn();
//        NewSysUser user = new NewSysUser();
//        user.setUserNo(sn);
//        NewSysUser findUser = null;
//        NewProductSn findsn = null;
//        try {
//            findUser = userBiz.findUser(user);
//            if (findUser != null) {
//                String shortname = findUser.getUserShortname();
//                shortname = URLEncoder.encode(shortname);
//                String encode = "http://jfshanghu.daoqidata.com/PhonePospInterface/wxPNPay2.jsp?saruname=" + shortname
//                        + "&saruLruid=" + findUser.getUserNo() + "&openid=" + payForm.getOpenid() + "&unionid="
//                        + payForm.getUnionid() + "&from=" + payForm.getFrom() + "&type=" + payForm.getType();
//                return "redirect:" + encode;
//            }
//            NewProductSn newProductSn = new NewProductSn();
//            newProductSn.setQrcode(sn);
//
//            findsn = ProductSnBiz.findBySn(newProductSn);
//            if (findsn == null) {
//                return "redirect:" + "http://mp.weixin.qq.com/s/87lyKJlu0NA91f_hj4jvpw";
//            }
//
//            NewSysUser user2 = new NewSysUser();
//            user2.setId(findsn.getUserId());
//            findUser = userBiz.findUser(user2);
//            if (findUser != null) {
//                String shortname = findUser.getUserShortname();
//                shortname = URLEncoder.encode(shortname);
//                String encode = "http://jfshanghu.daoqidata.com/PhonePospInterface/wxPNPay2.jsp?saruname=" + shortname
//                        + "&saruLruid=" + findUser.getUserNo() + "&openid=" + payForm.getOpenid() + "&unionid="
//                        + payForm.getUnionid() + "&from=" + payForm.getFrom() + "&type=" + payForm.getType();
//                return "redirect:" + encode;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "redirect:" + "http://mp.weixin.qq.com/s/QQfieiV8ooIx5nYc3zOtpw";
//    }



    @RequestMapping("/index")
    public String page(Model model, @RequestParam(value = "device_id", required = true, defaultValue = "000000") String name,
                       @RequestParam(value = "openid", required = false, defaultValue = "0") String openid,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("openid " + openid + "device_id " + name);
        model.addAttribute("device_id", name);
        model.addAttribute("openid", openid);

        List<Consume> consumes = dataStore.queryAllConsume();

        if (!openid.equals('0')) {
            model.addAttribute("consumes", consumes);
            return "newdevice";
        } else {
            consumes.removeIf(k -> k.getPrice() <= 0);
            model.addAttribute("consumes", consumes);
            return "newdevice";
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


    @RequestMapping("/device_monitor")
    public String device_monitor(Model model, @RequestParam(value = "userid", required = true, defaultValue = "-1") int userid,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("userid " + userid);
        model.addAttribute("userid", userid);

        if(userid == -1){
            List<Device> list = deviceService.getAllDevices();
            model.addAttribute("devices", list);
        }
        else{
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


