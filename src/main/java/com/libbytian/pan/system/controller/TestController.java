package com.libbytian.pan.system.controller;


import com.libbytian.pan.system.common.AjaxResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试类接口
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/test")
public class TestController {


    private final RedisTemplate redisTemplate;

    @RequestMapping(value = "/getredis", method = RequestMethod.GET)
    public AjaxResult getRedisExprie() {

        try {
            Long expire = redisTemplate.boundHashOps("aidianying").getExpire();
            System.out.println("redis有效时间：" + expire + "S");
            return AjaxResult.success(expire);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }
    }
