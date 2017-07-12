package com.ctj.oa.net;

import com.alibaba.fastjson.JSON;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * Created by lewis on 16/5/30.
 */
public class BeanJsonRequest<E> extends RestRequest<E> {
    private Class<E> clazz;

    public BeanJsonRequest(String url, Class<E> clazz) {
        this(url, RequestMethod.POST, clazz);
    }

    public BeanJsonRequest(String url, RequestMethod requestMethod, Class<E> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }
    @Override
    public E parseResponse(Headers responseHeaders, byte[] responseBody) throws Throwable {
        String response = StringRequest.parseResponseString(responseHeaders, responseBody);

        // 这里如果数据格式错误，或者解析失败，会在失败的回调方法中返回 ParseError 异常。
        return JSON.parseObject(response, clazz);
    }
}
