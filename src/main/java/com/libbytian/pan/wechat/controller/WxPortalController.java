package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemTemplateService;

import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.wechat.constant.TemplateKeyword;
import com.libbytian.pan.wechat.service.AsyncSearchCachedServiceImpl;
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
import org.omg.CosNaming.BindingIterator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.internal.tools.Kinit;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    private final ISystemTemDetailsService iSystemTemDetailsService;
    private final ISystemUserService iSystemUserService;
    private final AsyncSearchCachedServiceImpl asyncSearchCachedService;

    private final ISystemKeywordService systemKeywordService;


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

    @RequestMapping(path = "/{verification}", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String authGet(
            @PathVariable String verification,
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

            SystemKeywordModel systemKeywordModel = systemKeywordService.getKeywordByUser(username);

            String userStart = systemKeywordModel.getStartTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

            Date userStartdate = simpleDateFormat.parse(userStart);

            String userEnd = systemKeywordModel.getEndTime();


            Date userEnddate = simpleDateFormat.parse(userEnd);

            ZoneId shanghaiZoneId = ZoneId.of("Asia/Shanghai");
            ZonedDateTime shanghaiZonedDateTime = ZonedDateTime.now(shanghaiZoneId);

            int nowHour = shanghaiZonedDateTime.getHour();
            int nowMinutes = shanghaiZonedDateTime.getMinute();

            Date nowDate = simpleDateFormat.parse(String.valueOf(nowHour) + ":" + String.valueOf(nowMinutes));


//        Date1.after(Date2),当Date1大于Date2时，返回TRUE，当小于等于时，返回false；
//        Date1.before(Date2)，当Date1小于Date2时，返回TRUE，当大于等于时，返回false；
            if (!"00:00".equals(userStart) && !"23:59".equals(userEnd)) {
                //如果当前时间不小于用户其实时间  那么是允许放行的

                if (nowDate.compareTo(userStartdate) < 0) {
                    return "当前时间 不在用户限定时间内";
                }

                if (nowDate.compareTo(userEnddate) >0) {
                    return "当前时间 不在用户限定时间内";
                }

            }

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
//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";

    }


    @RequestMapping(path = "/{verification}", method = RequestMethod.POST, produces = "application/xml; charset=UTF-8")
    public String post(
            @PathVariable String verification,
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

//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }


        //解析传入的username,拿到user,查询对应模板
        String username = new String(decoder.decode(verification), "UTF-8");


        //根据用户关键字时间、判断是否是可返回接口状态


        //启用状态

        //如果启用时间是00:00 ~ 23:59  则全时段放行
        //否则 只跑 时间段范围内的逻辑 。例如, 10:00 ~ 23:59 则 不允许 00:00 ~ 09:59  访问接口


        /**
         * 获取用户名绑定的模板
         */
        SystemUserModel systemUserModel = new SystemUserModel();
        systemUserModel.setUsername(username);

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

//            异步获取一次消息
                String searchWord = inMessage.getContent().trim();
                List<String> crawlerNames = new ArrayList<>();
                crawlerNames.add("ALL");

                asyncSearchCachedService.searchAsyncWord(crawlerNames,    "'"+searchWord+ "'");
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

                    }
                }

                /**
                 * 响应内容
                 * 关键字 头部广告 headModel.getKeywordToValue()
                 * 关键字 底部广告 lastModel.getKeywordToValue()
                 */

                if (headModel != null) {
                    stringBuffer.append(headModel.getKeywordToValue());
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                }

//                stringBuffer.append("<a href =\"http:///#/mobileView?searchname=");
                stringBuffer.append("<a href =\"http://findfish.top/#/mobileView?searchname=");
                stringBuffer.append(searchName);

                stringBuffer.append("&verification=");
                stringBuffer.append(verification);
                stringBuffer.append("&type=mobile");
                stringBuffer.append("\">[");
                stringBuffer.append(searchName);
                stringBuffer.append("]关键词已获取，点击查看是否找到该内容</a>");

                if (lastModel != null) {
                    stringBuffer.append("\r\n");
                    stringBuffer.append("\r\n");
                    stringBuffer.append(lastModel.getKeywordToValue());
                }

//

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

    /**
     * 注册用户获取公众号配置URL
     * 目前使用穿透测试 拼接URL 第一段 随时需换
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "geturl", method = RequestMethod.GET)
    public String getURL(@RequestParam String username) {

        String encodeusername = encoder.encodeToString(username.getBytes());
        return "http://6itty7.natappfree.cc" + "/wechat/portal/" + encodeusername;

    }


    /**
     * 公众号秘钥功能 暂未实现
     *
     * @return
     */
    @RequestMapping(value = "random", method = RequestMethod.GET)
    public int getRandom() {

        int number = (int) ((Math.random() * 9 + 1) * 100000);

        return number;

    }


}
