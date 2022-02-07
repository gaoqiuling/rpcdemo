package com.itisacat.rpc.service.service;

import com.itisacat.rpc.service.anno.RpcService;
import com.itisacat.rpcdemo.serviceapi.facade.IGoodbyeService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class GoodbyeService implements IGoodbyeService {
    @Override
    public String sayGoodbye(String somebody) {
         return "goodbye " + somebody + "!";
    }
}
