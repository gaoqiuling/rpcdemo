package com.itisacat.rpcdemo.client.invoker;

import com.itisacat.rpcdemo.client.register.ClientRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokeProxy implements InvocationHandler {
    private Class<?> targetInterface;

    public InvokeProxy(Class<?> targetInterface) {
        this.targetInterface = targetInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceKey = targetInterface.getName();
        ClientRegister.getInstance().ge
    }
}
