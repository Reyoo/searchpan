package com.libbytian.pan.proxy.test;

/**
 * @author: QiSun
 * @date: 2021-01-14
 * @Description:
 */
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @author Jason
 * @date  Oct 27, 2010
 * @version 1.0
 */
public class TestProxyIp  {

    public static void main(String[] args) throws IOException {
        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");
        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
        String ip = "113.194.50.165";
        System.getProperties().setProperty("http.proxyHost", ip);
        System.getProperties().setProperty("http.proxyPort", "9999");

        //确定代理是否设置成功
        System.out.println((getHtml("http://www.lxxh7.com/?s=%e6%b3%b0%e5%9d%a6")));

    }

    private static String getHtml(String address){
        StringBuffer html = new StringBuffer();
        String result = null;
        try{
            URL url = new URL(address);
            URLConnection conn = url.openConnection();

            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Host", "www.lxxh7.com");
            conn.setRequestProperty("Cache-Control","max-age=0");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36");
            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");

//            conn.setRequestProperty("Cookie", "UM_distinctid=176f48973abb0f-04d554e383a876-2775204d-62220-176f48973ac3b; CNZZDATA1277388696=108239928-1610417615-%7C1610698621");
            conn.setReadTimeout(30000);



            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());


            try{
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
            }finally{
                in.close();
                conn = null;
                url = null;
            }
            result = new String(html.toString().trim().getBytes("ISO-8859-1"), "gb2312").toLowerCase();

            System.out.println(gb2312ToUtf8(result));

//            System.out.println("=================");
//            System.out.println(        URLDecoder.decode(result,"utf-8"));
//            System.out.println("=================");

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            html = null;
        }
        return result;
    }


    public static String gb2312ToUtf8(String str) {

        String urlEncode = "" ;

        try {

            urlEncode = URLEncoder.encode (str, "UTF-8" );

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return urlEncode;

    }
}