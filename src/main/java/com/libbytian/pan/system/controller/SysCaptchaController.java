package com.libbytian.pan.system.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.lang.UUID;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;

import com.libbytian.pan.system.common.AjaxResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码（支持算术形式）
 * @author SunQi
 */

@RestController
@RequestMapping("/captcha")
@Slf4j
public class SysCaptchaController  {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 验证码生成
     */
    @GetMapping(value = "/captchaImage")
    public AjaxResult getKaptchaImage(HttpServletRequest request, HttpServletResponse response) {

        log.info("begin captcha/captchaImage ");
        LocalDateTime now = LocalDateTime.now();
        ServletOutputStream out = null;
        try {
            ArrayList typeList = new ArrayList();
            typeList.add("math");
            typeList.add("char");

//            int index = (int) (Math.random() * typeList.size());
//            String type= (String)typeList.get(index);
            String type = "char";
            String capStr;
            String code = null;
            BufferedImage bufferedImage = null;
            if ("math".equals(type)) {
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf('@'));
                code = capText.substring(capText.lastIndexOf('@') + 1);
                bufferedImage = captchaProducerMath.createImage(capStr);
            } else if ("char".equals(type)) {
                capStr = code = captchaProducer.createText();
                bufferedImage = captchaProducer.createImage(capStr);
            }

            String uuid = UUID.fastUUID().toString();
            log.info("验证码-->" + code);
            redisTemplate.opsForValue().set(uuid,code,10, TimeUnit.MINUTES);


            // 转换流信息写出
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();
            try
            {
                ImageIO.write(bufferedImage, "jpg", os);
            }
            catch (IOException e)
            {
                return AjaxResult.error(e.getMessage());
            }

            AjaxResult ajax = AjaxResult.success();
            ajax.put("captcha", uuid);
            ajax.put("img", Base64.encode(os.toByteArray()));
            LocalDateTime end = LocalDateTime.now();
            System.out.println("以毫秒计的时间差：" + Duration.between(now, end).toMillis());
            return ajax;


        } catch (Exception e) {
            log.error("验证码生成异常!", e);
        }
        return AjaxResult.error();
    }
}