package com.libbytian.pan.wechat.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * HTTP帮助类
 *
 * @Author sfh
 * @Date 2019/11/18 14:37
 */
@Slf4j
public class SubscribeHttpUtil {

//    private static CloseableHttpClient httpClient;
//
//    static {
//        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//        cm.setMaxTotal(100);
//        cm.setDefaultMaxPerRoute(20);
//        cm.setDefaultMaxPerRoute(50);
//        httpClient = HttpClients.custom().setConnectionManager(cm).build();
//    }

    /**
     * 发送post请求,获取访问令牌
     * 参数是url,json对象
     */
    public static String doPostBodyByJson(String url, String param) {
        try {
            StringEntity stringEntity = new StringEntity(param, CharEncoding.UTF_8);
            return doPost(url, stringEntity, CharEncoding.UTF_8, ContentType.APPLICATION_JSON);
        } catch (Exception e) {
            log.error("doPostBodyByJson", e);
        }
        return null;
    }


    /**
     * 发送 表单 POST请求
     *
     * @Author sfh
     * @Date 2019/11/12 11:33
     */
    public static String doPostFormToMap(String url, Map<String, Object> map) {

        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, CharEncoding.UTF_8);
            return doPost(url, urlEncodedFormEntity, CharEncoding.UTF_8, ContentType.APPLICATION_FORM_URLENCODED);
        } catch (Exception e) {
            log.error("doPostForm", e);
        }
        return null;
    }

    /**
     * 通过反射获取参数
     * 表单提交
     *
     * @param url
     * @param info
     * @return
     */
    public static String doPostFormToObject(String url, Object info) {

        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            if (info != null) {
                Class<?> aClass = info.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field f : declaredFields) {
                    String key = f.getName();
                    Object value = f.get(info);
                    if (value == null) {
                        continue;
                    }
                    nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
                }
            }
            //将post请求设置请求实体
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, CharEncoding.UTF_8);
            return doPost(url, urlEncodedFormEntity, CharEncoding.UTF_8, ContentType.APPLICATION_FORM_URLENCODED);
        } catch (Exception e) {
            log.error("doPostForm", e);
        }
        return null;
    }


    /**
     * post 请求底层封装
     *
     * @param url         请求地址
     * @param paramEntity 数据实体
     * @param charset     编码格式
     * @param contentType 请求类型
     * @return String 返回数据
     */
    public static String doPost(String url, HttpEntity paramEntity, String charset, ContentType contentType) {
        //创建默认httpclient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //创建post请求实例
            HttpPost httpPost = new HttpPost(url);
            //创建接受对象
            String result = null;
            httpPost.setHeader("content-type", contentType.getMimeType());
            //设置请求头
            //将post请求设置请求实体
            httpPost.setEntity(paramEntity);
            //执行请求
            //接收响应
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            //处理响应结果,从响应结果中获取httpentity
            HttpEntity entity = httpResponse.getEntity();

            //判断entity
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            return result;
        } catch (IOException e) {
            log.error("doPost 基础封装", e);
        } finally {
            //释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String sendGet(String url, String param) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String urlNameString = url + "?" + param;

            HttpGet httpGet = new HttpGet(urlNameString);
            httpGet.setHeader("accept", "*/*");
            httpGet.setHeader("connection", "Keep-Alive");
            httpGet.setHeader("album-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            HttpEntity entity = execute.getEntity();

            //判断entity
            if (entity != null) {
                result = EntityUtils.toString(entity, CharEncoding.UTF_8);
            }
            return result;
        } catch (Exception e) {
            log.error("doGET", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据url和请求参数获取URI
     */
    public static URI getURIwithParams(String url, MultiValueMap<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(params);
        return builder.build().encode().toUri();
    }

    /**
     * 获取用户IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String[] ipHeaders = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        String[] localhostIp = {"127.0.0.1", "0:0:0:0:0:0:0:1"};
        String ip = request.getRemoteAddr();
        for (String header : ipHeaders) {
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
            ip = request.getHeader(header);
        }
        for (String local : localhostIp) {
            if (ip != null && ip.equals(local)) {
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ignored) {

                }
                break;
            }
        }
        if (ip != null && ip.length() > 15 && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(','));
        }
        return ip;
    }

    public static String get(String url) {
        CloseableHttpResponse response = null;
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpGet.setConfig(requestConfig);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("Accept", "application/json");
            CloseableHttpClient httpClient = HttpClients.createDefault();

            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // 判断返回状态是否为200
            //判断entity
            if (entity != null) {
                result = EntityUtils.toString(entity, CharEncoding.UTF_8);
            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String post(String url, String jsonString) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
            CloseableHttpClient httpClient = HttpClients.createDefault();

            response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, CharEncoding.UTF_8);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @Author: FJW
     * @Date: 2020/3/3 10:01
     * @desc get请求, map封装请求参数, 请求格式form
     */
    public static String doGetByMap(String url, Map<String, String> params) {
        //客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //封装请求参数
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        //响应结果
        String responStr = "";
        CloseableHttpResponse response = null;

        try {
            //封装uri及参数
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(pairs);

            //get请求对象
            HttpGet httpGet = new HttpGet(builder.build());
            response = httpClient.execute(httpGet);

            //判断返回结果
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responStr = EntityUtils.toString(entity);
            }
            return responStr;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeClient(httpClient, response);
        }
        return responStr;
    }

    /**
     * @Author: FJW
     * @Date: 2020/3/3 10:01
     * @desc post请求, map封装请求参数, 请求头格式json
     */
    public static String doPostByMap(String url, String type, Map<String, String> params) {
        //客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //封装请求参数
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        //响应结果
        String responStr = "";
        CloseableHttpResponse response = null;

        try {
            //封装uri及参数
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            //设置请求头
            if ("json".equals(type)) {
                httpPost.setHeader("content-type", "application/json");
            } else if ("form".equals(type)) {
                httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
            }

            response = httpClient.execute(httpPost);

            //判断返回结果
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responStr = EntityUtils.toString(entity);
            }
            return responStr;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeClient(httpClient, response);
        }
        return responStr;
    }

    private static void closeClient(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            httpClient.close();
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author: FJW
     * @Date: 2020/3/3 10:02
     * @desc post请求, map封装请求参数, 请求头格式json
     */
    public static String doPostByJson(String url, String type, String jsonParams) {
        //客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //响应结果
        String responStr = "";
        CloseableHttpResponse response = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(jsonParams.getBytes("UTF-8")));
            //设置请求头
            if ("json".equals(type)) {
                httpPost.setHeader("content-type", "application/json");
            } else if ("form".equals(type)) {
                httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
            }
            System.out.println("发送请求：" + url);
            response = httpClient.execute(httpPost);
            //判断返回结果
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responStr = EntityUtils.toString(entity);
                System.out.println("entity:" + responStr);
            }
            return responStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeClient(httpClient, response);
        }
        return responStr;
    }

}