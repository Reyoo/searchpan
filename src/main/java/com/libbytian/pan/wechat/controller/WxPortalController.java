package com.libbytian.pan.wechat.controller;

import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemTemplateService;

import com.libbytian.pan.system.service.ISystemUserService;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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

    private final ISystemTemplateService systemTemplateService;

    private final ISystemTemDetailsService iSystemTemDetailsService;

    private final ISystemUserService iSystemUserService;

    private final RedisTemplate redisTemplate;

    private final AsyncSearchCachedServiceImpl asyncSearchCachedService;


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


        /**
         * 获取用户名绑定的模板
         */
        SystemUserModel systemUserModel = new SystemUserModel();
        systemUserModel.setUsername(username);
        //获取用户模板
        List<SystemTemplateModel> systemTemplateModels = systemTemplateService.listTemplatelByUser(systemUserModel);
        //获取启用状态的模板 (状态为True)  前端需要控制只能有一个 启用状态下的模板。
        List<SystemTemplateModel> systemTemplateModelListstatusOn = systemTemplateModels.stream().filter(systemTemplateModel -> systemTemplateModel.getTemplatestatus().equals(Boolean.TRUE)).collect(Collectors.toList());
        //通过模板ID，查询对应的模板详情，取出关键词，头部广告，底部广告


        SystemTemplateModel templateModel = new SystemTemplateModel();

        templateModel = systemTemplateModelListstatusOn.get(0);
        List<SystemTemDetailsModel> systemdetails = iSystemTemDetailsService.getTemDetails(templateModel);
        //头部广告
//        SystemTemDetailsModel headModel = new SystemTemDetailsModel();
//        //底部广告
//        SystemTemDetailsModel lastModel = new SystemTemDetailsModel();
//        //隐藏资源
//        SystemTemDetailsModel hidden = new SystemTemDetailsModel();
//        //隐藏内容
//        SystemTemDetailsModel hiddenDetais = new SystemTemDetailsModel();
//        //维护开始时间
//        SystemTemDetailsModel sleepStart = new SystemTemDetailsModel();
//        //维护结束时间
//        SystemTemDetailsModel sleepEnd = new SystemTemDetailsModel();
//        //维护内容
//        SystemTemDetailsModel sleepDetais = new SystemTemDetailsModel();
//        //秘钥
//        SystemTemDetailsModel secretKey = new SystemTemDetailsModel();
//        //秘钥回复
//        SystemTemDetailsModel secretValue = new SystemTemDetailsModel();


//        for (SystemTemDetailsModel model : systemdetails) {
//            //头部广告
//            if (TemplateKeyword.TOP_ADVS.equals(model.getKeyword())){
//                headModel.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.TAIL_ADVS.equals(model.getKeyword())){
//                lastModel.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.HIDE_RESOURCES.equals(model.getKeyword())){
//                hidden.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.HIDE_REPLY.equals(model.getKeyword())){
//                hiddenDetais.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.START_MAINTAIN.equals(model.getKeyword())){
//                sleepStart.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.END_MAINTAIN.equals(model.getKeyword())){
//                sleepEnd.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.MAINTENANCE_CONTENT.equals(model.getKeyword())){
//                sleepDetais.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.SECRET_KEY_CONTENT.equals(model.getKeyword())){
//                secretKey.setKeywordToValue(model.getKeywordToValue());
//            }
//            if (TemplateKeyword.SECRET_KEY_REPLY.equals(model.getKeyword())){
//                secretValue.setKeywordToValue(model.getKeywordToValue());
//            }
//        }

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
                asyncSearchCachedService.searchAsyncWord(searchWord);

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

//            String searchName = "";
                //未使用秘钥
//            while (iterator.hasNext()) {
//                Element stu = (Element) iterator.next();
//                if (stu.getName().equals("Content")) {
//                    List<Node> attributes = stu.content();
//                    searchName = attributes.get(0).getText();
//
//                }
//            }

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

//            从数据库中获取 是否有头信息 并且头部信息不为空和空串
//            if (headModel != null){
//                stringBuffer.append(headModel.getKeywordToValue());
//            }


                stringBuffer.append("\r\n");
                stringBuffer.append("\r\n");
                stringBuffer.append("<a href =\"http://findfish.top/#/mobileView?searchname=");
                stringBuffer.append(searchName);

                stringBuffer.append("&verification=");
                stringBuffer.append(verification);
                stringBuffer.append("&type=mobile");
                stringBuffer.append("\">[");
                stringBuffer.append(searchName);
                stringBuffer.append("]关键词已获取，点击查看是否找到该内容</a>");
                stringBuffer.append("\r\n");
                stringBuffer.append("\r\n");

                //            从数据库中获取 是否有头信息 并且头部信息不为空和空串
//            if (lastModel != null){
//                stringBuffer.append(lastModel.getKeywordToValue());
//            }

//
//            /**
//             * 关键词 隐藏判断
//             */
//            if (hidden != null) {
//                //隐藏的片名以空格分隔，获取隐藏片名的数组
//                String[] str = hidden.getKeywordToValue().split(" ");
//
//                for (String s : str) {
//                    //判断传入的 片名 是否在隐藏资源中
//                    if (s.equals(searchAsyncWord)) {
//                        stringBuffer.setLength(0);
//                        stringBuffer.append(hiddenDetais.getKeywordToValue());
//                        break;
//
//                    }
//                }
//            }


                /**
                 * 关键词 维护判断
                 */
                //维护时间
//            String start = sleepStart.getKeywordToValue();
//            String end = sleepEnd.getKeywordToValue();

//            if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
//
//                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//                Date dateStart = df.parse(start);
//                Date dateEnd = df.parse(end);
//                //当前时间
//                Date now = df.parse(df.format(new Date()));
//
//                Calendar nowTime = Calendar.getInstance();
//                nowTime.setTime(now);
//
//                Calendar beginTime = Calendar.getInstance();
//                beginTime.setTime(dateStart);
//
//                Calendar endTime = Calendar.getInstance();
//                endTime.setTime(dateEnd);
//
//                //如果开始时间 > 结束时间，跨天 给结束时间加一天
//                if (beginTime.after(endTime)) {
//                    endTime.add(Calendar.DAY_OF_MONTH, 1);
//                }
//
//                //如果当前时间在维护期内，返回维护内容
//                if (nowTime.after(beginTime) && nowTime.before(endTime)) {
//                    stringBuffer.setLength(0);
//                    stringBuffer.append(sleepDetais.getKeywordToValue());
//                }
//
//            }

                /**
                 * HuangS  11.15
                 * 关键词 判断秘钥
                 * 最简单实现
                 * 判断传入的关键词中是否包含秘钥
                 */
//             if (StringUtils.isNotBlank(secretKey.getKeywordToValue()) && !searchContent.contains(secretKey.getKeywordToValue())){
//
//                    stringBuffer.setLength(0);
//                    if (StringUtils.isNotBlank(secretValue.getKeywordToValue())){
//                        stringBuffer.append(secretValue.getKeywordToValue());
//                    }else {
//                        stringBuffer.append(" ");
//                    }
//            }


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
