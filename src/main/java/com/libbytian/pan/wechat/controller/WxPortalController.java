package com.libbytian.pan.wechat.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.libbytian.pan.system.common.TemplateDetailsGetKeywordComponent;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.wechat.constant.TemplateKeywordConstant;
import com.libbytian.pan.wechat.service.KeyWordSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sun7127
 * @description: 微信请求接口
 */
@RequiredArgsConstructor
@RestController
@Log4j2
@SentinelResource("wechat")
@RequestMapping("/wechat/portal")

public class WxPortalController {

    private final WxMpService wxService;
    private final WxMpMessageRouter messageRouter;

    //首次关注 需要开发编写新接口
//    private final SubscribeHandler subscribeHandler;

    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final ISystemUserService iSystemUserService;
    private final KeyWordSettingService keyWordSettingService;
    private final RedisTemplate redisTemplate;
    private final ISystemUserSearchMovieService iSystemUserSearchMovieService;
    private final TemplateDetailsGetKeywordComponent templateDetailsGetKeywordComponent;
    private final ISystemKeywordService systemKeywordService;



    final Base64.Decoder decoder = Base64.getDecoder();


    /**
     * 与微信做认证通信 通过认证后调用其他接口
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr   c3VucWklM0ExMjM0NTY3OA==
     * @return
     */


    @RequestMapping(path = "/{verification}/{appId}", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String authGet(
            @PathVariable String verification,
            @PathVariable String appId,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            return "非法请求";
        }

        try {
            String username = new String(decoder.decode(verification), "UTF-8");

            SystemUserModel systemUserModel = new SystemUserModel();
            systemUserModel.setUsername(username);
            iSystemUserService.checkUserStatus(systemUserModel);
            if (iSystemUserService.getUser(systemUserModel) == null) {
                return "无此接口认证权限，请联系管理员！";
            }

            //正则校验appid
            String regular = "^wx(?=.*\\d)(?=.*[a-z])[\\da-z]{16}$";
            Pattern p = Pattern.compile(regular);
            if (!p.matcher(appId).matches()){
                return "appid格式不对,请前往公众号获取";
            }

            //判断用户账号到期时间
//            SystemUserModel userModel =iSystemUserService.getUser(systemUserModel);
//            if ( LocalDateTime.now().isAfter(userModel.getActTime())){
//                Thread.sleep(10000);
//            }


        } catch (Exception e) {
            return e.getMessage();
        }
        /**
         * 如果限制appid 则为私有
         */
//        if (!this.wxService.switchover(appId)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
//        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";

    }


    @RequestMapping(path = "/{verification}/{appId}", method = RequestMethod.POST, produces = "application/xml; charset=UTF-8")
    public String post(
            @PathVariable String verification,
            @PathVariable String appId,
            @RequestBody String requestBody,
            @RequestParam(value = "openid" ,required = false) String openid,
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "encrypt_type", required = false) String encType,
            @RequestParam(value = "msg_signature", required = false) String msgSignature)
            throws Exception {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);


        //解析传入的username,拿到user,查询对应模板
        String username = new String(decoder.decode(verification), "UTF-8");

        System.out.println("===========================================appId： "+appId+"============================================");


        SystemUserModel systemUserModel = new SystemUserModel();
        systemUserModel.setUsername(username);

        //判断用户账号到期时间
        SystemUserModel userModel =iSystemUserService.getUser(systemUserModel);

        if ( LocalDateTime.now().isAfter(userModel.getActTime())){
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("该公众号提供服务已过期");
            stringBuffer.append("\r\n");
            stringBuffer.append("\r\n");
            stringBuffer.append("关注公众号『影子的胡言乱语』");
            stringBuffer.append("\r\n");
            stringBuffer.append("\r\n");
            stringBuffer.append("可继续使用");

            outMessage = WxMpXmlOutTextMessage.TEXT()
                    .toUser(inMessage.getFromUser())
                    .fromUser(inMessage.getToUser())
                    .content(stringBuffer.toString()).build();

            return outMessage.toXml();
        }



        //获取用调用接口时间
//        systemUserModel.setCallTime(LocalDateTime.now());
//        iSystemUserService.updateUser(systemUserModel);

        String out = null;
        try {

            if (!wxService.checkSignature(timestamp, nonce, signature)) {
                throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
            }

            if (encType == null) {
                // 明文传输的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);


                //方法提前
                StringBuffer stringBuffer = new StringBuffer();
                WxMpXmlOutMessage outMessage = this.route(inMessage);

                if (outMessage == null) {
                    return "";
                }


                System.out.println(inMessage.getContent());
//                String searchWord = inMessage.getContent();
                String searchName = inMessage.getContent();

                //首次关注
                if (searchName== null){
                    SystemTemDetailsModel firstLike = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.First_Like);
                    if (firstLike.getEnableFlag()){
                        stringBuffer.append(firstLike.getKeywordToValue());
                    }else {
                        Thread.sleep(5000);
                    }

                    outMessage = WxMpXmlOutTextMessage.TEXT()
                            .toUser(inMessage.getFromUser())
                            .fromUser(inMessage.getToUser())
                            .content(stringBuffer.toString()).build();

                    out = outMessage.toXml();

                    return out;
                }


//                searchWord = inMessage.getContent().trim();


//                String searchName = null;
//                if(searchWord.contains(" ")){
//                    int idx = searchWord.lastIndexOf(" ");
//                     searchName = searchWord.substring(idx + 1);
//                }else{
//                    searchName = searchWord;
//                }

                SystemKeywordModel systemKeywordModel = systemKeywordService.getKeywordByUser(systemUserModel.getUsername());
                String key = systemKeywordModel.getFansKey();
                String splitName = null;
                if (searchName.startsWith(key)){
                    splitName = searchName.split(key)[1].trim();

                }else {
                    splitName=searchName;
                }




                /**
                 * 统计用户查询记录
                 */
//                iSystemUserSearchMovieService.userSearchMovieCountInFindfish(searchName);

                //从Redis中取出所有key,判断是传入内容是否为敏感词
                if (redisTemplate.boundHashOps("SensitiveWord").keys().contains(splitName)){
                    return "";
                }

                /**
                 * 响应内容
                 * 关键字 头部广告 headModel.getKeywordToValue()
                 * 关键字 底部广告 lastModel.getKeywordToValue()
                 */

                SystemTemDetailsModel headModel = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.TOP_ADVS);
                SystemTemDetailsModel lastModel = templateDetailsGetKeywordComponent.getUserKeywordDetail(systemUserModel, TemplateKeywordConstant.TAIL_ADVS);



                if (headModel.getEnableFlag()) {
                    stringBuffer.append(headModel.getKeywordToValue());
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                }

//                String urlSearchName = URLEncoder.encode(splitName,"UTF-8");
                String dealName = splitName.replaceAll(" ","+");

                stringBuffer.append("<a href =\"http://findfish.top/#/mobileView?searchname=");
                stringBuffer.append(dealName);
                stringBuffer.append("&verification=");
                stringBuffer.append(verification);
                stringBuffer.append("&type=mobile");
                stringBuffer.append("\">[");
                stringBuffer.append(splitName);
                stringBuffer.append("]关键词已获取，点击查看查询结果</a>");
                if (lastModel.getEnableFlag()) {
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                    stringBuffer.append(lastModel.getKeywordToValue());
                }

                System.out.println(stringBuffer);

//                stringBuffer = keyWordSettingService.getTemplateKeyWord(systemUserModel, searchName, stringBuffer, searchWord);
                stringBuffer = keyWordSettingService.getTemplateKeyWord(systemUserModel, splitName, searchName,stringBuffer,systemKeywordModel);

                outMessage = WxMpXmlOutTextMessage.TEXT()
                        .toUser(inMessage.getFromUser())
                        .fromUser(inMessage.getToUser())
                        .content(stringBuffer.toString()).build();

                out = outMessage.toXml();


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
        } catch (Exception e) {
            e.printStackTrace();
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
