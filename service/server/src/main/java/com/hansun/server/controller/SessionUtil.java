package com.hansun.server.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuanl2 on 2017/09/05.
 */
public class SessionUtil {

    public static void addAtt(HttpServletRequest request, String key, Object value){
        request.getSession().setAttribute(key, value);
    }

    public static void removeAtt(HttpServletRequest request, String key){
        request.getSession().removeAttribute(key);
    }

    public static String getAtt(HttpServletRequest request, String key){
        return (String)request.getSession().getAttribute(key);
    }

    public static Object getAttObj(HttpServletRequest request, String key){
        return request.getSession().getAttribute(key);
    }

    public static String optAtt(HttpServletRequest request, String key, String value){
        String r = (String)request.getSession().getAttribute(key);
        if (r == null){
            r = value;
        }
        return r;
    }
}
