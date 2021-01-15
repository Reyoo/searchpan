package com.libbytian.pan.crawler.service.aidianying;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: AiDianyingService
 * @Author: sun71
 * @Description: 爱电影爬虫获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AiDianyingService {


    private final RedisTemplate redisTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final GetProxyService getProxyService;


    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIp, int proxyPort) {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();

        String userAgent = UserAgentUtil.randomUserAgent();

        Set<String> movieUrlInLxxh = new HashSet();
        log.info("-------------------------开始爬取爱电影 begin ----------------------------");
        try {
            String urlAiDianying = lxxhUrl + "/?s=" + searchMovieName;
            log.info(urlAiDianying);
            System.getProperties().setProperty("proxySet", "true");
            System.setProperty("http.proxyHost", proxyIp);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("http.maxRedirects", "1");
            URL url = new URL(urlAiDianying);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
//            connection.setRequestProperty("Host", "www.lxxh7.com");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Connection", "keep-alive");

            connection.setConnectTimeout(18000);
            connection.connect();

            String redirectUrl = connection.getHeaderField("Location");
            if(redirectUrl != null && !redirectUrl.isEmpty()) {
                urlAiDianying = redirectUrl;

                log.info("line 85 -> " + urlAiDianying);
            }

            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            StringBuffer stringBuffer = new StringBuffer();
            while (inputStream.read(bytes) >= 0) {
                stringBuffer.append(new String(bytes));
                System.out.println(stringBuffer.toString());
            }


            log.info("-------------------------爱电影 end ----------------------------");


            Document document = Jsoup.parse(stringBuffer.toString());
            //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
            Elements attr = document.getElementsByTag("h2").select("a");
            for (Element element : attr) {
                String jumpUrl = element.attr("href").trim();
                log.info(jumpUrl);
                if(!jumpUrl.contains(" http://www.lxxh7.com")){
                    continue;
                }
                movieUrlInLxxh.add(jumpUrl);
            }
            //直接获取百度网盘
            if (attr.size() <= 0) {
                movieUrlInLxxh.add(urlAiDianying);
            }
        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            log.error(e.getMessage());
            e.printStackTrace();
        }


        try {

            //说明搜索到了 url 电影路径
            if (movieUrlInLxxh.size() > 0) {
                for (String url : movieUrlInLxxh) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    MovieNameAndUrlModel movieNameAndUrlModel = getMovieLoopsAiDianying(url, userAgent, proxyIp, proxyPort);
                    movieNameAndUrlModelList.add(movieNameAndUrlModel);
                }
            }
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, "url_movie_aidianying");
            redisTemplate.opsForHash().put("aidianying", searchMovieName, movieNameAndUrlModelList);
            redisTemplate.expire(searchMovieName, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            e.printStackTrace();
            log.error("searchMovieName --> " + searchMovieName);
            log.error("AiDianyingService.saveOrFreshRealMovieUrl()  ->" + e.getMessage());
        }

    }


    /**
     * 新版本获取关键词搜索爬虫第一步
     *
     * @param searchMovieName
     * @return
     */
    public Set<String> getNormalUrlAidianying(String searchMovieName, String userAgent, String proxyIp, int proxyPort) {
        Set<String> aiDianYingNormalUrlSet = new HashSet();

        try {

            String urlAiDianying = lxxhUrl + "/?s=" + searchMovieName;
            System.setProperty("http.maxRedirects", "3");
            System.getProperties().setProperty("proxySet", "true");
            System.setProperty("http.proxyHost", proxyIp);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            URL url = new URL(urlAiDianying);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.21(0x17001522) NetType/WIFI Language/zh_CN");
            connection.setRequestProperty("Host", "www.lxxh7.com");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setConnectTimeout(18000);
            connection.setReadTimeout(30000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            StringBuffer stringBuffer = new StringBuffer();
            while (inputStream.read(bytes) >= 0) {
                stringBuffer.append(new String(bytes));
            }

            log.info("-------------------------爱电影 begin ----------------------------");
            log.info(stringBuffer.toString());
            log.info("-------------------------爱电影 end ----------------------------");


            Document document = Jsoup.parse(stringBuffer.toString());
            //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
            Elements attr = document.getElementsByTag("h2").select("a");
            for (Element element : attr) {
                System.out.println(element.attr("href").trim());
                aiDianYingNormalUrlSet.add(element.attr("href").trim());
            }
            //直接获取百度网盘
            if (attr.size() <= 0) {
                aiDianYingNormalUrlSet.add(urlAiDianying);
            }
        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return aiDianYingNormalUrlSet;
    }


    /**
     * 最新版本   二次获取 爬取的电影   http://www.zhimaruanjian.com/
     *
     * @param secondUrlLxxh
     * @return
     */
    public MovieNameAndUrlModel getMovieLoopsAiDianying(String secondUrlLxxh, String userAgent, String proxyIp, int proxyPort) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();


        StringBuffer stringBuffer = new StringBuffer();
        try {
            movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
//            System.setProperty("http.proxyHost", proxyIp);
//            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            URL url = new URL(secondUrlLxxh);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.21(0x17001522) NetType/WIFI Language/zh_CN");
//            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            connection.setRequestProperty("Host", "www.lxxh7.com");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setConnectTimeout(18000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];

            while (inputStream.read(bytes) >= 0) {
                stringBuffer.append(new String(bytes));
                System.out.println(stringBuffer.toString());
            }
        } catch (Exception e) {
            log.error("getMovieLoopsAiDianying ---> " + " secondUrlLxxh " +  secondUrlLxxh + "    errorInfo ->" + e.getMessage());
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            return movieNameAndUrlModel;
        }

        try {
            Document document = Jsoup.parse(stringBuffer.toString());
            String name = document.getElementsByTag("title").first().text();
            movieNameAndUrlModel.setMovieName(name);
            log.info("爱电影第二部----> begin <--------------------");
            log.info(document.body().toString());
            log.info("爱电影第二部----> end <--------------------");
            Elements attr = document.getElementsByTag("p").select("span");
            for (Element element : attr) {
                for (Element aTag : element.getElementsByTag("a")) {
                    String linkhref = aTag.attr("href");
                    if (linkhref.contains("pan.baidu.com")) {
                        log.info("这里已经拿到要爬取的url : " + linkhref);
                        movieNameAndUrlModel.setWangPanUrl(linkhref);
                        movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                        break;
                    } else {
                        continue;
                    }
                }

            }
//                第二种情况 span标签 里没有 url
            if (StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())) {
                Elements urlFinals = document.getElementsByTag("p").select("a");
                for (Element urlFianl : urlFinals) {
                    String linkhref = urlFianl.attr("href");
                    if (linkhref.contains("pan.baidu.com")) {
                        log.info("这里已经拿到要爬取的url : " + linkhref);
                        movieNameAndUrlModel.setWangPanUrl(linkhref);
                        movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                        System.out.println(linkhref);
                        break;
                    } else {
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            log.error("getMovieLoopsAiDianying --> parse error " + e.getMessage());
        }
        return movieNameAndUrlModel;
    }


    public static ResponseEntity getHttpHeader(String url, RestTemplate restTemplate, String proxyIp, int proxyPort) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(
                //设置代理服务
                new Proxy(
                        Proxy.Type.HTTP,
                        new InetSocketAddress(proxyIp, proxyPort)
                )
        );
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        requestHeaders.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        requestHeaders.add("Host", "www.lxxh7.com");
        requestHeaders.add("Upgrade-Insecure-Requests", "1");
        requestHeaders.add("Cache-Control", "max-age=0");
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET, requestEntity, String.class);
        return resultResponseEntity;
    }


    public HashMap<String, String> convertCookie(String cookie) {
        HashMap<String, String> cookiesMap = new HashMap<String, String>();
        String[] items = cookie.trim().split(";");
        for (String item : items) {
            cookiesMap.put(item.split("=")[0], item.split("=")[1]);
        }
        return cookiesMap;
    }
    /**
     * 爱电影  第二次传ID调用  老版本
     * http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
     *
     * @param url
     * @return
     */
   /* public MovieNameAndUrlModel getMovieLoopsAiDianying(String url) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        try {
            movieNameAndUrlModel.setMovieUrl(url);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);
            if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
                String html = resultResponseEntity.getBody();
*//*                System.out.println("=========================================");
                System.out.println(html);
                System.out.println("=========================================");*//*
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();
                movieNameAndUrlModel.setMovieName(name);
//                System.out.println("******");
//                System.out.println(name);
//                System.out.println("******");



                *//* 完整爬取 比较消耗资源
                Elements attr = document.getElementsByTag("p");
                for (Element element : attr) {
                    for (Element aTag : element.getElementsByTag("span").select("a")) {
                        System.out.println(aTag.text());
                        String linkhref = aTag.attr("href");
                        if (linkhref.contains("pan.baidu.com")) {
                            log.info("这里已经拿到要爬取的url : " + linkhref);
                            movieNameAndUrlModel.setWangPanUrl(linkhref);
                            System.out.println(linkhref);


                        } else {
                            continue;
                        }

                    }
                    if (element.text().contains("密码")) {
                        movieNameAndUrlModel.setWangPanPassword(element.text().split("【")[0].split(" ")[1]);
                        break;
                    }
                }*//*


//                第一种情况
                Elements attr = document.getElementsByTag("p").select("span");
                for (Element element : attr) {
                    for (Element aTag : element.getElementsByTag("a")) {
                        String linkhref = aTag.attr("href");
                        if (linkhref.contains("pan.baidu.com")) {
                            log.info("这里已经拿到要爬取的url : " + linkhref);
                            movieNameAndUrlModel.setWangPanUrl(linkhref);
                            movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
//                            System.out.println(linkhref);
                            break;
                        } else {
                            continue;
                        }
                    }

                }
//                第二种情况 span标签 里没有 url
                if(StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())){
                    Elements urlFinals = document.getElementsByTag("p").select("a");
                    for(Element urlFianl: urlFinals){
                        String linkhref = urlFianl.attr("href");
                        if (linkhref.contains("pan.baidu.com")) {
                            log.info("这里已经拿到要爬取的url : " + linkhref);
                            movieNameAndUrlModel.setWangPanUrl(linkhref);
                            movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                            System.out.println(linkhref);
                            break;
                        } else {
                            continue;
                        }
                    }
                }

//                System.out.println("-----------------");
            }
            return movieNameAndUrlModel;
        } catch (Exception e) {
            return movieNameAndUrlModel;
        }
    }*/

    /**
     * 根据电影名获取爱电影的百度盘资源
     *
     * @param searchMovieName
     * @return
     */
  /*  public List<MovieNameAndUrlModel> getAiDianYingCrawlerResult(String searchMovieName) {

        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        Set<String> set = getNormalUrlAidianying(searchMovieName);

        try {
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    MovieNameAndUrlModel movieNameAndUrlModel = getMovieLoopsAiDianying(url);
                    movieNameAndUrlModelList.add(movieNameAndUrlModel);
                }
            }
//            List<MovieNameAndUrlModel> couldUseMovieUrl = new ArrayList<>();
//            //存入数据库 做可链接校验
//            couldUseMovieUrl = invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", couldUseMovieUrl);
//            redisTemplate.opsForHash().put("aidianying", searchMovieName, couldUseMovieUrl);



            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_aidianying");
            redisTemplate.opsForHash().put("aidianying", searchMovieName, movieNameAndUrlModelList);



        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return movieNameAndUrlModelList;

    }*/


//    }
}