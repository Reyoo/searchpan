package com.libbytian.pan.proxy.test;

import com.libbytian.pan.system.util.UserAgentUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: QiSun
 * @date: 2021-01-27
 * @Description:
 */
public class PhantomJsTest {

    public static void main(String[] args) throws InterruptedException {
//        System.setProperty("webdriver.chrome.driver", "F:\\git_workspaces\\crawler\\driver\\chromedriver.exe");



        String href = "http://www.lxxh7.com/?s=%e9%a3%8e%e5%a3%b0";
//        String href = "http://www.baidu.com";
        PhantomJSDriver driver = create(href);


        Document document = Jsoup.parse(driver.getPageSource());
        System.out.println(document.body());


    }


    static PhantomJSDriver create(String href) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        //ssl证书支持
        desiredCapabilities.setCapability("acceptSslCerts", true);
        //截屏支持，这里不需要
        desiredCapabilities.setCapability("takesScreenshot", false);
        //css搜索支持
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
                UserAgentUtil.randomUserAgent());
//        desiredCapabilities.setPlatform(new Platform();
        desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, "Safari");
        desiredCapabilities.setCapability("platformName", "Android");
        //js支持
        desiredCapabilities.setJavascriptEnabled(true);
        //驱动支持DesiredCapabilities
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "E:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
        //这里注意，把窗口的大小调整为最大，如果不设置可能会出现元素不可用的问题
        driver.manage().window().maximize();
        //获取csdn主页
        driver.get(href);

        // 超过8秒即为超时，会抛出Exception
        driver.manage().timeouts().pageLoadTimeout(8, TimeUnit.SECONDS);

        return driver;

    }

}
