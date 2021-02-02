package org.verwandlung.voj.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;


@SpringBootApplication
@EnableJms  //启动Jms，替代了之前在下面写的Bean
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
