package com.itisacat.rpcdemo.restclient.controller;

import com.itisacat.rpcdemo.restclient.anno.RpcClient;
import com.itisacat.rpcdemo.serviceapi.facade.ICryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/demo/v1.1/")
public class DemoController {
    @RpcClient
    private ICryService cryService;
    @RequestMapping(value = "cry", method = RequestMethod.GET)
    public String sayHello(@RequestParam("voice") String voice) {
        return cryService.cryVoice(voice);
    }
}
