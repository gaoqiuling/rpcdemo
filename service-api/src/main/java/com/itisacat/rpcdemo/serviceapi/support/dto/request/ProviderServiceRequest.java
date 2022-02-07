package com.itisacat.rpcdemo.serviceapi.support.dto.request;

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
}
