package com.itisacat.rpcdemo.restservice.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.itisacat.rpcdemo.restservice"})
public class RestServiceStart {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceStart.class, args);
    }

}
