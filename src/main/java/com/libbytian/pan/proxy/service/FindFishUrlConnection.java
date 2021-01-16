package com.libbytian.pan.proxy.service;

import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.Cleanup;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


@Component

public class FindFishUrlConnection {


    public String FindFishUrlConnection(String proxyIp, int proxyPort, String crawlerWebUrl) throws IOException {
        StringBuffer html = new StringBuffer();
        String userAgent = UserAgentUtil.randomUserAgent();
        System.getProperties().setProperty("proxySet", "true");
        System.setProperty("http.proxyHost", proxyIp);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));
        URL url = new URL(crawlerWebUrl);

        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Host", "www.lxxh7.com");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        conn.setReadTimeout(30000);

        @Cleanup BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

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

        return new String(html.toString().trim().getBytes("ISO-8859-1"), "UTF-8").toLowerCase();


    }


}


