package com.libbytian.pan.system.controller;

import cn.hutool.core.lang.UUID;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码（支持算术形式）
 * @author SunQi
 */

@Controller
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
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");

            String type = request.getParameter("type");
            type = "math";
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
            System.out.println(uuid);
            System.out.println(code);
            redisTemplate.opsForValue().set(uuid,code,1, TimeUnit.MINUTES);
            Cookie cookie = new Cookie("captcha",uuid);
            /*key写入cookie，验证时获取*/
            response.addCookie(cookie);
            out = response.getOutputStream();
            ImageIO.write(bufferedImage, "jpg", out);
            out.flush();

        } catch (Exception e) {
            log.error("验证码生成异常!", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("验证码生成异常!", e);
            }
        }
        return null;
    }
}