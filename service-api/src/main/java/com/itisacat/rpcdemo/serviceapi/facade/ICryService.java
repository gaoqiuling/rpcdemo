package com.itisacat.rpcdemo.serviceapi.facade;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cry/")
public interface ICryService {

    @RequestMapping(value = "loud", method = RequestMethod.GET)
    String cryVoice(@RequestParam("voice") String voice);
}
