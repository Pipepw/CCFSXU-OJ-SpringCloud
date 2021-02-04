package org.verwandlung.voj.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
public class CCFSXU_OJ_Consumer_DashBoard_App {

    /**
     * 访问地址: http://localhost:9002/hystrix，因为9001被占用了。。。
     * 监控地址：http://localhost/hystrix.stream
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(CCFSXU_OJ_Consumer_DashBoard_App.class, args);
    }
}
