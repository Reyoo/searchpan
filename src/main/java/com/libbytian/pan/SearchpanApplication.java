package com.libbytian.pan;


import com.libbytian.pan.proxy.service.GetProxyService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class SearchpanApplication {


    public static void main(String[] args) {
        SpringApplication.run(SearchpanApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }


//	@Bean
//	public RestTemplate restTemplate() throws Exception {
//
//		final String proxyUrl = "42.54.159.22";
//		final int port = 4235;
//
//		HttpHost myProxy = new HttpHost(proxyUrl, port);
//		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
//
//		clientBuilder.setProxy(myProxy).disableCookieManagement();
//
//		HttpClient httpClient = clientBuilder.build();
//		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		factory.setHttpClient(httpClient);
//
//		return new RestTemplate(factory);
//	}

}
