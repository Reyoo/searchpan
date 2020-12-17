package com.libbytian.pan.crawler.controller;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.crawler.service.AsyncTask;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.impl.MovieNameAndUrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Random;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.crawler.controller
 * @ClassName: CrawlerWebInfoController
 * @Author: sun71
 * @Description: 获取未读影单信息
 * @Date: 2020/12/13 11:26
 * @Version: 1.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/initmovie")
@Slf4j
public class CrawlerWebInfoController {

    private final AsyncTask asyncTask;


    private final MovieNameAndUrlServiceImpl movieNameAndUrlService;
    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    /**
     * 调用电影PID 入库 触发接口类
     */
    @RequestMapping(value = "/getall/unread", method = RequestMethod.GET)
    public AjaxResult loopGetMoviePid() {
        try {


            //http://www.unreadmovie.com/?p=5011
            //循环调用 接口  p =1  到99999 存入库中
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(unreadUrl);
            stringBuffer.append("/?p=");
            String urlBase = stringBuffer.toString();
            asyncTask.getAllmovieInit(urlBase);
            return AjaxResult.success("表入库成功");
        } catch (Exception e) {
            return AjaxResult.error("表入库失败");
        }

    }



    /**
     * 调用电影PID 入库 触发接口类
     */
    @RequestMapping(value = "/getall/lxxh", method = RequestMethod.GET)
    public AjaxResult loopGet() {
        try {








//            http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(lxxhUrl);
            stringBuffer.append("/");
//            stringBuffer.append(maybeMonth);
            stringBuffer.append(12);


            String urlBase = stringBuffer.toString();
            asyncTask.getAllmovieInit(urlBase);
            return AjaxResult.success("表入库成功");
        } catch (Exception e) {
            return AjaxResult.error("表入库失败");
        }

    }

}
