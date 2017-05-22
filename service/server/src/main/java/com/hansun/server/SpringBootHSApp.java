package com.hansun.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
// same as @Configuration @EnableAutoConfiguration @ComponentScan
public class SpringBootHSApp extends org.springframework.boot.context.web.SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootHSApp.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHSApp.class, args);
    }
}
