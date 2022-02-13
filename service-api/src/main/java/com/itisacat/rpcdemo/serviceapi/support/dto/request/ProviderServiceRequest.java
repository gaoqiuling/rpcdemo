package com.itisacat.rpcdemo.serviceapi.support.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Data
public class ProviderServiceRequest implements Serializable {
    private Class<?> serviceInterface;
    private transient Object serviceObject;
    private transient List<Method> methodList;
    private String serverIp;
    private int serverPort;
    private long timeout;
    //服务提供者唯一标识
    private String appKey;

    @JSONField(serialize = false)
    public ProviderServiceRequest copy() {
        ProviderServiceRequest providerService = new ProviderServiceRequest();
        providerService.setServerIp(serverIp);
        providerService.setServerPort(serverPort);
        providerService.setTimeout(timeout);
        providerService.setAppKey(appKey);
        providerService.setServiceInterface(serviceInterface);
        providerService.setServiceObject(serviceObject);
        providerService.setMethodList(methodList);
        return providerService;
    }

}
