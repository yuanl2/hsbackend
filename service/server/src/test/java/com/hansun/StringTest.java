package com.hansun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yuanl2 on 2017/4/18.
 */
public class StringTest {

    public static void main(String[] args) {

        String str = "SpecificUserList:weiwxie,changche,lijjin,hellotom019,Global:yuw3";
        if (str.startsWith("SpecificUserList:")) {
            int start = str.indexOf(":");

            String[] users = str.substring(start + 1).split(",");

            Set<String> userSet = new HashSet<String>();

            for (String u : users)
                userSet.add(u.replaceFirst("^[a-zA-Z]+:", ""));

            System.out.println(userSet.size());

            userSet.forEach(v -> System.out.println(v));
        }


        List<String> list1 = new ArrayList<>();
        list1.add("user1");
        list1.add("user2");
        list1.add("user3");

        List<String> list2 = new ArrayList<>();
        list2.add("user3");
        list2.add("user2");
        list2.add("user1");

        list2.removeAll(list1);
        list1.addAll(list2);

        list1.forEach(System.out::println);
    }
}
