package com.itisacat.rpcdemo.restclient.support.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokeMethodRequest {
    private Class interfaceClass;
    private Method method;
    private Object[] params;
}
