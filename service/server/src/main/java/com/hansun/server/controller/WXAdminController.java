package com.hansun.server.controller;

import com.hansun.server.HttpClientUtil;
import com.hansun.server.db.dao.SuperAccountDao;
import com.hansun.server.dto.SuperAccount;
import com.hansun.server.util.ConstantUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yuanl2
 */
@Controller
public class WXAdminController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SuperAccountDao superAccountDao;

    @RequestMapping("/wxaddadmin")
    public String dispatch(Model model, HttpServletRequest request, HttpServletResponse response) {

        String useragent = request.getHeader("User-Agent");
        String url = "wxadmingetuserinfo";
        String reg = ".*micromessenger.*";
        try {

            if (useragent.toLowerCase().matches(reg)) {
                // Constant.GET_CODE_URL_WX = https://open.weixin.qq.com/connect/oauth2/authorize 请求WX接口
                url = ConstantUtil.GET_CODE_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&redirect_uri=" + ConstantUtil.REDIRECT_URI_WX_ADMIN
                        + "&response_type=code&scope=snsapi_userinfo&state=WXZF"
                        + "&connect_redirect=1#wechat_redirect";
                logger.info("addAdmin url = {}", url);
            } else {
//                url = Constant.GET_CODE_URL_ZFB + "?app_id=" + Constant.APPID_ZFB + "&scope=auth_base&redirect_uri="
//                        + URLEncoder.encode(REDIRECT_URI_ZFB, "utf-8") + "&state=" + state + ":ZFBZF"
//                        + (payForm.getFrom() == null ? ":ZC" : ":JF");
            }
        } catch (Exception e) {
            logger.error("get user openid error", e);
        }
        return "redirect:" + url;

    }

    @RequestMapping("/wxadmingetuserinfo")
    public String window(Model model, String code, String state, String auth_code, HttpServletRequest request) {
        logger.debug("alicode = {}", auth_code);
        logger.debug("state = {}", state);
        logger.debug("code = {}", code);
        Map requestParams = request.getParameterMap();
        String openid = "";
        try {
            if ("WXZF".equals(state)) {
                String url = ConstantUtil.GET_OPENID_URL_WX + "?appid=" + ConstantUtil.APP_ID + "&secret="
                        + ConstantUtil.APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
                String doGet = HttpClientUtil.doGet(url, null);
                Map<Object, Object> jsonToMap = JSONObject.fromObject(doGet);
                openid = (String) jsonToMap.get("openid");

                String accessToken = (String) jsonToMap.get("access_token");

                logger.info("getUserInfo openid = {}, accessToken = {}", openid, accessToken);

                String getUserInfoUrl = ConstantUtil.GET_USERINFO_URL_WX + "?access_token=" + accessToken + "&openid=" + openid + "&lang=en";
                String result = HttpClientUtil.doGet(getUserInfoUrl, null);
                jsonToMap = JSONObject.fromObject(result);
                jsonToMap.forEach((key, value) -> logger.info("Key: {} Value: {}", key, value));

                SuperAccount superAccount = new SuperAccount();
                superAccount.setName(openid);
                superAccount.setSex((Integer) jsonToMap.get("sex"));
                superAccount.setLanguage((String)jsonToMap.get("language"));
                superAccount.setHeadimgurl((String)jsonToMap.get("headimgurl"));
                superAccount.setProvince((String)jsonToMap.get("province"));
                superAccount.setNickname((String)jsonToMap.get("nickname"));
                superAccount.setCity((String)jsonToMap.get("city"));
                superAccount.setCountry((String)jsonToMap.get("country"));

                SuperAccount superAccount1 = superAccountDao.findByName(openid);
                if (superAccount1 == null) {
                    logger.info("add super user {}",superAccountDao.save(superAccount));
                }
            }
        } catch (Exception e) {
            logger.error("ZF error ", e);
        }
        model.addAttribute("openid", openid);
        return "admin_finish";
    }
}
