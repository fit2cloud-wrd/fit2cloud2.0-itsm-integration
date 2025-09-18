package com.fit2cloud.itsm.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String HTTPS = "https";

    /**
     * 请求体转换为x-www-form-urlencoded请求的格式
     * @param params
     * @return
     */
    public static String mapTranselateToEncode(Map<String, String> params) {
        /*待返回结果*/
        StringBuilder stringBuilder = new StringBuilder();

        /*执行转换*/
        int count = 0;
        for (String key : params.keySet()) {
            count++;
            stringBuilder.append(key + "=");
            if (!org.springframework.util.StringUtils.isEmpty(params.get(key))) {
                stringBuilder.append(params.get(key));
            }
            if (count != params.size()) {
                stringBuilder.append("&");
            }
        }

        /*返回结果*/
        return stringBuilder.toString();
    }

    /**
     * 根据url构建HttpClient（区分http和https）
     *
     * @param url 请求地址
     * @return CloseableHttpClient实例
     */
    private static CloseableHttpClient buildHttpClient(String url) {
        try {
            if (url.startsWith(HTTPS)) {
                // https 增加信任设置
                TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).build();
                HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
                return HttpClients.custom()
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(hostnameVerifier)
                        .build();
            } else {
                // http
                return HttpClientBuilder.create().build();
            }
        } catch (Exception e) {
            throw new RuntimeException("HttpClient构建失败", e);
        }
    }

    /**
     * Get http请求
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @return 响应结果字符串
     */
    public static String get(String url, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpGet httpGet = new HttpGet(url);

        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpGet.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpGet.addHeader(key, header.get(key));
            }

            httpGet.addHeader(HTTP.CONTENT_ENCODING, config.getCharset());

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, config.getCharset());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * Post请求，请求内容必须为JSON格式的字符串
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @param json   JSON格式的字符串
     * @return 响应结果字符串
     */
    public static String post(String url, String json, HttpClientConfig config) {
        String result = "";
        LogUtil.info("postUtl:" + url + "==============json: " + json);
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpPost.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPost.addHeader(HTTP.CONTENT_ENCODING, config.getCharset());

            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            entityBuilder.setContentEncoding(config.getCharset());
            HttpEntity requestEntity = entityBuilder.build();
            httpPost.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, config.getCharset());
            if (StringUtils.isNotBlank(result)
                    && (JSONObject.parseObject(result).containsKey("success") && !JSONObject.parseObject(result).getBoolean("success"))
                    && (JSONObject.parseObject(result).containsKey("BAD_REQUEST") &&!"BAD_REQUEST".equals(JSONObject.parseObject(result).getString("code")))){
                JSONObject resultObj = JSONObject.parseObject(result);
                if (resultObj.containsKey("message") && StringUtils.isNotBlank(resultObj.getString("message"))){
                    throw new RuntimeException(resultObj.getString("message"));
                }else{
                    throw new RuntimeException(result);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("HttpClient查询失败:"+e.getMessage(), e);
            throw new RuntimeException("HttpClient查询失败:"+e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    public static String postFormUrlEncoded(String url, String json, HttpClientConfig config) {
        String result = " ";
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        try {
            BasicResponseHandler handler = new BasicResponseHandler();
            //设置请求格式
            StringEntity entity = new StringEntity(json, "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            if (!header.isEmpty()) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            //执行POST请求
            result = httpClient.execute(httpPost, handler);
            if (null != result) {
                JSONObject parse = JSONObject.parseObject(result);
                JSONObject data = parse.getJSONObject("data");
                if (null != data && data.getInteger("failed_count") > 0) {
                    JSONArray array = data.getJSONArray("data");
                    LogUtil.error(">>>>>>>>postFormUrlEncoded Exception: " + array.toJSONString());
                    throw new RuntimeException(array.getJSONObject(0).getString("error") + ":" +
                            array.getJSONObject(0).toJSONString());
                }
            }
            return result;
        } catch (Exception e) {
            LogUtil.error(">>>>>>>>postFormUrlEncoded Exception: " + e.toString());
            LogUtil.info(">>>>>>>>postFormUrlEncoded Exception: " + e.toString());
            throw new RuntimeException(e.getMessage());
        } finally {
            //释放连接
            try {
                httpClient.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * Post请求，请求内容必须为JSON格式的字符串
     *
     * @param url  请求地址
     * @param json JSON格式的字符串
     * @return 响应结果字符串
     */
    public static String post(String url, String json) {
        return HttpClientUtil.post(url, json, null);
    }

    /**
     * Post请求，请求内容必须为键值对参数
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @param body   请求内容键值对参数
     * @return 响应结果字符串
     */
    public static String post(String url, Map<String, String> body, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpPost.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPost.addHeader(key, header.get(key));
            }
            httpPost.addHeader(HTTP.CONTENT_ENCODING, config.getCharset());

            if (body != null && body.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<>();
                for (String key : body.keySet()) {
                    nvps.add(new BasicNameValuePair(key, body.get(key)));
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, config.getCharset()));
                } catch (Exception e) {
                    logger.error("HttpClient转换编码错误", e);
                    throw new RuntimeException("HttpClient转换编码错误", e);
                }
            }

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, config.getCharset());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }


    /**
     * Post请求，请求内容必须为JSON格式的字符串
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @param json   JSON格式的字符串
     * @return 响应结果字符串
     */
    public static String put(String url, String json, HttpClientConfig config) {
        LogUtil.info("putUtl:" + url + "==============json: " + json);
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPut httpPut = new HttpPut(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpPut.setConfig(config.buildRequestConfig());

            Map<String, String> header = config.getHeader();
            for (String key : header.keySet()) {
                httpPut.addHeader(key, header.get(key));
            }
            httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPut.addHeader(HTTP.CONTENT_ENCODING, config.getCharset());

            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            entityBuilder.setContentEncoding(config.getCharset());
            HttpEntity requestEntity = entityBuilder.build();
            httpPut.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, config.getCharset());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    public static HttpClientConfig auth() {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        httpClientConfig.addHeader("Accept", "application/json;charset=UTF-8");
        httpClientConfig.addHeader("Content-type", "application/json");
        return httpClientConfig;
    }

}
