package com.itisacat.rpcdemo.restclient.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.itisacat.rpcdemo.restclient"})
public class RestClientStart {
    public static void main(String[] args) {
        SpringApplication.run(RestClientStart.class, args);
    }
}
