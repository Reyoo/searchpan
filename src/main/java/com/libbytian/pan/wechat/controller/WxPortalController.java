package com.libbytian.pan.wechat.controller;


import com.libbytian.pan.wechat.model.MovieNameAndUrlModel;
import com.libbytian.pan.wechat.service.NormalPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author sun7127
 * @description: 微信请求接口
 */
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/wechat/portal")

public class WxPortalController {

    private final WxMpService wxService;
    private final WxMpMessageRouter messageRouter;

    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();


     @Autowired
     NormalPageService normalPageService;
//
    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    /**
     * 与微信做认证通信 通过认证后调用其他接口
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     *
     * c3VucWklM0ExMjM0NTY3OA==
     * @return
     */

    @RequestMapping(path = "/{verification}",method = RequestMethod.GET , produces = "text/plain;charset=utf-8")
    public String authGet(
            @PathVariable String verification,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {

        }


        if(!"c3VucWklM0ExMjM0NTY3OA==".equals(verification)){
            throw new IllegalArgumentException("权限有误，请核实!");
        }
        /**
         * 加参数验证动态形成url
         */


        /**
         * 如果限制appid 则为私有
         */
//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }


        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";
    }


    @RequestMapping(path = "/{verification}" , method = RequestMethod.POST ,produces = "application/xml; charset=UTF-8")
    public String post(
            @PathVariable String verification,
                       @RequestBody String requestBody,
                       @RequestParam(value = "signature") String signature,
                       @RequestParam(value = "timestamp") String timestamp,
                       @RequestParam(value = "nonce") String nonce,
                       @RequestParam(value = "openid") String openid,
                       @RequestParam(value = "encrypt_type", required = false) String encType,
                       @RequestParam(value = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }
        String out = null;
        try {


        if(!"c3VucWklM0ExMjM0NTY3OA==".equals(verification)){
            throw new IllegalArgumentException("权限有误，请核实!");
        }

        //根据用户 verification 解析成用户名密码获取用户设置模板
        String userAndPasswd =  new String(decoder.decode(verification), "UTF-8");
        System.out.println(userAndPasswd);

        //从数据库中获取用户模板信息
        //从模板信息中获取用户模板详细

        //返回模板信息

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }


        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }


            List<MovieNameAndUrlModel> realMovieList = new ArrayList();
            List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+inMessage.getContent()).get("data");

            LocalTime begin = LocalTime.now();
            movieNameAndUrls.stream().forEach( movieNameAndUrl ->
                    realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
//            List<MovieNameAndUrlModel> movieNameAndUrls1 =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+inMessage.getContent()).get("data");
//            movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
//                    realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));
            LocalTime end = LocalTime.now();
            Duration duration = Duration.between(begin, end);
            System.out.println("Duration: " + duration);
            StringBuffer stringBuffer = new StringBuffer();
            realMovieList.stream().forEach(innerMovie -> {
                stringBuffer.append("电影名 :" )
                        .append(innerMovie.getMovieName())
                        .append("<a href=\\\"http://www.baidu.com/signin.html?openid=\" + openid + \"\\\">登录/注册</a>\"")
                        .append("\n")
                .append("百度网盘 : ")
                .append(innerMovie.getWangPanUrl())
                .append("\n")
                .append(innerMovie.getWangPanPassword())
                        .append("\n")
                        .append("----->分隔符<-----");
            } );

            outMessage = WxMpXmlOutTextMessage.TEXT()
                    .toUser(inMessage.getFromUser())
                    .fromUser(inMessage.getToUser())
                    .content(stringBuffer.toString()).build();




//            System.out.println("==============");
//            System.out.println(outMessage.toString());
//            System.out.println("==============");
            out = outMessage.toXml();
            System.out.println(out);
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }
        }catch (Exception e){
            return e.getMessage();
        }

        log.debug("\n组装回复信息：{}", out);
        return out;
    }



    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }

}
