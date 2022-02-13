package com.itisacat.rpcdemo.restclient.http;

import com.itisacat.rpcdemo.restclient.support.dto.request.BaseRequest;
import com.itisacat.rpcdemo.restclient.support.dto.request.InvokeMethodRequest;
import com.itisacat.rpcdemo.restclient.utils.ClassUtils;
import com.itisacat.rpcdemo.restclient.utils.HttpClientUtil;
import com.itisacat.rpcdemo.restclient.utils.HttpMethodUtils;
import com.itisacat.rpcdemo.restclient.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * *@author by bxy
 * *@Date 2020/1/2
 **/
@Slf4j
public class ProtocolHandleImpl {
    public static ProtocolHandleImpl instance = new ProtocolHandleImpl();

    public Object execute(InvokeMethodRequest invocation) throws IOException {
        RequestMapping annotation = (RequestMapping) invocation.getInterfaceClass().getAnnotation(RequestMapping.class);
        RequestMapping methodAnnotation = invocation.getMethod().getAnnotation(RequestMapping.class);
        if (methodAnnotation == null) {
            throw new RuntimeException("方法参数不正确");
        }
        String path = (annotation == null ? "" : annotation.value()[0]) + "/" + (methodAnnotation == null ? "" : methodAnnotation.value()[0]);
        try {
            String requestMethod = methodAnnotation.method().length > 0 ? methodAnnotation.method()[0].name() : "GET";
            if (requestMethod.equals("GET"))
                //如果是GET请求需要组装Url数据
                path = HttpMethodUtils.MethodGetParamsHandle(path, invocation.getMethod(), invocation.getParams());
            //获取请求域名
            String requestDomain = findRequestDomain(invocation);
            if (StringUtils.isEmpty(requestDomain)) {
                throw new RuntimeException("domain url not config");
            }
            String requestUrl = requestDomain + path;
            Object requestParam = invocation.getParams()[0];
            if (requestParam instanceof BaseRequest) {
                BaseRequest request = (BaseRequest) requestParam;
                request.setUrl(requestUrl);
                String result = getResult(request, requestMethod);
                return JsonUtil.json2Type(result, invocation.getMethod().getGenericReturnType());
            } else {
                return UrlNormalRequest(invocation, requestMethod, requestUrl);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    private String findRequestDomain(InvokeMethodRequest invocation) {
        //后续走配置
        String name = invocation.getInterfaceClass().getName();
        if (name.contains(".serviceapi.")) {
            return "http://localhost:8800";
        }
        return null;
    }

    private Object UrlNormalRequest(InvokeMethodRequest invocation, String requestMethod, String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setDoOutput(true);
        uc.setRequestMethod(requestMethod);
        uc.setRequestProperty("content-type", "application/json");
        if (requestMethod.equals("POST") && invocation.getParams().length > 0) {
            OutputStream outputStream = uc.getOutputStream();
            outputStream.write(JsonUtil.object2JSON(invocation.getParams()[0]).getBytes());
            outputStream.flush();
            outputStream.close();
        }
        try (InputStream inputStream = uc.getInputStream()) {
            String returnStr = IOUtils.toString(inputStream);
            if (ClassUtils.isPrimitive((Class<?>) invocation.getMethod().getGenericReturnType())) {
                return returnStr;
            }
            return JsonUtil.json2Type(returnStr, invocation.getMethod().getGenericReturnType());
        } catch (Exception ex) {
            String errorLog = String.format("proxy GET request fail url:{},request:{},ex:{}", requestUrl, invocation.getParams(), ex);
            log.error(errorLog);
            throw new RuntimeException("系统异常，请稍后重试");
        }
    }


    private <R extends BaseRequest> String getResult(R request, String requestMethod) {
        switch (HttpMethod.valueOf(requestMethod)) {
            case GET:
                return HttpClientUtil.sendHttpGet(request.getUrl());
            case POST:
                return HttpClientUtil.sendHttpPost(request.getUrl(), JsonUtil.object2JSON(request.getParamData()), null);
            default:
                throw new RuntimeException("Not support '" + requestMethod + " HttpMehthod");
        }
    }
}
