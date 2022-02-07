package com.itisacat.rpc.service.service;

import com.itisacat.rpc.service.anno.RpcService;
import com.itisacat.rpcdemo.serviceapi.facade.IHelloService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class HelloService implements IHelloService {
    @Override
    public String sayHello(String somebody) {
        return "hello " + somebody + "!";
    }
}
