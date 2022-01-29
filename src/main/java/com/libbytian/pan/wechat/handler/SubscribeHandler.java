package com.libbytian.pan.wechat.handler;


import com.libbytian.pan.system.common.TemplateDetailsGetKeywordComponent;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.wechat.builder.TextBuilder;
import com.libbytian.pan.wechat.constant.TemplateKeywordConstant;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
@RequiredArgsConstructor
public class SubscribeHandler extends AbstractHandler {

    private final TemplateDetailsGetKeywordComponent templateDetailsGetKeywordComponent;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
        // 获取微信用户基本信息
        try {
//            WxMpUser userWxInfo = weixinService.getUserService()
//                    .userInfo(wxMessage.getFromUser(), null);
//            if (userWxInfo != null) {
//                // TODO 可以添加关注用户到本地数据库
//            }
            WxMpXmlOutMessage responseResult = this.handleSpecial(wxMessage);
            if (responseResult != null) {
                return responseResult;
            }
            SystemUserModel userinfo = (SystemUserModel) context.get("userinfo");
            StringBuffer stringBuffer = new StringBuffer();
            SystemTemDetailsModel firstLike = templateDetailsGetKeywordComponent.getUserKeywordDetail(userinfo, TemplateKeywordConstant.First_Like);
            if (firstLike.getEnableFlag()) {
                stringBuffer.append(firstLike.getKeywordToValue());
            }
            return new TextBuilder().build(stringBuffer.toString(), wxMessage, weixinService);
        } catch (WxErrorException e) {
            e.printStackTrace();
            if (e.getError().getErrorCode() == 48001) {
                this.logger.info("该公众号没有获取用户信息权限！");
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage)
            throws Exception {
//        /**
//         *@Author zcm
//         *@Email zcm6092@fjdaze.com
//         *@Description 自动回复 一条消息
//         *@Date 14:21 2020/4/24
//         */
//        //第一句，设置服务器端编码
//        response.setCharacterEncoding("utf-8");
//        //第二句，设置浏览器端解码
//        response.setContentType("text/xml;charset=utf-8");
//        String str = "你好呀！欢迎来到我的微信公众号。\n\n";
//        //创建消息文本
//        WxMpXmlOutTextMessage text = WxMpXmlOutTextMessage.TEXT().toUser(fromUser).fromUser(toUser).content(str).build();
//        String xml = text.toXml();
//        PrintWriter out = null;
//        try {
//            out = response.getWriter();
//            out.print(xml);
//        } catch (IOException e) {
//            out.close();
//            out = null;
//            e.printStackTrace();
//        }
//        out.close();
//        out = null;
//
//        //第二条信息  使用客服模式推送
//        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
//        JSONObject object = new JSONObject();
//        object.put("touser", fromUser);
//        object.put("msgtype", "image");
//        JSONObject object1 = new JSONObject();
//        object1.put("media_id", "WNtGBTNVULve98fkEJWUnDIMZZGlWEpONV2NK50un_U_12211");
//        object.put("image", object1);
//        System.out.println("JSONObject:" + object);
//        HttpUtil.post(url, object.toJSONString());

        return null;
    }

}
