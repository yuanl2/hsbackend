//package com.hansun;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.hansun.server.common.Utils;
//import com.hansun.server.dto.StoreInfo;
//import com.hansun.server.dto.UserAdditionInfo;
//import org.apache.commons.text.StringEscapeUtils;
//import org.json.JSONObject;
//import org.junit.Test;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//public class JsonTest {
//
//    @Test
//    public void test1() throws Exception{
//
//        UserAdditionInfo info = new UserAdditionInfo();
//        info.setUserName("user1");
//        List<StoreInfo> storeInfoList = new ArrayList<>();
//
//        StoreInfo storeInfo = new StoreInfo();
//        storeInfo.setAddress("上海市宜山路900号");
//        storeInfo.setName("Proxy 1");
//        storeInfo.setPromoDate(Utils.getNowTime());
//        storeInfo.setUserName("user1");
//        storeInfoList.add(storeInfo);
//        info.setStores(storeInfoList);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//
//
//
//
//        String s = objectMapper.writeValueAsString(info);
//
//        String format =  s.replace("\"","\\\"");
//
//        System.out.println(format);
//
//        String o = StringEscapeUtils.escapeXSI(s);
//
//        String t1 = "{\"stores\":[{\"userName\":\"user1\",\"name\":\"Proxy 1\",\"address\":\"上海市宜山路900号\",\"tel\":null,\"promoDate\":\"2018-07-31 06:41:23\"}],\"userName\":\"user1\"}";
//
//
//        System.out.println(t1);
//        System.out.println(t1.equals(format));
//        System.out.println(StringEscapeUtils.escapeJson(format));
////        System.out.println(StringEscapeUtils.escapeEcmaScript(s));
////        System.out.println(StringEscapeUtils.escapeXSI(s));
////        System.out.println(StringEscapeUtils.escapeXml10(s));
//
//        Object t = objectMapper.readValue(format,UserAdditionInfo.class);
//
//
//        System.out.println(""+new String(objectMapper.writeValueAsBytes(info)));
//
//
//
//    }
//}
package com.hansun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hansun.server.common.Utils;
import com.hansun.server.dto.StoreInfo;
import com.hansun.server.dto.UserAdditionInfo;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JsonTest {

    @Test
    public void test1() throws Exception{

        UserAdditionInfo info = new UserAdditionInfo();
        info.setUserName("user1");
        List<StoreInfo> storeInfoList = new ArrayList<>();

        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setAddress("上海市宜山路900号");
        storeInfo.setName("Proxy 1");
        storeInfo.setPromoDate(Utils.getNowTime());
        storeInfo.setUserName("user1");
        storeInfoList.add(storeInfo);
        info.setStores(storeInfoList);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);




        String s = objectMapper.writeValueAsString(info);

        JSONObject jsonObject = new JSONObject(s);

        System.out.println(jsonObject.toString());

        String format =  s.replace("\"","\\\"");
        String build= new StringBuilder().append("\"").append(format).append("\"").toString();

        System.out.println(build);

        String o = StringEscapeUtils.escapeXSI(s);

        String t1 = "{\"stores\":[{\"userName\":\"user1\",\"name\":\"Proxy 1\",\"address\":\"上海市宜山路900号\",\"tel\":null,\"promoDate\":\"2018-07-31 06:41:23\"}],\"userName\":\"user1\"}";


        System.out.println(format);

        System.out.println(t1);
        System.out.println(t1.equals(jsonObject.toString()));
//        System.out.println(StringEscapeUtils.escapeJson(format));
//        System.out.println(StringEscapeUtils.escapeEcmaScript(s));
//        System.out.println(StringEscapeUtils.escapeXSI(s));
//        System.out.println(StringEscapeUtils.escapeXml10(s));

        Object t = objectMapper.readValue(jsonObject.toString(),UserAdditionInfo.class);


        System.out.println(t);



    }
}
