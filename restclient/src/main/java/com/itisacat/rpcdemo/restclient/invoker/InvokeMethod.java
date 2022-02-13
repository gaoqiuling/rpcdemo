package com.itisacat.rpcdemo.restclient.invoker;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * *@author by bxy
 * *@Date 2020/1/2
 **/
@Data
public class InvokeMethod {
    private Class interfaceClass;
    private Method method;
    private Object[] params;

    public InvokeMethod(Class interfaceClass, Method method, Object[] params) {
        this.interfaceClass = interfaceClass;
        this.method = method;
        this.params = params;
    }

}
