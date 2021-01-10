package com.libbytian.pan;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class SearchpanApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchpanApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(600000);
		factory.setReadTimeout(20000);
		return new RestTemplate(factory);
	}

}
