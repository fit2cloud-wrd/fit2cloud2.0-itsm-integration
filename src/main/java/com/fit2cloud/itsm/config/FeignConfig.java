package com.fit2cloud.itsm.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
import feign.Request;
import feign.Retryer;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import springfox.documentation.spring.web.json.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class FeignConfig {

    /**
     * FeigninClient的默认connectTimeout为10s，readTimeout为60。
     *
     * 仅设置超时可能不会立即生效，因为默认重试次数为5次。因此，如果想要快速失败，则必须同时自定义超时和重试的参数，并应确保反向代理。
     *
     * 例如，nginx的proxy_connect_timeout和proxy_read_timeout必须大于feign的配置才能生效。
     * 否则，nginx的504网关超时仍然会被外部用户感知，并且无法实现回滚效果。
     */

    /**
     * 配置请求重试
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(200, TimeUnit.SECONDS.toMillis(10), 10);
    }

    /**
     * 设置请求超时时间
     */
    @Bean
    Request.Options feignOptions() {
        return new Request.Options(60 * 1000, 60 * 1000);
    }

    /**
     * 打印请求日志
     *
     * NONE: 不记录任何信息
     * BASIE：仅记录请求方法，URL以及响应状态码和执行时间
     * HEADERS：除了记录BASIE级别得信息之外，还会记录请求和响应得头信息
     * FULL：记录所有请求与响应得明细，包括头信息，请求体，元数据等。
     *
     * @return
     */
    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }


    /**
     * feign 配置使用 RequestLine 注解
     *
     * 处理启动报错：Method getLinksForTrack not annotated with HTTP method type (ex. GET, POST)
     *
     * 原因：
     *      @RequestLine is a core Feign annotation, but you are using the Spring Cloud @FeignClientwhich uses Spring MVC annotations.
     *      feign 默认使用的是 spring mvc 注解（就是 RequestMapping 之类的） ，所以需要通过新增一个配置类来修改其“契约”。
     */
//    @Bean
//    public Contract feignContract() {
//        return new Contract.Default();
//    }


    @Bean
    public Encoder cmdbClientEncoder() {
        return new SpringEncoder(feignHttpConverter());
    }

    private ObjectFactory<HttpMessageConverters> feignHttpConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(buildFastJsonConverter());
        return () -> httpMessageConverters;
    }

    private FastJsonHttpMessageConverter buildFastJsonConverter() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>();
        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mediaTypes.add(mediaTypeJson);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.getSerializeConfig().put(Json.class, new SwaggerJsonSerializer());
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        return fastJsonHttpMessageConverter;
    }


//    @Bean
//    public Decoder itsmClientDecoder() {
//        return new ItsmClientDecoder();
//    }

}