package com.libbytian.pan.proxy.service;

import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import java.util.concurrent.TimeUnit;

/**
 * @author: QiSun
 * @date: 2021-01-27
 * @Description:
 */
@Slf4j
@Component
public class PhantomJsProxyCallService {


    @Value("${phantomjs.deploy.linuxpath}")
    String deployLinuxPath;

    @Value("${phantomjs.deploy.winpath}")
    String deployWindowsPath;

    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        // 注：这里的system，系统指的是 JRE (runtime)system，不是指 OS
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }


    public  PhantomJSDriver create(String href,String proxyIpAndPort) {

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyIpAndPort);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        //ssl证书支持
        desiredCapabilities.setCapability("acceptSslCerts", true);
        //截屏支持，这里不需要
        desiredCapabilities.setCapability("takesScreenshot", false);
        //css搜索支持
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
                UserAgentUtil.randomUserAgent());
        desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, "Safari");
        desiredCapabilities.setCapability("platformName", "Android");
        //js支持
        desiredCapabilities.setJavascriptEnabled(true);

        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
        //驱动支持DesiredCapabilities
        //如果是windows系统
        if(isWindowsOS()){
            desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,deployWindowsPath);
        }else{
            desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,deployLinuxPath);
        }

        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
        //这里注意，把窗口的大小调整为最大，如果不设置可能会出现元素不可用的问题
        driver.manage().window().maximize();
        //获取csdn主页
        driver.get(href);
        // 超过8秒即为超时，会抛出Exception
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        return driver;

    }

}
