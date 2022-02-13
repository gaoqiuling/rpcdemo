package com.itisacat.rpcdemo.restservice.controller;

import com.itisacat.rpcdemo.serviceapi.facade.ICryService;
import org.springframework.stereotype.Component;

@Component
public class DemoController implements ICryService {
    @Override
    public String cryVoice(String voice) {
        return "cry " + voice;
    }
}
