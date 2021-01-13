package com.libbytian.pan.proxy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.libbytian.pan.proxy.model.ProxyIpAndPortModel;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author: QiSun
 * @date: 2021-01-13
 * @Description:
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetProxyService {


    private final RedisTemplate redisTemplate;

    private  final RestTemplate restTemplate;


    /**
     * 这个可以 但是就是慢
     */
    public void getProxyIp() {
        System.out.println(redisTemplate.randomKey());
        System.out.println(        redisTemplate.keys("use_proxy").size());
        ArrayList<String> list = new ArrayList(redisTemplate.opsForHash().keys("use_proxy") );
        int randomIndex = new Random().nextInt(list.size());
        String randomItem = list.get(randomIndex);
        System.out.println(randomItem);
    }


    /**
     * 远程调用 获取ip
     * @return
     */
    @Bean
    public String getProxyIpFromRemote(){
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        factory.setHttpClient(httpClient);
        this.restTemplate.setRequestFactory(factory);

        requestHeaders.setContentType(MediaType.TEXT_HTML);
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        requestHeaders.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        requestHeaders.add("Upgrade-Insecure-Requests", "1");
        requestHeaders.add("Cache-Control", "max-age=0");

        HttpEntity<String> httpEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange("http://127.0.0.1:5010/get", HttpMethod.GET,httpEntity, String.class);
        ProxyIpAndPortModel proxyIpAndPortModel = JSONObject.parseObject(resultResponseEntity.getBody(),ProxyIpAndPortModel.class);
        System.out.println(proxyIpAndPortModel.getProxy());
        return proxyIpAndPortModel.getProxy();
    }


    public  void removeUnableProxy(String ipAndPort){
        redisTemplate.opsForHash().delete("use_proxy", ipAndPort);
//        直接从数据从删除
    }

}
