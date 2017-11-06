//package com.wxpay;
//
//import java.util.*;
//
///**
// * Created by yuanl2 on 2017/11/06.
// */
//public class CommonUtil {
//
//    public static boolean IsNumeric(String str) {
//        if (str.matches("\\d *")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    //map转成xml
//    public static String MapToXml(HashMap<String, Object> arr) {
//        String xml = "<xml>";
//
//        Iterator<Map.Entry<String, Object>> iter = arr.entrySet().iterator();
//        while (iter.hasNext()) {
//            Entry<String, Object> entry = iter.next();
//            String key = entry.getKey();
//            String val = entry.getValue()+"";
//            if (IsNumeric(val)) {
//                xml += "<" + key + ">" + val + "</" + key + ">";
//
//            } else
//                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
//        }
//
//        xml += "</xml>";
//        return xml;
//    }
//
//    //xml转成map
//    @SuppressWarnings("unchecked")
//    public static Map<String, String> parseXml(String xml) throws Exception {
//        Map<String, String> map = new HashMap<String, String>();
//        Document document = DocumentHelper.parseText(xml);
//        Element root = document.getRootElement();
//        List<Element> elementList = root.elements();
//        for (Element e : elementList) {
//            map.put(e.getName(), e.getText());
//        }
//        return map;
//    }
//
//
//    public static String FormatParamMap(HashMap<String, Object> parameters) throws SDKRuntimeException {
//        String buff = "";
//        try {
//            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(
//                    parameters.entrySet());
//            Collections.sort(infoIds,
//                    new Comparator<Map.Entry<String, Object>>() {
//                        public int compare(Map.Entry<String, Object> o1,
//                                           Map.Entry<String, Object> o2) {
//                            return (o1.getKey()).toString().compareTo(
//                                    o2.getKey());
//                        }
//                    });
//
//            for (int i = 0; i < infoIds.size(); i++) {
//                Map.Entry<String, Object> item = infoIds.get(i);
//                if (item.getKey() != "") {
//                    buff += item.getKey() + "="
//                            + URLEncoder.encode(item.getValue()+"", "utf-8") + "&";
//                }
//            }
//            if (buff.isEmpty() == false) {
//                buff = buff.substring(0, buff.length() - 1);
//            }
//        } catch (Exception e) {
//            throw new SDKRuntimeException(e.getMessage());
//        }
//        return buff;
//    }
//
//    public static String CreateNoncestr() {
//        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        String res = "";
//        for (int i = 0; i < 16; i++) {
//            Random rd = new Random();
//            res += chars.charAt(rd.nextInt(chars.length() - 1));
//        }
//        return res;
//    }
//}
