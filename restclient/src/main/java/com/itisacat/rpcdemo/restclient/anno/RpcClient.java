package com.itisacat.rpcdemo.restclient.anno;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {
 
}
