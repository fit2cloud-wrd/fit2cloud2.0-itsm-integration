package com.fit2cloud.itsm.util;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.HttpClientUtil;
import com.fit2cloud.itsm.common.utils.ApiUtil;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpClient {

    public <T> T get(String url, Map<String, String> params, Class<T> resultType, Class<?>... generics) {
        URI uri = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key));
                }
            }
            uri = builder.build();
        } catch (URISyntaxException e) {
            F2CException.throwException("URL地址格式错误：" + url);
        }
        String r = HttpClientUtil.get(uri.getPath(), null);
        return ApiUtil.parseResult(r, resultType, generics);
    }

    public <T> T post(String url, String requestBody, Class<T> resultType, Class<?>... generics) {
        String r = HttpClientUtil.post(url, requestBody);
        return ApiUtil.parseResult(r, resultType, generics);
    }

}
