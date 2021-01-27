package com.libbytian.pan.proxy.test;

/**
 * @author: QiSun
 * @date: 2021-01-14
 * @Description:
 */

import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Jason
 * @version 1.0
 * @date Oct 27, 2010
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestProxyIp {

    @Value("$(user.xiaoyou.yingmiao)")
    String xiaoyouUrl;


    private final RedisTemplate redisTemplate;

    public static void main(String[] args) throws IOException {

        String movieName = "海贼王之黄金城";
        Set<String> movieList = getXiaoyouUrl(movieName);
        List<MovieNameAndUrlModel> list = getbaiduPan(movieList);


        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");
        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
//        String ip = "180.250.12.10";
//        System.getProperties().setProperty("http.proxyHost", ip);
//        System.getProperties().setProperty("http.proxyPort", "80");

        //确定代理是否设置成功
        System.out.println((getHtml("http://www.lxxh7.com/?s=%e5%90%b8%e8%a1%80%e9%ac%bc%e7%8c%8e%e4%ba%baD")));

    }


    private static String getHtml(String address) {
        StringBuffer html = new StringBuffer();
        String result = null;
        try {
            URL url = new URL(address);
            URLConnection conn = url.openConnection();

            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Host", "www.lxxh7.com");
            conn.setRequestProperty("Cache-Control", "max-age=0");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36");
//            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");

//            conn.setRequestProperty("Cookie", "UM_distinctid=176f48973abb0f-04d554e383a876-2775204d-62220-176f48973ac3b; CNZZDATA1277388696=108239928-1610417615-%7C1610698621");
            conn.setReadTimeout(30000);

            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());


            try {
                String inputLine;
                byte[] buf = new byte[4096];
                int bytesRead = 0;
                while (bytesRead >= 0) {
                    inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");
                    html.append(inputLine);
                    bytesRead = in.read(buf);
                    inputLine = null;
                }
                buf = null;
            } finally {
                in.close();
                conn = null;
                url = null;
            }

//            System.out.println(html.toString());
            result = new String(html.toString().trim().getBytes("ISO-8859-1"), "UTF-8").toLowerCase();


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            html = null;
        }
        return result;
    }


    public static String gb2312ToUtf8(String str) {

        String urlEncode = "";

        try {

            urlEncode = URLEncoder.encode(str, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return urlEncode;

    }


    public static Set<String> getXiaoyouUrl(String movieName) throws IOException {


        Set<String> movieList = new HashSet<>();
        String url = "http://y.yuanxiao.net.cn" + "/?s=" + movieName;


        Document document = Jsoup.connect(url).get();

        System.out.println(document);


        //拿到查询结果 片名及链接
        Elements elements = document.getElementById("container").getElementsByClass("entry-title");


        for (Element element : elements) {
            String movieUrl = element.select("a").attr("href");

            if (StringUtils.isBlank(movieUrl)){
                movieList.add(url);
            }else {
                movieList.add(movieUrl);
            }
        }

        return movieList;
    }


    public static List<MovieNameAndUrlModel> getbaiduPan(Set<String> movieList) throws IOException {

        List<MovieNameAndUrlModel> movieNameAndUrlModel = new ArrayList<>();

        for (String url : movieList) {
            movieNameAndUrlModel.addAll(getPanUrl3(url));
        }

        System.out.println(movieNameAndUrlModel);

        return  movieNameAndUrlModel;










    }

    /**
     * 两层链接情况
     * @param url
     * @return
     * @throws IOException
     */
    public static List<MovieNameAndUrlModel> getPanUrl3(String url) throws IOException {

        List<MovieNameAndUrlModel> list = new ArrayList();


        Document document = Jsoup.connect(url).get();
        System.out.println("===================");
        System.out.println(document.text());
        System.out.println("===================");
        System.out.println(document.body());
        System.out.println("===================");
        System.out.println(document.toString());
        System.out.println("===================");
        String movieName = document.getElementsByTag("title").first().text();

        String[] arrName = movieName.split("- 小悠家");
        movieName = arrName[0];

        Elements pTagAttr = document.getElementsByTag("p");



        for (Element element : pTagAttr) {
            if (element.select("a").attr("href").contains("pan.baidu")) {
                MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();

                 if (element.childNodeSize() == 3){
                     String password = element.childNode(2).toString().split("&nbsp; &nbsp; ")[1];
                     movieNameAndUrlModel.setWangPanPassword(password);
                 }

                 //判断片名是否需要拼接
                int indexName = element.childNode(0).toString().indexOf(".视频：");
                 if (indexName == -1){
                     movieNameAndUrlModel.setMovieName(movieName);
                 }else {
                     movieNameAndUrlModel.setMovieName(movieName + element.childNode(0).toString().substring(0,indexName));
                 }


                movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
                movieNameAndUrlModel.setMovieUrl(url);

                list.add(movieNameAndUrlModel);
            } else {
                continue;
            }

        }

        return list;
    }



}