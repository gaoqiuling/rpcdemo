package com.itisacat.rpcdemo.restclient.invoker;

import com.itisacat.rpcdemo.restclient.http.ProtocolHandleImpl;
import com.itisacat.rpcdemo.restclient.support.dto.request.InvokeMethodRequest;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvokeFactoryBean implements FactoryBean {
    private Class<?> interfaceType;

    public InvokeFactoryBean(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                InvokeMethodRequest request = new InvokeMethodRequest(interfaceType, method, args);
                return ProtocolHandleImpl.instance.execute(request);
            }
        });

    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
