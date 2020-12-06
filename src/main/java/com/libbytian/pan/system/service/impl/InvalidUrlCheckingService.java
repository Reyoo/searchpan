package com.libbytian.pan.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.service.impl
 * @ClassName: InvalidUrlCheckingService
 * @Author: sun71
 * @Description: 失效链接检测业务类
 * @Date: 2020/12/5 21:17
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvalidUrlCheckingService {




    public boolean checkUrlMethod(String url) throws Exception{

        //从URL加载HTML
        Document document = Jsoup.connect(url).get();
        String title = document.title();
        //获取html中的标题
        System.out.println("title :"+title);
        if("百度网盘-链接不存在".equals(title)||"页面不存在".equals(title)){
            return true;
        }
        return false;
    }

}
