package com.libbytian.pan.system.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class UserAgentUtil {
    private static List<String> list = new ArrayList<String>();

    static {

      list.add( "Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/6.7.3(0x16070321) NetType/WIFI Language/zh_CN");
      list.add( "Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.12(0x17000c23) NetType/WIFI Language/zh_HK");
//      list.add( "Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 MQQBrowser/10.1.1 Mobile/15E148 Safari/604.1 QBWebViewUA/2 QBWebViewType/1 WKType/1");
      list.add( "Mozilla/5.0 (iPhone; CPU iPhone OS 13_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.12(0x17000c23) NetType/WIFI Language/zh_CN");
      list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.20(0x17001428) NetType/4G Language/zh_CN");

    }

    public static String randomUserAgent() {
        return list.get(getRandomIndex());
    }


    private static int getRandomIndex() {
        int max = 4;
        int min = 0;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }



}

