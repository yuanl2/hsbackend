package com.wxpay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yuanl2 on 2017/11/06.
 */
public class WebUtil {
    public static Object getSessionAttribute(HttpServletRequest req, String key) {
        Object ret = null;

        try {
            ret = req.getSession(false).getAttribute(key);
        } catch (Exception e) {
        }
        return ret;
    }

    public static void response(HttpServletResponse response, String result) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void response(HttpServletResponse response, ResponseMessage result) {
//        try {
//            response.setContentType("application/json;charset=utf-8");
//            response.getWriter().write(JsonUtil.objectToJsonNode(result).toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String packJsonp(String callback, String json) {
        if (json == null) {
            json = "";
        }
        if (callback == null || callback.isEmpty()) {
            return json;
        }

        return callback + "&&" + callback + '(' + json + ')';
    }

//    public static String packJsonp(String callback, ResponseMessage response) {
//        String json = null;
//        if (response == null) {
//            json = "";
//        } else {
//            json = JsonUtil.objectToJsonNode(response).toString();
//        }
//        if (callback == null || callback.isEmpty()) {
//            return json;
//        }
//
//        return callback + "&&" + callback + '(' + json + ')';
//    }
}
