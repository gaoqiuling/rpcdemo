package com.itisacat.rpcdemo.client.register;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class InitConfig {
    @PostConstruct
    private void init() {
        //ClientRegister.getInstance().init
    }
}
