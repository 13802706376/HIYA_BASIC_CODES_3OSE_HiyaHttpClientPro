package com.hiya.ee.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HiyaHttpClient
{

    private static final Log LOGGER = LogFactory.getLog(HiyaHttpClient.class);

    /**
     * 发送HTTP GET方式请求，返回请求响应中的字符串，不适应需要post返回响应数据的请求场景
     * 
     * @param reqUrl
     * @return
     */
    public static String sendHttpGetReqToServer(String reqUrl)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            HttpGet httpGet = new HttpGet(reqUrl);
            
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(5000).build();
            httpGet.setConfig(requestConfig);
            
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            try
            {
                if (null != response1 && null != response1.getEntity())
                {
                    return EntityUtils.toString(response1.getEntity(), StandardCharsets.UTF_8.toString());
                }
            } catch (ParseException e)
            {
                LOGGER.error("向Http服务端发送请求时发生异常，原因：", e);
            } finally
            {
                if (response1 != null)
                    response1.close();
            }
        } catch (IOException e)
        {
            LOGGER.error("向Http服务端发送请求时发生异常，原因：", e);
        } finally
        {
            if (httpclient != null)
            {
                try
                {
                    httpclient.close();
                } catch (IOException e)
                {
                    LOGGER.error("关闭httpclient时发生异常，原因：", e);
                }
            }
        }
        return null;
    }

    /**
     * 发送HTTP post方式请求
     * 
     * @param httpServerUrl
     *            请求地址
     * @param map
     *            携带的参数值
     * @return
     */
    public static String sendHttpPostReqToServerByParams(String httpServerUrl, Map<String, String> map)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = null;
        String status = null;
        try
        {
            HttpPost httpPost = new HttpPost(httpServerUrl);
            if (null != map && !map.isEmpty())
            {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : map.entrySet())
                {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            }
            CloseableHttpResponse response1 = httpclient.execute(httpPost);
            try
            {
                if (null != response1)
                {
                    if (null != response1.getStatusLine())
                        status = response1.getStatusLine().toString();
                    if (null != response1.getEntity())
                        result = EntityUtils.toString(response1.getEntity(), StandardCharsets.UTF_8);
                }
            } catch (ParseException e)
            {
                LOGGER.error("向Http服务端发送请求时发生异常，原因：", e);
            } finally
            {
                if (response1 != null)
                    response1.close();
            }
        } catch (IOException e)
        {
            LOGGER.error("向Http服务端发送请求时发生异常，原因：", e);
        } finally
        {
            if (httpclient != null)
            {
                try
                {
                    httpclient.close();
                } catch (IOException e)
                {
                    LOGGER.error("关闭httpclient时发生异常，原因：", e);
                }
            }
        }
        LOGGER.info(String.format("向Http服务端发送请求，请求地址：%s，请求返回状态：%s，请求返回报文：%s", httpServerUrl, status, result));
        return result;
    }

    public static void main(String[] args)
    {

    }
}
