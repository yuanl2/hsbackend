package com.hansun.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.hansun.server.db.dao")
@EntityScan("com.hansun.server.dto")
@SpringBootApplication
// same as @Configuration @EnableAutoConfiguration @ComponentScan
/**
 * @author yuanl2
 */
public class SpringBootHSApp extends SpringBootServletInitializer {

    public SpringBootHSApp() {
        setRegisterErrorPageFilter(false); // <- this one
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootHSApp.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHSApp.class, args);
    }
}
