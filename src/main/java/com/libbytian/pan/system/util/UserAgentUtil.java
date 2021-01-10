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
        list.add("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 Mobile Safari/537.36 T7/11.20 SP-engine/2.16.0 baiduboxapp/11.20.0.14 (Baidu; P1 7.1.1)");
        list.add("Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/13.0 MQQBrowser/10.1.0 Mobile/15B87 Safari/604.1 QBWebViewUA/2 QBWebViewType/1 WKType/1");
        list.add("Mozilla/5.0 (Linux; Android 9; COR-AL10 Build/HUAWEICOR-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 Mobile Safari/537.36 T7/11.20 SP-engine/2.16.0 baiduboxapp/11.20.0.14 (Baidu; P1 9) NABar/2.0");
        list.add("Mozilla/5.0 (Linux; Android 7.1.1; OPPO R11 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045008 Mobile Safari/537.36 V1_AND_SQ_7.8.2_926_YYB_D QQ/7.8.2.3750 NetType/4G WebP/0.3.0 Pixel/1080 StatusBarHeight/54");

    }

    public static String randomUserAgent() {
        return list.get(getRandomIndex());
    }


    private static int getRandomIndex() {
        int max = 8;
        int min = 0;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }


}

