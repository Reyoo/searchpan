package com.libbytian.pan.wechat.config;


import cn.hutool.core.collection.CollectionUtil;
import com.libbytian.pan.system.model.SystemWxUserConfigModel;
import com.libbytian.pan.system.service.ISystemWxUserConfigService;
import com.libbytian.pan.wechat.handler.*;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;


/**
 * wechat mp configuration
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@AllArgsConstructor
@Configuration
public class WxMpConfiguration {
    private final LogHandler logHandler;
    private final MsgHandler msgHandler;
    private final UnsubscribeHandler unsubscribeHandler;
    private final SubscribeHandler subscribeHandler;
    //扫码handler  勿删 后期迭代可能要用
//    private final ScanHandler scanHandler;
    //客服handler  勿删 后期迭代可能要用
//    private final KfSessionHandler kfSessionHandler;
    //门店审核事件handler  勿删 后期迭代可能要用
//    private final StoreCheckNotifyHandler storeCheckNotifyHandler;
    //定位handler  勿删 后期迭代可能要用
//    private final LocationHandler locationHandler;
//    private final NullHandler nullHandler;
//    private final MenuHandler menuHandler;
    /**
     * 动态获取用户appid 等信息
     */
    @Autowired
    ISystemWxUserConfigService systemWxUserConfigService;


    @Bean
    public WxMpService wxMpService() {
//       从数据库里加载配置
        List<SystemWxUserConfigModel> configModelList = systemWxUserConfigService.list();
        if (CollectionUtil.isEmpty(configModelList)) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }
        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configModelList
                .stream().map(a -> {
                    WxMpDefaultConfigImpl configStorage;
                    configStorage = new WxMpDefaultConfigImpl();
                    configStorage.setAppId(a.getWxAppId());
                    configStorage.setSecret(a.getWxSecret());
                    configStorage.setToken(a.getWxToken());
                    configStorage.setAesKey(a.getWxAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));

        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
//        newRouter.rule().async(false).msgType(EVENT).event(KF_CREATE_SESSION)
//                .handler(this.kfSessionHandler).end();
//        newRouter.rule().async(false).msgType(EVENT).event(KF_CLOSE_SESSION)
//                .handler(this.kfSessionHandler).end();
//        newRouter.rule().async(false).msgType(EVENT).event(KF_SWITCH_SESSION)
//                .handler(this.kfSessionHandler).end();

        // 门店审核事件
//        newRouter.rule().async(false).msgType(EVENT).event(POI_CHECK_NOTIFY).handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
//        newRouter.rule().async(false).msgType(EVENT).event(EventType.CLICK).handler(this.menuHandler).end();

        // 点击菜单连接事件
//        newRouter.rule().async(false).msgType(EVENT).event(EventType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.subscribeHandler).end();

        // 取消关注事件
//        newRouter.rule().async(false).msgType(EVENT).event(UNSUBSCRIBE).handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
//        newRouter.rule().async(false).msgType(EVENT).event(EventType.LOCATION).handler(this.locationHandler).end();

        // 接收地理位置消息
//        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION).handler(this.locationHandler).end();

        // 扫码事件
//        newRouter.rule().async(false).msgType(EVENT).event(EventType.SCAN).handler(this.scanHandler).end();

        // 默认
        newRouter.rule().async(false).handler(this.msgHandler).end();
        return newRouter;
    }


}
