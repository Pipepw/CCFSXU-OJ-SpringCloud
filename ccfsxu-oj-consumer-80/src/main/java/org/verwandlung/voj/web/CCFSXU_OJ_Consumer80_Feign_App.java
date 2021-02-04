package org.verwandlung.voj.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.verwandlung.voj.web"})
@EnableSwagger2
public class CCFSXU_OJ_Consumer80_Feign_App {
    public static void main(String[] args) {
        SpringApplication.run(CCFSXU_OJ_Consumer80_Feign_App.class, args);
    }
}
