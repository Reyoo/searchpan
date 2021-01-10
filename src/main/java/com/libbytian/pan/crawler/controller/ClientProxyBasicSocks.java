package com.libbytian.pan.crawler.controller;


import com.libbytian.pan.system.util.UserAgentUtil;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author librabin
 */

public class ClientProxyBasicSocks {

//    {"error_code":0,"reason":"成功","result":["http://190.214.9.10:41572","http://36.67.8.27:53281","http://36.67.57.45:53367","http://41.72.203.66:38057","http://84.214.150.146:8080","http://150.109.32.166:80","http://157.119.57.85:80","http://194.44.77.226:55443","http://185.189.211.70:8080","http://197.210.217.66:34808","http://13.92.119.142:80","http://220.247.174.12:45954","http://80.73.87.198:59175","http://180.250.12.10:80","http://103.71.46.139:55443","http://186.10.80.122:53281","http://77.93.42.134:47803","http://139.99.105.185:80","http://41.170.12.92:37444","http://190.214.9.10:41572","http://103.21.163.81:6666","http://180.179.98.22:3128","http://119.82.252.29:46872","http://139.59.114.182:8118","http://41.65.201.172:5555","http://207.191.165.8:8080","http://200.149.214.6:33701","http://45.7.177.10:49344","http://139.99.105.185:80","http://41.65.201.176:8080","http://139.99.102.114:80","http://185.198.184.14:48122","http://78.30.239.58:61355","http://212.115.232.79:31280","http://110.44.124.220:55443","http://170.81.141.36:53281","http://125.26.7.124:61642","http://119.252.168.52:53281","http://211.24.105.19:47615","http://105.30.250.108:53281","http://190.83.31.16:8080","http://45.248.43.33:50197","http://37.79.254.152:3128","http://1.179.148.9:55636","http://78.8.188.100:32040","http://41.65.201.176:8080","http://103.106.148.209:30223","http://139.59.122.42:8118","http://138.255.32.65:34641","http://192.162.193.243:36910"]}
    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://zip.market.alicloudapi.com/devtoolservice/ipagency?foreigntype=0&protocol=0";

        String appcode = "50870b9b10984164a54f24d100b2ee3e";


        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        requestHeaders.add("Authorization", "APPCODE " + appcode);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);


        ResponseEntity<String> resultResponseEntity = restTemplate.exchange(
                String.format(url),
                HttpMethod.GET, requestEntity, String.class);
        System.out.println(resultResponseEntity.getBody());


    }
}