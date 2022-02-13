package com.itisacat.rpcdemo.restclient.utils;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLEncoder;
import java.util.Map;

public class HttpMethodUtils {

    public static String MethodGetParamsHandle(String url, Method method, Object[] args) throws UnsupportedEncodingException {
        StringBuilder urlParams = new StringBuilder("?");
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            if (ClassUtils.isPrimitive(args[i].getClass())) {
                String paramName = "";
                if (!params[i].isNamePresent()) {
//                    //   paramName = localParams[i];
//                    MethodParameter param = new MethodParameter(method, i);
//                    param.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
//                    paramName = param.getParameterName();
                    RequestParam param = params[i].getAnnotation(RequestParam.class);
                    if (param != null)
                        paramName = param.value();
                    else
                        paramName = "arg" + i;

                } else
                    paramName = params[i].getName();

                String value = URLEncoder.encode(args[i].toString(), "utf-8");

                url = url.replaceAll("\\{" + paramName + "\\}", value);
                urlParams.append(paramName + "=" + value + "&");
            } else {
                Map<String, Object> objectMap = JsonUtil.json2Map(JsonUtil.object2JSON(args[i]));
                for (Map.Entry<String, Object> item : objectMap.entrySet()) {
                    if (item.getValue() != null && ClassUtils.isPrimitive(item.getValue().getClass())) {
                        String value = URLEncoder.encode(item.getValue().toString(), "utf-8");
                        url = url.replaceAll("\\{" + item.getKey() + "\\}", value);
                        urlParams.append(item.getKey() + "=" + value + "&");
                    }
                }
            }

        }
        return url + urlParams.toString().substring(0, urlParams.length() - 1);
    }


}
