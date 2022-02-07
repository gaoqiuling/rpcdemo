package com.itisacat.rpcdemo.client.controller;

import com.itisacat.rpcdemo.client.anno.RpcClient;
import com.itisacat.rpcdemo.serviceapi.facade.IGoodbyeService;
import com.itisacat.rpcdemo.serviceapi.facade.IHelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @RpcClient
    private IHelloService helloService;
    @RpcClient
    private IGoodbyeService goodbyeService;

    @GetMapping("/hello")
    public String sayHello(@RequestParam("words") String words) {
        return helloService.sayHello(words);
    }

    @GetMapping("/goodbye")
    public String sayGoodbye(@RequestParam("words") String words) {
        return goodbyeService.sayGoodbye(words);
    }

}
