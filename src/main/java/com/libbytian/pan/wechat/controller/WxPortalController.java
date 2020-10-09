package com.libbytian.pan.wechat.controller;


import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.wechat.service.NormalPageService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private final com.libbytian.pan.system.service.ISystemUserService ISystemUserService;


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



//
//        if(!"c3VucWklM0ExMjM0NTY3OA==".equals(verification)){
//            throw new IllegalArgumentException("weifu，请核实!");
//        }
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
                       @RequestParam(value = "msg_signature", required = false) String msgSignature)
                       throws Exception {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }



//        ---------------------------------------------------------------------------------

        //解析传入的username,拿到user,查询对应模板
        String username =  new String(decoder.decode(verification), "UTF-8");
        SystemUserModel user = ISystemUserService.findByUsername(username);
        List<SystemTemplateModel> systemTemplateModels =  ISystemUserService.findTemplateById(username);

        for(SystemTemplateModel systemTemplateModel :systemTemplateModels ){
            System.out.println("===========================");
            System.out.println(systemTemplateModel.getTemplatestatus());
            System.out.println("===========================");
        }






        List<SystemTemplateModel> systemTemplateModelListstatusOn = systemTemplateModels.stream().filter(systemTemplateModel -> systemTemplateModel.getTemplatestatus().equals(Boolean.TRUE)).collect(Collectors.toList());





        //通过模板ID，查询对应的模板详情，取出关键词，头部广告，底部广告
        List<SystemTemDetailsModel> systemdetails = systemTemplateService.findTemDetails(systemTemplateModelListstatusOn.get(0).getTemplateid());

        SystemTemDetailsModel headmodel = new SystemTemDetailsModel();
        SystemTemDetailsModel lastmodel = new SystemTemDetailsModel();

        for (SystemTemDetailsModel model : systemdetails) {
            if (model.getKeyword().equals("头部广告")){
                headmodel.setKeywordToValue(model.getKeywordToValue());
            }
            if (model.getKeyword().equals("底部广告")){
                lastmodel.setKeywordToValue(model.getKeywordToValue());
            }
        }

//      ---------------------------------------------------------------------------------

        String out = null;
        try {


//        if(!"c3VucWklM0ExMjM0NTY3OA==".equals(verification)){
//            throw new IllegalArgumentException("权限有误，请核实!");
//        }

            //根据用户 verification 解析成用户名密码获取用户设置模板
//        String userAndPasswd =  new String(decoder.decode(verification), "UTF-8");
//        System.out.println(userAndPasswd);

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


//            List<MovieNameAndUrlModel> realMovieList = new ArrayList();
//            List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+inMessage.getContent()).get("data");

                LocalTime begin = LocalTime.now();


//            movieNameAndUrls.stream().forEach( movieNameAndUrl ->
//                    realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
//            List<MovieNameAndUrlModel> movieNameAndUrls1 =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+inMessage.getContent()).get("data");
//            movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
//                    realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));
                LocalTime end = LocalTime.now();
                Duration duration = Duration.between(begin, end);
                System.out.println("Duration: " + duration);
                StringBuffer stringBuffer = new StringBuffer();


//            realMovieList.stream().forEach(innerMovie -> {
//                stringBuffer.append("电影名 :" )
//                        .append(innerMovie.getMovieName())
//                        .append("<a href=\\\"http://www.baidu.com/signin.html?openid=\" + openid + \"\\\">登录/注册</a>\"")
//
//                        .append("\n")
//                .append("百度网盘 : ")
//                .append(innerMovie.getWangPanUrl())
//                .append("\n")
//                .append(innerMovie.getWangPanPassword())
//                        .append("\n")
//                        .append("----->分隔符<-----");
//            } );


                // 准备数据并解析。
                byte[] bytes = requestBody.getBytes("UTF-8");
                //1.创建Reader对象
                SAXReader reader = new SAXReader();
                //2.加载xml
                Document document = reader.read(new ByteArrayInputStream(bytes));
                //3.获取根节点
                Element rootElement = document.getRootElement();
                Iterator iterator = rootElement.elementIterator();

                String searchName = "";

                while (iterator.hasNext()) {
                    Element stu = (Element) iterator.next();
                    if (stu.getName().equals("Content")) {
                        List<Node> attributes = stu.content();
                        searchName = attributes.get(0).getText();
                    }

                }




            /**
             * 响应内容
             * 关键字 头部广告 headmodel.getKeywordToValue()
             * 关键字 底部广告 lastmodel.getKeywordToValue()
             */
            stringBuffer.append(headmodel.getKeywordToValue());
            stringBuffer.append("\r\n");
            stringBuffer.append("\r\n");
            stringBuffer.append("<a href =\"https://search.douban.com/movie/subject_search?search_text=");
            stringBuffer.append(searchName);
            stringBuffer.append("\">[");
            stringBuffer.append(searchName);
            stringBuffer.append("]关键词已获取，点击查看是否找到该内容</a>");
            stringBuffer.append("\r\n");
            stringBuffer.append("\r\n");
            stringBuffer.append(lastmodel.getKeywordToValue());

                //        --------------------Redis----------------------------------

//                List<MovieNameAndUrlModel> realMovieList = new ArrayList();
//                //查询缓存中是否存在
//                boolean hasKey = redisUtils.exists(searchName);
//
//                if (hasKey) {
//                    //获取缓存
//                    Object object = redisUtils.get(searchName);
//
//                    List<MovieNameAndUrlModel>  list = JSONObject.parseArray((String) object,MovieNameAndUrlModel.class);
//
//                    log.info("从缓存获取的数据" + list);
//
//
//                } else {
//                    //从数据库中获取信息
//                    log.info("从爬虫中获取数据");
//
//                    List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+searchName).get("data");
//
//                    movieNameAndUrls.stream().forEach( movieNameAndUrl ->
//                            realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
//                    List<MovieNameAndUrlModel> movieNameAndUrls1 =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+searchName).get("data");
//                    movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
//                            realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));
//
//                    //数据插入缓存（set中的参数含义：key值，user对象，缓存存在时间10（long类型），时间单位）
//                    String list = JSON.toJSON(realMovieList).toString();
//
//                    redisUtils.set(searchName, list, 10L, TimeUnit.MINUTES);
//
//
//                    log.info("数据插入缓存" + realMovieList.toString());
//
//                    realMovieList.stream().forEach(innerMovie -> {
//                        stringBuffer.append("电影名 :" )
//                                .append(innerMovie.getMovieName())
//                                .append("\n")
//                                .append("百度网盘 : ")
//                                .append(innerMovie.getWangPanUrl())
//                                .append("\n")
//                                .append(innerMovie.getWangPanPassword())
//                                .append("\n")
//                                .append("----->分隔符<-----");
//                    } );
//
//                }

//        --------------------Redis----------------------------------

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

    /**
     * 注册用户获取公众号配置URL
     * 目前使用穿透测试 拼接URL 第一段 随时需换
     * @param username
     * @return
     */
    @RequestMapping(value = "geturl",method = RequestMethod.GET)
    public String getURL(@RequestParam String username){

        String encodeusername = encoder.encodeToString(username .getBytes());
        return "http://kwwaws.natappfree.cc"+"/wechat/portal/"+encodeusername;

    }



}
