package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class LearnSpringGatewaySwagger3Application {

    public static void main(String[] args) {
        SpringApplication.run(LearnSpringGatewaySwagger3Application.class, args);
    }

}
