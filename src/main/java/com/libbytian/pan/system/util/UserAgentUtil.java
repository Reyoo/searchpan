package com.libbytian.pan.system.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class UserAgentUtil {
    private static List<String> list = new ArrayList<String>();

    static {

        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/6.7.3(0x16070321) NetType/WIFI Language/zh_CN");
        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f28) NetType/WIFI Language/zh_CN");
        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 13_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.12(0x17000c23) NetType/WIFI Language/zh_CN");
        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.20(0x17001428) NetType/4G Language/zh_CN");
        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/13.0 MQQBrowser/10.1.0 Mobile/15B87 Safari/604.1 QBWebViewUA/2 QBWebViewType/1 WKType/1");
//list.add("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36");
    }

    public static String randomUserAgent() {
        return list.get(getRandomIndex());
    }


    private static int getRandomIndex() {
        int max = 5 ;
        int min = 0;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }


}

