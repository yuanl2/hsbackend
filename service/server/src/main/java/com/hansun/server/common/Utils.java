package com.hansun.server.common;

import com.hansun.server.dto.OrderInfo;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.util.ConstantUtil;
import com.hansun.server.util.MD5Util;
import com.hansun.server.util.TenpayUtil;
import com.wxpay.Pay;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * Created by yuanl2 on 2017/7/7.
 */
public class Utils {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * 验证签名
     *
     * @param map
     * @return
     */
    public static boolean verifyWeixinNotify(Map<String, String> map) {
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

    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public static String getAddrIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String create_nonce_str() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    public static String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = Pay.createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        return signValue;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public static String createSign(SortedMap<String, String> packageParams, String paternerKey) {
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
    public static String ArrayToXml(Map<String, String> parm, boolean isAddCDATA) {
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

    public static Map<String, String> doXMLParse(String xml) throws XmlPullParserException, IOException {
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

    public static String getOutTradeNoByTime() {
        //当前时间 yyyyMMddHHmmss
        String currTime = TenpayUtil.getCurrTime();
        //四位随机数
        String strRandom = TenpayUtil.buildRandom(5) + "";
        //10位序列号,可以自行调整。
        String strReq = currTime + strRandom;
        //订单号，此处用时间加随机数生成，商户根据自己情况调整，只要保持全局唯一就行
        return strReq;
    }

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    public static LocalDateTime convertToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
    }

    public static Instant convertToInstant(LocalDateTime time) {
        return time.toInstant(ZoneOffset.of("+8"));
    }

    public static LocalDateTime getNowTime() {
        return convertToLocalDateTime(Instant.now());
    }

    public static LocalDateTime getCurrentMonth() {
        return getMonth(getNowTime());
    }

    /**
     * 判断订单状态是否应该结束了，如果订单的创建或者开始时间加上任务执行时间比当前时间还早
     * 则说明该任务早已结束，返回true，否则返回false
     *
     * @param order
     * @return
     */
    public static boolean isOrderFinished(OrderInfo order) {
        boolean result = false;
        if (order != null && order.getCreateTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getCreateTime()).plus(Duration.ofSeconds(order.getDuration())));
        }
        if (!result && order.getStartTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getStartTime()).plus(Duration.ofSeconds(order.getDuration())));
        }
        return result;
    }

    public static boolean isOrderStarted(OrderInfo order, int time) {
        boolean result = false;
        if (order != null && order.getCreateTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getCreateTime()).plus(Duration.ofSeconds(time)));
        }
        if (!result && order.getStartTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getStartTime()).plus(Duration.ofSeconds(time)));
        }
        return result;
    }

    /**
     * 判断订单状态是否未结束
     *
     * @param order
     * @return
     */
    public static boolean isOrderNotFinished(OrderInfo order) {
        return !isOrderFinished(order);
    }

    /**
     * @param month
     * @return
     */
    public static LocalDateTime parseMonthTime(String month) {
        DateFormat formatter1;
        formatter1 = new SimpleDateFormat("yyyy-MM");
        try {
            Date d = formatter1.parse(month);
            return convertToLocalDateTime(d.toInstant());
        } catch (ParseException e) {

        }
        return null;
    }

    public static LocalDateTime getOldTime() {
        return parseMonthTime("2010-01-01");
    }

    /**
     * get the zero clock
     *
     * @param time
     * @return
     */
    public static LocalDateTime getZeroClock(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * get the day before {day} days
     *
     * @param time
     * @param day
     * @return
     */
    public static LocalDateTime getDayBefore(LocalDateTime time, int day) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * @param time
     * @return
     */
    public static LocalDateTime getMonth(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * get the first day of next month
     *
     * @param time
     * @return
     */
    public static LocalDateTime getNextMonth(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> boolean checkListNotNull(List<T> lists) {
        return lists != null && lists.size() > 0;
    }

    /**
     * @param localDateTime
     * @return
     */
    public static Date dateToLocalDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @param time
     * @return
     */
    public static LocalDateTime convertTime(String time) {
        try {
            if (time == null) {

                return convertToLocalDateTime(Instant.now());
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(time);
            return convertToLocalDateTime(d.toInstant());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * get the next date
     *
     * @param time
     * @return
     */
    public static LocalDateTime convertEndTime(String time) {
        try {
            TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
            Calendar calendar = Calendar.getInstance(curTimeZone);
            if (time == null) {
                calendar.setTimeInMillis(Instant.now().toEpochMilli());
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(time);

            calendar.setTimeInMillis(d.toInstant().toEpochMilli());

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            LocalDateTime date = convertToLocalDateTime(calendar.toInstant());
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDouble(double a, int b) {
        if (b == 0) {
            return String.format("%.2f", 0f);
        }
        return String.format("%.2f", a / b);
    }

    public static String formatDouble(double a) {
        return String.format("%.2f", a);
    }

    public static boolean isAdminUser(UserInfo userInfo) {
        boolean isAdmin = false;
        for (String access : userInfo.getAccess()
                ) {
            if (access.equalsIgnoreCase("admin")) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }


    public static String getFormatTime(LocalDateTime time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToLocalDate(time));
    }
}
