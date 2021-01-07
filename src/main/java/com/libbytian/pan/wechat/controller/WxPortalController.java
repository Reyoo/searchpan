package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;

import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.wechat.constant.TemplateKeyword;
import com.libbytian.pan.wechat.handler.SubscribeHandler;
import com.libbytian.pan.wechat.service.AsyncSearchCachedServiceImpl;
import com.libbytian.pan.wechat.service.KeyWordSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;

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

    private final SubscribeHandler subscribeHandler;

    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final ISystemUserService iSystemUserService;
    private final AsyncSearchCachedServiceImpl asyncSearchCachedService;

    private final ISystemKeywordService systemKeywordService;

    private final KeyWordSettingService keyWordSettingService;


    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();


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
//    @Async
    public String post(
            @PathVariable String verification,
            @PathVariable String appId,
            @RequestBody String requestBody,
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "openid") String openid,
            @RequestParam(value = "encrypt_type", required = false) String encType,
            @RequestParam(value = "msg_signature", required = false) String msgSignature)
            throws Exception {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

//        if (!this.wxService.switchover(appId)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
//        }


        //解析传入的username,拿到user,查询对应模板
        String username = new String(decoder.decode(verification), "UTF-8");

        /**
         * 获取用户名绑定的模板
         */

        SystemUserModel systemUserModel = new SystemUserModel();
        systemUserModel.setUsername(username);
        systemUserModel.setCallTime(LocalDateTime.now());

        //获取用调用接口时间
        iSystemUserService.updateUser(systemUserModel);

        SystemTemDetailsModel headModel = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.TOP_ADVS);
        SystemTemDetailsModel lastModel = iSystemTemDetailsService.getUserKeywordDetail(username, TemplateKeyword.TAIL_ADVS);


        String out = null;
        try {

            if (!wxService.checkSignature(timestamp, nonce, signature)) {
                throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
            }

            if (encType == null) {
                // 明文传输的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);

                System.out.println( wxService.getUserService());
                System.out.println( inMessage.getAllFieldsMap());

                String searchWord = inMessage.getContent().trim();
                List<String> crawlerNames = new ArrayList<>();
                crawlerNames.add("aidianying");
                crawlerNames.add("unreadmovie");
                crawlerNames.add("sumsu");

                asyncSearchCachedService.searchAsyncWord(crawlerNames, searchWord);
                WxMpXmlOutMessage outMessage = this.route(inMessage);


                if (outMessage == null) {
                    return "";
                }
                StringBuffer stringBuffer = new StringBuffer();
                // 准备数据并解析。
                byte[] bytes = requestBody.getBytes("UTF-8");
                //1.创建Reader对象
                SAXReader reader = new SAXReader();
                //2.加载xml
                Document document = reader.read(new ByteArrayInputStream(bytes));
                //3.获取根节点
                Element rootElement = document.getRootElement();
                Iterator iterator = rootElement.elementIterator();
                String searchContent = "";
                String searchName = "";


                while (iterator.hasNext()) {
                    Element stu = (Element) iterator.next();
                    if (stu.getName().equals("Content")) {
                        List<Node> attributes = stu.content();
                        searchContent = attributes.get(0).getText();
                        //传入秘钥+" "+片名，然后截取
                        int idx = searchContent.lastIndexOf(" ");
                        searchName = searchContent.substring(idx + 1);
                        break;

                    }
                }

                /**
                 * 响应内容
                 * 关键字 头部广告 headModel.getKeywordToValue()
                 * 关键字 底部广告 lastModel.getKeywordToValue()
                 */

                log.error(headModel.getEnableFlag());
                System.out.println(headModel.getEnableFlag());

                if (headModel.getEnableFlag()) {
                    stringBuffer.append(headModel.getKeywordToValue());
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                }

                stringBuffer.append("<a href =\"http://findfish.top/#/mobileView?searchname=");
                stringBuffer.append(searchName);
                stringBuffer.append("&verification=");
                stringBuffer.append(verification);
                stringBuffer.append("&type=mobile");
                stringBuffer.append("\">[");
                stringBuffer.append(searchName);
                stringBuffer.append("]关键词已获取，点击查看查询结果</a>");
                if (lastModel.getEnableFlag()) {
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                    stringBuffer.append(lastModel.getKeywordToValue());
                }

               stringBuffer =  keyWordSettingService.Setting(username,searchName ,stringBuffer ,searchContent);

                Thread.sleep(1500);
                outMessage = WxMpXmlOutTextMessage.TEXT()
                        .toUser(inMessage.getFromUser())
                        .fromUser(inMessage.getToUser())
                        .content(stringBuffer.toString()).build();

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
